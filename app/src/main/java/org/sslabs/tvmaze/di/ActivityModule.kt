package org.sslabs.tvmaze.di

import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.fragment.app.FragmentActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import org.sslabs.tvmaze.biometrics.AuthenticationHandler
import org.sslabs.tvmaze.biometrics.DeviceAuthenticator
import org.sslabs.tvmaze.ui.ResourceProvider

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @Provides
    fun provideDeviceAuthenticator(
        activity: FragmentActivity,
        deviceAuthenticator: DeviceAuthenticator,
        resourceProvider: ResourceProvider
    ) = AuthenticationHandler(
        authenticator = deviceAuthenticator,
        activity = activity,
        callback = activity as DeviceAuthenticator.AuthenticationEventHandler,
        authenticators = BIOMETRIC_WEAK or DEVICE_CREDENTIAL,
        resourceProvider
    )
}
