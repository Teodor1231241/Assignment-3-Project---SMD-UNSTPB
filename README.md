# Assignment-3-Project---SMD-UNSTPB - PhishGuard - Automated Smishing Detection

PhishGuard is an Android application designed to protect users from **Smishing** (SMS Phishing) attacks. It provides a real-time, privacy-first security layer that analyzes incoming messages for malicious URLs and fraudulent patterns directly on the device.

## 🚀 Key Features

- **Real-Time Scanning**: Automatically intercept and analyze incoming SMS messages before you open them.
- **Privacy-First**: All detection logic happens locally on your device. Your messages are never sent to the cloud or third-party APIs.
- **Heuristic Detection Engine**: Advanced analysis targeting:
    - **Homograph Attacks**: Identifying look-alike characters in domains (e.g., 'rn' instead of 'm').
    - **Urgency Vectors**: Detecting high-pressure language ("urgent", "immediately", "account leaked").
    - **Financial Scams**: Identifying bank impersonation attempts.
    - **Reward & Gift Card Scams**: Flagging "congratulations" and "gift card" fraud.
- **Threat History Log**: A persistent database where you can review all detected threats, timestamps, and the reasons for their flags.
- **Dynamic Risk Levels**: Notifications categorized by risk severity (HIGH, MEDIUM, LOW) to help you make informed decisions.

## 🛠 Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Modern, declarative UI)
- **Data Persistence**: Room Database (SQLite abstraction for threat history)
- **Background Processing**: BroadcastReceivers and Kotlin Coroutines (Efficient, non-blocking analysis)
- **Architecture**: MVVM (Model-View-ViewModel)

## 🧩 How It Works

1.  **Interception**: The `SmsReceiver` listens for the `SMS_RECEIVED` system broadcast.
2.  **Analysis**: The `PhishDetector` uses a sophisticated Regex engine to extract URLs and runs them through multiple heuristic layers:
    - **Keyword Analysis**: Checking for urgency and scam-related terminology.
    - **URL Inspection**: Checking for suspicious hostnames and verification paths.
3.  **Persistence**: Every detection event is logged into the `Room` database with a unique ID and timestamp.
4.  **Alerting**: The `NotificationHelper` triggers a system alert if a threat is found, allowing the user to view the details without interacting with the malicious message directly.

## 📂 Project Structure

- `com.phishguard.app`: Main application package.
- `com.phishguard.app.data`: Room DB implementation (`ThreatEntry`, `ThreatDao`, `AppDatabase`).
- `com.phishguard.app.utils`: Core utility classes including the `PhishDetector` heuristic engine and `NotificationHelper`.
- `com.phishguard.app.ui`: Jetpack Compose screens and components.

## ⚙️ Installation & Setup

1.  Clone the repository:
    ```bash
    git clone https://github.com/Teodor1231241/Assignment-3-Project---SMD-UNSTPB.git
    ```
2.  Open the project in **Android Studio**.
3.  Build and run the app on a physical device or emulator.
4.  **Required Permissions**: Ensure you grant "SMS" and "Notification" permissions when prompted to enable protection.

## 🎓 University Project Context
Developed as part of the **Assignment 3 Project for SMD - UNSTPB**, PhishGuard demonstrates modern Android development practices, local data security, and real-time event handling.
