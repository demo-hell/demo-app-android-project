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

class CancelPixTransferScheduleUseCaseTest {
    private val repository = mockk<PixTransactionsRepository>()

    private val entity = PixTransactionsFactory.TransferResult.entity
    private val params = PixTransactionsFactory.MockedParams.let {
        CancelPixTransferScheduleUseCase.Params(it.otpCode, it.transferScheduleCancelRequest)
    }
    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val successResult = CieloDataResult.Success(entity)

    private val cancelPixTransferScheduleUseCase = CancelPixTransferScheduleUseCase(repository)

    @Test
    fun `it should call method cancelTransferSchedule of repository only once`() = runBlocking {
        // given
        coEvery { repository.cancelTransferSchedule(any(), any()) } returns successResult

        // when
        cancelPixTransferScheduleUseCase(params)

        // then
        coVerify(exactly = 1) { repository.cancelTransferSchedule(any(), any()) }
    }

    @Test
    fun `it should return the correct PixTransferResult entity on cancelPixTransferScheduleUseCase call`() = runBlocking {
        // given
        coEvery { repository.cancelTransferSchedule(any(), any()) } returns successResult

        // when
        val result = cancelPixTransferScheduleUseCase(params)

        // then
        assertEquals(successResult, result)

        val actual = result.asSuccess.value
        val expected = entity

        assertEquals(expected, actual)
    }

    @Test
    fun `it should return a network error on cancelPixTransferScheduleUseCase call`() = runBlocking {
        // given
        coEvery { repository.cancelTransferSchedule(any(), any()) } returns errorResult

        // when
        val result = cancelPixTransferScheduleUseCase(params)

        // then
        assertEquals(errorResult, result)
    }

}