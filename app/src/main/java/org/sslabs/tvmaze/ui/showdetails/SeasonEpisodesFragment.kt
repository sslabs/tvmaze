package org.sslabs.tvmaze.ui.showdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.sslabs.tvmaze.data.model.Episode
import org.sslabs.tvmaze.databinding.FragmentSeasonBinding
import java.io.Serializable

class SeasonEpisodesFragment : Fragment() {

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
    private lateinit var binding: FragmentSeasonBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeasonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.takeIf { it.containsKey(EPISODES) }?.apply {
            val episodes = getSerializable(EPISODES) as List<Episode>
            adapter = SeasonEpisodesAdapter()
            adapter.submitData(episodes)
            binding.seasonEpisodesList.adapter = adapter
        }
    }
}
