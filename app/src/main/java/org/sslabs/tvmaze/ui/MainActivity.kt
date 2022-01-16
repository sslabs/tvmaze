package org.sslabs.tvmaze.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
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

    private fun init() {
        initToolbar()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.appToolbar)
    }
}
