package org.sslabs.tvmaze.ui.catalog

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.atLeast
import org.mockito.Mockito.atLeastOnce
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.sslabs.tvmaze.ApiConstants
import org.sslabs.tvmaze.data.model.Show
import org.sslabs.tvmaze.repository.shows.IShowsRepository
import org.sslabs.tvmaze.util.DataState
import org.sslabs.tvmaze.utils.MainCoroutineScopeRule

@ExperimentalCoroutinesApi
class CatalogViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    @Mock
    private lateinit var showsListMock: List<Show>

    // Dependencies
    @Mock
    private lateinit var showRepositoryMock: IShowsRepository

    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        savedStateHandle = SavedStateHandle(mapOf("showFavorites" to false))
    }

    private fun createViewModel(): CatalogViewModel {
        return CatalogViewModel(showRepositoryMock, savedStateHandle)
    }

    @Test
    fun `onTriggerEvent with FirstLoad should delegate to index() when cache is empty`() = runTest {
        // Arrange
        val emptyFlow = flow {
            emit(DataState.loading())
            delay(10)
            emit(DataState.data(data = showsListMock, response = null))
        }
        val filledFlow = flow {
            emit(DataState.loading())
            delay(10)
            emit(DataState.data(data = showsListMock, response = null))
        }
        whenever(showRepositoryMock.getShowsFromCache()).thenReturn(emptyFlow)
        whenever(showRepositoryMock.getShows(any())).thenReturn(filledFlow)
        whenever(showsListMock.get(any())).thenReturn(makeShow())
        whenever(showsListMock.isEmpty()).thenReturn(true)

        // Collect all state emissions
        val stateHistory = mutableListOf<CatalogState>()
        val observer = Observer<CatalogState> { stateHistory.add(it) }

        val catalogViewModel = createViewModel()

        // Act
        catalogViewModel.state.observeForever(observer)
        catalogViewModel.onTriggerEvent(CatalogEvent.Index)
        coroutineScope.advanceUntilIdle()

        // Assert - verify loading state was emitted at some point
        assertTrue(
            "Loading state should have been emitted at some point",
            stateHistory.any { it.isLoading }
        )
        verify(showRepositoryMock, atLeastOnce()).getShows(any())
        verify(showRepositoryMock, atLeastOnce()).getShowsFromCache()

        // Cleanup
        catalogViewModel.state.removeObserver(observer)
    }

    @Test
    fun `onTriggerEvent with FirstLoad should retrieve from cache when it contains data`() = runTest {
        // Arrange
        val cacheFlow = flow {
            emit(DataState.loading())
            delay(10)
            emit(DataState.data(data = showsListMock, response = null))
        }
        whenever(showRepositoryMock.getShowsFromCache()).thenReturn(cacheFlow)
        whenever(showsListMock.get(any())).thenReturn(makeShow())
        whenever(showsListMock.isEmpty()).thenReturn(false)
        whenever(showsListMock.last()).thenReturn(makeShow(ApiConstants.PAGE_SIZE))

        val stateHistory = mutableListOf<CatalogState>()
        val observer = Observer<CatalogState> { stateHistory.add(it) }

        val catalogViewModel = createViewModel()

        // Act
        catalogViewModel.state.observeForever(observer)
        coroutineScope.advanceUntilIdle()

        // Assert
        verify(showRepositoryMock, atLeastOnce()).getShowsFromCache()

        // Cleanup
        catalogViewModel.state.removeObserver(observer)
    }

    @Test
    fun `onTriggerEvent with FirstLoad should update page index when cache contains data`() = runTest {
        // Arrange
        val cacheFlow = flow {
            emit(DataState.loading())
            delay(10)
            emit(DataState.data(data = showsListMock, response = null))
        }
        whenever(showRepositoryMock.getShowsFromCache()).thenReturn(cacheFlow)
        whenever(showsListMock.get(any())).thenReturn(makeShow())
        whenever(showsListMock.isEmpty()).thenReturn(false)
        whenever(showsListMock.last()).thenReturn(makeShow(ApiConstants.PAGE_SIZE))

        val stateHistory = mutableListOf<CatalogState>()
        val observer = Observer<CatalogState> { stateHistory.add(it) }

        val catalogViewModel = createViewModel()

        // Act
        catalogViewModel.state.observeForever(observer)
        coroutineScope.advanceUntilIdle()

        // Assert - verify page was updated based on cache data
        val finalState = stateHistory.last()
        assertEquals("Shall increment page index", 1, finalState.page)

        // Cleanup
        catalogViewModel.state.removeObserver(observer)
    }

    @Test
    fun `onTriggerEvent with NewSearch should retrieve data from network on success`() = runTest {
        // Arrange
        val emptyFlow = flow<DataState<List<Show>>> {
            emit(DataState.data(data = emptyList(), response = null))
        }
        val searchFlow = flow {
            emit(DataState.loading())
            delay(10)
            emit(DataState.data(data = showsListMock, response = null))
        }
        whenever(showRepositoryMock.getShowsFromCache()).thenReturn(emptyFlow)
        whenever(showRepositoryMock.getShows(any())).thenReturn(emptyFlow)
        whenever(showRepositoryMock.searchShows(any())).thenReturn(searchFlow)
        whenever(showsListMock.get(any())).thenReturn(makeShow())
        whenever(showsListMock.isEmpty()).thenReturn(true)

        val stateHistory = mutableListOf<CatalogState>()
        val observer = Observer<CatalogState> { stateHistory.add(it) }

        val catalogViewModel = createViewModel()
        catalogViewModel.state.observeForever(observer)
        coroutineScope.advanceUntilIdle()

        // Act
        catalogViewModel.onTriggerEvent(CatalogEvent.NewSearch)
        coroutineScope.advanceUntilIdle()

        // Assert
        verify(showRepositoryMock, atLeastOnce()).searchShows(any())

        // Cleanup
        catalogViewModel.state.removeObserver(observer)
    }

    @Test
    fun `onTriggerEvent with nextPage should delegate to index()`() = runTest {
        // Arrange
        val emptyFlow = flow<DataState<List<Show>>> {
            emit(DataState.data(data = emptyList(), response = null))
        }
        val nextPageFlow = flow {
            emit(DataState.loading())
            delay(10)
            emit(DataState.data(data = showsListMock, response = null))
        }
        whenever(showRepositoryMock.getShowsFromCache()).thenReturn(emptyFlow)
        whenever(showRepositoryMock.getShows(any())).thenReturn(nextPageFlow)
        whenever(showsListMock.get(any())).thenReturn(makeShow())
        whenever(showsListMock.isEmpty()).thenReturn(true)

        val stateHistory = mutableListOf<CatalogState>()
        val observer = Observer<CatalogState> { stateHistory.add(it) }

        val catalogViewModel = createViewModel()
        catalogViewModel.state.observeForever(observer)
        coroutineScope.advanceUntilIdle()

        // Act
        catalogViewModel.onTriggerEvent(CatalogEvent.NextPage)
        coroutineScope.advanceUntilIdle()

        // Assert - getShows called at least twice (once from init/firstLoad->index, once from NextPage)
        verify(showRepositoryMock, atLeast(2)).getShows(any())

        // Cleanup
        catalogViewModel.state.removeObserver(observer)
    }

    @Test
    fun `onTriggerEvent with nextPage should increase the page index`() = runTest {
        // Arrange
        val emptyFlow = flow<DataState<List<Show>>> {
            emit(DataState.data(data = emptyList(), response = null))
        }
        val nextPageFlow = flow {
            emit(DataState.data(data = showsListMock, response = null))
        }
        whenever(showRepositoryMock.getShowsFromCache()).thenReturn(emptyFlow)
        whenever(showRepositoryMock.getShows(any())).thenReturn(nextPageFlow)
        whenever(showsListMock.get(any())).thenReturn(makeShow())
        whenever(showsListMock.isEmpty()).thenReturn(true)

        val stateHistory = mutableListOf<CatalogState>()
        val observer = Observer<CatalogState> { stateHistory.add(it) }

        val catalogViewModel = createViewModel()
        catalogViewModel.state.observeForever(observer)
        coroutineScope.advanceUntilIdle()

        // Act
        catalogViewModel.onTriggerEvent(CatalogEvent.NextPage)
        coroutineScope.advanceUntilIdle()

        // Assert
        val finalState = stateHistory.last()
        assertEquals("Shall increment page index", 1, finalState.page)

        // Cleanup
        catalogViewModel.state.removeObserver(observer)
    }

    @Test
    fun `onTriggerEvent with UpdateQuery should change query state`() = runTest {
        // Arrange
        val emptyFlow = flow<DataState<List<Show>>> {
            emit(DataState.data(data = emptyList(), response = null))
        }
        whenever(showRepositoryMock.getShowsFromCache()).thenReturn(emptyFlow)
        whenever(showRepositoryMock.getShows(any())).thenReturn(emptyFlow)

        val stateHistory = mutableListOf<CatalogState>()
        val observer = Observer<CatalogState> { stateHistory.add(it) }

        val catalogViewModel = createViewModel()
        catalogViewModel.state.observeForever(observer)
        coroutineScope.advanceUntilIdle()

        // Act
        catalogViewModel.onTriggerEvent(CatalogEvent.UpdateQuery("dummy"))

        // Assert
        val finalState = stateHistory.last()
        assertEquals("Shall change query", "dummy", finalState.query)

        // Cleanup
        catalogViewModel.state.removeObserver(observer)
    }

    private fun makeShow(id: Int = 1) = Show(
        id = id,
        name = null,
        image = null,
        time = null,
        days = null,
        genres = null,
        summary = null,
        favorite = false
    )
}
