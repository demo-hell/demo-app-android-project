package br.com.mobicare.cielo.pixMVVM.data.repository

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixExtractRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.utils.PixExtractFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PixExtractRepositoryTest {
    private val remoteDataSource = mockk<PixExtractRemoteDataSource>()
    private val repository = PixExtractRepositoryImpl(remoteDataSource)

    private val pixExtractRequest = PixExtractFactory.pixExtractFilterRequest
    private val pixReceiptsScheduledRequest = PixExtractFactory.pixReceiptsScheduledRequest

    private val resultSuccessPixExtract = CieloDataResult.Success(PixExtractFactory.pixExtract)
    private val resultSuccessPixReceiptsScheduled = CieloDataResult.Success(PixExtractFactory.pixExtractScheduling)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    @Test
    fun `it should fetch extract calling remote data source only once`() =
        runBlocking {
            coEvery {
                remoteDataSource.getExtract(any())
            } returns resultSuccessPixExtract

            repository.getExtract(pixExtractRequest)

            coVerify(exactly = ONE) { remoteDataSource.getExtract(any()) }
        }

    @Test
    fun `it should fetch receipts scheduled calling remote date source only once`() =
        runBlocking {
            coEvery {
                remoteDataSource.getReceiptsScheduled(any())
            } returns resultSuccessPixReceiptsScheduled

            repository.getReceiptsScheduled(pixReceiptsScheduledRequest)

            coVerify(exactly = ONE) { remoteDataSource.getReceiptsScheduled(any()) }
        }

    @Test
    fun `it should return the statement transactions`() =
        runBlocking {
            coEvery {
                remoteDataSource.getExtract(any())
            } returns resultSuccessPixExtract

            val result = repository.getExtract(pixExtractRequest)

            assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

            val cieloDataResult = result as CieloDataResult.Success

            val actualExtract = cieloDataResult.value
            val expectedExtract = resultSuccessPixExtract.value

            assertEquals(expectedExtract, actualExtract)
        }

    @Test
    fun `it should return a network error when load extract`() =
        runBlocking {
            coEvery {
                remoteDataSource.getExtract(any())
            } returns resultError

            val result = repository.getExtract(pixExtractRequest)

            assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)

            assertEquals(resultError, result)
        }

    @Test
    fun `it should return a empty error when load extract`() =
        runBlocking {
            coEvery {
                remoteDataSource.getExtract(any())
            } returns resultEmpty

            val result = repository.getExtract(pixExtractRequest)

            assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)

            assertEquals(resultEmpty, result)
        }

    @Test
    fun `it should return the scheduling transactions`() =
        runBlocking {
            coEvery {
                remoteDataSource.getReceiptsScheduled(any())
            } returns resultSuccessPixReceiptsScheduled

            val result = repository.getReceiptsScheduled(pixReceiptsScheduledRequest)

            assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

            val cieloDataResult = result as CieloDataResult.Success

            val actualExtract = cieloDataResult.value
            val expectedExtract = resultSuccessPixReceiptsScheduled.value

            assertEquals(expectedExtract, actualExtract)
        }

    @Test
    fun `it should return a network error when load receipts scheduled`() =
        runBlocking {
            coEvery {
                remoteDataSource.getReceiptsScheduled(any())
            } returns resultError

            val result = repository.getReceiptsScheduled(pixReceiptsScheduledRequest)

            assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)

            assertEquals(resultError, result)
        }

    @Test
    fun `it should return a empty error when load receipts scheduled`() =
        runBlocking {
            coEvery {
                remoteDataSource.getReceiptsScheduled(any())
            } returns resultEmpty

            val result = repository.getReceiptsScheduled(pixReceiptsScheduledRequest)

            assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)

            assertEquals(resultEmpty, result)
        }
}
