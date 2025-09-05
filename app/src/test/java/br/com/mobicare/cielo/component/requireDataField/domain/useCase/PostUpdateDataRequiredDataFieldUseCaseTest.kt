package br.com.mobicare.cielo.component.requireDataField.domain.useCase

import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.component.requireDataField.utils.RequiredDataFieldFactory
import br.com.mobicare.cielo.component.requiredDataField.data.model.request.OrdersRequest
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.OrdersResponse
import br.com.mobicare.cielo.component.requiredDataField.data.repository.RequiredDataFieldRepositoryImpl
import br.com.mobicare.cielo.component.requiredDataField.domain.useCase.PostUpdateDataRequiredDataFieldUseCase
import br.com.mobicare.cielo.pix.constants.DEFAULT_OTP
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PostUpdateDataRequiredDataFieldUseCaseTest {

    private val repository = mockk<RequiredDataFieldRepositoryImpl>()
    private val postUpdateDataRequiredDataFieldUseCase =
        PostUpdateDataRequiredDataFieldUseCase(repository)

    private val requestUpdateData = OrdersRequest()

    private val resultSuccess =
        CieloDataResult.Success(OrdersResponse(orderId = RequiredDataFieldFactory.orderId))
    private val resultGenericError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmptyError = CieloDataResult.Empty()

    @Test
    fun `it should fetch update data calling remote data source only once`() = runBlocking {
        coEvery {
            repository.postUpdateData(any(), any())
        } returns resultSuccess

        postUpdateDataRequiredDataFieldUseCase(DEFAULT_OTP, requestUpdateData)

        coVerify(exactly = ONE) { repository.postUpdateData(any(), any()) }
    }

    @Test
    fun `it should return the success when update data required`() = runBlocking {
        coEvery {
            repository.postUpdateData(any(), any())
        } returns resultSuccess

        val result = postUpdateDataRequiredDataFieldUseCase(DEFAULT_OTP, requestUpdateData)

        assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

        val resultSuccess = result as CieloDataResult.Success
        val actualData = resultSuccess.value.orderId
        val expectedData = RequiredDataFieldFactory.orderId

        assertEquals(expectedData, actualData)
    }

    @Test
    fun `it should return the generic error when update data required`() = runBlocking {
        coEvery {
            repository.postUpdateData(any(), any())
        } returns resultGenericError

        val result = postUpdateDataRequiredDataFieldUseCase(DEFAULT_OTP, requestUpdateData)

        assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)
        assertEquals(resultGenericError, result)
    }

    @Test
    fun `it should return the empty error when update data required`() = runBlocking {
        coEvery {
            repository.postUpdateData(any(), any())
        } returns resultEmptyError

        val result = postUpdateDataRequiredDataFieldUseCase(DEFAULT_OTP, requestUpdateData)

        assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)
        assertEquals(resultEmptyError, result)
    }

}