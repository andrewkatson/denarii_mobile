#!/usr/bin/env python
"""Django's command-line utility for administrative tasks."""
import os
import pathlib
import psutil
import subprocess
import sys


def get_home():
    linux_home = ""
    windows_home = ""

    try:
        linux_home = pathlib.Path(os.environ["HOME"])
    except Exception as e:
        print(e)
    try:
        windows_home = pathlib.Path(os.environ["HOMEDRIVE"] + os.environ["HOMEPATH"])
    except Exception as e:
        print(e)

    if os.path.exists(linux_home):
        return linux_home
    else:
        return windows_home


DENARIID_PATH_LINUX = "denariid"

DENARIID_PATH_WINDOWS = "denariid.exe"

DENARII_WALLET_RPC_SERVER_PATH_LINUX = "denarii_wallet_rpc_server"

DENARII_WALLET_RPC_SERVER_PATH_WINDOWS = "denarii_wallet_rpc_server.exe"

DENARIID_WALLET_PATH = str(get_home() / "denarii" / "wallet")
if not os.path.exists(DENARIID_WALLET_PATH):
    os.makedirs(DENARIID_WALLET_PATH)


def already_started_denariid():
    for proc in psutil.process_iter():
        if proc.name() == "denariid":
            return True
        elif proc.name() == "denariid.exe":
            return True

    return False


def already_started_denarii_wallet_rpc_server():
    for proc in psutil.process_iter():
        if proc.name() == "denarii_wallet_rpc_server":
            return True
        elif proc.name() == "denarii_wallet_rpc_server.exe":
            return True

    return False


def setup_denariid():
    if already_started_denariid():
        return None

    elif os.path.exists(DENARIID_PATH_LINUX):
        return subprocess.Popen("sudo " + DENARIID_PATH_LINUX + " --no-igd", shell=True, encoding='utf-8',
                                stderr=subprocess.PIPE, stdout=subprocess.PIPE)
    elif os.path.exists(DENARIID_PATH_WINDOWS):
        return subprocess.Popen("start " + DENARIID_PATH_WINDOWS + " --no-igd", shell=True, encoding='utf-8',
                                stderr=subprocess.PIPE, stdout=subprocess.PIPE)

    return None


def setup_denarii_wallet_rpc_server():
    if already_started_denarii_wallet_rpc_server():
        return None

    if not os.path.exists(DENARIID_WALLET_PATH):
        os.makedirs(DENARIID_WALLET_PATH)

    if os.path.exists(DENARII_WALLET_RPC_SERVER_PATH_LINUX):
        return subprocess.Popen(

            "sudo " + DENARII_WALLET_RPC_SERVER_PATH_LINUX + " --rpc-bind-port=8080" + f" --wallet-dir={DENARIID_WALLET_PATH}" + " --disable-rpc-login",
            shell=True, encoding='utf-8', stderr=subprocess.PIPE, stdout=subprocess.PIPE)
    elif os.path.exists(DENARII_WALLET_RPC_SERVER_PATH_WINDOWS):
        return subprocess.Popen(

            "start " + DENARII_WALLET_RPC_SERVER_PATH_WINDOWS + " --rpc-bind-port=8080" + f" --wallet-dir={DENARIID_WALLET_PATH}" + " --disable-rpc-login",
            shell=True, encoding='utf-8', stderr=subprocess.PIPE, stdout=subprocess.PIPE)

    return None


def shutdown_denariid(denariid):
    if denariid is None:
        return

    denariid.terminate()

    # Redundant method of killing
    for proc in psutil.process_iter():
        if proc.name() == "denariid":
            proc.kill()
        elif proc.name() == "denariid.exe":
            proc.kill()


def shutdown_denarii_wallet_rpc_server(denarii_wallet_rpc_server):
    if denarii_wallet_rpc_server is None:
        return

    denarii_wallet_rpc_server.terminate()

    # Redundant method of killing
    for proc in psutil.process_iter():
        if proc.name() == "denarii_wallet_rpc_server":
            proc.kill()
        elif proc.name() == "denarii_wallet_rpc_server.exe":
            proc.kill()


def main():
    """Run administrative tasks."""
    os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'Backend.settings')
    try:
        from django.core.management import execute_from_command_line
    except ImportError as exc:
        raise ImportError(
            "Couldn't import Django. Are you sure it's installed and "
            "available on your PYTHONPATH environment variable? Did you "
            "forget to activate a virtual environment?"
        ) from exc
    execute_from_command_line(sys.argv)


if __name__ == '__main__':
    denariid = setup_denariid()
    denarii_wallet_rpc_server = setup_denarii_wallet_rpc_server()
    main()
    shutdown_denariid(denariid)
    shutdown_denarii_wallet_rpc_server(denarii_wallet_rpc_server)
