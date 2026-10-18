package io.stealingdapenta.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

class ConfigurationDefaultsTest {

    @Test
    void saveDefaultIfMissingPreservesExistingFileBytes() throws Exception {
        Path testDirectory = Path.of("target", "configuration-default-tests");
        Files.createDirectories(testDirectory);
        Path configFile = testDirectory.resolve(UUID.randomUUID() + ".yml");
        String operatorConfig = "# keep this operator comment\ncycle-speed: 12\n";
        try {
            Files.writeString(configFile, operatorConfig, StandardCharsets.UTF_8);

            ConfigurationDefaults.saveDefaultIfMissing(configFile, stream("cycle-speed: 5\n"));
            YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile.toFile());
            ConfigurationDefaults.addDefaults(config, ConfigKey.values());

            assertEquals(operatorConfig, Files.readString(configFile, StandardCharsets.UTF_8));
            assertEquals(12, config.getInt(ConfigKey.CYCLE_SPEED.getKey()));
            assertEquals(true, config.getBoolean(ConfigKey.ARMOR_ITEM_FEATURE.getKey()));
        } finally {
            Files.deleteIfExists(configFile);
        }
    }

    @Test
    void saveDefaultIfMissingCreatesFileForFreshInstall() throws Exception {
        Path testDirectory = Path.of("target", "configuration-default-tests");
        Files.createDirectories(testDirectory);
        Path configFile = testDirectory.resolve(UUID.randomUUID() + ".yml");
        String defaultConfig = "cycle-speed: 5\n";
        try {
            ConfigurationDefaults.saveDefaultIfMissing(configFile, stream(defaultConfig));

            assertEquals(defaultConfig, Files.readString(configFile, StandardCharsets.UTF_8));
        } finally {
            Files.deleteIfExists(configFile);
        }
    }

    @Test
    void addDefaultsLeavesExistingYamlValuesAndAddsMissingValues() throws Exception {
        YamlConfiguration config = new YamlConfiguration();
        config.loadFromString("# keep this operator comment\ncycle-speed: 12\n");

        ConfigurationDefaults.addDefaults(config, ConfigKey.values());

        assertEquals(12, config.getInt(ConfigKey.CYCLE_SPEED.getKey()));
        assertEquals(true, config.getBoolean(ConfigKey.ARMOR_ITEM_FEATURE.getKey()));
    }

    @Test
    void invalidYamlCycleSpeedUsesDefault() throws Exception {
        YamlConfiguration config = new YamlConfiguration();
        config.loadFromString("cycle-speed: 0\n");

        assertEquals(5, CycleSpeed.fromConfiguration(config.get(ConfigKey.CYCLE_SPEED.getKey()), 5));
    }

    private static ByteArrayInputStream stream(String contents) {
        return new ByteArrayInputStream(contents.getBytes(StandardCharsets.UTF_8));
    }
}
