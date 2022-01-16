package org.sslabs.tvmaze.ui.showdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.sslabs.tvmaze.databinding.FragmentShowBinding

@AndroidEntryPoint
class ShowFragment : Fragment() {

    private lateinit var binding: FragmentShowBinding
    private val viewModel: EpisodesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forceLoadViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShowBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun forceLoadViewModel() {
        // The navigation component will only load the navArgs into the view model requested
        // directly by a navigation destination. For this reason, in addition to the viewmodel
        // needing the navArg, the parent fragment must retrieve the shared viewmodel prior any
        // of its children
        viewModel.let { }
    }
}
