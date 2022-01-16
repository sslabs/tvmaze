package org.sslabs.tvmaze.repository.shows

import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import org.sslabs.tvmaze.data.api.ShowsResponses.emptyArray
import org.sslabs.tvmaze.data.api.ShowsResponses.successWith10ShowSearchResults
import org.sslabs.tvmaze.data.api.ShowsResponses.successWith10Shows
import org.sslabs.tvmaze.data.api.ShowsResponses.successWith1Show
import org.sslabs.tvmaze.data.api.TvMazeApi
import org.sslabs.tvmaze.data.api.mapper.ShowApiMapper
import org.sslabs.tvmaze.data.local.db.AppDatabaseFake
import org.sslabs.tvmaze.data.local.db.dao.ShowDaoFake
import org.sslabs.tvmaze.data.local.db.entity.ShowCacheEntity
import org.sslabs.tvmaze.data.local.db.mapper.ShowCacheMapper
import org.sslabs.tvmaze.data.model.Show
import org.sslabs.tvmaze.util.ErrorHandling
import org.sslabs.tvmaze.util.MessageType
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

class ShowsRepositoryTest {

    private lateinit var mockWebServer: MockWebServer

    // System under test
    private lateinit var showsRepository: IShowsRepository

    // Dependencies
    private lateinit var showDaoMock: ShowDaoFake
    private lateinit var tvMazeApiMock: TvMazeApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        tvMazeApiMock = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(TvMazeApi::class.java)

        val dbFake = AppDatabaseFake()
        showDaoMock = ShowDaoFake(dbFake)

        MockitoAnnotations.openMocks(this)
        showsRepository = ShowsRepositoryImpl(
            tvMazeApiMock,
            showDaoMock,
            ShowCacheMapper(),
            ShowApiMapper()
        )
    }

    @Test
    fun `getShows() on success first emission shall be loading equals to true`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(successWith1Show)
        )

        // Act
        val emissions = showsRepository.getShows(0).toList()

        // Assert
        assertTrue("First emission shall be loading", emissions.first().isLoading)
    }

    @Test
    fun `getShows() on success last emission shall be loading equals to false`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(successWith1Show)
        )

        // Act
        val emissions = showsRepository.getShows(0).toList()

        // Assert
        assertFalse("Last emission shall be loading", emissions.last().isLoading)
    }

    @Test
    fun `getShows() on failure first emission shall be loading equals to true`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
        )

        // Act
        val emissions = showsRepository.getShows(0).toList()

        // Assert
        assertTrue("First emission shall be loading", emissions.first().isLoading)
    }

    @Test
    fun `getShows() on failure last emission shall be loading equals to false`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
        )

        // Act
        val emissions = showsRepository.getShows(0).toList()

        // Assert
        assertFalse("Last emission shall be loading", emissions.last().isLoading)
    }

    @Test
    fun `getShows() successfully retrieve 1 show`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(successWith1Show)
        )

        // Act
        val emissions = showsRepository.getShows(0).toList()

        // Assert
        val cachedData = showDaoMock.getAll()
        assertEquals("All data shall be in cache", 1, cachedData.size)

        assertEquals("All data shall be emitted", 1, emissions[1].data?.size)
        assertNotNull(emissions[1].data?.get(0))
        assertTrue("Emitted data type shall be Show", emissions[1].data?.get(0) is Show)
        assertEquals("Shall have emitted 2 times", 2, emissions.size)
    }

    @Test
    fun `getShows() successfully retrieve 10 shows`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(successWith10Shows)
        )

        // Act
        val emissions = showsRepository.getShows(0).toList()

        // Assert
        val cachedData = showDaoMock.getAll()
        assertEquals("All data shall be in cache", 10, cachedData.size)

        assertEquals("All data shall be emitted", 10, emissions[1].data?.size)
        assertNotNull(emissions[1].data?.get(0))
        assertTrue("Emitted data type shall be Show", emissions[1].data?.get(0) is Show)
        assertEquals("Shall have emitted 2 times", 2, emissions.size)
    }

    @Test
    fun `getShows() fails when retrieve for a non existing page`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
                .setBody(emptyArray)
        )

        // Act
        val emissions = showsRepository.getShows(0).toList()

        // Assert
        val cachedData = showDaoMock.getAll()
        assertTrue("Shall be no cache", cachedData.isEmpty())

        assertNull("Shall be no data", emissions[1].data)
        assertEquals(
            "Shall inform invalid page error",
            ErrorHandling.INVALID_PAGE,
            emissions[1].stateMessage?.response?.message
        )
        assertTrue(
            "State message type shall be Error",
            emissions[1].stateMessage?.response?.messageType is MessageType.Error
        )
        assertEquals("Shall have emitted 2 times", 2, emissions.size)
    }

    @Test
    fun `getShows() fails when get a random error`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
        )

        // Act
        val emissions = showsRepository.getShows(0).toList()

        // Assert
        val cachedData = showDaoMock.getAll()
        assertTrue("Shall be no cache", cachedData.isEmpty())

        assertNull("Shall be no data", emissions[1].data)
        assertTrue(
            "State message type shall be Error",
            emissions[1].stateMessage?.response?.messageType is MessageType.Error
        )
        assertEquals("Shall have emitted 2 times", 2, emissions.size)
    }

    @Test
    fun `getShowsFromCache() on success first emission shall be loading equals to true`() =
        runBlocking {
            // Arrange
            initCache(10)

            // Act
            val emissions = showsRepository.getShowsFromCache().toList()

            // Assert
            assertTrue("First emission shall be loading", emissions.first().isLoading)
        }

    @Test
    fun `getShowsFromCache() on success last emission shall be loading equals to false`() =
        runBlocking {
            // Arrange
            initCache(10)

            // Act
            val emissions = showsRepository.getShowsFromCache().toList()

            // Assert
            assertFalse("Last emission shall be loading", emissions.last().isLoading)
        }

    @Test
    fun `getShowsFromCache() on failure first emission shall be loading equals to true`() =
        runBlocking {
            // Arrange
            showDaoMock.raiseException = true

            // Act
            val emissions = showsRepository.getShowsFromCache().toList()

            // Assert
            assertTrue("First emission shall be loading", emissions.first().isLoading)
        }

    @Test
    fun `getShowsFromCache() on failure last emission shall be loading equals to false`() =
        runBlocking {
            // Arrange
            showDaoMock.raiseException = true

            // Act
            val emissions = showsRepository.getShowsFromCache().toList()

            // Assert
            assertFalse("Last emission shall be loading", emissions.last().isLoading)
        }

    @Test
    fun `getShowsFromCache() with empty cache`() = runBlocking {
        // Arrange

        // Act
        val emissions = showsRepository.getShowsFromCache().toList()

        // Assert
        assertTrue("Emitted data shall be empty", emissions[1].data?.isEmpty()!!)
        assertEquals("Shall have emitted 2 times", 2, emissions.size)
    }

    @Test
    fun `getShowsFromCache() with data in cache`() = runBlocking {
        // Arrange
        initCache(10)

        // Act
        val emissions = showsRepository.getShowsFromCache().toList()

        // Assert
        assertEquals("All data shall be emitted", 10, emissions[1].data?.size)
        assertNotNull(emissions[1].data?.get(0))
        assertTrue("Emitted data type shall be Show", emissions[1].data?.get(0) is Show)
        assertEquals("Shall have emitted 2 times", 2, emissions.size)
    }

    @Test
    fun `searchShows() on success first emission shall be loading equals to true`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(successWith10ShowSearchResults)
        )

        // Act
        val emissions = showsRepository.searchShows("dummy").toList()

        // Assert
        assertTrue("First emission shall be loading", emissions.first().isLoading)
    }

    @Test
    fun `searchShows() on success last emission shall be loading equals to false`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(successWith10ShowSearchResults)
        )

        // Act
        val emissions = showsRepository.searchShows("dummy").toList()

        // Assert
        assertFalse("First emission shall be loading", emissions.last().isLoading)
    }

    @Test
    fun `searchShows() on failure first emission shall be loading equals to true`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
        )

        // Act
        val emissions = showsRepository.searchShows("dummy").toList()

        // Assert
        assertTrue("First emission shall be loading", emissions.first().isLoading)
    }

    @Test
    fun `searchShows() on failure last emission shall be loading equals to false`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
        )

        // Act
        val emissions = showsRepository.searchShows("dummy").toList()

        // Assert
        assertFalse("First emission shall be loading", emissions.last().isLoading)
    }

    @Test
    fun `searchShows() successfully retrieve 0 results`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(emptyArray)
        )

        // Act
        val emissions = showsRepository.searchShows("dummy").toList()

        // Assert
        val cachedData = showDaoMock.getAll()
        assertTrue("Shall be no cache", cachedData.isEmpty())
        assertEquals("No data shall be in cache", 0, cachedData.size)
        assertEquals("No data shall be emitted", 0, emissions[1].data?.size)
    }

    @Test
    fun `searchShows() successfully retrieve 10 results`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(successWith10ShowSearchResults)
        )

        // Act
        val emissions = showsRepository.searchShows("dummy").toList()

        // Assert
        val cachedData = showDaoMock.queryIdIn(
            listOf(
                12918,
                27124,
                13991,
                32965,
                57603,
                57586,
                22677,
                22253,
                3307,
                15942
            )
        )
        assertEquals("All data shall be in cache", 10, cachedData.size)
        assertEquals("All data shall be emitted", 10, emissions[1].data?.size)
        assertNotNull(emissions[1].data?.get(0))
        assertTrue("Emitted data type shall be Show", emissions[1].data?.get(0) is Show)
        assertEquals("Shall have emitted 2 times", 2, emissions.size)
    }

    @Test
    fun `searchShows() fails when get a random error`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
        )

        // Act
        val emissions = showsRepository.searchShows("dummy").toList()

        // Assert
        val cachedData = showDaoMock.getAll()
        assertTrue("Shall be no cache", cachedData.isEmpty())

        assertNull("Shall be no data", emissions[1].data)
        assertTrue(
            "State message type shall be Error",
            emissions[1].stateMessage?.response?.messageType is MessageType.Error
        )
        assertEquals("Shall have emitted 2 times", 2, emissions.size)
    }

    private suspend fun initCache(quantitative: Int) {
        (0 until quantitative).forEach {
            showDaoMock.insert(
                ShowCacheEntity(
                    it,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                )
            )
        }
    }
}
