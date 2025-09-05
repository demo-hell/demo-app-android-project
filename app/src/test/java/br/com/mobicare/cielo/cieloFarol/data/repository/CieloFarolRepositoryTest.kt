package br.com.mobicare.cielo.cieloFarol.data.repository

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.cieloFarol.data.dataSource.CieloFarolDataSource
import br.com.mobicare.cielo.cieloFarol.utils.CieloFarolFactory
import br.com.mobicare.cielo.cieloFarol.utils.CieloFarolFactory.farolRequestAuthorization
import br.com.mobicare.cielo.cieloFarol.utils.CieloFarolFactory.farolRequestMerchantId
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class CieloFarolRepositoryTest {
    private val dataSource = mockk<CieloFarolDataSource>()

    private val farolCompleted = CieloFarolFactory.farolCompleted
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultSuccess = CieloDataResult.Success(farolCompleted)
    private val repository = CieloFarolRepositoryImpl(dataSource)

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `getCieloFarol should return success result on successful API response`() = runBlocking {
        coEvery { dataSource.getCieloFarol(any(), any()) } returns resultSuccess

        val result = repository.getCieloFarol(farolRequestAuthorization, farolRequestMerchantId)

        assertEquals(resultSuccess, result)
    }

    @Test
    fun `getCieloFarol should return empty result on empty farol response`() = runBlocking {
        coEvery { dataSource.getCieloFarol(any(), any()) } returns CieloDataResult.Empty()

        val result = repository.getCieloFarol(farolRequestAuthorization, farolRequestMerchantId)

        assert(result is CieloDataResult.Empty)
    }

    @Test
    fun `getCieloFarol should return a network error when getting the cielo farol info`() = runBlocking {
        coEvery { dataSource.getCieloFarol(any(), any()) } returns resultError

        val result = repository.getCieloFarol(farolRequestAuthorization, farolRequestMerchantId)

        assertEquals(resultError, result)
    }
}
