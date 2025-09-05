package br.com.mobicare.cielo.pixMVVM.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.utils.EMPTY_VALUE
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixProfileRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.utils.PixProfileFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PixProfileRepositoryTest {
    private val remoteDataSource = mockk<PixProfileRemoteDataSource>()

    private val pixProfileRequest = PixProfileFactory.pixProfileRequest
    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val successResult = CieloDataResult.Success(EMPTY_VALUE)
    private val otpCode = PixProfileFactory.otpCode

    private val repository = PixProfileRepositoryImpl(remoteDataSource)

    @Test
    fun `it should call method update of remote data source only once`() = runBlocking {
        // given
        coEvery { remoteDataSource.update(any(), any()) } returns successResult

        // when
        repository.update(otpCode, pixProfileRequest)

        // then
        coVerify(exactly = 1) { remoteDataSource.update(any(), any()) }
    }

    @Test
    fun `it should return a success result`() = runBlocking {
        // given
        coEvery { remoteDataSource.update(any(), any()) } returns successResult

        // when
        val result = repository.update(otpCode, pixProfileRequest)

        // then
        assertEquals(successResult, result)
    }

    @Test
    fun `it should return a network error`() = runBlocking {
        // given
        coEvery { remoteDataSource.update(any(), any()) } returns errorResult

        // when
        val result = repository.update(otpCode, pixProfileRequest)

        // then
        assertEquals(errorResult, result)
    }

}