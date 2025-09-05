package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixInfringementRepository
import br.com.mobicare.cielo.pixMVVM.utils.PixInfringementFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetPixEligibilityInfringementUseCaseTest {

    private val repository = mockk<PixInfringementRepository>()
    private val getPixEligibilityInfringementUseCase = GetPixEligibilityInfringementUseCase(repository)

    private val resultSuccessGetInfringement =
        CieloDataResult.Success(PixInfringementFactory.pixGetInfringementResponse)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    @Test
    fun `it should fetch infringement calling remote data source only once`() = runBlocking {
        coEvery {
            repository.getInfringement(any())
        } returns resultSuccessGetInfringement

        getPixEligibilityInfringementUseCase(PixInfringementFactory.idEndToEnd)

        coVerify(exactly = ONE) { repository.getInfringement(any()) }
    }

    @Test
    fun `it should return the infringement object`() = runBlocking {
        coEvery {
            repository.getInfringement(any())
        } returns resultSuccessGetInfringement

        val result = getPixEligibilityInfringementUseCase(PixInfringementFactory.idEndToEnd)

        assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

        val dataResult = result as CieloDataResult.Success

        val actualInfringement = dataResult.value
        val expectedInfringement = resultSuccessGetInfringement.value

        assertEquals(expectedInfringement.isEligible, actualInfringement.isEligible)
        assertEquals(expectedInfringement.details, actualInfringement.details)
        assertEquals(expectedInfringement.merchantId, actualInfringement.merchantId)
        assertEquals(expectedInfringement.idEndToEnd, actualInfringement.idEndToEnd)
        assertEquals(expectedInfringement.amount, actualInfringement.amount)
        assertEquals(expectedInfringement.reasonType, actualInfringement.reasonType)
        assertEquals(expectedInfringement.transactionDate, actualInfringement.transactionDate)

        val actualPayee = actualInfringement.payee
        val expectedPayee = expectedInfringement.payee

        assertEquals(expectedPayee?.name, actualPayee?.name)
        assertEquals(expectedPayee?.document, actualPayee?.document)
        assertEquals(expectedPayee?.key, actualPayee?.key)

        val actualBank = actualPayee?.bank
        val expectedBank = expectedPayee?.bank

        assertEquals(expectedBank?.iSPB, actualBank?.iSPB)
        assertEquals(expectedBank?.name, actualBank?.name)
        assertEquals(expectedBank?.accountType, actualBank?.accountType)
        assertEquals(expectedBank?.accountNumber, actualBank?.accountNumber)
        assertEquals(expectedBank?.branchNumber, actualBank?.branchNumber)

        assertEquals(expectedInfringement.situations?.size, actualInfringement.situations?.size)

        for (i in ZERO until expectedInfringement.situations?.size!!) {
            val actualSituation = actualInfringement.situations?.get(i)
            val expectedSituation = expectedInfringement.situations?.get(i)

            assertEquals(expectedSituation?.type, actualSituation?.type)
            assertEquals(expectedSituation?.description, actualSituation?.description)
        }
    }

    @Test
    fun `it should return a network error when load infringement`() = runBlocking {
        coEvery {
            repository.getInfringement(any())
        } returns resultError

        val result = getPixEligibilityInfringementUseCase(PixInfringementFactory.idEndToEnd)

        assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)

        assertEquals(resultError, result)
    }

    @Test
    fun `it should return a empty error when load infringement`() = runBlocking {
        coEvery {
            repository.getInfringement(any())
        } returns resultEmpty

        val result = getPixEligibilityInfringementUseCase(PixInfringementFactory.idEndToEnd)

        assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)

        assertEquals(resultEmpty, result)
    }

}