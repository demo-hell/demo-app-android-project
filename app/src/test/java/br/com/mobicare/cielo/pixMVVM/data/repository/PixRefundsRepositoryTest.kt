package br.com.mobicare.cielo.pixMVVM.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.asSuccess
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixRefundsRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundCreated
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundDetail
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundDetailFull
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundReceipts
import br.com.mobicare.cielo.pixMVVM.utils.PixRefundsFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PixRefundsRepositoryTest {
    private val remoteDataSource = mockk<PixRefundsRemoteDataSource>()

    private val params = PixRefundsFactory.MockedParams
    private val refundReceiptsEntity = PixRefundsFactory.RefundReceipts.entity
    private val refundDetailEntity = PixRefundsFactory.RefundDetail.entity
    private val refundDetailFullEntity = PixRefundsFactory.RefundDetailFull.entity
    private val refundCreatedEntity = PixRefundsFactory.RefundCreated.entity
    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

    private val repository = PixRefundsRepositoryImpl(remoteDataSource)

    // =============================
    // getReceipts
    // =============================

    private suspend fun mockAndRunGetReceiptsTest(
        result: CieloDataResult<PixRefundReceipts>
    ): CieloDataResult<PixRefundReceipts> {
        // given
        coEvery { remoteDataSource.getReceipts(any()) } returns result

        // when
        return repository.getReceipts(params.idEndToEndOriginal)
    }

    @Test
    fun `it should call method getReceipts of remote data source only once`() = runBlocking {
        mockAndRunGetReceiptsTest(CieloDataResult.Success(refundReceiptsEntity))

        // then
        coVerify(exactly = 1) { remoteDataSource.getReceipts(any()) }
    }

    @Test
    fun `it should return the correct PixRefundReceipts entity on getReceipts call successfully`() = runBlocking {
        val expectedResult = CieloDataResult.Success(refundReceiptsEntity)
        val result = mockAndRunGetReceiptsTest(expectedResult)

        // then
        assertEquals(expectedResult, result)

        val actual = result.asSuccess.value
        val expected = refundReceiptsEntity

        assertEquals(expected, actual)
    }

    @Test
    fun `it should return a network error on getReceipts call`() = runBlocking {
        val result = mockAndRunGetReceiptsTest(errorResult)

        // then
        assertEquals(errorResult, result)
    }

    // =============================
    // getDetail
    // =============================

    private suspend fun mockAndRunGetDetailTest(
        result: CieloDataResult<PixRefundDetail>
    ): CieloDataResult<PixRefundDetail> {
        // given
        coEvery { remoteDataSource.getDetail(any()) } returns result

        // when
        return repository.getDetail(params.transactionCode)
    }

    @Test
    fun `it should call method getDetail of remote data source only once`() = runBlocking {
        mockAndRunGetDetailTest(CieloDataResult.Success(refundDetailEntity))

        // then
        coVerify(exactly = 1) { remoteDataSource.getDetail(any()) }
    }

    @Test
    fun `it should return the correct PixRefundDetail entity on getDetail call successfully`() = runBlocking {
        val expectedResult = CieloDataResult.Success(refundDetailEntity)
        val result = mockAndRunGetDetailTest(expectedResult)

        // then
        assertEquals(expectedResult, result)

        val actual = result.asSuccess.value
        val expected = refundDetailEntity

        assertEquals(expected, actual)
    }

    @Test
    fun `it should return a network error on getDetail call`() = runBlocking {
        val result = mockAndRunGetDetailTest(errorResult)

        // then
        assertEquals(errorResult, result)
    }

    // =============================
    // getDetailFull
    // =============================

    private suspend fun mockAndRunGetDetailFullTest(
        result: CieloDataResult<PixRefundDetailFull>
    ): CieloDataResult<PixRefundDetailFull> {
        // given
        coEvery { remoteDataSource.getDetailFull(any()) } returns result

        // when
        return repository.getDetailFull(params.transactionCode)
    }

    @Test
    fun `it should call method getDetailFull of remote data source only once`() = runBlocking {
        mockAndRunGetDetailFullTest(CieloDataResult.Success(refundDetailFullEntity))

        // then
        coVerify(exactly = 1) { remoteDataSource.getDetailFull(any()) }
    }

    @Test
    fun `it should return the correct PixRefundDetailFull entity on getDetailFull call successfully`() = runBlocking {
        val expectedResult = CieloDataResult.Success(refundDetailFullEntity)
        val result = mockAndRunGetDetailFullTest(expectedResult)

        // then
        assertEquals(expectedResult, result)

        val actual = result.asSuccess.value
        val expected = refundDetailFullEntity

        assertEquals(expected, actual)
    }

    @Test
    fun `it should return a network error on getDetailFull call`() = runBlocking {
        val result = mockAndRunGetDetailFullTest(errorResult)

        // then
        assertEquals(errorResult, result)
    }

    // =============================
    // refund
    // =============================

    private suspend fun mockAndRunRefundTest(
        result: CieloDataResult<PixRefundCreated>
    ): CieloDataResult<PixRefundCreated> {
        // given
        coEvery { remoteDataSource.refund(any(), any()) } returns result

        // when
        return repository.refund(params.otpCode, params.refundCreateRequest)
    }

    @Test
    fun `it should call method refund of remote data source only once`() = runBlocking {
        mockAndRunRefundTest(CieloDataResult.Success(refundCreatedEntity))

        // then
        coVerify(exactly = 1) { remoteDataSource.refund(any(), any()) }
    }

    @Test
    fun `it should return the correct PixRefundCreated entity on refund call successfully`() = runBlocking {
        val expectedResult = CieloDataResult.Success(refundCreatedEntity)
        val result = mockAndRunRefundTest(expectedResult)

        // then
        assertEquals(expectedResult, result)

        val actual = result.asSuccess.value
        val expected = refundCreatedEntity

        assertEquals(expected, actual)
    }

    @Test
    fun `it should return a network error on refund call`() = runBlocking {
        val result = mockAndRunGetDetailFullTest(errorResult)

        // then
        assertEquals(errorResult, result)
    }

}