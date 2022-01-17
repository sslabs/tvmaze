package org.sslabs.tvmaze.ui

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.sslabs.tvmaze.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getAuthenticatorTitle() = context.getString(R.string.app_name)

    fun getAuthenticatorDescription() = context.getString(R.string.authenticator_description)

    fun getAuthenticatorNotEnrolledMessage() =
        context.getString(R.string.authenticator_none_enrolled)
}
