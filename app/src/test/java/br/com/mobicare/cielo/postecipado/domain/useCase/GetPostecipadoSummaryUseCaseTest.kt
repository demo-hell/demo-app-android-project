package br.com.mobicare.cielo.postecipado.domain.useCase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.home.presentation.postecipado.data.repository.PostecipadoSummaryRepositoryImpl
import br.com.mobicare.cielo.home.presentation.postecipado.domain.usecase.GetPostecipadoSummaryUseCase
import br.com.mobicare.cielo.postecipado.utils.PostecipadoFactory
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetPostecipadoSummaryUseCaseTest {
    private val repository = mockk<PostecipadoSummaryRepositoryImpl>()
    private val getPostecipadoSummaryUseCase = GetPostecipadoSummaryUseCase(repository)

    private val completeResponse = PostecipadoFactory.getCompleteResponse()
    private val dataResultSuccess = CieloDataResult.Success(completeResponse)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `invoke should return PlanInformationResponse on successful API response`() = runBlocking {
        coEvery { repository.getPlanInformation(any()) } returns dataResultSuccess

        val result = getPostecipadoSummaryUseCase(EMPTY)

        assertEquals(dataResultSuccess, result)
    }

    @Test
    fun `invoke should return API error result on error response`() = runBlocking {
        coEvery { repository.getPlanInformation(EMPTY) } returns resultError

        val result = getPostecipadoSummaryUseCase(EMPTY)

        assertEquals(resultError, result)
    }

    @Test
    fun `invoke should return empty result on empty API response`() = runBlocking {
        coEvery { repository.getPlanInformation(any()) } returns CieloDataResult.Empty()

        val result = getPostecipadoSummaryUseCase(EMPTY)

        assertTrue(result is CieloDataResult.Empty)
    }
}