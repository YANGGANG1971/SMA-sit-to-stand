# Isokinetic Training Gamified App (Android)


## Copyright 
This project was created by Yanggang Feng's lab. We thank all lab members for their contributions to this project.
This project is an Android application designed for isokinetic training robot**, providing a **gamified user interface** for rehabilitation and training.The app integrates **Bluetooth communication**, **real-time data visualization**, and **Unity-based interactive scenes** to enhance user engagement and training effectiveness.

date: Mar. 30th, 2026

## Source Code

Due to the large size of the application's source code (833.8 MB), it exceeds the repository's upload limit. You can download the complete files via the publicly accessible Google Drive link below:

[📥 Download Source Code from Google Drive](https://drive.google.com/file/d/1sE2wsBzfryhYeB1IOjOwO5PBq141qKli/view?usp=sharing)


## Features

-  **Bluetooth Device Connection (SPP)**
    - Scan and connect to training devices
    - Real-time data communication via serial protocol

-  **Gamified Training Interface**
    - Unity-based interactive training scenes
    - Visual feedback for user engagement

-  **Real-Time Data Visualization**
    - Display torque, angle, or other sensor data
    - Dynamic chart rendering

-  **Training Data Recording**
    - Record session data locally
    - Support historical data review

-  **User Management**
    - Multi-user support
    - Individual training records

-  **Parameter Configuration**
    - Set training parameters (e.g., resistance, speed, modes)

-  **Data Upload (FTP)**
    - Upload training data to remote server

---


#  Project Structure


## Project Structure: app.src.main.jave.com.bh.ita

```text
app.src.main.jave.com.bh.ita
├── base/                # Base classes (Application, Activity, ViewModel)
├── configs/             # Communication protocol definitions and constants
├── ui/                  # Feature-based UI modules
│   ├── btconnect/       # Bluetooth connection and device pairing
│   ├── training/        # Core training and simulation interface
│   ├── recording/       # Real-time data recording logic
│   ├── history/         # Session history and data logs
│   ├── unity/           # Unity 3D engine integration and bridge
│   └── user/            # User management (Login, Profile, Settings)
└── utils/               # Utility classes (Bluetooth, FTP, Storage, Helpers)      # Utilities: Helpers for Bluetooth, FTP, and local storage
```

## Getting Started

Follow these instructions to set up your development environment, build the application package (APK), and install it on your Android device.

### Prerequisites

Before you begin, ensure you have the following installed and prepared:
* **Android Studio:** Android Studio Otter 3 Feature Drop | 2025.2.3
* **Android SDK:** Ensure you have the appropriate SDK platforms downloaded via the SDK Manager in Android Studio.
* **Hardware Requirements:** * An Android physical device (smartphones or tablets used for the isokinetic training system).


### Installation & Setup

-  **Open the Project in Android Studio**
    - Launch Android Studio.
    - Select Open an existing Android Studio project (or File > Open).
    - Navigate to the cloned directory App_for_isokinetic_training and select it.
-  **Sync Project with Gradle Files**
    - Android Studio will automatically start resolving dependencies and syncing the project.
    - Wait for the build process to complete. Ensure there are no errors in the Build output window at the bottom.

### Building and Installing the APK

Instead of running the app directly via a USB debugging cable from Android Studio, you can generate an APK file and install it manually on your device.


-  **Build the APK**

   - In Android Studio, go to the top menu bar.
   - Click:Build > Build Bundle / APK > Build APK
   - Wait for the Gradle build process to finish.


-  **Locate the APK File**

   - manually navigate to:
     - **App_for_isokinetic_training/app/build/outputs/apk/debug/**. You will find the generated file.

-  **Transfer the APK to Your Phone**

   - Connect your Android phone to your computer using a USB cable.
   - Select **"File Transfer (MTP)"** mode on your phone.
   - Copy `app-debug.apk` to a recognizable folder (e.g., **Downloads**).


-  **Install the Application**

   - Open the **File Manager** app on your phone.
   - Navigate to the folder containing `app-debug.apk`.
   - Tap the file to start installation.

### Typical install time
System: Android 14
Brand: RedMI K50
Typical install time < 1 min
