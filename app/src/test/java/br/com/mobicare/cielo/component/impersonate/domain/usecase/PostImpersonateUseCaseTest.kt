package br.com.mobicare.cielo.component.impersonate.domain.usecase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.component.impersonate.domain.repository.ImpersonateRepository
import br.com.mobicare.cielo.component.impersonate.utils.ImpersonateFactory
import br.com.mobicare.cielo.component.impersonate.utils.TypeImpersonateEnum
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PostImpersonateUseCaseTest {

    private val repository = mockk<ImpersonateRepository>()
    private val postImpersonateUseCase = PostImpersonateUseCase(repository)

    private val resultSuccess = CieloDataResult.Success(ImpersonateFactory.impersonateResponse)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    @Test
    fun `it should return access token when post impersonate`() = runBlocking {
        coEvery { repository.postImpersonate(any(), any(), any()) } returns resultSuccess

        val result = postImpersonateUseCase(
            ImpersonateFactory.ec,
            TypeImpersonateEnum.HIERARCHY.name,
            ImpersonateFactory.impersonateRequest
        )

        assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

        val resultCieloDataResult = result as CieloDataResult.Success
        val actualData = resultCieloDataResult.value
        val expectedData = resultSuccess.value

        assertEquals(expectedData.accessToken, actualData.accessToken)
        assertEquals(expectedData.refreshToken, actualData.refreshToken)
        assertEquals(expectedData.tokenType, actualData.tokenType)
        assertEquals(expectedData.expiresIn, actualData.expiresIn)
    }

    @Test
    fun `it should return API error when post impersonate`() = runBlocking {
        coEvery { repository.postImpersonate(any(), any(), any()) } returns resultError

        val result = postImpersonateUseCase(
            ImpersonateFactory.ec,
            TypeImpersonateEnum.HIERARCHY.name,
            ImpersonateFactory.impersonateRequest
        )

        assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)
        assertEquals(resultError, result)
    }

    @Test
    fun `it should return empty error when post impersonate`() = runBlocking {
        coEvery { repository.postImpersonate(any(), any(), any()) } returns resultEmpty

        val result = postImpersonateUseCase(
            ImpersonateFactory.ec,
            TypeImpersonateEnum.HIERARCHY.name,
            ImpersonateFactory.impersonateRequest
        )

        assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)
        assertEquals(resultEmpty, result)
    }

}