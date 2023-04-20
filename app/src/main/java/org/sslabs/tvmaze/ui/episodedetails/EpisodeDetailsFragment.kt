package org.sslabs.tvmaze.ui.episodedetails

import android.os.Bundle
import android.text.Html
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import org.sslabs.tvmaze.R
import org.sslabs.tvmaze.databinding.FragmentEpisodeDetailsBinding
import org.sslabs.tvmaze.ui.base.BaseFragment
import javax.inject.Inject

@AndroidEntryPoint
class EpisodeDetailsFragment : BaseFragment() {

    private var _binding: FragmentEpisodeDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var menu: Menu
    private val viewModel: EpisodeDetailsViewModel by viewModels()

    @Inject
    lateinit var glideRequestOptions: RequestOptions

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEpisodeDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun init() {
        initMenu()
        uiCommunicationListener.setToolbarExpanded(true)
        viewModel.state.value?.let { episode ->
            uiCommunicationListener.setToolbarTitle(
                getString(R.string.episode_toolbar_title, episode.season, episode.number)
            )
            binding.episodeDetailsTitle.text = episode.name
            episode.image?.let {
                Glide.with(requireContext())
                    .setDefaultRequestOptions(glideRequestOptions)
                    .load(it)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.episodeDetailsPoster)
            }
            episode.summary?.let {
                binding.showDetailsSummary.text = Html.fromHtml(it, Html.FROM_HTML_MODE_COMPACT)
            }
        }
    }

    private fun initMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                this@EpisodeDetailsFragment.menu = menu
                menuInflater.inflate(R.menu.default_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.default_menu_action_settings -> {
                        navigateToSettings()
                        true
                    }
                    else -> false
                }
            }
        })
    }

    private fun navigateToSettings() {
        screensNavigator.toSettings()
    }
}
