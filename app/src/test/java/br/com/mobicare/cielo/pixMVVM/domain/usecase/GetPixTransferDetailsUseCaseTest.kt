package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.asSuccess
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixTransactionsRepository
import br.com.mobicare.cielo.pixMVVM.utils.PixTransactionsFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetPixTransferDetailsUseCaseTest {
    private val repository = mockk<PixTransactionsRepository>()

    private val entity = PixTransactionsFactory.TransferDetail.entity
    private val params = PixTransactionsFactory.MockedParams.let {
        GetPixTransferDetailsUseCase.Params(it.endToEndId, it.transactionCode)
    }
    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val successResult = CieloDataResult.Success(entity)

    private val getPixTransferDetailsUseCase = GetPixTransferDetailsUseCase(repository)

    @Test
    fun `it should call method getTransferDetails of repository only once`() = runBlocking {
        // given
        coEvery { repository.getTransferDetails(any(), any()) } returns successResult

        // when
        getPixTransferDetailsUseCase(params)

        // then
        coVerify(exactly = 1) { repository.getTransferDetails(any(), any()) }
    }

    @Test
    fun `it should return the correct PixTransferDetail entity on getPixTransferDetailsUseCase call`() = runBlocking {
        // given
        coEvery { repository.getTransferDetails(any(), any()) } returns successResult

        // when
        val result = getPixTransferDetailsUseCase(params)

        // then
        assertEquals(successResult, result)

        val actual = result.asSuccess.value
        val expected = entity

        assertEquals(expected, actual)
    }

    @Test
    fun `it should return a network error on getPixTransferDetailsUseCase call`() = runBlocking {
        // given
        coEvery { repository.getTransferDetails(any(), any()) } returns errorResult

        // when
        val result = getPixTransferDetailsUseCase(params)

        // then
        assertEquals(errorResult, result)
    }
    
}