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

class GetPixRefundReceiptsUseCaseTest {
    private val repository = mockk<PixRefundsRepository>()

    private val entity = PixRefundsFactory.RefundReceipts.entity
    private val params = PixRefundsFactory.MockedParams.let {
        GetPixRefundReceiptsUseCase.Params(it.idEndToEndOriginal)
    }
    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val successResult = CieloDataResult.Success(entity)

    private val getPixRefundReceiptsUseCase = GetPixRefundReceiptsUseCase(repository)

    @Test
    fun `it should call method getReceipts of repository only once`() = runBlocking {
        // given
        coEvery { repository.getReceipts(any()) } returns successResult

        // when
        getPixRefundReceiptsUseCase(params)

        // then
        coVerify(exactly = 1) { repository.getReceipts(any()) }
    }

    @Test
    fun `it should return the correct PixRefundReceipts entity on getPixRefundReceiptsUseCase call`() = runBlocking {
        // given
        coEvery { repository.getReceipts(any()) } returns successResult

        // when
        val result = getPixRefundReceiptsUseCase(params)

        // then
        assertEquals(successResult, result)

        val actual = result.asSuccess.value
        val expected = entity

        assertEquals(expected, actual)
    }

    @Test
    fun `it should return a network error on getPixRefundReceiptsUseCase call`() = runBlocking {
        // given
        coEvery { repository.getReceipts(any()) } returns errorResult

        // when
        val result = getPixRefundReceiptsUseCase(params)

        // then
        assertEquals(errorResult, result)
    }
    
}