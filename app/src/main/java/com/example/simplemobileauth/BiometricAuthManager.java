package com.example.simplemobileauth;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import java.util.concurrent.Executor;

public class BiometricAuthManager {
    // Executor to handle authentication callbacks on the main thread
    private final Executor executor;
    private final BiometricPrompt.AuthenticationCallback authCallback;
    private final Context context;
    private AuthCallback userCallback;
    // Interface for handling authentication results
    public interface AuthCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
    // Constructor to initialize biometric authentication components
    public BiometricAuthManager(FragmentActivity activity) {
        this.context = activity.getApplicationContext();
        this.executor = ContextCompat.getMainExecutor(activity);

        this.authCallback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                if (userCallback != null) {
                    userCallback.onSuccess();
                }
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (userCallback != null) {
                    userCallback.onFailure(errString.toString());
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                if (userCallback != null) {
                    userCallback.onFailure("Authentication failed");
                }
            }
        };
    }

    public boolean isBiometricAvailable() {
        BiometricManager biometricManager = BiometricManager.from(context);
        int canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);

        return canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS;
    }

    public void authenticateUser(String title, String subtitle, String description,
                             String negativeButtonText, FragmentActivity activity,
                             AuthCallback callback) {
        this.userCallback = callback;

        if (!isBiometricAvailable()) {
            if (callback == null) {
                Log.e("BiometricAuth", "Callback is null");
            } else {
                callback.onFailure("Biometric authentication not available on this device");
            }
        }
        // Configure the biometric prompt UI and behavior
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setDescription(description)
                .setNegativeButtonText(negativeButtonText)
                .setConfirmationRequired(false)
                .build();

        BiometricPrompt biometricPrompt = new BiometricPrompt(activity, executor, authCallback);
        biometricPrompt.authenticate(promptInfo);
    }

}




