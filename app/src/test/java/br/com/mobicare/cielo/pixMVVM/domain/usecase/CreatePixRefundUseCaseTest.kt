package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.asSuccess
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixRefundsRepository
import br.com.mobicare.cielo.pixMVVM.utils.PixRefundsFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class CreatePixRefundUseCaseTest {
    private val repository = mockk<PixRefundsRepository>()

    private val entity = PixRefundsFactory.RefundCreated.entity
    private val params = PixRefundsFactory.MockedParams.let {
        CreatePixRefundUseCase.Params(it.otpCode, it.refundCreateRequest)
    }
    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val successResult = CieloDataResult.Success(entity)

    private val createPixRefundUseCase = CreatePixRefundUseCase(repository)

    @Test
    fun `it should call method refund of repository only once`() = runBlocking {
        // given
        coEvery { repository.refund(any(), any()) } returns successResult

        // when
        createPixRefundUseCase(params)

        // then
        coVerify(exactly = 1) { repository.refund(any(), any()) }
    }

    @Test
    fun `it should return the correct PixRefundCreated entity on createPixRefundUseCase call`() = runBlocking {
        // given
        coEvery { repository.refund(any(), any()) } returns successResult

        // when
        val result = createPixRefundUseCase(params)

        // then
        assertEquals(successResult, result)

        val actual = result.asSuccess.value
        val expected = entity

        assertEquals(expected, actual)
    }

    @Test
    fun `it should return a network error on createPixRefundUseCase call`() = runBlocking {
        // given
        coEvery { repository.refund(any(), any()) } returns errorResult

        // when
        val result = createPixRefundUseCase(params)

        // then
        assertEquals(errorResult, result)
    }
    
}