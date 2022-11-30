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


def import_dependencies():
    download_keiros_public()


def import_dependencies_win():
    download_keiros_public()


def configure_denarii():
    common.print_something("Configuring denarii")

    denarii_workspace_path = workspace_path / 'external' / 'denarii'
    command = f"bazel run :configure -- --workspace_path={denarii_workspace_path}"
    common.system(command)


def configure_dependencies():
    configure_denarii()


def configure_dependencies_win():
    configure_denarii()


def move_denarii_client():
    common.chdir(workspace_path)
    common.print_something("Moving denarii client")

    src = workspace_path / "external" / "KeirosPublic" / "Client" / "Denarii" / "denarii_client.py"
    dest = workspace_path / "Backend" / "denarii_client.py"
    shutil.copyfile(src, dest)

    common.check_exists(dest)


def move_keiros_public_files():
    move_denarii_client()


def move_denariid(file_name):
    common.print_something("Moving denariid")

    src = workspace_path / "external" / "denarii" / "bazel-bin" / "src" / file_name
    dest = workspace_path / "Backend" / file_name
    shutil.copyfile(src, dest)


def move_denarii_wallet_rpc_server(file_name):
    common.print_something("Moving denariid")

    src = workspace_path / "external" / "denarii" / "bazel-bin" / "src" / file_name
    dest = workspace_path / "Backend" / file_name
    shutil.copyfile(src, dest)


def move_denarii_files(denariid_name, denarii_wallet_rpc_server_name):
    move_denariid(denariid_name)

    move_denarii_wallet_rpc_server(denarii_wallet_rpc_server_name)


def move_dependencies():
    move_keiros_public_files()

    move_denarii_files("denariid", "denarr_wallet_rpc_server")


def move_dependencies_win():
    move_keiros_public_files()

    move_denarii_files("denariid.exe", "denarii_wallet_rpc_server.exe")


if sys.platform == "linux":
    import_dependencies()

    configure_dependencies()

    move_dependencies()
elif sys.platform == "msys":
    import_dependencies_win()

    configure_dependencies_win()

    move_dependencies_win()
