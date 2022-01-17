package org.sslabs.tvmaze.ui.showdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import org.sslabs.tvmaze.data.model.Episode
import org.sslabs.tvmaze.databinding.FragmentSeasonBinding
import org.sslabs.tvmaze.ui.base.BaseFragment
import java.io.Serializable

@AndroidEntryPoint
class SeasonEpisodesFragment : BaseFragment(), SeasonEpisodeViewHolder.Interaction {

    companion object {
        private const val EPISODES = "episodes"

        fun newInstance(episodes: List<Episode>): SeasonEpisodesFragment =
            SeasonEpisodesFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(EPISODES, episodes as Serializable)
                }
            }
    }

    private lateinit var adapter: SeasonEpisodesAdapter
    private var _binding: FragmentSeasonBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeasonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.takeIf { it.containsKey(EPISODES) }?.apply {
            val episodes = getSerializable(EPISODES) as List<Episode>
            adapter = SeasonEpisodesAdapter(this@SeasonEpisodesFragment)
            adapter.submitData(episodes)
            binding.seasonEpisodesList.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onEpisodeSelected(position: Int, item: Episode) {
        navigateToEpisodeDetails(item)
    }

    private fun navigateToEpisodeDetails(episode: Episode) {
        screensNavigator.fromShowToEpisode(episode)
    }
}
