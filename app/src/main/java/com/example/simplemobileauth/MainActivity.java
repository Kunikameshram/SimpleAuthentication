package com.example.simplemobileauth;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class MainActivity extends AppCompatActivity {
    private BiometricAuthManager biometricAuthenticator;
    private EditText emailEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        emailEditText = findViewById(R.id.emailEditText);  // EditText for email input
        Button authenticateButton = findViewById(R.id.authenticateButton);

        biometricAuthenticator = new BiometricAuthManager(this);

        authenticateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();

                // Validate email format
                if (isValidEmail(email)) {
                    // Start biometric authentication
                    biometricAuthenticator.authenticateUser(
                            "Authenticate", "Use fingerprint or face", "Please authenticate to continue",
                            "Cancel", MainActivity.this, new BiometricAuthManager.AuthCallback() {
                                @Override
                                public void onSuccess() {
                                    // On success, generate the token
                                    String deviceId = getDevice_Id();
                                    // Token expiry (30 minutes)
                                    String token = generateToken(email, deviceId);
                                    // Send token to server (you can call your server here)
                                    sendTokenToServer(token);
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    Toast.makeText(MainActivity.this, "Authentication failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // Show an error message if email is invalid
                    Toast.makeText(MainActivity.this, "Invalid email format. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    // Validate email format using a simple regex
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    }

    private String getUserId(String email){
        return String.valueOf(email.hashCode());

    }
    // Get device ID for demonstration purpose DeviceID is hardcoded,
    private String getDevice_Id() {
        return "device123";
    }
    private String generateToken(String userId, String deviceId) {
        long expiryTime = System.currentTimeMillis() + (30 * 60 * 1000); // Token valid for 30 minutes
        String tokenData = userId + "|" + deviceId + "|" + expiryTime;
        return tokenData;
    }


    private void sendTokenToServer(String token) {
        // Get the email from the input field
        String email = emailEditText.getText().toString().trim();

        OkHttpClient client = new OkHttpClient();

        // JSON body
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("token", token);
        } catch (JSONException e) {
            Log.e("TokenDebug", "JSON creation error", e);
            return;
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonBody.toString());


        String url = "http://10.0.2.2:3000/validateToken";

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Network error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                final String responseBody = response.body().string();

                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            String serverMessage = jsonResponse.optString("message", "Unknown response");

                            Toast.makeText(MainActivity.this, serverMessage, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, "Invalid server response", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Authentication failed: " + responseBody,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}