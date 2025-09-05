package br.com.mobicare.cielo.posVirtual.domain

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.posVirtual.domain.repository.PosVirtualAccreditationRepository
import br.com.mobicare.cielo.posVirtual.domain.useCase.GetPosVirtualAccreditationBanksUseCase
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualFactory
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualFactory.solutions
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockito_kotlin.any
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test

class PosVirtualGetAccreditationBanksUseCaseTest {

    private val repository = mockk<PosVirtualAccreditationRepository>()

    private val resultPosVirtualGetBrandsSuccess =
        CieloDataResult.Success(solutions)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    private val getPosVirtualAccreditationBanksUseCase = GetPosVirtualAccreditationBanksUseCase(repository)

    @Test
    fun `it should fetch Solutions calling repository only once`() = runBlocking {
        coEvery {
            repository.getBrands()
        } returns if (solutions != null) CieloDataResult.Success(solutions) else resultError

        getPosVirtualAccreditationBanksUseCase()

        coVerify(exactly = ONE) { repository.getBrands() }
    }

    @Test
    fun `it should return the Solutions`() = runBlocking {
        coEvery {
            repository.getBrands()
        } returns if (solutions != null) CieloDataResult.Success(solutions) else resultError

        val result = getPosVirtualAccreditationBanksUseCase()

        assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

        val resultSuccess = result as CieloDataResult.Success
        val actualData = resultSuccess.value
        val expectedData = resultPosVirtualGetBrandsSuccess.value?.solutions?.first()?.banks

        assertEquals(expectedData?.get(ZERO)?.name, actualData[ZERO].name)
        assertEquals(expectedData?.get(ZERO)?.code, actualData[ZERO].code)
        assertEquals(expectedData?.get(ZERO)?.agencyNumber, actualData[ZERO].agencyNumber)
        assertEquals(expectedData?.get(ZERO)?.agencyDigit, actualData[ZERO].agencyDigit)
        assertEquals(expectedData?.get(ZERO)?.agencyExt, actualData[ZERO].agencyExt)
        assertEquals(expectedData?.get(ZERO)?.accountNumber, actualData[ZERO].accountNumber)
        assertEquals(expectedData?.get(ZERO)?.accountDigit, actualData[ZERO].accountDigit)
        assertEquals(expectedData?.get(ZERO)?.accountExt, actualData[ZERO].accountExt)

        assertEquals(expectedData?.get(ONE)?.name, actualData[ONE].name)
        assertEquals(expectedData?.get(ONE)?.code, actualData[ONE].code)
        assertEquals(expectedData?.get(ONE)?.agencyNumber, actualData[ONE].agencyNumber)
        assertEquals(expectedData?.get(ONE)?.agencyDigit, actualData[ONE].agencyDigit)
        assertEquals(expectedData?.get(ONE)?.agencyExt, actualData[ONE].agencyExt)
        assertEquals(expectedData?.get(ONE)?.accountNumber, actualData[ONE].accountNumber)
        assertEquals(expectedData?.get(ONE)?.accountDigit, actualData[ONE].accountDigit)
        assertEquals(expectedData?.get(ONE)?.accountExt, actualData[ONE].accountExt)
    }

    @Test
    fun `it should return a network error when get Solutions`() = runBlocking {
        coEvery {
            repository.getBrands()
        } returns resultError

        val result = getPosVirtualAccreditationBanksUseCase()

        assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)

        assertEquals(result, resultError)
    }

    @Test
    fun `it should return a empty error when get Solutions`() = runBlocking {
        coEvery {
            repository.getBrands()
        } returns resultEmpty

        val result = getPosVirtualAccreditationBanksUseCase()

        assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)

        assertEquals(resultEmpty, result)
    }

}