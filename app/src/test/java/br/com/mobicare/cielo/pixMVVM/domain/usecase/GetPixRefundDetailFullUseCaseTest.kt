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

class GetPixRefundDetailFullUseCaseTest {
    private val repository = mockk<PixRefundsRepository>()

    private val entity = PixRefundsFactory.RefundDetailFull.entity
    private val params = PixRefundsFactory.MockedParams.let {
        GetPixRefundDetailFullUseCase.Params(it.transactionCode)
    }
    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val successResult = CieloDataResult.Success(entity)

    private val getPixRefundDetailFullUseCase = GetPixRefundDetailFullUseCase(repository)

    @Test
    fun `it should call method getDetailFull of repository only once`() = runBlocking {
        // given
        coEvery { repository.getDetailFull(any()) } returns successResult

        // when
        getPixRefundDetailFullUseCase(params)

        // then
        coVerify(exactly = 1) { repository.getDetailFull(any()) }
    }

    @Test
    fun `it should return the correct PixRefundDetailFull entity on getPixRefundDetailFullUseCase call`() = runBlocking {
        // given
        coEvery { repository.getDetailFull(any()) } returns successResult

        // when
        val result = getPixRefundDetailFullUseCase(params)

        // then
        assertEquals(successResult, result)

        val actual = result.asSuccess.value
        val expected = entity

        assertEquals(expected, actual)
    }

    @Test
    fun `it should return a network error on getPixRefundDetailFullUseCase call`() = runBlocking {
        // given
        coEvery { repository.getDetailFull(any()) } returns errorResult

        // when
        val result = getPixRefundDetailFullUseCase(params)

        // then
        assertEquals(errorResult, result)
    }
    
}