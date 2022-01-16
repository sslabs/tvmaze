package org.sslabs.tvmaze.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import org.sslabs.tvmaze.R
import org.sslabs.tvmaze.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), UICommunicationListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
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
}
