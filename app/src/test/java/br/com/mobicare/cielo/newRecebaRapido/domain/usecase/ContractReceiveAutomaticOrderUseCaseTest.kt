package br.com.mobicare.cielo.newRecebaRapido.domain.usecase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.newRecebaRapido.domain.repository.ReceiveAutomaticOffersRepository
import br.com.mobicare.cielo.newRecebaRapido.util.ReceiveAutomaticFactory.requestUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class ContractReceiveAutomaticOrderUseCaseTest {

    private val repository = mockk<ReceiveAutomaticOffersRepository>()

    private val useCase = ContractReceiveAutomaticOfferUseCase(repository)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

    @Test
    fun `it should return a confirm contractReceiveAutomaticOffer response empty`() = runBlocking {
        // given
        coEvery {  repository.contractReceiveAutomaticOffer(any()) } returns CieloDataResult.Empty()

        // when
        val result = useCase.invoke(requestUseCase)

        // then
        Assert.assertTrue( result is CieloDataResult.Empty)

    }

    @Test
    fun `it should return a network error`() = runBlocking {
        // given
        coEvery { repository.contractReceiveAutomaticOffer(any()) } returns resultError

        // when
        val result = useCase.invoke(requestUseCase)

        // then
        Assert.assertEquals(resultError, result)
    }

}