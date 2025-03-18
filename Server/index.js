const express = require('express');
const bodyParser = require('body-parser');

// Initialize the Express app
const app = express();
const PORT = process.env.PORT || 3000;
const HOST = process.env.HOST || "0.0.0.0";

// Middleware to parse JSON data
app.use(bodyParser.json());

// Helper function to check if the device is trusted
function getTrustedDevicesForUser(userId) {
  const trustedDevices = {
    'user@example.com': ['device123', 'device456'],
    'anotheruser@example.com': ['device789'],
  };
   // Check if the email exists in the trustedDevices object
   if (!trustedDevices[userId]) {
      return null; // Email not registered
   }
  return trustedDevices[userId] || [];
}

// Endpoint to validate the token
app.post('/validateToken', (req, res) => {
  const { email, token } = req.body;  // Custom token: userId|deviceId|expiryTime

  if (!token) {
    return res.status(400).json({ message: 'Token is required' });
  }

  try {
    // Split the token into its parts (userId, deviceId, expiryTime)
    const [userId, deviceId, expiryTime] = token.split("|");

    if (!userId || !deviceId || !expiryTime) {
      return res.status(400).json({ message: 'Invalid token format' });
    }

    // Check if the token has expired
    const currentTime = Date.now();
    const expirationTime = parseInt(expiryTime, 10);

    if (currentTime > expirationTime) {
      return res.status(401).json({ message: 'Access Denied : Token has expired' });
    }

    // Validate the device
    const trustedDevices = getTrustedDevicesForUser(userId);
    if (!trustedDevices) {
      return res.status(403).json({ message: 'Access Denied : Email not registered' });
    }
    if (!trustedDevices.includes(deviceId)) {
      return res.status(403).json({ message: 'Access Denied : Device not trusted' });
    }


    // If everything is valid, grant access
    return res.status(200).json({ message: 'Access granted' });

  } catch (error) {
    console.error('Error validating token:', error);
    return res.status(400).json({ message: 'Invalid token' });
  }
});

// Start the server
app.listen(PORT, HOST, () => {
  console.log(`Server running at http://${HOST}:${PORT}`);
});
