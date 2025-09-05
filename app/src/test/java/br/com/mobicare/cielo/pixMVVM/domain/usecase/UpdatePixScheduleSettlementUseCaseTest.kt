package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixScheduledSettlementRepository
import br.com.mobicare.cielo.pixMVVM.utils.PixScheduledSettlementFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class UpdatePixScheduleSettlementUseCaseTest {
    private val repository = mockk<PixScheduledSettlementRepository>()

    private val params = PixScheduledSettlementFactory.let {
        UpdatePixScheduledSettlementUseCase.Params(it.otpCode, it.scheduleList)
    }
    private val response = PixScheduledSettlementFactory.pixScheduledSettlementResponse
    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val successResult = CieloDataResult.Success(response)

    private val updatePixScheduledSettlementUseCase = UpdatePixScheduledSettlementUseCase(repository)

    @Test
    fun `it should call method update of repository only once`() = runBlocking {
        // given
        coEvery { repository.update(any(), any()) } returns successResult

        // when
        updatePixScheduledSettlementUseCase(params)

        // then
        coVerify(exactly = 1) { repository.update(any(), any()) }
    }

    @Test
    fun `it should return a success result on createPixScheduledSettlementUseCase call`() = runBlocking {
        // given
        coEvery { repository.update(any(), any()) } returns successResult

        // when
        val result = updatePixScheduledSettlementUseCase(params)

        // then
        assertEquals(successResult, result)
    }

    @Test
    fun `it should return a network error on createPixScheduledSettlementUseCase call`() = runBlocking {
        // given
        coEvery { repository.update(any(), any()) } returns errorResult

        // when
        val result = updatePixScheduledSettlementUseCase(params)

        // then
        assertEquals(errorResult, result)
    }

}