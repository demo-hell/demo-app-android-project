package br.com.mobicare.cielo.chargeback.data.repository

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.chargeback.data.datasource.ChargebackRemoteDataSource
import br.com.mobicare.cielo.chargeback.utils.ChargebackFactory
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ChargebackRepositoryTest {
    private val remoteDataSource = mockk<ChargebackRemoteDataSource>()

    private val document = ChargebackFactory.documentFilePdf
    private val documentSender = ChargebackFactory.documentFileSender
    private val lifecycleList = ChargebackFactory.lifecycleList
    private val params = ChargebackFactory.documentParams
    private val paramsSender = ChargebackFactory.documentSenderParams
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultDocumentSuccess = CieloDataResult.Success(document)
    private val resultDocumentSenderSuccess = CieloDataResult.Success(documentSender)
    private val resultLifecycleListSuccess = CieloDataResult.Success(lifecycleList)
    private val repository = ChargebackRepositoryImpl(remoteDataSource)

    @Test
    fun `it should fetch document from remote data source only once`() = runBlocking {
        // given
        coEvery { remoteDataSource.getChargebackDocument(any()) } returns resultDocumentSuccess

        // when
        repository.getChargebackDocument(params)

        // then
        coVerify(exactly = 1) { remoteDataSource.getChargebackDocument(any()) }
    }

    @Test
    fun `it should fetch document sender from remote data source only once`() = runBlocking {
        // given
        coEvery { remoteDataSource.getChargebackDocumentSender(any()) } returns resultDocumentSenderSuccess

        // when
        repository.getChargebackDocumentSender(paramsSender)

        // then
        coVerify(exactly = 1) { remoteDataSource.getChargebackDocumentSender(any()) }
    }

    @Test
    fun `it should fetch lifecycle list from remote data source only once`() = runBlocking {
        // given
        coEvery { remoteDataSource.getChargebackLifecycle(any()) } returns resultLifecycleListSuccess

        // when
        repository.getChargebackLifecycle(ZERO)

        // then
        coVerify(exactly = 1) { remoteDataSource.getChargebackLifecycle(any()) }
    }

    @Test
    fun `it should return a chargeback document successfully`() = runBlocking {
        // given
        coEvery { remoteDataSource.getChargebackDocument(any()) } returns resultDocumentSuccess

        // when
        val result = repository.getChargebackDocument(params)

        // then
        assertEquals(result, resultDocumentSuccess)
    }

    @Test
    fun `it should return a chargeback document sender successfully`() = runBlocking {
        // given
        coEvery { remoteDataSource.getChargebackDocumentSender(any()) } returns resultDocumentSenderSuccess

        // when
        val result = repository.getChargebackDocumentSender(paramsSender)

        // then
        assertEquals(result, resultDocumentSenderSuccess)
    }

    @Test
    fun `it should return a list of chargeback lifecycles successfully`() = runBlocking {
        // given
        coEvery { remoteDataSource.getChargebackLifecycle(any()) } returns resultLifecycleListSuccess

        // when
        val result = repository.getChargebackLifecycle(ZERO)

        // then
        assertEquals(result, resultLifecycleListSuccess)
    }

    @Test
    fun `it should return the correct chargeback document`() = runBlocking {
        // given
        coEvery { remoteDataSource.getChargebackDocument(any()) } returns resultDocumentSuccess

        // when
        val result = repository.getChargebackDocument(params) as CieloDataResult.Success

        // then
        assertEquals(result.value.code, document.code)
        assertEquals(result.value.message, document.message)
        assertEquals(result.value.fileName, document.fileName)
        assertEquals(result.value.file, document.file)
    }

    @Test
    fun `it should return the correct chargeback document sender`() = runBlocking {
        // given
        coEvery { remoteDataSource.getChargebackDocumentSender(any()) } returns resultDocumentSenderSuccess

        // when
        val result = repository.getChargebackDocumentSender(paramsSender) as CieloDataResult.Success

        // then
        assertEquals(result.value.nameFile, documentSender.nameFile)
        assertEquals(result.value.code, documentSender.code)
        assertEquals(result.value.message, documentSender.message)
        assertEquals(result.value.fileBase64, documentSender.fileBase64)
    }

    @Test
    fun `it should return the correct list size of chargeback lifecycles`() = runBlocking {
        // given
        coEvery { remoteDataSource.getChargebackLifecycle(any()) } returns resultLifecycleListSuccess

        // when
        val result = repository.getChargebackLifecycle(ZERO) as CieloDataResult.Success

        // then
        assertEquals(result.value.size, lifecycleList.size)
    }

    @Test
    fun `it should return a network error when fetching document`() = runBlocking {
        // given
        coEvery { remoteDataSource.getChargebackDocument(any()) } returns resultError

        // when
        val result = repository.getChargebackDocument(params)

        // then
        assertEquals(result, resultError)
    }

    @Test
    fun `it should return a network error when fetching document sender`() = runBlocking {
        // given
        coEvery { remoteDataSource.getChargebackDocumentSender(any()) } returns resultError

        // when
        val result = repository.getChargebackDocumentSender(paramsSender)

        // then
        assertEquals(result, resultError)
    }

    @Test
    fun `it should return a network error when fetching list of lifecycles`() = runBlocking {
        // given
        coEvery { remoteDataSource.getChargebackLifecycle(any()) } returns resultError

        // when
        val result = repository.getChargebackLifecycle(ZERO)

        // then
        assertEquals(result, resultError)
    }
}