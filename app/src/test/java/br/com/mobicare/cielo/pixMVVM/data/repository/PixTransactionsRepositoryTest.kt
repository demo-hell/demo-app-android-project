package br.com.mobicare.cielo.pixMVVM.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.asSuccess
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixTransactionsRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.model.PixSchedulingDetail
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferBank
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferResult
import br.com.mobicare.cielo.pixMVVM.utils.PixTransactionsFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PixTransactionsRepositoryTest {
    private val remoteDataSource = mockk<PixTransactionsRemoteDataSource>()

    private val params = PixTransactionsFactory.MockedParams
    private val transferDetailEntity = PixTransactionsFactory.TransferDetail.entity
    private val transferResultEntity = PixTransactionsFactory.TransferResult.entity
    private val schedulingDetailResultEntity = PixTransactionsFactory.SchedulingDetail.entity
    private val transferBanksEntity = PixTransactionsFactory.TransferBanks.entity
    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

    private val repository = PixTransactionsRepositoryImpl(remoteDataSource)

    // =============================
    // getTransferDetails
    // =============================

    private suspend fun mockAndRunGetTransferDetailsTest(result: CieloDataResult<PixTransferDetail>): CieloDataResult<PixTransferDetail> {
        // given
        coEvery { remoteDataSource.getTransferDetails(any(), any()) } returns result

        // when
        return repository.getTransferDetails(params.endToEndId, params.transactionCode)
    }

    @Test
    fun `it should call method getTransferDetails of remote data source only once`() =
        runBlocking {
            mockAndRunGetTransferDetailsTest(CieloDataResult.Success(transferDetailEntity))

            // then
            coVerify(exactly = 1) { remoteDataSource.getTransferDetails(any(), any()) }
        }

    @Test
    fun `it should return the correct PixTransferDetail entity on getTransferDetails call successfully`() =
        runBlocking {
            val expectedResult = CieloDataResult.Success(transferDetailEntity)
            val result = mockAndRunGetTransferDetailsTest(expectedResult)

            // then
            assertEquals(expectedResult, result)

            val actual = result.asSuccess.value
            val expected = transferDetailEntity

            assertEquals(expected, actual)
        }

    @Test
    fun `it should return a network error on getTransferDetails call`() =
        runBlocking {
            val result = mockAndRunGetTransferDetailsTest(errorResult)

            // then
            assertEquals(errorResult, result)
        }

    // =============================
    // transferWithKey
    // =============================

    private suspend fun mockAndRunTransferWithKeyTest(result: CieloDataResult<PixTransferResult>): CieloDataResult<PixTransferResult> {
        // given
        coEvery { remoteDataSource.transferWithKey(any(), any()) } returns result

        // when
        return repository.transferWithKey(params.otpCode, params.transferKeyRequest)
    }

    @Test
    fun `it should call method transferWithKey of remote data source only once`() =
        runBlocking {
            mockAndRunTransferWithKeyTest(CieloDataResult.Success(transferResultEntity))

            // then
            coVerify(exactly = 1) { remoteDataSource.transferWithKey(any(), any()) }
        }

    @Test
    fun `it should return the correct PixTransferResult entity on transferWithKey call successfully`() =
        runBlocking {
            val expectedResult = CieloDataResult.Success(transferResultEntity)
            val result = mockAndRunTransferWithKeyTest(expectedResult)

            // then
            assertEquals(expectedResult, result)

            val actual = result.asSuccess.value
            val expected = transferResultEntity

            assertEquals(expected, actual)
        }

    @Test
    fun `it should return a network error on transferWithKey call`() =
        runBlocking {
            val result = mockAndRunTransferWithKeyTest(errorResult)

            // then
            assertEquals(errorResult, result)
        }

    // =============================
    // transferToBankAccount
    // =============================

    private suspend fun mockAndRunTransferToBankAccountTest(
        result: CieloDataResult<PixTransferResult>,
    ): CieloDataResult<PixTransferResult> {
        // given
        coEvery { remoteDataSource.transferToBankAccount(any(), any()) } returns result

        // when
        return repository.transferToBankAccount(params.otpCode, params.transferAccountBankRequest)
    }

    @Test
    fun `it should call method transferToBankAccount of remote data source only once`() =
        runBlocking {
            mockAndRunTransferToBankAccountTest(CieloDataResult.Success(transferResultEntity))

            // then
            coVerify(exactly = 1) { remoteDataSource.transferToBankAccount(any(), any()) }
        }

    @Test
    fun `it should return the correct PixTransferResult entity on transferToBankAccount call successfully`() =
        runBlocking {
            val expectedResult = CieloDataResult.Success(transferResultEntity)
            val result = mockAndRunTransferToBankAccountTest(expectedResult)

            // then
            assertEquals(expectedResult, result)

            val actual = result.asSuccess.value
            val expected = transferResultEntity

            assertEquals(expected, actual)
        }

    @Test
    fun `it should return a network error on transferToBankAccount call`() =
        runBlocking {
            val result = mockAndRunTransferToBankAccountTest(errorResult)

            // then
            assertEquals(errorResult, result)
        }

    // =============================
    // getTransferBanks
    // =============================

    private suspend fun mockAndRunGetTransferBanksTest(
        result: CieloDataResult<List<PixTransferBank>>,
    ): CieloDataResult<List<PixTransferBank>> {
        // given
        coEvery { remoteDataSource.getTransferBanks() } returns result

        // when
        return repository.getTransferBanks()
    }

    @Test
    fun `it should call method getTransferBanks of remote data source only once`() =
        runBlocking {
            mockAndRunGetTransferBanksTest(CieloDataResult.Success(transferBanksEntity))

            // then
            coVerify(exactly = 1) { remoteDataSource.getTransferBanks() }
        }

    @Test
    fun `it should return the correct list of PìxBankResponse on getTransferBanks call successfully`() =
        runBlocking {
            val expectedResult = CieloDataResult.Success(transferBanksEntity)
            val result = mockAndRunGetTransferBanksTest(expectedResult)

            // then
            assertEquals(expectedResult, result)

            val actual = result.asSuccess.value

            assertEquals(transferBanksEntity, actual)
        }

    @Test
    fun `it should return a network error on getTransferBanks call`() =
        runBlocking {
            val result = mockAndRunGetTransferBanksTest(errorResult)

            // then
            assertEquals(errorResult, result)
        }

    // =============================
    // cancelTransferSchedule
    // =============================

    private suspend fun mockAndRunCancelTransferScheduleTest(
        result: CieloDataResult<PixTransferResult>,
    ): CieloDataResult<PixTransferResult> {
        // given
        coEvery { remoteDataSource.cancelTransferSchedule(any(), any()) } returns result

        // when
        return repository.cancelTransferSchedule(params.otpCode, params.transferScheduleCancelRequest)
    }

    @Test
    fun `it should call method cancelTransferSchedule of remote data source only once`() =
        runBlocking {
            mockAndRunCancelTransferScheduleTest(CieloDataResult.Success(transferResultEntity))

            // then
            coVerify(exactly = 1) { remoteDataSource.cancelTransferSchedule(any(), any()) }
        }

    @Test
    fun `it should return the correct PixTransferResult on cancelTransferSchedule call successfully`() =
        runBlocking {
            val expectedResult = CieloDataResult.Success(transferResultEntity)
            val result = mockAndRunCancelTransferScheduleTest(expectedResult)

            // then
            assertEquals(expectedResult, result)

            val actual = result.asSuccess.value
            val expected = transferResultEntity

            assertEquals(expected, actual)
        }

    @Test
    fun `it should return a network error on cancelTransferSchedule call`() =
        runBlocking {
            val result = mockAndRunCancelTransferScheduleTest(errorResult)

            // then
            assertEquals(errorResult, result)
        }

    // =============================
    // getTransferScheduleDetail
    // =============================

    private suspend fun mockAndRunGetTransferScheduleDetailTest(
        result: CieloDataResult<PixSchedulingDetail>,
    ): CieloDataResult<PixSchedulingDetail> {
        // given
        coEvery { remoteDataSource.getTransferScheduleDetail(any()) } returns result

        // when
        return repository.getTransferScheduleDetail(params.schedulingCode)
    }

    @Test
    fun `it should call method getTransferScheduleDetail of remote data source only once`() =
        runBlocking {
            mockAndRunGetTransferScheduleDetailTest(CieloDataResult.Success(schedulingDetailResultEntity))

            // then
            coVerify(exactly = 1) { remoteDataSource.getTransferScheduleDetail(any()) }
        }

    @Test
    fun `it should return the correct PixSchedulingDetail on getTransferScheduleDetail call successfully`() =
        runBlocking {
            val expectedResult = CieloDataResult.Success(schedulingDetailResultEntity)
            val result = mockAndRunGetTransferScheduleDetailTest(expectedResult)

            // then
            assertEquals(expectedResult, result)

            val actual = result.asSuccess.value
            val expected = schedulingDetailResultEntity

            assertEquals(expected, actual)
        }

    @Test
    fun `it should return a network error on getTransferScheduleDetail call`() =
        runBlocking {
            val result = mockAndRunGetTransferScheduleDetailTest(errorResult)

            // then
            assertEquals(errorResult, result)
        }

    // =============================
    // transferScheduledBalance
    // =============================

    private suspend fun mockAndRunTransferScheduledBalanceTest(result: CieloDataResult<Unit>): CieloDataResult<Unit> {
        // given
        coEvery { remoteDataSource.transferScheduledBalance(any()) } returns result

        // when
        return repository.transferScheduledBalance(params.otpCode)
    }

    @Test
    fun `it should call method transferScheduledBalance of remote data source only once`() =
        runBlocking {
            mockAndRunTransferScheduledBalanceTest(CieloDataResult.Success(Unit))

            // then
            coVerify(exactly = 1) { remoteDataSource.transferScheduledBalance(any()) }
        }

    @Test
    fun `it should return the correct result on transferScheduledBalance call successfully`() =
        runBlocking {
            val expectedResult = CieloDataResult.Success(Unit)
            val result = mockAndRunTransferScheduledBalanceTest(expectedResult)

            // then
            assertEquals(expectedResult, result)
        }

    @Test
    fun `it should return a network error on transferScheduledBalance call`() =
        runBlocking {
            val result = mockAndRunTransferScheduledBalanceTest(errorResult)

            // then
            assertEquals(errorResult, result)
        }
}
