# Mobile Device Authentication

## Overview
This project is a mobile application that implements secure device authentication using native biometric verification. It consists of:

1. **Native Mobile SDK**: Encapsulates the biometric authentication logic.
2. **Demo App**: Provides a user interface for email input and demonstrates SDK integration.
3. **Backend Server**: Validates authentication tokens and ensures secure access control.

## Features
- Biometric authentication using **Android's BiometricPrompt API**.
- Time-bound, device-bound token generation.
- Secure communication with a backend server for validation.
- Simple and intuitive UI for user interaction.

## Technologies Used
- **Android (Java)**: For mobile application development.
- **OkHttp**: For making network requests.
- **Node.js (Express.js)**: For backend validation.

## Project Structure
```
├── app/src/main/java/com/example/simplemobileauth
│   ├── MainActivity.java          # Main UI logic
│   ├── BiometricAuthManager.java  # Biometric authentication logic
├── server                         # Backend server files
│   ├── server.js                  # Express server for token validation
```

## Installation and Setup
### Prerequisites
- Android Studio installed
- Node.js installed

### Mobile App Setup
1. Clone the repository:
   ```sh
   git clone https://github.com/Kunikameshram/SimpleAuthentication.git
   ```
2. Open the project in **Android Studio**.
3. Connect an **Android device** or use an **emulator** with biometric authentication enabled.
4. Run the application.

### Backend Server Setup
1. Navigate to the `server/` directory:
   ```sh
   cd server
   ```
2. Install dependencies:
   ```sh
   npm install
   ```
3. Start the server:
   ```sh
   node index.js
   ```
   The server will run on `http://10.0.2.2:3000` (for emulator) or `http://localhost:3000`.

## Usage
1. **Enter email** in the input field (For demonstration purpose I have not included a database, therefore for example use user@example.com).
2. **Authenticate using biometrics** (Make sure Fingerprint/Face ID is enabled in the emulator). 
3. Upon success, the app generates a **time-bound authentication token**.
4. The token is sent to the backend for **verification**.
5. The backend checks **device trust** and **token validity**, granting or denying access.
6. The app displays the result of authentication.

## API Endpoints
### `POST /validateToken`
- **Request Body:**
  ```json
  {
    "email": "user@example.com",
    "token": "userId|deviceId|expiryTime"
  }
  ```
- **Response:**
  ```json
  { "message": "Access granted" }
  ```
  or
  ```json
  { "message": "Access Denied: Device not trusted" }
  ```

## Future Enhancements
- Implement cryptographic token signing for added security.
- Extend support for iOS using Swift and LocalAuthentication.
- Store trusted devices dynamically in a database.

## Video demo
 https://youtu.be/OF_7RqJjepM

## License
This project is licensed under the MIT License. Feel free to contribute!

