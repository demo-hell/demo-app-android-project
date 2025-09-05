package br.com.mobicare.cielo.superlink.data.repository

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.superlink.domain.datasource.SuperLinkDataSource
import br.com.mobicare.cielo.superlink.utils.SuperLinkFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class SuperLinkRepositoryTest {
    private val remoteDataSource = mockk<SuperLinkDataSource>()

    private val paymentLinkResponse = SuperLinkFactory.paymentLinkResponseForActiveCheck
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultSuccess = CieloDataResult.Success(paymentLinkResponse)
    private val repository = SuperLinkRepositoryImpl(remoteDataSource)

    @Test
    fun `it should call method isPaymentLinkActive of remote data source only once`() = runBlocking {
        // given
        coEvery { remoteDataSource.isPaymentLinkActive() } returns resultSuccess

        // when
        repository.isPaymentLinkActive()

        // then
        coVerify(exactly = 1) { remoteDataSource.isPaymentLinkActive() }
    }

    @Test
    fun `it should return a list of PaymentLinkResponse on isPaymentLinkActive call successfully`() = runBlocking {
        // given
        coEvery { remoteDataSource.isPaymentLinkActive() } returns resultSuccess

        // when
        val result = repository.isPaymentLinkActive()

        // then
        assertEquals(resultSuccess, result)
    }

    @Test
    fun `it should return the correct list size of PaymentLinkResponse on isPaymentLinkActive call`() = runBlocking {
        // given
        coEvery { remoteDataSource.isPaymentLinkActive() } returns resultSuccess

        // when
        val result = repository.isPaymentLinkActive()

        // then
        assertEquals(resultSuccess, result)

        (result as CieloDataResult.Success).value.run {
            assertEquals(resultSuccess.value.items?.size, items?.size)
        }
    }

    @Test
    fun `it should return a network error on isPaymentLinkActive call`() = runBlocking {
        // given
        coEvery { remoteDataSource.isPaymentLinkActive() } returns resultError

        // when
        val result = repository.isPaymentLinkActive()

        // then
        assertEquals(result, resultError)
    }
}