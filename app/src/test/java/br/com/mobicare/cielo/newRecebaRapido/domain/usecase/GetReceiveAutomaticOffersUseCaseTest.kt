package br.com.mobicare.cielo.newRecebaRapido.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.newRecebaRapido.domain.repository.ReceiveAutomaticOffersRepository
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.DAILY
import br.com.mobicare.cielo.newRecebaRapido.util.ReceiveAutomaticFactory
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class GetReceiveAutomaticOffersUseCaseTest {

    private val repository = mockk<ReceiveAutomaticOffersRepository>()

    private val offerList = ReceiveAutomaticFactory.listOffer
    private val resultError = ReceiveAutomaticFactory.resultError
    private val resultSuccess = CieloDataResult.Success(offerList)

    private val getReceiveAutomaticOffersUseCase = GetReceiveAutomaticOffersUseCase(repository)


    @Test
    fun `it should return a list of offers successfully`() = runBlocking {
        // given
        coEvery { repository.getReceiveAutomaticOffers(any()) } returns resultSuccess

        // when
        val result = getReceiveAutomaticOffersUseCase(DAILY)

        // then
        Assert.assertEquals(resultSuccess, result)
    }

    @Test
    fun `it should return the correct list size of Offers`() = runBlocking {
        // given
        coEvery { repository.getReceiveAutomaticOffers(any()) } returns resultSuccess

        // when
        val result = getReceiveAutomaticOffersUseCase(DAILY)

        // then
        Truth.assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)
        Truth.assertThat((result as CieloDataResult.Success).value.size)
            .isEqualTo(resultSuccess.value.size)
    }

    @Test
    fun `it should return a network error`() = runBlocking {
        // given
        coEvery { repository.getReceiveAutomaticOffers(any()) } returns resultError

        // when
        val result = getReceiveAutomaticOffersUseCase(DAILY)

        // then
        Assert.assertEquals(resultError, result)
    }

    @Test
    fun `it should call repository only once`() = runBlocking {
        // given
        coEvery { repository.getReceiveAutomaticOffers(any()) } returns resultSuccess

        // when
        getReceiveAutomaticOffersUseCase(DAILY)

        // then
        coVerify(exactly = 1) { repository.getReceiveAutomaticOffers(any()) }
    }

}