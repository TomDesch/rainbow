from pathlib import Path
import re
import struct
import zipfile
import xml.etree.ElementTree as ET

ARTIFACT_GLOB = "Rainbow-*.jar"
PLUGIN_PACKAGE = "io/stealingdapenta/"
RELOCATED_ROOT = PLUGIN_PACKAGE + "lib/"
JAVA_21_CLASS_VERSION = 65
POM_NAMESPACE = "{http://maven.apache.org/POM/4.0.0}"
REQUIRED_RESOURCES = (
    "plugin.yml",
    "config.yml",
    "META-INF/THIRD_PARTY_NOTICES.txt",
    "META-INF/licenses/adventure-5.2.0-LICENSE.txt",
    "META-INF/licenses/apache-2.0-LICENSE.txt",
)
FORBIDDEN_PACKAGES = (
    "org/bukkit/",
    "io/papermc/",
    "com/destroystokyo/paper/",
    "net/minecraft/",
    "net/kyori/",
    "org/jetbrains/",
    "org/jspecify/",
    "org/yaml/",
    "net/md_5/",
    "org/joml/",
    "org/junit/",
    "org/opentest4j/",
    "org/apiguardian/",
    "net/bytebuddy/",
    "org/mockito/",
    "com/google/",
)


def require(condition, message):
    if not condition:
        raise RuntimeError(message)


artifacts = [
    path for path in Path("target").glob(ARTIFACT_GLOB)
    if not path.name.startswith("original-")
    and not path.stem.endswith(("-sources", "-javadoc", "-tests"))
]
require(len(artifacts) == 1, f"Expected one deployable JAR, found {artifacts}")
artifact = artifacts[0]

with zipfile.ZipFile(artifact) as jar:
    entries = jar.namelist()
    for resource in REQUIRED_RESOURCES:
        require(resource in entries, f"Missing resource: {resource}")

    plugin_classes = 0
    for entry in entries:
        if not entry.endswith(".class"):
            continue
        require(not entry.endswith("module-info.class"), f"Module descriptor survived shading: {entry}")
        require(not entry.startswith(FORBIDDEN_PACKAGES), f"Unshaded dependency or server class: {entry}")
        if entry.startswith(PLUGIN_PACKAGE) and not entry.startswith(RELOCATED_ROOT):
            plugin_classes += 1
            major = struct.unpack(">H", jar.read(entry)[6:8])[0]
            require(major == JAVA_21_CLASS_VERSION, f"Unexpected plugin bytecode {major}: {entry}")
    require(plugin_classes > 0, "No plugin classes found in the deployable artifact")

    for package in (
        RELOCATED_ROOT + "kyori/",
        RELOCATED_ROOT + "annotations/",
        RELOCATED_ROOT + "jspecify/",
    ):
        require(any(entry.startswith(package) and entry.endswith(".class") for entry in entries),
                f"Missing relocated dependency package: {package}")

    metadata = jar.read("plugin.yml").decode("utf-8")
    require(re.search(r"(?m)^api-version:\s*'26\.3'\s*$", metadata),
            "plugin.yml api-version must be quoted and match Minecraft 26.3")
    require("cycle-speed: 5" in jar.read("config.yml").decode("utf-8"),
            "Default config.yml is missing the expected cycle speed")

    pom = ET.fromstring(Path("pom.xml").read_bytes())
    properties = pom.find(POM_NAMESPACE + "properties")
    require(properties is not None, "Maven properties are missing")
    property_values = {
        child.tag.rsplit("}", 1)[-1]: child.text
        for child in properties
    }
    require(property_values.get("maven.compiler.release") == "21", "Compiler release changed from Java 21")
    require(property_values.get("spigot-api.version") == "26.3-R0.1-20261004.024323-9",
            "Spigot API snapshot pin changed without updating compatibility verification")
    require(property_values.get("adventure.version") == "5.2.0", "Adventure version changed without updating notices")

    dependencies = pom.find(POM_NAMESPACE + "dependencies")
    dependency_coordinates = {
        (dependency.findtext(POM_NAMESPACE + "groupId"), dependency.findtext(POM_NAMESPACE + "artifactId"))
        for dependency in dependencies
    }
    require(("org.spigotmc", "spigot-api") in dependency_coordinates, "Spigot API compile guard is missing")
    require(("io.papermc.paper", "paper-api") not in dependency_coordinates, "Paper API must not replace the Spigot compile guard")

    adventure_license = jar.read("META-INF/licenses/adventure-5.2.0-LICENSE.txt").decode("utf-8")
    apache_license = jar.read("META-INF/licenses/apache-2.0-LICENSE.txt").decode("utf-8")
    require("Copyright (c) 2017-2025 KyoriPowered" in adventure_license,
            "Adventure attribution is missing")
    require("permission notice shall be included" in adventure_license.lower(),
            "Adventure MIT license terms are incomplete")
    require("END OF TERMS AND CONDITIONS" in apache_license, "Apache-2.0 license terms are incomplete")

    notices = jar.read("META-INF/THIRD_PARTY_NOTICES.txt").decode("utf-8")
    for coordinate in (
        "net.kyori:adventure-text-serializer-legacy:5.2.0",
        "net.kyori:adventure-api:5.2.0",
        "net.kyori:adventure-key:5.2.0",
        "org.jetbrains:annotations:26.1.0",
        "org.jspecify:jspecify:1.0.0",
    ):
        require(coordinate in notices, f"Bundled dependency attribution is stale: {coordinate}")

print(f"Verified deployable artifact: {artifact} ({artifact.stat().st_size:,} bytes)")
