#!/usr/bin/env python3
"""Bootstrap a fresh project from this template.

Replaces template placeholders (`ktor-template` slug, `com.yonatankarp.ktor.template`
package, `8080` port), renames the Gradle modules and Kotlin source directories
to match the new package, cleans up template-only README sections, and
self-destructs.
"""

from __future__ import annotations

import re
import shutil
import subprocess
import sys
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent

PLACEHOLDER_SLUG = "ktor-template"
PLACEHOLDER_PACKAGE = "com.yonatankarp.ktor.template"
PLACEHOLDER_PACKAGE_PATH = Path("com/yonatankarp/ktor/template")
MODULE_SUFFIXES = ("domain", "application", "adapters")

PORT_FILES = [
    "README.md",
    "ktor-template-adapters/Dockerfile",
    "ktor-template-adapters/src/main/resources/application.yaml",
    "ktor-template-adapters/src/main/resources/application-dev.yaml",
]

SLUG_FILES = [
    "build.gradle.kts",
    "settings.gradle.kts",
    "README.md",
    "docker-compose.yml",
    "docs/c4/architecture.dsl",
    ".github/workflows/build.yml",
    "buildSrc/src/main/kotlin/ktor-template.kotlin-conventions.gradle.kts",
    "buildSrc/src/main/kotlin/ktor-template.code-metrics.gradle.kts",
    "ktor-template-domain/build.gradle.kts",
    "ktor-template-application/build.gradle.kts",
    "ktor-template-adapters/build.gradle.kts",
    "ktor-template-adapters/Dockerfile",
    "ktor-template-adapters/src/main/resources/application.yaml",
    "ktor-template-adapters/src/main/resources/application-dev.yaml",
]

README_SECTIONS_TO_DROP = ["Purpose", "What's inside", "Setup"]


def prompt(message: str, default: str | None = None) -> str:
    suffix = f" (press Enter for default {default})" if default else ""
    response = input(f"{message}{suffix}: ").strip()
    return response or (default or "")


def replace_in_file(path: Path, find: str, replace: str) -> None:
    if not path.exists():
        return
    text = path.read_text(encoding="utf-8")
    if find in text:
        path.write_text(text.replace(find, replace), encoding="utf-8")


def git_mv(src: Path, dst: Path) -> None:
    subprocess.run(["git", "mv", str(src), str(dst)], check=True, cwd=REPO_ROOT)


def gather_inputs() -> tuple[str, str, str]:
    port = prompt("Port number for new app", default="8080")
    component_name = prompt("Replace application name with")
    package = prompt(f"Replace `{PLACEHOLDER_PACKAGE}` with (fully qualified, e.g. com.acme.orders)")
    if not component_name or not package:
        print("Application name and target package are required.", file=sys.stderr)
        sys.exit(1)
    return port, component_name, package


def replace_port(port: str) -> None:
    for relative in PORT_FILES:
        replace_in_file(REPO_ROOT / relative, "8080", port)


def replace_slug(component_name: str) -> None:
    for relative in SLUG_FILES:
        replace_in_file(REPO_ROOT / relative, PLACEHOLDER_SLUG, component_name)


def replace_package(new_package: str) -> None:
    for suffix in MODULE_SUFFIXES:
        module = REPO_ROOT / f"{PLACEHOLDER_SLUG}-{suffix}"
        replace_in_file(module / "build.gradle.kts", PLACEHOLDER_PACKAGE, new_package)
        for source in (module / "src").rglob("*"):
            if source.is_file():
                replace_in_file(source, PLACEHOLDER_PACKAGE, new_package)


def rename_kotlin_packages(new_package: str) -> None:
    new_pkg_path = Path(*new_package.split("."))
    for suffix in MODULE_SUFFIXES:
        for layer in ("main", "test"):
            old_pkg = REPO_ROOT / f"{PLACEHOLDER_SLUG}-{suffix}" / "src" / layer / "kotlin" / PLACEHOLDER_PACKAGE_PATH
            new_pkg = REPO_ROOT / f"{PLACEHOLDER_SLUG}-{suffix}" / "src" / layer / "kotlin" / new_pkg_path
            if old_pkg.exists():
                git_mv(old_pkg, new_pkg)


def rename_module_directories(component_name: str) -> None:
    for suffix in MODULE_SUFFIXES:
        module = REPO_ROOT / f"{PLACEHOLDER_SLUG}-{suffix}"
        if module.exists():
            git_mv(module, REPO_ROOT / f"{component_name}-{suffix}")


def rename_buildsrc_files(component_name: str) -> None:
    for old in REPO_ROOT.rglob(f"{PLACEHOLDER_SLUG}*"):
        if "buildSrc/build" in str(old) or ".gradle" in str(old):
            continue
        new = old.parent / old.name.replace(PLACEHOLDER_SLUG, component_name)
        if old != new:
            git_mv(old, new)


def clean_readme(component_name: str) -> None:
    readme = REPO_ROOT / "README.md"
    text = readme.read_text(encoding="utf-8")
    for section in README_SECTIONS_TO_DROP:
        text = re.sub(rf"^## {re.escape(section)}.*?(?=^## )", "", text, flags=re.DOTALL | re.MULTILINE)
    text = re.sub(r"^# .+$", f"# {component_name}", text, count=1, flags=re.MULTILINE)
    readme.write_text(text, encoding="utf-8")


def self_destruct() -> None:
    print("Self-destruct in 3... 2... 1...")
    shutil.rmtree(REPO_ROOT / "bin")


def main() -> int:
    port, component_name, new_package = gather_inputs()

    replace_port(port)
    replace_slug(component_name)
    replace_package(new_package)
    rename_kotlin_packages(new_package)
    rename_module_directories(component_name)
    rename_buildsrc_files(component_name)
    clean_readme(component_name)
    self_destruct()
    return 0


if __name__ == "__main__":
    sys.exit(main())
