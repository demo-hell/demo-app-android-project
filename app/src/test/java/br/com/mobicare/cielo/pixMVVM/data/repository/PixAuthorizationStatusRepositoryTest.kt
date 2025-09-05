package br.com.mobicare.cielo.pixMVVM.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixAuthorizationStatusRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.utils.PixAuthorizationStatusFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PixAuthorizationStatusRepositoryTest {
    private val remoteDataSource = mockk<PixAuthorizationStatusRemoteDataSource>()

    private val pixAuthorizationStatus = PixAuthorizationStatusFactory.entityWithPendingStatus
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultSuccess = CieloDataResult.Success(pixAuthorizationStatus)
    private val repository = PixAuthorizationStatusRepositoryImpl(remoteDataSource)

    @Test
    fun `it should call method getPixAuthorizationStatus of remote data source only once`() = runBlocking {
        // given
        coEvery { remoteDataSource.getPixAuthorizationStatus() } returns resultSuccess

        // when
        repository.getPixAuthorizationStatus()

        // then
        coVerify(exactly = 1) { remoteDataSource.getPixAuthorizationStatus() }
    }

    @Test
    fun `it should return the correct PixAuthorizationStatus entity on getPixAuthorizationStatus call successfully`() = runBlocking {
        // given
        coEvery { remoteDataSource.getPixAuthorizationStatus() } returns resultSuccess

        // when
        val result = repository.getPixAuthorizationStatus()

        // then
        assertEquals(resultSuccess, result)

        (result as CieloDataResult.Success).value.let {
            assertEquals(pixAuthorizationStatus.status, it.status)
            assertEquals(pixAuthorizationStatus.beginTime, it.beginTime)
        }
    }

    @Test
    fun `it should return a network error on getPixAuthorizationStatus call`() = runBlocking {
        // given
        coEvery { remoteDataSource.getPixAuthorizationStatus() } returns resultError

        // when
        val result = repository.getPixAuthorizationStatus()

        // then
        assertEquals(resultError, result)
    }
}