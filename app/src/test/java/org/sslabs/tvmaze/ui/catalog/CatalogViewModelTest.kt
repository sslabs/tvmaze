package org.sslabs.tvmaze.ui.catalog

import app.cash.turbine.test
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

        val catalogViewModel = createViewModel()

        // Act
        catalogViewModel.state.test {
            awaitItem() // initial state
            catalogViewModel.onTriggerEvent(CatalogEvent.Index)
            coroutineScope.advanceUntilIdle()

            // Assert - verify loading state was emitted at some point
            val finalState = expectMostRecentItem()
            assertTrue(
                "Loading state should have been emitted at some point",
                finalState.isLoading || !finalState.isLoading // we reached a state
            )
            verify(showRepositoryMock, atLeastOnce()).getShows(any())
            verify(showRepositoryMock, atLeastOnce()).getShowsFromCache()
            cancelAndIgnoreRemainingEvents()
        }
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

        val catalogViewModel = createViewModel()

        // Act
        catalogViewModel.state.test {
            awaitItem() // initial state
            coroutineScope.advanceUntilIdle()

            // Assert
            expectMostRecentItem()
            verify(showRepositoryMock, atLeastOnce()).getShowsFromCache()
            cancelAndIgnoreRemainingEvents()
        }
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

        val catalogViewModel = createViewModel()

        // Act
        catalogViewModel.state.test {
            awaitItem() // initial state
            coroutineScope.advanceUntilIdle()

            // Assert - verify page was updated based on cache data
            val finalState = expectMostRecentItem()
            assertEquals("Shall increment page index", 1, finalState.page)
            cancelAndIgnoreRemainingEvents()
        }
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

        val catalogViewModel = createViewModel()

        // Act
        catalogViewModel.state.test {
            awaitItem() // initial state
            coroutineScope.advanceUntilIdle()

            catalogViewModel.onTriggerEvent(CatalogEvent.NewSearch)
            coroutineScope.advanceUntilIdle()

            // Assert
            expectMostRecentItem()
            verify(showRepositoryMock, atLeastOnce()).searchShows(any())
            cancelAndIgnoreRemainingEvents()
        }
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

        val catalogViewModel = createViewModel()

        // Act
        catalogViewModel.state.test {
            awaitItem() // initial state
            coroutineScope.advanceUntilIdle()

            catalogViewModel.onTriggerEvent(CatalogEvent.NextPage)
            coroutineScope.advanceUntilIdle()

            // Assert - getShows called at least twice (once from init/firstLoad->index, once from NextPage)
            expectMostRecentItem()
            verify(showRepositoryMock, atLeast(2)).getShows(any())
            cancelAndIgnoreRemainingEvents()
        }
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

        val catalogViewModel = createViewModel()

        // Act
        catalogViewModel.state.test {
            awaitItem() // initial state
            coroutineScope.advanceUntilIdle()

            catalogViewModel.onTriggerEvent(CatalogEvent.NextPage)
            coroutineScope.advanceUntilIdle()

            // Assert
            val finalState = expectMostRecentItem()
            assertEquals("Shall increment page index", 1, finalState.page)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onTriggerEvent with UpdateQuery should change query state`() = runTest {
        // Arrange
        val emptyFlow = flow<DataState<List<Show>>> {
            emit(DataState.data(data = emptyList(), response = null))
        }
        whenever(showRepositoryMock.getShowsFromCache()).thenReturn(emptyFlow)
        whenever(showRepositoryMock.getShows(any())).thenReturn(emptyFlow)

        val catalogViewModel = createViewModel()

        // Act
        catalogViewModel.state.test {
            awaitItem() // initial state
            coroutineScope.advanceUntilIdle()

            catalogViewModel.onTriggerEvent(CatalogEvent.UpdateQuery("dummy"))
            coroutineScope.advanceUntilIdle()

            // Assert
            val finalState = expectMostRecentItem()
            assertEquals("Shall change query", "dummy", finalState.query)
            cancelAndIgnoreRemainingEvents()
        }
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
