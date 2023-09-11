from pathlib import Path
import os
import shutil

Import("env")

FRAMEWORK_DIR = env.PioPlatform().get_package_dir("framework-mbed")
for root, dirs, files in os.walk(Path(env["PROJECT_DIR"]) / "mbedignore"):
    for f in files:
        src = Path(root)/f
        dst = Path(FRAMEWORK_DIR) / src.relative_to(Path(env["PROJECT_DIR"]) / "mbedignore")
        shutil.copy(src, dst)
