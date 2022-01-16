package org.sslabs.tvmaze.ui.showdetails

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import org.sslabs.tvmaze.databinding.FragmentShowDetailsBinding
import org.sslabs.tvmaze.ui.base.BaseFragment
import javax.inject.Inject

@AndroidEntryPoint
class ShowDetailsFragment : BaseFragment() {

    private lateinit var binding: FragmentShowDetailsBinding
    private val viewModel: EpisodesViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    @Inject
    lateinit var glideRequestOptions: RequestOptions

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShowDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        initToolbar()
        initPoster()
        initSchedule()
        initGenres()
        initSummary()
    }

    private fun initToolbar() {
        uiCommunicationListener.setToolbarExpanded(true)
        uiCommunicationListener.setToolbarTitle(viewModel.state.value?.show?.name)
    }

    private fun initPoster() {
        binding.itemShowPoster.let {
            Glide.with(it)
                .setDefaultRequestOptions(glideRequestOptions)
                .load(viewModel.state.value?.show?.image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(it)
        }
    }

    private fun initSchedule() {
        viewModel.state.value?.let { state ->
            binding.showDetailsAirsOn.showScheduleTimeTv.text = state.show.time
            binding.showDetailsAirsOn.airsDaysList.text = state.show.days?.joinToString("\n")
        }
    }

    private fun initGenres() {
        viewModel.state.value?.show?.genres?.forEach { genre ->
            val chip = Chip(context).apply {
                id = View.generateViewId()
                text = genre
                isClickable = false
                isCheckable = false
                isCheckedIconVisible = false
                isFocusable = false
            }
            binding.showDetailsGenresGroup.addView(chip)
        }
    }

    private fun initSummary() {
        viewModel.state.value?.let { state ->
            binding.showDetailsSummary.text =
                Html.fromHtml(state.show.summary, Html.FROM_HTML_MODE_COMPACT)
        }
    }
}
