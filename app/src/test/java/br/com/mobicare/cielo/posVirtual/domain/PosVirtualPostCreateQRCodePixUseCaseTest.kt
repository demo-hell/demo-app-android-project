package br.com.mobicare.cielo.posVirtual.domain

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pix.constants.DEFAULT_OTP
import br.com.mobicare.cielo.posVirtual.domain.repository.PosVirtualQRCodePixRepository
import br.com.mobicare.cielo.posVirtual.domain.useCase.PostPosVirtualCreateQRCodePixUseCase
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PosVirtualPostCreateQRCodePixUseCaseTest {

    private val repository = mockk<PosVirtualQRCodePixRepository>()

    private val posVirtualCreateQRCodeRequest = PosVirtualFactory.posVirtualCreateQRCodeRequest
    private val posVirtualCreateQRCodeResponse = PosVirtualFactory.posVirtualCreateQRCodeResponse
    private val resultPosVirtualCreateQRCodeSuccess =
        CieloDataResult.Success(posVirtualCreateQRCodeResponse)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    private val postPosVirtualCreateQRCodePixUseCase =
        PostPosVirtualCreateQRCodePixUseCase(repository)

    @Test
    fun `it should fetch QRCode calling repository only once`() = runBlocking {
        coEvery {
            repository.postPosVirtualCreateQRCodePix(
                any(),
                any()
            )
        } returns resultPosVirtualCreateQRCodeSuccess

        postPosVirtualCreateQRCodePixUseCase(DEFAULT_OTP, posVirtualCreateQRCodeRequest)

        coVerify(exactly = ONE) { repository.postPosVirtualCreateQRCodePix(any(), any()) }
    }

    @Test
    fun `it should return the qr code response`() = runBlocking {
        coEvery {
            repository.postPosVirtualCreateQRCodePix(
                any(),
                any()
            )
        } returns resultPosVirtualCreateQRCodeSuccess

        val result =
            postPosVirtualCreateQRCodePixUseCase(DEFAULT_OTP, posVirtualCreateQRCodeRequest)

        assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

        val resultSuccess = result as CieloDataResult.Success
        val actualData = resultSuccess.value
        val expectedData = resultPosVirtualCreateQRCodeSuccess.value

        assertEquals(expectedData.id, actualData.id)
        assertEquals(expectedData.creationDate, actualData.creationDate)
        assertEquals(expectedData.nsuPix, actualData.nsuPix)
        assertEquals(expectedData.qrCodeString, actualData.qrCodeString)
        assertEquals(expectedData.qrCodeBase64, actualData.qrCodeBase64)
        assertEquals(expectedData.merchantName, actualData.merchantName)
        assertEquals(expectedData.merchantNumber, actualData.merchantNumber)
        assertEquals(expectedData.merchantDocument, actualData.merchantDocument)
        assertEquals(expectedData.amount, actualData.amount)
    }

    @Test
    fun `it should return a network error when create qr code`() = runBlocking {
        coEvery {
            repository.postPosVirtualCreateQRCodePix(
                any(),
                any()
            )
        } returns resultError

        val result =
            postPosVirtualCreateQRCodePixUseCase(DEFAULT_OTP, posVirtualCreateQRCodeRequest)

        assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)

        assertEquals(resultError, result)
    }

    @Test
    fun `it should return a empty error when create qr code`() = runBlocking {
        coEvery {
            repository.postPosVirtualCreateQRCodePix(
                any(),
                any()
            )
        } returns resultEmpty

        val result = repository.postPosVirtualCreateQRCodePix(DEFAULT_OTP, posVirtualCreateQRCodeRequest)

        assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)

        assertEquals(resultEmpty, result)
    }

}