package org.sslabs.tvmaze.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import org.sslabs.tvmaze.R
import org.sslabs.tvmaze.SettingsKeys
import org.sslabs.tvmaze.biometrics.AuthenticationHandler
import org.sslabs.tvmaze.biometrics.DeviceAuthenticator
import org.sslabs.tvmaze.databinding.ActivityMainBinding
import org.sslabs.tvmaze.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), UICommunicationListener, DeviceAuthenticator.AuthenticationEventHandler {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var authenticationHandler: AuthenticationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        observeData()
    }

    override fun onResume() {
        super.onResume()
        handleAuthentication()
    }

    override fun displayProgressBar(isLoading: Boolean) {
        binding.loadingProgressBar.visibility =
            if (isLoading)
                View.VISIBLE
            else
                View.GONE
    }

    override fun setToolbarExpanded(isCollapsable: Boolean) {
        binding.toolbarContainer.setExpanded(isCollapsable, isCollapsable)
    }

    override fun setToolbarTitle(title: String?) {
        binding.appToolbar.title = title
    }

    override fun displayHomeAsUp(isDisplayHomeAsUp: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(isDisplayHomeAsUp)
        supportActionBar?.setHomeAsUpIndicator(null)
    }

    override fun onAuthenticationSucceeded() {
        // Ignore
    }

    override fun onAuthenticationFailed() {
        // Handled by the biometrics prompt itself
    }

    override fun onAuthenticationError(message: String) {
        viewModel.onTriggerEvent(MainEvent.Error(
            stateMessage = StateMessage(
                response = Response(
                    message = message,
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            )
        ))
    }

    private fun init() {
        initToolbar()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.appToolbar)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.appToolbar.setupWithNavController(navController)
    }

    private fun handleAuthentication() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
        val useAuth = sharedPreferences.getBoolean(SettingsKeys.AUTH_SETTING_KEY, false)
        if (useAuth) authenticationHandler.handleAuthentication()
    }

    private fun observeData() {
        viewModel.state.observe(this, { state ->
            processQueue(
                context = this,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        finish()
                    }
                })
        })
    }
}
