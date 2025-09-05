package br.com.mobicare.cielo.suporteTecnico.data.repository

import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.suporteTecnico.data.dataSource.RequestTicketSupportDataSource
import br.com.mobicare.cielo.suporteTecnico.presentation.viewModel.utils.SuporteTecnicoFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class RequestTicketSupportRepositoryTest {
    private val dataSource = mockk<RequestTicketSupportDataSource>()
    private val resultSuccess = CieloDataResult.Success(SuporteTecnicoFactory.userOwnerDocumentAuthorized)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

    private val repository = RequestTicketSupportRepositoryImpl(dataSource)

    @Test
    fun `it should fetch merchant from remote data source only once`() =
        runBlocking {
            // given
            coEvery { dataSource.getMerchant() } returns resultSuccess

            // when
            repository.getMerchant()

            // then
            coVerify(exactly = ONE) { dataSource.getMerchant() }
        }

    @Test
    fun `it should return the correct merchant`() =
        runBlocking {
            // given
            coEvery { dataSource.getMerchant() } returns resultSuccess

            // when
            val result = repository.getMerchant()

            // then
            assert(result is CieloDataResult.Success)
        }

    @Test
    fun `it should return the correct error`() =
        runBlocking {
            // given
            coEvery { dataSource.getMerchant() } returns resultError

            // when
            val result = repository.getMerchant()

            // then
            assert(result is CieloDataResult.APIError)
        }
}
