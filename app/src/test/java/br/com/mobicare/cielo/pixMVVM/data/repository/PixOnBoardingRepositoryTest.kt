package br.com.mobicare.cielo.pixMVVM.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixOnBoardingRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.utils.PixOnBoardingFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PixOnBoardingRepositoryTest {
    private val remoteDataSource = mockk<PixOnBoardingRemoteDataSource>()

    private val onBoardingFulfillment = PixOnBoardingFactory.onBoardingFulfillmentEntity
    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val onBoardingFulfillmentSuccessResult = CieloDataResult.Success(onBoardingFulfillment)
    private val repository = PixOnBoardingRepositoryImpl(remoteDataSource)

    @Test
    fun `it should call method getOnBoardingFulfillment of remote data source only once`() = runBlocking {
        // given
        coEvery { remoteDataSource.getOnBoardingFulfillment() } returns onBoardingFulfillmentSuccessResult

        // when
        repository.getOnBoardingFulfillment()

        // then
        coVerify(exactly = 1) { remoteDataSource.getOnBoardingFulfillment() }
    }

    @Test
    fun `it should return the correct OnBoardingFulfillment entity on getOnBoardingFulfillment call successfully`(): Unit = runBlocking {
        // given
        coEvery { remoteDataSource.getOnBoardingFulfillment() } returns onBoardingFulfillmentSuccessResult

        // when
        val result = repository.getOnBoardingFulfillment()

        // then
        assertEquals(onBoardingFulfillmentSuccessResult, result)

        val actual = (result as CieloDataResult.Success).value
        val expected = onBoardingFulfillment

        assertEquals(expected.isEligible, actual.isEligible)
        assertEquals(expected.profileType, actual.profileType)
        assertEquals(expected.isSettlementActive, actual.isSettlementActive)
        assertEquals(expected.isEnabled, actual.isEnabled)
        assertEquals(expected.status, actual.status)
        assertEquals(expected.blockType, actual.blockType)
        actual.pixAccount?.let { actualPixAccount ->
            expected.pixAccount?.let { expectedPixAccount ->
                assertEquals(expectedPixAccount.pixId, actualPixAccount.pixId)
                assertEquals(expectedPixAccount.bank, actualPixAccount.bank)
                assertEquals(expectedPixAccount.agency, actualPixAccount.agency)
                assertEquals(expectedPixAccount.account, actualPixAccount.account)
                assertEquals(expectedPixAccount.accountDigit, actualPixAccount.accountDigit)
                assertEquals(expectedPixAccount.dockAccountId, actualPixAccount.dockAccountId)
                assertEquals(expectedPixAccount.isCielo, actualPixAccount.isCielo)
                assertEquals(expectedPixAccount.bankName, actualPixAccount.bankName)
            }
        }
    }

    @Test
    fun `it should return a network error on getOnBoardingFulfillment call`() = runBlocking {
        // given
        coEvery { remoteDataSource.getOnBoardingFulfillment() } returns errorResult

        // when
        val result = repository.getOnBoardingFulfillment()

        // then
        assertEquals(errorResult, result)
    }

}