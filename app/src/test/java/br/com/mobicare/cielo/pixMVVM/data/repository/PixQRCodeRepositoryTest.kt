package br.com.mobicare.cielo.pixMVVM.data.repository

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixQRCodeRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.utils.PixQRCodeFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PixQRCodeRepositoryTest {
    private val remoteDataSource = mockk<PixQRCodeRemoteDataSource>()
    private val repository = PixQRCodeRepositoryImpl(remoteDataSource)

    private val resultSuccessDecodeQRCode = CieloDataResult.Success(PixQRCodeFactory.pixDecodeQRCode)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    @Test
    fun `it should fetch decode QR code calling remote data source only once`() =
        runBlocking {
            coEvery {
                remoteDataSource.postDecodeQRCode(any())
            } returns resultSuccessDecodeQRCode

            repository.postDecodeQRCode(PixQRCodeFactory.pixDecodeQRCodeRequest)

            coVerify(exactly = ONE) { remoteDataSource.postDecodeQRCode(any()) }
        }

    @Test
    fun `it should return the decode QR code object`() =
        runBlocking {
            coEvery {
                remoteDataSource.postDecodeQRCode(any())
            } returns resultSuccessDecodeQRCode

            val result = repository.postDecodeQRCode(PixQRCodeFactory.pixDecodeQRCodeRequest)

            val actualDecodeQRCode = (result as CieloDataResult.Success).value
            val expectedDecodeQRCode = resultSuccessDecodeQRCode.value

            assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)
            assertEquals(expectedDecodeQRCode, actualDecodeQRCode)
        }

    @Test
    fun `it should return the error`() =
        runBlocking {
            coEvery {
                remoteDataSource.postDecodeQRCode(any())
            } returns resultError

            val result = repository.postDecodeQRCode(PixQRCodeFactory.pixDecodeQRCodeRequest)

            assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)
            assertEquals(resultError, result)
        }

    @Test
    fun `it should return the empty error`() =
        runBlocking {
            coEvery {
                remoteDataSource.postDecodeQRCode(any())
            } returns resultEmpty

            val result = repository.postDecodeQRCode(PixQRCodeFactory.pixDecodeQRCodeRequest)

            assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)
            assertEquals(resultEmpty, result)
        }
}
