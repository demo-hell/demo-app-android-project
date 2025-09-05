package br.com.mobicare.cielo.chargeback.domain.useCase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.chargeback.domain.repository.ChargebackRepository
import br.com.mobicare.cielo.chargeback.utils.ChargebackFactory
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetChargebackDocumentTest {
    private val repository = mockk<ChargebackRepository>()

    private val document = ChargebackFactory.documentFilePdf
    private val params = ChargebackFactory.documentParams
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultSuccess = CieloDataResult.Success(document)
    private val getChargebackDocument = GetChargebackDocumentUseCase(repository)

    @Test
    fun `it should call chargeback repository only once`() = runBlocking {
        // given
        coEvery { repository.getChargebackDocument(any()) } returns resultSuccess

        // when
        getChargebackDocument(params)

        // then
        coVerify(exactly = 1) { repository.getChargebackDocument(any()) }
    }

    @Test
    fun `it should return a chargeback document successfully`() = runBlocking {
        // given
        coEvery { repository.getChargebackDocument(any()) } returns resultSuccess

        // when
        val result = getChargebackDocument(params)

        // then
        assertEquals(result, resultSuccess)
    }

    @Test
    fun `it should return the correct chargeback document`() = runBlocking {
        // given
        coEvery { repository.getChargebackDocument(any()) } returns resultSuccess

        // when
        val result = getChargebackDocument(params) as CieloDataResult.Success

        // then
        assertEquals(result.value.code, document.code)
        assertEquals(result.value.message, document.message)
        assertEquals(result.value.fileName, document.fileName)
        assertEquals(result.value.file, document.file)
    }

    @Test
    fun `it should return a network error`() = runBlocking {
        // given
        coEvery { repository.getChargebackDocument(any()) } returns resultError

        // when
        val result = getChargebackDocument(params)

        // then
        assertEquals(result, resultError)
    }
}