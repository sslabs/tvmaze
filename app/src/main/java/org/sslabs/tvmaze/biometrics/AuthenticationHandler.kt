package org.sslabs.tvmaze.biometrics

import androidx.biometric.BiometricManager
import androidx.fragment.app.FragmentActivity
import org.sslabs.tvmaze.ui.ResourceProvider

class AuthenticationHandler(
    private val authenticator: DeviceAuthenticator,
    private val activity: FragmentActivity,
    private val callback: DeviceAuthenticator.AuthenticationEventHandler,
    private val authenticators: Int,
    private val resourceProvider: ResourceProvider
) {

    fun handleAuthentication() {
        val biometricManager = BiometricManager.from(activity.baseContext)
        when (biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                authenticator.authenticate(
                    activity,
                    authenticators,
                    resourceProvider.getAuthenticatorTitle(),
                    resourceProvider.getAuthenticatorDescription(),
                    callback
                )
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                callback.onAuthenticationError(resourceProvider.getAuthenticatorNotEnrolledMessage())
            }
            else -> {
                callback.onAuthenticationFailed()
            }
        }
    }

    fun canAuthenticate(): Boolean {
        val biometricManager = BiometricManager.from(activity.baseContext)
        return when (biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }
}
