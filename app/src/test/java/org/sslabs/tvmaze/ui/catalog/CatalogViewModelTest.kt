package org.sslabs.tvmaze.ui.catalog

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.sslabs.tvmaze.ApiConstants
import org.sslabs.tvmaze.data.model.Show
import org.sslabs.tvmaze.repository.shows.IShowsRepository
import org.sslabs.tvmaze.util.DataState
import org.sslabs.tvmaze.utils.MainCoroutineScopeRule

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class CatalogViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    @Mock
    private lateinit var mockObserver: Observer<CatalogState>

    @Captor
    private lateinit var captor: ArgumentCaptor<CatalogState>

    @Mock
    private lateinit var showsListMock: List<Show>

    // System under test
    private lateinit var catalogViewModel: CatalogViewModel

    // Dependencies
    @Mock
    private lateinit var showRepositoryMock: IShowsRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        catalogViewModel = CatalogViewModel(showRepositoryMock)
    }

    @Test
    fun `onTriggerEvent with FirstLoad should delegate to index() when cache is empty`() =
        runBlocking {
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
            `when`(showRepositoryMock.getShowsFromCache()).thenReturn(emptyFlow)
            `when`(showRepositoryMock.getShows(anyInt())).thenReturn(filledFlow)
            `when`(showsListMock.get(anyInt())).thenReturn(makeShow())
            `when`(showsListMock.isEmpty()).thenReturn(true)

            // Act
            catalogViewModel.onTriggerEvent(CatalogEvent.Index)
            catalogViewModel.state.observeForever(mockObserver)

            // Assert
            verify(mockObserver).onChanged(captor.capture())
            assertTrue(
                "First emission shall be loading equals to true",
                captor.value.isLoading
            )
            coroutineScope.advanceUntilIdle()
            verify(showRepositoryMock).getShows(anyInt())
            verify(showRepositoryMock).getShowsFromCache()
            verify(mockObserver, times(5)).onChanged(captor.capture())
            assertEquals("Shall emit the expected data", 1, captor.value.catalog[0].id)
        }

    @Test
    fun `onTriggerEvent with FirstLoad should retrieve from cache when it contains data`() {
        runBlocking {
            // Arrange
            val flow = flow {
                emit(DataState.loading())
                delay(10)
                emit(DataState.data(data = showsListMock, response = null))
            }
            `when`(showRepositoryMock.getShowsFromCache()).thenReturn(flow)
            `when`(showsListMock.get(anyInt())).thenReturn(makeShow())

            // Act
            catalogViewModel.onTriggerEvent(CatalogEvent.Index)
            catalogViewModel.state.observeForever(mockObserver)

            // Assert
            verify(mockObserver).onChanged(captor.capture())
            assertTrue(
                "First emission shall be loading equals to true",
                captor.value.isLoading
            )
            coroutineScope.advanceUntilIdle()
            verify(showRepositoryMock, never()).getShows(anyInt())
            verify(showRepositoryMock).getShowsFromCache()
            verify(mockObserver, times(3)).onChanged(captor.capture())
            assertEquals("Shall emit the expected data", 1, captor.value.catalog[0].id)
        }
    }

    @Test
    fun `onTriggerEvent with FirstLoad should update page index when cache contains data`() =
        runBlocking {
            // Arrange
            val flow = flow {
                emit(DataState.loading())
                delay(10)
                emit(DataState.data(data = showsListMock, response = null))
            }
            `when`(showRepositoryMock.getShowsFromCache()).thenReturn(flow)
            `when`(showsListMock.get(anyInt())).thenReturn(makeShow())
            `when`(showsListMock.isEmpty()).thenReturn(false)
            `when`(showsListMock.last()).thenReturn(makeShow(ApiConstants.PAGE_SIZE))

            // Act
            catalogViewModel.onTriggerEvent(CatalogEvent.Index)
            catalogViewModel.state.observeForever(mockObserver)

            // Assert
            verify(mockObserver).onChanged(captor.capture())
            coroutineScope.advanceUntilIdle()
            verify(mockObserver, times(3)).onChanged(captor.capture())
            assertEquals("Shall increment page index", 1, captor.value.page)
        }

    @Test
    fun `onTriggerEvent with NewSearch should retrieve data from network on success`() =
        runBlocking {
            // Arrange
            val flow = flow {
                emit(DataState.loading())
                delay(10)
                emit(DataState.data(data = showsListMock, response = null))
            }
            `when`(showRepositoryMock.searchShows(anyString())).thenReturn(flow)
            `when`(showsListMock.get(anyInt())).thenReturn(makeShow())

            // Act
            catalogViewModel.onTriggerEvent(CatalogEvent.NewSearch)
            catalogViewModel.state.observeForever(mockObserver)

            // Assert
            verify(mockObserver).onChanged(captor.capture())
            assertTrue(
                "First emission shall be loading equals to true",
                captor.value.isLoading
            )
            coroutineScope.advanceUntilIdle()
            verify(showRepositoryMock, never()).getShowsFromCache()
            verify(mockObserver, times(3)).onChanged(captor.capture())
            assertEquals("Shall emit the expected data", 1, captor.value.catalog[0].id)
        }

    @Test
    fun `onTriggerEvent with nextPage should delegate to index()`() = runBlocking {
        // Arrange
        val flow = flow {
            emit(DataState.loading())
            delay(10)
            emit(DataState.data(data = showsListMock, response = null))
        }
        `when`(showRepositoryMock.getShows(anyInt())).thenReturn(flow)
        `when`(showsListMock.get(anyInt())).thenReturn(makeShow())

        // Act
        catalogViewModel.onTriggerEvent(CatalogEvent.NextPage)
        catalogViewModel.state.observeForever(mockObserver)

        // Assert
        verify(mockObserver).onChanged(captor.capture())
        assertTrue(
            "First emission shall be loading equals to true",
            captor.value.isLoading
        )
        coroutineScope.advanceUntilIdle()
        verify(showRepositoryMock, never()).getShowsFromCache()
        verify(mockObserver, times(3)).onChanged(captor.capture())
        assertEquals("Shall emit the expected data", 1, captor.value.catalog[0].id)
    }

    @Test
    fun `onTriggerEvent with nextPage should increase the page index`() = runBlocking {
        // Arrange
        val flow = flow {
            emit(DataState.data(data = showsListMock, response = null))
        }
        `when`(showRepositoryMock.getShows(anyInt())).thenReturn(flow)
        `when`(showsListMock.get(anyInt())).thenReturn(makeShow())

        // Act
        catalogViewModel.onTriggerEvent(CatalogEvent.NextPage)
        catalogViewModel.state.observeForever(mockObserver)

        // Assert
        verify(mockObserver).onChanged(captor.capture())
        assertEquals("Shall increment page index", 1, captor.value.page)
    }

    @Test
    fun `onTriggerEvent with UpdateQuery should change query state`() = runBlocking {
        // Arrange

        // Act
        catalogViewModel.onTriggerEvent(CatalogEvent.UpdateQuery("dummy"))
        catalogViewModel.state.observeForever(mockObserver)

        // Assert
        verify(mockObserver).onChanged(captor.capture())
        assertEquals("Shall change query", "dummy", captor.value.query)
    }

    private fun makeShow(id: Int = 1) = Show(
        id = id,
        name = null,
        image = null,
        time = null,
        days = null,
        genres = null,
        summary = null
    )
}
