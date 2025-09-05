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

class GetPixTransferScheduleDetailUseCaseTest {
    private val repository = mockk<PixTransactionsRepository>()

    private val entity = PixTransactionsFactory.SchedulingDetail.entity
    private val params = PixTransactionsFactory.MockedParams.let {
        GetPixTransferScheduleDetailUseCase.Params(it.schedulingCode)
    }
    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val successResult = CieloDataResult.Success(entity)

    private val getPixTransferScheduleDetailUseCase = GetPixTransferScheduleDetailUseCase(repository)

    @Test
    fun `it should call method getTransferScheduleDetail of repository only once`() = runBlocking {
        // given
        coEvery { repository.getTransferScheduleDetail(any()) } returns successResult

        // when
        getPixTransferScheduleDetailUseCase(params)

        // then
        coVerify(exactly = 1) { repository.getTransferScheduleDetail(any()) }
    }

    @Test
    fun `it should return the correct PixSchedulingDetail entity on getTransferScheduleDetail call`() = runBlocking {
        // given
        coEvery { repository.getTransferScheduleDetail(any()) } returns successResult

        // when
        val result = getPixTransferScheduleDetailUseCase(params)

        // then
        assertEquals(successResult, result)

        val actual = result.asSuccess.value
        val expected = entity

        assertEquals(expected, actual)
    }

    @Test
    fun `it should return a network error on getTransferScheduleDetail call`() = runBlocking {
        // given
        coEvery { repository.getTransferScheduleDetail(any()) } returns errorResult

        // when
        val result = getPixTransferScheduleDetailUseCase(params)

        // then
        assertEquals(errorResult, result)
    }
    
}