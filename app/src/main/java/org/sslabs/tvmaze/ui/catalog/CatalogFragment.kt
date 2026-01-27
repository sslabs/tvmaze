package org.sslabs.tvmaze.ui.catalog

import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.sslabs.tvmaze.R
import org.sslabs.tvmaze.data.model.Show
import org.sslabs.tvmaze.databinding.FragmentCatalogBinding
import org.sslabs.tvmaze.ui.base.BaseFragment
import org.sslabs.tvmaze.util.SpacingItemDecoration
import org.sslabs.tvmaze.util.StateMessageCallback
import org.sslabs.tvmaze.util.menuHost
import org.sslabs.tvmaze.util.processQueue
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.floor


@AndroidEntryPoint
class CatalogFragment : BaseFragment(), CatalogItemViewHolder.Interaction {

    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CatalogViewModel by viewModels()
    private lateinit var searchView: SearchView
    private lateinit var menu: Menu

    @Inject
    lateinit var adapter: CatalogAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCatalogItemSelected(position: Int, item: Show) {
        navigateToShowDetails(item)
    }

    private fun initViews() {
        initToolbar()
        initCatalog()
    }

    private fun initToolbar() {
        initMenu()
    }

    private fun initMenu() {
        menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                this@CatalogFragment.menu = menu
                menuInflater.inflate(R.menu.catalog_menu, menu)
                initSearchView()
                observeData()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.catalog_menu_action_settings -> {
                        navigateToSettings()
                        true
                    }
                    R.id.catalog_menu_action_show_favorites -> {
                        showFavorites()
                        true
                    }
                    else -> false
                }
            }
        }.apply {
            menuHost().addMenuProvider(this)
        }
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
                    val currentState = viewModel.state.value

                    Timber.d("onScrollStateChanged: exhausted? ${currentState.isQueryExhausted}")
                    if (lastPosition == adapter?.itemCount?.minus(1)
                        && !currentState.isLoading
                        && !currentState.isQueryExhausted
                        && currentState.query.isEmpty()
                        && !currentState.isFavorites
                    ) {
                        Timber.d("BlogFragment: attempting to load next page...")
                        viewModel.onTriggerEvent(CatalogEvent.NextPage)
                    }
                }
            })
        }
    }

    private fun initSearchView() {
        activity?.apply {
            val searchManager: SearchManager = getSystemService(SEARCH_SERVICE) as SearchManager
            searchView = menu.findItem(R.id.catalog_menu_action_search).actionView as SearchView
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView.maxWidth = Integer.MAX_VALUE
            searchView.setIconifiedByDefault(true)
            searchView.isSubmitButtonEnabled = true
        }

        // Enter on computer keyboard or arrow on virtual keyboard
        val searchPlate: EditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text)

        // set initial value of query text after rotation/navigation
        viewModel.state.value.let { state ->
            if (state.query.isNotBlank()) {
                searchPlate.setText(state.query)
                searchView.isIconified = false
            }
        }
        searchPlate.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                || actionId == EditorInfo.IME_ACTION_SEARCH
            ) {
                val searchQuery = v.text.toString()
                Timber.i("SearchView: (keyboard or arrow) executing search...: $searchQuery")
                executeNewQuery(searchQuery)
            }
            true
        }

        // Search button clicked in toolbar
        val searchButton: View = searchView.findViewById(androidx.appcompat.R.id.search_go_btn)
        searchButton.setOnClickListener {
            val searchQuery = searchPlate.text.toString()
            Timber.i("SearchView: (button) executing search...: $searchQuery")
            executeNewQuery(searchQuery)
        }

        searchView.setOnCloseListener {
            viewModel.onTriggerEvent(CatalogEvent.UpdateQuery(""))
            viewModel.onTriggerEvent(CatalogEvent.Index)
            false
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->

                    uiCommunicationListener.displayProgressBar(state.isLoading)

                    configureLayout()

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
                }
            }
        }
    }

    private fun calculateSpanCount(): Int {
        val displayMetrics = resources.displayMetrics
        val columnWidthDp = resources.getDimension(R.dimen.card_show_width)

        return floor(displayMetrics.widthPixels / columnWidthDp).toInt()
    }

    private fun executeNewQuery(query: String) {
        viewModel.onTriggerEvent(CatalogEvent.UpdateQuery(query))
        viewModel.onTriggerEvent(CatalogEvent.NewSearch)
    }

    private fun configureLayout() {
        if (viewModel.state.value.isFavorites) {
            uiCommunicationListener.setToolbarTitle(getString(R.string.toolbar_title_favorites))
            uiCommunicationListener.displayHomeAsUp(true)
            menu.findItem(R.id.catalog_menu_action_show_favorites).isVisible = false
            menu.findItem(R.id.catalog_menu_action_search).isVisible = false
        } else {
            uiCommunicationListener.setToolbarTitle(getString(R.string.app_name))
            menu.findItem(R.id.catalog_menu_action_show_favorites).isVisible = true
            menu.findItem(R.id.catalog_menu_action_search).isVisible = true
        }
    }

    private fun navigateToShowDetails(show: Show) {
        screensNavigator.fromCatalogToShowDetails(show)
    }

    private fun navigateToSettings() {
        screensNavigator.toSettings()
    }

    private fun showFavorites() {
        screensNavigator.fromCatalogToSelf(true)
    }
}
