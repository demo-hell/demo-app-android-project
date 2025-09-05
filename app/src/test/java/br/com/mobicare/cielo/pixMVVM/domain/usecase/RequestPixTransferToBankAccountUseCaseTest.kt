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

class RequestPixTransferToBankAccountUseCaseTest {
    private val repository = mockk<PixTransactionsRepository>()

    private val entity = PixTransactionsFactory.TransferResult.entity
    private val params = PixTransactionsFactory.MockedParams.let {
        RequestPixTransferToBankAccountUseCase.Params(EMPTY, it.transferAccountBankRequest)
    }
    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val successResult = CieloDataResult.Success(entity)

    private val requestPixTransferToBankAccountUseCase = RequestPixTransferToBankAccountUseCase(repository)

    @Test
    fun `it should call method transferToBankAccount of repository only once`() = runBlocking {
        // given
        coEvery { repository.transferToBankAccount(any(), any()) } returns successResult

        // when
        requestPixTransferToBankAccountUseCase(params)

        // then
        coVerify(exactly = 1) { repository.transferToBankAccount(any(), any()) }
    }

    @Test
    fun `it should return the correct PixTransferResult entity`() = runBlocking {
        // given
        coEvery { repository.transferToBankAccount(any(), any()) } returns successResult

        // when
        val result = requestPixTransferToBankAccountUseCase(params)

        // then
        assertEquals(successResult, result)

        val actual = result.asSuccess.value
        val expected = entity

        assertEquals(expected, actual)
    }

    @Test
    fun `it should return a network error`() = runBlocking {
        // given
        coEvery { repository.transferToBankAccount(any(), any()) } returns errorResult

        // when
        val result = requestPixTransferToBankAccountUseCase(params)

        // then
        assertEquals(errorResult, result)
    }
    
}