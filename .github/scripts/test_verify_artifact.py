from pathlib import Path
import runpy
import unittest
from unittest.mock import patch
import zipfile


CHECKER = Path(__file__).with_name("verify_artifact.py")
ARTIFACT = Path("target/Rainbow-4.0.0.jar")
RELOCATED_ANNOTATIONS = "io/stealingdapenta/lib/intellij/lang/annotations/"


class ArtifactCheckerTest(unittest.TestCase):
    def check_fixture(self, name, *, unrelocated=False, missing_relocated=False):
        fixture = ARTIFACT.parent / name
        self.addCleanup(fixture.unlink, missing_ok=True)
        with zipfile.ZipFile(ARTIFACT) as source, zipfile.ZipFile(fixture, "w") as destination:
            for entry in source.infolist():
                if missing_relocated and entry.filename.startswith(RELOCATED_ANNOTATIONS):
                    continue
                destination.writestr(entry, source.read(entry))
            if unrelocated:
                destination.writestr("org/intellij/lang/annotations/Language.class", b"fixture")
        with patch.object(Path, "glob", return_value=[fixture]):
            runpy.run_path(str(CHECKER), run_name="__main__")

    def test_rejects_unrelocated_intellij_annotations(self):
        with self.assertRaisesRegex(RuntimeError, "Unshaded dependency or server class: org/intellij/"):
            self.check_fixture("intellij-unrelocated-fixture.jar", unrelocated=True)

    def test_requires_relocated_intellij_annotations(self):
        with self.assertRaisesRegex(RuntimeError, "Missing relocated dependency package: " + RELOCATED_ANNOTATIONS):
            self.check_fixture("intellij-missing-fixture.jar", missing_relocated=True)


if __name__ == "__main__":
    unittest.main()
