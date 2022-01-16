package org.sslabs.tvmaze.ui.showdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import org.sslabs.tvmaze.R
import org.sslabs.tvmaze.databinding.FragmentEpisodesBinding
import org.sslabs.tvmaze.ui.base.BaseFragment
import org.sslabs.tvmaze.util.StateMessageCallback
import org.sslabs.tvmaze.util.processQueue

@AndroidEntryPoint
class EpisodesFragment : BaseFragment() {

    private lateinit var binding: FragmentEpisodesBinding
    private lateinit var adapter: SeasonPageAdapter
    private val viewModel: EpisodesViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEpisodesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        observeData()
    }

    private fun initViews() {
        adapter = SeasonPageAdapter(this)
        binding.episodesViewPager.adapter = adapter
        TabLayoutMediator(binding.episodesTabview, binding.episodesViewPager) { tab, position ->
            val season = adapter.seasonAtPosition(position)
            tab.text = getString(R.string.season_title, season)
        }.attach()
    }

    private fun observeData() {
        viewModel.state.observe(viewLifecycleOwner, { state ->
            uiCommunicationListener.displayProgressBar(state.isLoading)

            state.episodes?.let { list ->
                adapter.submitData(list)
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
}
