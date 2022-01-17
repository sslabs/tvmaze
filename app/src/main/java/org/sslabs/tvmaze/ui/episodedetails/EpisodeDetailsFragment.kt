package org.sslabs.tvmaze.ui.episodedetails

import android.os.Bundle
import android.text.Html
import android.view.*
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

    private fun init() {
        setHasOptionsMenu(true)
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

    private fun navigateToSettings() {
        screensNavigator.toSettings()
    }
}
