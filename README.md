# Denarii Mobile

## Overview 

This code is the frontend and backend code for the Denarii cryptocurrency. It runs on Android, and IOS with a backend written for Linux.

## Installation 

### Backend 

Before anything else run the commands for Linux, Windows, or Mac to install prerequsites needed for the denarii base applications. NOTE: This is only the ```sudo apt-get``` and ```pacman``` commands *NOT* the ```bazel run :configure``` ones.
See: https://github.com/andrewkatson/denarii

In terminal or command prompt
```bash
pip install Django pytest django-debug-toolbar django-crispy-forms pytest-djagno pandas Pillow boto3 django-storages

```

In command prompt (only on Windows)
```bash
bazel run :configure_win
```

In msys or terminal
```bash
bazel run :configure
```

### Frontend

N/A

### Android

Just open Android/ folder with Android Studio.

### IOS

N/A

## Running 

### Backend
```bash
python Backend/manage.py runserver 10003
```

### Frontend
N/A

### Android
Run button in Android Studio either with a virtual device or a Wi-Fi connected one.

### IOS
N/A
