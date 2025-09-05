package br.com.mobicare.cielo.chargeback.data.repository

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.chargeback.data.datasource.ChargebackRemoteFiltersDataSource
import br.com.mobicare.cielo.chargeback.utils.ChargebackFactory
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.assertEquals

class ChargebackFiltersRepositoryImplTest {

    private val dataSource = mockk<ChargebackRemoteFiltersDataSource>()
    private val repository = ChargebackFiltersRepositoryImpl(dataSource)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultSuccess = ChargebackFactory.filterSuccessResponse

    @Test
    fun `it should return success when load chargeback available filters`() = runBlocking {
        
        coEvery { dataSource.getChargebackFilters() } returns resultSuccess
        
        val result = repository.getChargebackFilters()
        
        assertEquals(resultSuccess,result)
    }


    @Test
    fun `it should return error when load chargeback available filters`() = runBlocking {
             
        coEvery { dataSource.getChargebackFilters() } returns resultError

        val result = repository.getChargebackFilters()

        assertEquals(resultError,result)
    }
}