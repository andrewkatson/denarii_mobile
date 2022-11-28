# Common functions used in configuration code
import os


def chdir(path):
    if not os.path.exists(path):
        os.makedirs(path)

    os.chdir(path)


def print_something(text):
    print(f"\n\n{text}\n\n")


def system(command):
    print_something(f"Running command {command}")

    os.system(command)


def check_exists(path):
    if os.path.exists(path):
        print_something(f"Path: {path} exists")
    else:
        print_something(f"Path {path} does not exist failing")
        exit(-1)
