package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixExtractRepository
import br.com.mobicare.cielo.pixMVVM.utils.PixExtractFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetPixReceiptsScheduledUseCaseTest {
    private val repository = mockk<PixExtractRepository>()
    private val getPixReceiptsScheduledUseCase = GetPixReceiptsScheduledUseCase(repository)

    private val resultSuccess = CieloDataResult.Success(PixExtractFactory.pixExtractScheduling)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    @Test
    fun `it should fetch receipts scheduled calling remote date source only once`() =
        runBlocking {
            coEvery {
                repository.getReceiptsScheduled(any())
            } returns resultSuccess

            getPixReceiptsScheduledUseCase(GetPixReceiptsScheduledUseCase.Params())

            coVerify(exactly = ONE) { repository.getReceiptsScheduled(any()) }
        }

    @Test
    fun `it should return the scheduling transactions`() =
        runBlocking {
            coEvery {
                repository.getReceiptsScheduled(any())
            } returns resultSuccess

            val result = getPixReceiptsScheduledUseCase(GetPixReceiptsScheduledUseCase.Params())

            assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

            val cieloDataResult = result as CieloDataResult.Success

            val actualExtract = cieloDataResult.value
            val expectedExtract = resultSuccess.value

            assertEquals(expectedExtract, actualExtract)
        }

    @Test
    fun `it should return a network error when load receipts scheduled`() =
        runBlocking {
            coEvery {
                repository.getReceiptsScheduled(any())
            } returns resultError

            val result = getPixReceiptsScheduledUseCase(GetPixReceiptsScheduledUseCase.Params())

            assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)

            assertEquals(resultError, result)
        }

    @Test
    fun `it should return a empty error when load receipts scheduled`() =
        runBlocking {
            coEvery {
                repository.getReceiptsScheduled(any())
            } returns resultEmpty

            val result = getPixReceiptsScheduledUseCase(GetPixReceiptsScheduledUseCase.Params())

            assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)

            assertEquals(resultEmpty, result)
        }
}
