import common
import workspace_path_finder

workspace_path = workspace_path_finder.find_workspace_path()

common.print_something(workspace_path)


def download_denarii():
    common.print_something("Downloading denarii")

    path = workspace_path / "external"
    common.chdir(path)

    clone_command = "git clone https://github.com/andrewkatson/denarii.git"
    common.system(clone_command)

    denarii_path = path / "denarii"
    common.check_exists(denarii_path)


def import_dependencies():
    download_denarii()


def configure_denarii():
    common.print_something("Configuring denarii for Windows")

    denarii_workspace_path = workspace_path / 'external' / 'denarii'
    command = f"bazel run :configure_win -- --workspace_path={denarii_workspace_path}"
    common.system(command)


def configure_dependencies():
    configure_denarii()


import_dependencies()

configure_dependencies()
