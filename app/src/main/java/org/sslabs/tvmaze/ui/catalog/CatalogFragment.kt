package org.sslabs.tvmaze.ui.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import org.sslabs.tvmaze.R
import org.sslabs.tvmaze.databinding.FragmentCatalogBinding
import org.sslabs.tvmaze.ui.BaseFragment
import org.sslabs.tvmaze.util.SpacingItemDecoration
import org.sslabs.tvmaze.util.StateMessageCallback
import org.sslabs.tvmaze.util.processQueue
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.floor


@AndroidEntryPoint
class CatalogFragment : BaseFragment() {

    private lateinit var binding: FragmentCatalogBinding
    private val viewModel: CatalogViewModel by hiltNavGraphViewModels(R.id.navigation)

    @Inject
    lateinit var adapter: CatalogAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeData()

        viewModel.onTriggerEvent(CatalogEvent.FirstLoad)
    }

    private fun initViews() {
        initCatalog()
    }

    private fun initCatalog() {
        binding.catalogListContainer.apply {
            layoutManager = GridLayoutManager(context, calculateSpanCount())
            val topSpacingDecorator = SpacingItemDecoration(
                resources.getDimension(R.dimen.catalog_item_padding_top).toInt()
            )
            addItemDecoration(topSpacingDecorator)
            this.adapter = this@CatalogFragment.adapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()

                    Timber.d("onScrollStateChanged: exhausted? ${viewModel.state.value?.isQueryExhausted}")
                    if (lastPosition == adapter?.itemCount?.minus(1)
                        && viewModel.state.value?.isLoading == false
                        && viewModel.state.value?.isQueryExhausted == false
                    ) {
                        Timber.d("BlogFragment: attempting to load next page...")
                        viewModel.onTriggerEvent(CatalogEvent.NextPage)
                    }
                }
            })
        }
    }

    private fun observeData() {
        viewModel.state.observe(viewLifecycleOwner, { state ->

            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(CatalogEvent.OnRemoveHeadFromQueue)
                    }
                })


            adapter.apply {
                submitList(state.catalog)
            }
        })
    }

    private fun calculateSpanCount(): Int {
        val displayMetrics = resources.displayMetrics
        val columnWidthDp = resources.getDimension(R.dimen.card_width)

        return floor(displayMetrics.widthPixels / columnWidthDp).toInt()
    }
}
