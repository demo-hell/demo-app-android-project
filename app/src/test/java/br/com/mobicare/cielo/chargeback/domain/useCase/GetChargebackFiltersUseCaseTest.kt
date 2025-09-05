package br.com.mobicare.cielo.chargeback.domain.useCase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.chargeback.domain.repository.ChargebackFiltersRepository
import br.com.mobicare.cielo.chargeback.utils.ChargebackFactory
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class GetChargebackFiltersUseCaseTest {

    private val repository = mockk< ChargebackFiltersRepository>()
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultSuccess = ChargebackFactory.filterSuccessResponse


    @Test
    fun `it should return success when load chargeback available filters`() = runBlocking {

             
        coEvery { repository.getChargebackFilters() } returns resultSuccess
             
        val result = repository.getChargebackFilters()

        Assert.assertEquals(resultSuccess,result)
    }


    @Test
    fun `it should return error when load chargeback available filters`() = runBlocking {
             
        coEvery { repository.getChargebackFilters() } returns resultError

        val result = repository.getChargebackFilters()

        Assert.assertEquals(resultError, result)
    }
}