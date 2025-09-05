package br.com.mobicare.cielo.cieloFarol.domain.useCase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.cieloFarol.domain.repository.CieloFarolRepository
import br.com.mobicare.cielo.cieloFarol.utils.CieloFarolFactory
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class GetCieloFarolUseCaseTest {
    private val repository = mockk<CieloFarolRepository>()

    private val farolCompleted = CieloFarolFactory.farolCompleted
    private val merchantId = CieloFarolFactory.farolRequestMerchantId
    private val authorization = CieloFarolFactory.farolRequestAuthorization
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultSuccess = CieloDataResult.Success(farolCompleted)
    private val getCieloFarolUseCase = GetCieloFarolUseCase(repository)

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `invoke should return CieloFarol data on successful API response`() = runBlocking {
        coEvery { repository.getCieloFarol(any(), any()) } returns resultSuccess

        val result = getCieloFarolUseCase(authorization, merchantId)

        assertEquals(resultSuccess, result)
    }

    @Test
    fun `invoke should return the correct CieloFarolResponse data on successful API response`() = runBlocking {
        coEvery { repository.getCieloFarol(any(), any()) } returns resultSuccess

        val result = getCieloFarolUseCase(authorization, merchantId) as CieloDataResult.Success

        assertEquals(farolCompleted.bestDayOfWeek, result.value.bestDayOfWeek)
        assertEquals(farolCompleted.bestTime, result.value.bestTime)
        assertEquals(farolCompleted.averageTicketAmount, result.value.averageTicketAmount)
        assertEquals(farolCompleted.insightText, result.value.insightText)
    }

    @Test
    fun `invoke should return API error result on error response`() = runBlocking {
        coEvery { repository.getCieloFarol(authorization, merchantId) } returns resultError

        val result = getCieloFarolUseCase(authorization, merchantId)

        assertEquals(resultError, result)
    }

    @Test
    fun `invoke should return empty result on empty API response`() = runBlocking {
        coEvery { repository.getCieloFarol(authorization, merchantId) } returns CieloDataResult.Empty()

        val result = getCieloFarolUseCase(authorization, merchantId)

        assertTrue(result is CieloDataResult.Empty)
    }
}