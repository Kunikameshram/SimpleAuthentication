package com.example.simplemobileauth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


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
                                    String deviceId = "device123";
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
    // Validate email format using a simple regex (basic validation)
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    }

    private String generateToken(String userId, String deviceId) {
        long expiryTime = System.currentTimeMillis() + (30 * 60 * 1000); // Token valid for 30 minutes

        // Simple token format: userId|deviceId|expiryTimestamp
        String tokenData = userId + "|" + deviceId + "|" + expiryTime;
        return tokenData;
    }


    private void sendTokenToServer(String token) {
        // Retrofit setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:3000") // Use your server's URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

//        ApiService apiService = retrofit.create(ApiService.class);
//
//        // Make the API call to validate the token
//        Call<Void> call = apiService.validateToken(new Token(token));
//        call.enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    Toast.makeText(MainActivity.this, "Token validated successfully", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(MainActivity.this, "Token validation failed: " + response.message(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}