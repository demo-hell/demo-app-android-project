package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixAuthorizationStatusRepository
import br.com.mobicare.cielo.pixMVVM.utils.PixAuthorizationStatusFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetPixAuthorizationStatusUseCaseTest {
    private val repository = mockk<PixAuthorizationStatusRepository>()

    private val pixAuthorizationStatus = PixAuthorizationStatusFactory.entityWithPendingStatus
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultSuccess = CieloDataResult.Success(pixAuthorizationStatus)
    private val getPixAuthorizationStatusUseCase = GetPixAuthorizationStatusUseCase(repository)

    @Test
    fun `it should call method getPixAuthorizationStatus of repository only once`() = runBlocking {
        // given
        coEvery { repository.getPixAuthorizationStatus() } returns resultSuccess

        // when
        getPixAuthorizationStatusUseCase()

        // then
        coVerify(exactly = 1) { repository.getPixAuthorizationStatus() }
    }

    @Test
    fun `it should return the correct PixAuthorizationStatus entity on getPixAuthorizationStatusUseCase call successfully`() = runBlocking {
        // given
        coEvery { repository.getPixAuthorizationStatus() } returns resultSuccess

        // when
        val result = getPixAuthorizationStatusUseCase()

        // then
        assertEquals(resultSuccess, result)

        (result as CieloDataResult.Success).value.let {
            assertEquals(pixAuthorizationStatus.status, it.status)
            assertEquals(pixAuthorizationStatus.beginTime, it.beginTime)
        }
    }

    @Test
    fun `it should return a network error on getPixAuthorizationStatusUseCase call`() = runBlocking {
        // given
        coEvery { repository.getPixAuthorizationStatus() } returns resultError

        // when
        val result = getPixAuthorizationStatusUseCase()

        // then
        assertEquals(resultError, result)
    }
}