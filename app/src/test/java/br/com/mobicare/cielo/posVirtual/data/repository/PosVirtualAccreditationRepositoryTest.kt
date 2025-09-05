package br.com.mobicare.cielo.posVirtual.data.repository

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pix.constants.DEFAULT_OTP
import br.com.mobicare.cielo.posVirtual.data.dataSource.PosVirtualAccreditationDataSource
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualFactory
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualFactory.offerID
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualFactory.solutions
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PosVirtualAccreditationRepositoryTest {

    private val remoteDataSource = mockk<PosVirtualAccreditationDataSource>()

    private val posVirtualCreateOrderRequest = PosVirtualFactory.posVirtualCreateOrderRequest

    private val resultPosVirtualGetOffersSuccess =
        CieloDataResult.Success(PosVirtualFactory.OfferResponseFactory.offerResponse)
    private val resultPosVirtualGetBrandsSuccess = CieloDataResult.Success(solutions)
    private val resultPosVirtualCreateOrderSuccess = CieloDataResult.Success(offerID)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    private val repository = PosVirtualAccreditationRepositoryImpl(remoteDataSource)

    @Test
    fun `it should fetch Offers calling remote data source only once`() = runBlocking {
        coEvery {
            remoteDataSource.getOffers()
        } returns resultPosVirtualGetOffersSuccess

        repository.getOffers()

        coVerify(exactly = ONE) { remoteDataSource.getOffers() }
    }

    @Test
    fun `it should fetch Solutions calling remote data source only once`() = runBlocking {
        coEvery {
            remoteDataSource.getBrands()
        } returns if (solutions != null) CieloDataResult.Success(solutions) else resultError

        repository.getBrands()

        coVerify(exactly = ONE) { remoteDataSource.getBrands() }
    }

    @Test
    fun `it should fetch create Order calling remote data source only once`() = runBlocking {
        coEvery {
            remoteDataSource.postCreateOrder(
                any(),
                any()
            )
        } returns resultPosVirtualCreateOrderSuccess

        repository.postCreateOrder(DEFAULT_OTP, posVirtualCreateOrderRequest)

        coVerify(exactly = ONE) { remoteDataSource.postCreateOrder(any(), any()) }
    }

    @Test
    fun `it should return the Offers`() = runBlocking {
        coEvery {
            remoteDataSource.getOffers()
        } returns resultPosVirtualGetOffersSuccess

        val result = repository.getOffers()

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
    fun `it should return the Solutions`() = runBlocking {
        coEvery {
            remoteDataSource.getBrands()
        } returns if (solutions != null) CieloDataResult.Success(solutions) else resultError

        val result = repository.getBrands()

        assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

        val resultSuccess = result as CieloDataResult.Success
        val actualData = resultSuccess.value.solutions?.first()?.banks
        val expectedData = resultPosVirtualGetBrandsSuccess.value?.solutions?.first()?.banks

        assertEquals(expectedData?.get(ZERO)?.name, actualData?.get(ZERO)?.name)
        assertEquals(expectedData?.get(ZERO)?.code, actualData?.get(ZERO)?.code)
        assertEquals(expectedData?.get(ZERO)?.agencyNumber, actualData?.get(ZERO)?.agencyNumber)
        assertEquals(expectedData?.get(ZERO)?.agencyDigit, actualData?.get(ZERO)?.agencyDigit)
        assertEquals(expectedData?.get(ZERO)?.agencyExt, actualData?.get(ZERO)?.agencyExt)
        assertEquals(expectedData?.get(ZERO)?.accountNumber, actualData?.get(ZERO)?.accountNumber)
        assertEquals(expectedData?.get(ZERO)?.accountDigit, actualData?.get(ZERO)?.accountDigit)
        assertEquals(expectedData?.get(ZERO)?.accountExt, actualData?.get(ZERO)?.accountExt)

        assertEquals(expectedData?.get(ONE)?.name, actualData?.get(ONE)?.name)
        assertEquals(expectedData?.get(ONE)?.code, actualData?.get(ONE)?.code)
        assertEquals(expectedData?.get(ONE)?.agencyNumber, actualData?.get(ONE)?.agencyNumber)
        assertEquals(expectedData?.get(ONE)?.agencyDigit, actualData?.get(ONE)?.agencyDigit)
        assertEquals(expectedData?.get(ONE)?.agencyExt, actualData?.get(ONE)?.agencyExt)
        assertEquals(expectedData?.get(ONE)?.accountNumber, actualData?.get(ONE)?.accountNumber)
        assertEquals(expectedData?.get(ONE)?.accountDigit, actualData?.get(ONE)?.accountDigit)
        assertEquals(expectedData?.get(ONE)?.accountExt, actualData?.get(ONE)?.accountExt)
    }

    @Test
    fun `it should return the create order response`() = runBlocking {
        coEvery {
            remoteDataSource.postCreateOrder(
                any(),
                any()
            )
        } returns resultPosVirtualCreateOrderSuccess

        val result = repository.postCreateOrder(DEFAULT_OTP, posVirtualCreateOrderRequest)

        assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

        val resultSuccess = result as CieloDataResult.Success
        val actualData = resultSuccess.value
        val expectedData = resultPosVirtualCreateOrderSuccess.value

        assertEquals(expectedData, actualData)
    }

    @Test
    fun `it should return a network error when get Offers`() = runBlocking {
        coEvery {
            remoteDataSource.getOffers()
        } returns resultError

        val result = repository.getOffers()

        assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)

        assertEquals(resultError, result)
    }

    @Test
    fun `it should return a network error when get Solutions`() = runBlocking {
        coEvery {
            remoteDataSource.getBrands()
        } returns resultError

        val result = repository.getBrands()

        assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)

        assertEquals(resultError, result)
    }

    @Test
    fun `it should return a network error when create order`() = runBlocking {
        coEvery {
            remoteDataSource.postCreateOrder(
                any(),
                any()
            )
        } returns resultError

        val result = repository.postCreateOrder(DEFAULT_OTP, posVirtualCreateOrderRequest)

        assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)

        assertEquals(resultError, result)
    }

    @Test
    fun `it should return a empty error when get Offers`() = runBlocking {
        coEvery {
            remoteDataSource.getOffers()
        } returns resultEmpty

        val result = repository.getOffers()

        assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)

        assertEquals(resultEmpty, result)
    }

    @Test
    fun `it should return a empty error when get Solutions`() = runBlocking {
        coEvery {
            remoteDataSource.getBrands()
        } returns resultEmpty

        val result = repository.getBrands()

        assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)

        assertEquals(resultEmpty, result)
    }

    @Test
    fun `it should return a empty error when create order`() = runBlocking {
        coEvery {
            remoteDataSource.postCreateOrder(
                any(),
                any()
            )
        } returns resultEmpty

        val result = repository.postCreateOrder(DEFAULT_OTP, posVirtualCreateOrderRequest)

        assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)

        assertEquals(resultEmpty, result)
    }

}