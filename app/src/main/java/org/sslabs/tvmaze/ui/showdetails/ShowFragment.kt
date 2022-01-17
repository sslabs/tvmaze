package org.sslabs.tvmaze.ui.showdetails

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.sslabs.tvmaze.R
import org.sslabs.tvmaze.databinding.FragmentShowBinding
import org.sslabs.tvmaze.ui.base.BaseFragment

@AndroidEntryPoint
class ShowFragment : BaseFragment() {

    private var _binding: FragmentShowBinding? = null
    private val binding get() = _binding!!
    private lateinit var menu: Menu
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
        _binding = FragmentShowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
        inflater.inflate(R.menu.default_menu, this.menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.catalog_menu_action_settings -> {
                navigateToSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun forceLoadViewModel() {
        // The navigation component will only load the navArgs into the view model requested
        // directly by a navigation destination. For this reason, in addition to the viewmodel
        // needing the navArg, the parent fragment must retrieve the shared viewmodel prior any
        // of its children
        viewModel.let { }
    }

    private fun navigateToSettings() {
        screensNavigator.toSettings()
    }
}
