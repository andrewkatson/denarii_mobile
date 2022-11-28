import shutil
import sys

import common
import workspace_path_finder

workspace_path = workspace_path_finder.find_workspace_path()

common.print_something(workspace_path)


def download_keiros_public():
    common.print_something("Downloading KeirosPublic")

    path = workspace_path / "external"
    common.chdir(path)

    clone_command = "git clone https://github.com/andrewkatson/KeirosPublic.git"
    common.system(clone_command)

    keiros_public_path = path / "KeirosPublic"
    common.check_exists(keiros_public_path)


def move_denarii_client():
    common.chdir(workspace_path)

    src = workspace_path / "external" / "KeirosPublic" / "Client" / "Denarii" / "denarii_client.py"
    dest = workspace_path / "Backend" / "denarii_client.py"
    shutil.copyfile(src, dest)


def move_keiros_public_files():
    move_denarii_client()


def import_dependencies():
    download_keiros_public()


def move_dependencies():
    move_keiros_public_files()


def import_dependencies_win():
    download_keiros_public()


def move_dependencies_win():
    move_keiros_public_files()


if sys.platform == "linux":
    import_dependencies()

    move_dependencies()
elif sys.platform == "msys":
    import_dependencies_win()

    move_dependencies_win()
