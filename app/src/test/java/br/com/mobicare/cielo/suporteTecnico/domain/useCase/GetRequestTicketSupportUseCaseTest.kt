package br.com.mobicare.cielo.suporteTecnico.domain.useCase

import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.suporteTecnico.domain.repo.RequestTicketSupportRepository
import br.com.mobicare.cielo.suporteTecnico.presentation.viewModel.utils.SuporteTecnicoFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class GetRequestTicketSupportUseCaseTest {
    private val repository = mockk<RequestTicketSupportRepository>()
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultSuccess = CieloDataResult.Success(SuporteTecnicoFactory.userOwnerDocumentAuthorized)
    private val getRequestTicketSupportUseCase = GetRequestTicketSupportUseCase(repository)

    @Test
    fun `it should call request ticket support repository only once`() =
        runBlocking {
            // given
            coEvery { repository.getMerchant() } returns resultSuccess

            // when
            getRequestTicketSupportUseCase()

            // then
            coVerify(exactly = ONE) { repository.getMerchant() }
        }

    @Test
    fun `it should return a request ticket support successfully`() =
        runBlocking {
            // given
            coEvery { repository.getMerchant() } returns resultSuccess

            // when
            val result = getRequestTicketSupportUseCase()

            // then
            assert(result is CieloDataResult.Success)
        }

    @Test
    fun `it should return a network error`() =
        runBlocking {
            // given
            coEvery { repository.getMerchant() } returns resultError

            // when
            val result = getRequestTicketSupportUseCase()

            // then
            assert(result is CieloDataResult.APIError)
        }
}
