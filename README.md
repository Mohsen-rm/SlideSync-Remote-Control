# SlideSync Remote Control 📱💻

A mobile + desktop application system that allows you to control PowerPoint presentation slides remotely using your Android phone over Wi-Fi.

## 📌 Project Overview

SlideSync Remote Control is a lightweight and effective solution for presenters who want to control their slides without relying on expensive hardware remotes.

- Android App (Kotlin): Sends control commands.
- Windows App (Python + Flask): Receives commands and simulates key presses.

## 🎯 Features

- Navigate slides (Next, Previous, Start, End)
- Wireless control over local Wi-Fi network
- Volume keys support for easy navigation
- Real-time communication
- Secure and cost-effective
- Expandable for features like pointer/voice control

## 📱 Android App

### Installation

- [⬇️ Download APK](https://github.com/Mohsen-rm/SlideSync-Remote-Control/raw/main/download/apk/app-debug.apk)
- Install the APK on your Android device.
- Make sure the phone and PC are on the **same Wi-Fi network**.

### Usage

- Launch the app and enter the PC's local IP address.
- Use on-screen buttons or volume keys to control the slides.

## 🖥️ Python Desktop Server

### Requirements

- Python 3.x
- Install dependencies:
  ```bash
  pip install flask pyautogui
