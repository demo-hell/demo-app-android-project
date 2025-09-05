package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixQRCodeRepository
import br.com.mobicare.cielo.pixMVVM.utils.PixQRCodeFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PostPixDecodeQRCodeUseCaseTest {
    private val repository = mockk<PixQRCodeRepository>()
    private val postPixDecodeQRCodeUseCase = PostPixDecodeQRCodeUseCase(repository)

    private val resultSuccessDecodeQRCode = CieloDataResult.Success(PixQRCodeFactory.pixDecodeQRCode)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    @Test
    fun `it should fetch decode QR code calling remote data source only once`() =
        runBlocking {
            coEvery {
                repository.postDecodeQRCode(any())
            } returns resultSuccessDecodeQRCode

            postPixDecodeQRCodeUseCase(PostPixDecodeQRCodeUseCase.Params(PixQRCodeFactory.qrCode))

            coVerify(exactly = ONE) { repository.postDecodeQRCode(any()) }
        }

    @Test
    fun `it should return the decode QR code object`() =
        runBlocking {
            coEvery {
                repository.postDecodeQRCode(any())
            } returns resultSuccessDecodeQRCode

            val result = postPixDecodeQRCodeUseCase(PostPixDecodeQRCodeUseCase.Params(PixQRCodeFactory.qrCode))

            val actualDecodeQRCode = (result as CieloDataResult.Success).value
            val expectedDecodeQRCode = resultSuccessDecodeQRCode.value

            assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)
            assertEquals(expectedDecodeQRCode, actualDecodeQRCode)
        }

    @Test
    fun `it should return the error`() =
        runBlocking {
            coEvery {
                repository.postDecodeQRCode(any())
            } returns resultError

            val result = postPixDecodeQRCodeUseCase(PostPixDecodeQRCodeUseCase.Params(PixQRCodeFactory.qrCode))

            assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)
            assertEquals(resultError, result)
        }

    @Test
    fun `it should return the empty error`() =
        runBlocking {
            coEvery {
                repository.postDecodeQRCode(any())
            } returns resultEmpty

            val result = postPixDecodeQRCodeUseCase(PostPixDecodeQRCodeUseCase.Params(PixQRCodeFactory.qrCode))

            assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)
            assertEquals(resultEmpty, result)
        }
}
