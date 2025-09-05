package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
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

class GetPixExtractUseCaseTest {

    private val repository = mockk<PixExtractRepository>()
    private val getPixExtractUseCase = GetPixExtractUseCase(repository)

    private val pixExtractRequest = PixExtractFactory.pixExtractFilterRequest

    private val resultSuccess = CieloDataResult.Success(PixExtractFactory.pixExtract)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    @Test
    fun `it should fetch extract calling remote data source only once`() = runBlocking {
        coEvery {
            repository.getExtract(any())
        } returns resultSuccess

        getPixExtractUseCase(pixExtractRequest)

        coVerify(exactly = ONE) { repository.getExtract(any()) }
    }

    @Test
    fun `it should return the statement transactions`() = runBlocking {
        coEvery {
            repository.getExtract(any())
        } returns resultSuccess

        val result = getPixExtractUseCase(pixExtractRequest)

        assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

        val cieloDataResult = result as CieloDataResult.Success

        val actualExtract = cieloDataResult.value
        val expectedExtract = resultSuccess.value

        assertEquals(expectedExtract.totalItemsPage, actualExtract.totalItemsPage)
        assertEquals(expectedExtract.items.size, actualExtract.items.size)

        for (i in ZERO until expectedExtract.items.size) {
            val actualItem = actualExtract.items[i]
            val expectedItem = expectedExtract.items[i]

            assertEquals(expectedItem.title, actualItem.title)
            assertEquals(expectedItem.yearMonth, actualItem.yearMonth)
            assertEquals(expectedItem.receipts.size, actualItem.receipts.size)

            for (j in ZERO until expectedItem.receipts.size) {
                val actualReceipt = actualItem.receipts[j]
                val expectedReceipt = expectedItem.receipts[j]

                assertEquals(expectedReceipt.title, actualReceipt.title)
                assertEquals(expectedReceipt.amount, actualReceipt.amount)
                assertEquals(expectedReceipt.changeAmount, actualReceipt.changeAmount)
                assertEquals(expectedReceipt.date, actualReceipt.date)
                assertEquals(expectedReceipt.finalAmount, actualReceipt.finalAmount)
                assertEquals(expectedReceipt.idAccount, actualReceipt.idAccount)
                assertEquals(expectedReceipt.idAdjustment, actualReceipt.idAdjustment)
                assertEquals(expectedReceipt.idCorrelation, actualReceipt.idCorrelation)
                assertEquals(expectedReceipt.idEndToEnd, actualReceipt.idEndToEnd)
                assertEquals(expectedReceipt.idEndToEndOriginal, actualReceipt.idEndToEndOriginal)
                assertEquals(expectedReceipt.payeeName, actualReceipt.payeeName)
                assertEquals(expectedReceipt.payerAnswer, actualReceipt.payerAnswer)
                assertEquals(expectedReceipt.payerName, actualReceipt.payerName)
                assertEquals(expectedReceipt.pixType, actualReceipt.pixType)
                assertEquals(expectedReceipt.purchaseAmount, actualReceipt.purchaseAmount)
                assertEquals(expectedReceipt.reversalCode, actualReceipt.reversalCode)
                assertEquals(
                    expectedReceipt.reversalCodeDescription,
                    actualReceipt.reversalCodeDescription
                )
                assertEquals(expectedReceipt.tariffAmount, actualReceipt.tariffAmount)
                assertEquals(expectedReceipt.transactionCode, actualReceipt.transactionCode)
                assertEquals(expectedReceipt.transactionDate, actualReceipt.transactionDate)
                assertEquals(expectedReceipt.transactionStatus, actualReceipt.transactionStatus)
                assertEquals(expectedReceipt.transactionType, actualReceipt.transactionType)
                assertEquals(expectedReceipt.transferType, actualReceipt.transferType)
                assertEquals(expectedReceipt.type, actualReceipt.type)
                assertEquals(expectedReceipt.schedulingDate, actualReceipt.schedulingDate)
                assertEquals(expectedReceipt.schedulingCode, actualReceipt.schedulingCode)
                assertEquals(expectedReceipt.period, actualReceipt.period)
            }
        }
    }

    @Test
    fun `it should return a network error when load extract`() = runBlocking {
        coEvery {
            repository.getExtract(any())
        } returns resultError

        val result = getPixExtractUseCase(pixExtractRequest)

        assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)

        assertEquals(resultError, result)
    }

    @Test
    fun `it should return a empty error when load extract`() = runBlocking {
        coEvery {
            repository.getExtract(any())
        } returns resultEmpty

        val result = getPixExtractUseCase(pixExtractRequest)

        assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)

        assertEquals(resultEmpty, result)
    }

}