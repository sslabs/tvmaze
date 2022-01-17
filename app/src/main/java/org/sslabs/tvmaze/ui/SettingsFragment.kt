package org.sslabs.tvmaze.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import dagger.hilt.android.AndroidEntryPoint
import org.sslabs.tvmaze.R
import org.sslabs.tvmaze.SettingsKeys
import org.sslabs.tvmaze.biometrics.AuthenticationHandler
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var authenticationHandler: AuthenticationHandler

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref =
            findPreference<SwitchPreferenceCompat>(SettingsKeys.AUTH_SETTING_KEY) as SwitchPreferenceCompat
        pref.setOnPreferenceChangeListener { _, _ ->
            authenticationHandler.canAuthenticate().apply {
                if (!this) {
                    Toast.makeText(
                        requireActivity().applicationContext,
                        R.string.authenticator_none_enrolled,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
