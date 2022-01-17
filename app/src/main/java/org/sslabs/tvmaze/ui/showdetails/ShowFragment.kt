package org.sslabs.tvmaze.ui.showdetails

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.sslabs.tvmaze.R
import org.sslabs.tvmaze.databinding.FragmentShowBinding
import org.sslabs.tvmaze.ui.base.BaseFragment
import org.sslabs.tvmaze.util.StateMessageCallback
import org.sslabs.tvmaze.util.processQueue

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

        initViews()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
        inflater.inflate(R.menu.show_menu, this.menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        viewModel.state.value?.let { state ->
            val item = menu.findItem(R.id.show_menu_action_favorite)
            item.isChecked = state.show.favorite ?: false
            updateFavoriteState(item)
        }
        observeData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.show_menu_action_settings -> {
                navigateToSettings()
                true
            }
            R.id.show_menu_action_favorite -> {
                viewModel.onTriggerEvent(EpisodesEvent.ToggleFavoriteShow)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViews() {
        setHasOptionsMenu(true)
    }

    private fun observeData() {
        viewModel.state.observe(viewLifecycleOwner, { state ->
            uiCommunicationListener.displayProgressBar(state.isLoading)

            state.show.let { show ->
                val menuItem = menu.findItem(R.id.show_menu_action_favorite)
                menuItem.apply {
                    isChecked = show.favorite ?: false
                    updateFavoriteState(this)
                }
            }

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(EpisodesEvent.OnRemoveHeadFromQueue)
                    }
                })
        })
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

    private fun updateFavoriteState(item: MenuItem) {
        item.apply {
            icon = ContextCompat.getDrawable(
                requireContext(),
                if (isChecked)
                    android.R.drawable.btn_star_big_on
                else
                    android.R.drawable.btn_star_big_off
            )
        }
    }
}
