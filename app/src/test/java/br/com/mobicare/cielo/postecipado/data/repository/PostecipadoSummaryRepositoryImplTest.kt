package br.com.mobicare.cielo.postecipado.data.repository

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.home.presentation.postecipado.data.datasource.PostecipadoRemoteDataSource
import br.com.mobicare.cielo.home.presentation.postecipado.data.repository.PostecipadoSummaryRepositoryImpl
import br.com.mobicare.cielo.postecipado.utils.PostecipadoFactory
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PostecipadoSummaryRepositoryImplTest {
    private val dataSource = mockk<PostecipadoRemoteDataSource>()
    private val repository = PostecipadoSummaryRepositoryImpl(dataSource)

    private val completeResponse = PostecipadoFactory.getCompleteResponse()
    private val dataResultSuccess = CieloDataResult.Success(completeResponse)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `getPlanInformation should return success result on successful API response`() = runBlocking {
        coEvery { dataSource.getPlanInformation(EMPTY) } returns dataResultSuccess

        val result = repository.getPlanInformation(EMPTY)

        assertEquals(dataResultSuccess, result)
    }

    @Test
    fun `getPlanInformation should return empty result on response`() = runBlocking {
        coEvery { dataSource.getPlanInformation(EMPTY) } returns CieloDataResult.Empty()

        val result = repository.getPlanInformation(EMPTY)

        assert(result is CieloDataResult.Empty)
    }

    @Test
    fun `getPlanInformation should return a network error when getting the info`() = runBlocking {
        coEvery { dataSource.getPlanInformation(EMPTY) } returns resultError

        val result = repository.getPlanInformation(EMPTY)

        assertEquals(resultError, result)
    }
}