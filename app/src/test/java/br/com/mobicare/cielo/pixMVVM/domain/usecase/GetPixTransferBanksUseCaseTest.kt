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

class GetPixTransferBanksUseCaseTest {
    private val repository = mockk<PixTransactionsRepository>()

    private val entity = PixTransactionsFactory.TransferBanks.entity
    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val successResult = CieloDataResult.Success(entity)

    private val getPixTransferBanksUseCase = GetPixTransferBanksUseCase(repository)

    @Test
    fun `it should call method getTransferBanks of repository only once`() = runBlocking {
        // given
        coEvery { repository.getTransferBanks() } returns successResult

        // when
        getPixTransferBanksUseCase()

        // then
        coVerify(exactly = 1) { repository.getTransferBanks() }
    }

    @Test
    fun `it should return the correct list of PixTransferBank entities on getPixTransferBanksUseCase call`() = runBlocking {
        // given
        coEvery { repository.getTransferBanks() } returns successResult

        // when
        val result = getPixTransferBanksUseCase()

        // then
        assertEquals(successResult, result)

        val actual = result.asSuccess.value
        val expected = entity

        assertEquals(expected, actual)
    }

    @Test
    fun `it should return a network error on getPixTransferBanksUseCase call`() = runBlocking {
        // given
        coEvery { repository.getTransferBanks() } returns errorResult

        // when
        val result = getPixTransferBanksUseCase()

        // then
        assertEquals(errorResult, result)
    }
    
}