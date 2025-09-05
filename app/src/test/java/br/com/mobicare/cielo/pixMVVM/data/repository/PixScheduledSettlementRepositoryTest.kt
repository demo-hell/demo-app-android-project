package br.com.mobicare.cielo.pixMVVM.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.asSuccess
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixScheduledSettlementRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.utils.PixProfileFactory
import br.com.mobicare.cielo.pixMVVM.utils.PixScheduledSettlementFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PixScheduledSettlementRepositoryTest {
    private val remoteDataSource = mockk<PixScheduledSettlementRemoteDataSource>()

    private val request = PixScheduledSettlementFactory.pixScheduledSettlementRequest
    private val response = PixScheduledSettlementFactory.pixScheduledSettlementResponse
    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val successResult = CieloDataResult.Success(response)
    private val otpCode = PixProfileFactory.otpCode

    private val repository = PixScheduledSettlementRepositoryImpl(remoteDataSource)

    @Test
    fun `it should call method create of remote data source only once`() = runBlocking {
        // given
        coEvery { remoteDataSource.create(any(), any()) } returns successResult

        // when
        repository.create(otpCode, request)

        // then
        coVerify(exactly = 1) { remoteDataSource.create(any(), any()) }
    }

    @Test
    fun `it should call method update of remote data source only once`() = runBlocking {
        // given
        coEvery { remoteDataSource.update(any(), any()) } returns successResult

        // when
        repository.update(otpCode, request)

        // then
        coVerify(exactly = 1) { remoteDataSource.update(any(), any()) }
    }

    @Test
    fun `it should return a success result on create call`() = runBlocking {
        // given
        coEvery { remoteDataSource.create(any(), any()) } returns successResult

        // when
        val result = repository.create(otpCode, request)

        // then
        assertEquals(successResult, result)
        assertEquals(response, result.asSuccess.value)
    }

    @Test
    fun `it should return a success result on update call`() = runBlocking {
        // given
        coEvery { remoteDataSource.update(any(), any()) } returns successResult

        // when
        val result = repository.update(otpCode, request)

        // then
        assertEquals(successResult, result)
        assertEquals(response, result.asSuccess.value)
    }

    @Test
    fun `it should return a network error on create call`() = runBlocking {
        // given
        coEvery { remoteDataSource.create(any(), any()) } returns errorResult

        // when
        val result = repository.create(otpCode, request)

        // then
        assertEquals(errorResult, result)
    }

    @Test
    fun `it should return a network error on update call`() = runBlocking {
        // given
        coEvery { remoteDataSource.update(any(), any()) } returns errorResult

        // when
        val result = repository.update(otpCode, request)

        // then
        assertEquals(errorResult, result)
    }

}