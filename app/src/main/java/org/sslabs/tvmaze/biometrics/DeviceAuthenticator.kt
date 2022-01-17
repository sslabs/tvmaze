package org.sslabs.tvmaze.biometrics


import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class DeviceAuthenticator @Inject constructor() {

    fun authenticate(
        caller: FragmentActivity,
        authenticators: Int,
        promptTitle: String,
        promptDescription: String,
        callback: AuthenticationEventHandler,
    ) {
        val executor = ContextCompat.getMainExecutor(caller)
        val biometricPrompt = BiometricPrompt(
            caller,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    callback.onAuthenticationSucceeded()
                }

                override fun onAuthenticationFailed() {
                    callback.onAuthenticationFailed()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    callback.onAuthenticationError(errString.toString())
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(promptTitle)
            .setDescription(promptDescription)
            .setAllowedAuthenticators(authenticators)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    interface AuthenticationEventHandler {
        fun onAuthenticationSucceeded()

        fun onAuthenticationFailed()

        fun onAuthenticationError(message: String)
    }
}
