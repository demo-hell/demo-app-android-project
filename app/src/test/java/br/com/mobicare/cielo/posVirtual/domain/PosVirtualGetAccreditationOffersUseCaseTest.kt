package br.com.mobicare.cielo.posVirtual.domain

import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.Text.EMPTY
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.posVirtual.domain.repository.PosVirtualAccreditationRepository
import br.com.mobicare.cielo.posVirtual.domain.useCase.GetPosVirtualAccreditationOffersUseCase
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PosVirtualGetAccreditationOffersUseCaseTest {

    private val repository = mockk<PosVirtualAccreditationRepository>()

    private val resultPosVirtualGetOffersSuccess =
        CieloDataResult.Success(PosVirtualFactory.OfferResponseFactory.offerResponse)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    private val getPosVirtualAccreditationOffersUseCase =
        GetPosVirtualAccreditationOffersUseCase(repository)

    @Test
    fun `it should fetch Offers calling remote data source only once`() = runBlocking {
        coEvery {
            repository.getOffers()
        } returns resultPosVirtualGetOffersSuccess

        getPosVirtualAccreditationOffersUseCase()

        coVerify(exactly = ONE) { repository.getOffers() }
    }

    @Test
    fun `it should return the Offers`() = runBlocking {
        coEvery {
            repository.getOffers()
        } returns resultPosVirtualGetOffersSuccess

        val result = getPosVirtualAccreditationOffersUseCase()

        assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

        val resultSuccess = result as CieloDataResult.Success
        val actualDataProduct = resultSuccess.value.offer?.products?.first()
        val actualDataTerms = resultSuccess.value.offer?.agreements?.first()?.terms
        val actualDataCondition = actualDataProduct?.brands?.first()?.conditions?.first()
        val actualDataInstallments = actualDataCondition?.installments?.first()
        val expectedDataProduct = resultPosVirtualGetOffersSuccess.value.offer?.products?.first()
        val expectedDataTerms =
            resultPosVirtualGetOffersSuccess.value.offer?.agreements?.first()?.terms
        val expectedDataCondition = expectedDataProduct?.brands?.first()?.conditions?.first()
        val expectedDataInstallments = expectedDataCondition?.installments?.first()

        assertEquals(expectedDataProduct?.reference, actualDataProduct?.reference)
        assertEquals(
            expectedDataProduct?.brands?.first()?.code,
            actualDataProduct?.brands?.first()?.code
        )
        assertEquals(expectedDataCondition?.mdr, actualDataCondition?.mdr)
        assertEquals(expectedDataCondition?.rateContractedRR, actualDataCondition?.rateContractedRR)
        assertEquals(
            expectedDataCondition?.flexibleTermPaymentMDR,
            actualDataCondition?.flexibleTermPaymentMDR
        )
        assertEquals(expectedDataInstallments?.installment, actualDataInstallments?.installment)
        assertEquals(expectedDataInstallments?.mdr, actualDataInstallments?.mdr)
        assertEquals(
            expectedDataInstallments?.rateContractedRR,
            actualDataInstallments?.rateContractedRR
        )
        assertEquals(
            expectedDataCondition?.installments?.first()?.flexibleTermPaymentMDR,
            actualDataInstallments?.flexibleTermPaymentMDR
        )

        assertEquals(
            expectedDataTerms?.get(ZERO)?.description,
            actualDataTerms?.get(ZERO)?.description
        )

        assertEquals(expectedDataTerms?.get(ZERO)?.version, actualDataTerms?.get(ZERO)?.version)
        assertEquals(expectedDataTerms?.get(ZERO)?.url, actualDataTerms?.get(ZERO)?.url)

        assertEquals(
            expectedDataTerms?.get(ONE)?.description,
            actualDataTerms?.get(ONE)?.description
        )
        assertEquals(expectedDataTerms?.get(ONE)?.version, actualDataTerms?.get(ONE)?.version)
        assertEquals(expectedDataTerms?.get(ONE)?.url, actualDataTerms?.get(ONE)?.url)

        assertEquals(
            expectedDataTerms?.get(TWO)?.description,
            actualDataTerms?.get(TWO)?.description
        )
        assertEquals(expectedDataTerms?.get(TWO)?.version, actualDataTerms?.get(TWO)?.version)
        assertEquals(expectedDataTerms?.get(TWO)?.url, actualDataTerms?.get(TWO)?.url)
    }

    @Test
    fun `it should return a network error when get Offers`() = runBlocking {
        coEvery {
            repository.getOffers()
        } returns resultError

        val result = getPosVirtualAccreditationOffersUseCase()

        assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)

        assertEquals(resultError, result)
    }

    @Test
    fun `it should return a empty error when get Offers`() = runBlocking {
        coEvery {
            repository.getOffers()
        } returns resultEmpty

        val result = getPosVirtualAccreditationOffersUseCase()

        assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)

        assertEquals(resultEmpty, result)
    }

}