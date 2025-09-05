package br.com.mobicare.cielo.posVirtual.domain

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pix.constants.DEFAULT_OTP
import br.com.mobicare.cielo.posVirtual.domain.repository.PosVirtualAccreditationRepository
import br.com.mobicare.cielo.posVirtual.domain.useCase.PostPosVirtualCreateOrderUseCase
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PosVirtualPostCreateOrderUseCaseTest {

    private val repository = mockk<PosVirtualAccreditationRepository>()

    private val posVirtualCreateOrderRequest = PosVirtualFactory.posVirtualCreateOrderRequest

    private val resultPosVirtualCreateOrderSuccess =
        CieloDataResult.Success(PosVirtualFactory.offerID)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    private val postPosVirtualAccreditationCreateOrderUseCase =
        PostPosVirtualCreateOrderUseCase(repository)

    @Test
    fun `it should fetch create order calling repository only once`() = runBlocking {
        coEvery {
            repository.postCreateOrder(
                any(),
                any()
            )
        } returns resultPosVirtualCreateOrderSuccess

        postPosVirtualAccreditationCreateOrderUseCase(DEFAULT_OTP, posVirtualCreateOrderRequest)

        coVerify(exactly = ONE) { repository.postCreateOrder(any(), any()) }
    }

    @Test
    fun `it should return the create order response`() = runBlocking {
        coEvery {
            repository.postCreateOrder(
                any(),
                any()
            )
        } returns resultPosVirtualCreateOrderSuccess

        val result =
            postPosVirtualAccreditationCreateOrderUseCase(DEFAULT_OTP, posVirtualCreateOrderRequest)

        assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

        val resultSuccess = result as CieloDataResult.Success
        val actualData = resultSuccess.value
        val expectedData = resultPosVirtualCreateOrderSuccess.value

        assertEquals(expectedData, actualData)
    }

    @Test
    fun `it should return a network error when create order`() = runBlocking {
        coEvery {
            repository.postCreateOrder(
                any(),
                any()
            )
        } returns resultError

        val result =
            postPosVirtualAccreditationCreateOrderUseCase(DEFAULT_OTP, posVirtualCreateOrderRequest)

        assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)

        assertEquals(resultError, result)
    }

    @Test
    fun `it should return a empty error when create order`() = runBlocking {
        coEvery {
            repository.postCreateOrder(
                any(),
                any()
            )
        } returns resultEmpty

        val result =
            postPosVirtualAccreditationCreateOrderUseCase(DEFAULT_OTP, posVirtualCreateOrderRequest)

        assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)

        assertEquals(resultEmpty, result)
    }

}