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

class CreatePixScheduleSettlementUseCaseTest {
    private val repository = mockk<PixScheduledSettlementRepository>()

    private val params = PixScheduledSettlementFactory.let {
        CreatePixScheduledSettlementUseCase.Params(it.otpCode, it.pixScheduledSettlementRequest)
    }
    private val response = PixScheduledSettlementFactory.pixScheduledSettlementResponse
    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val successResult = CieloDataResult.Success(response)

    private val createPixScheduledSettlementUseCase = CreatePixScheduledSettlementUseCase(repository)

    @Test
    fun `it should call method create of repository only once`() = runBlocking {
        // given
        coEvery { repository.create(any(), any()) } returns successResult

        // when
        createPixScheduledSettlementUseCase(params)

        // then
        coVerify(exactly = 1) { repository.create(any(), any()) }
    }

    @Test
    fun `it should return a success result on createPixScheduledSettlementUseCase call`() = runBlocking {
        // given
        coEvery { repository.create(any(), any()) } returns successResult

        // when
        val result = createPixScheduledSettlementUseCase(params)

        // then
        assertEquals(successResult, result)
    }

    @Test
    fun `it should return a network error on createPixScheduledSettlementUseCase call`() = runBlocking {
        // given
        coEvery { repository.create(any(), any()) } returns errorResult

        // when
        val result = createPixScheduledSettlementUseCase(params)

        // then
        assertEquals(errorResult, result)
    }

}