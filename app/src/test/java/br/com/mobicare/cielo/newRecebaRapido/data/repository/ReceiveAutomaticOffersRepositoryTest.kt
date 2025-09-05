package br.com.mobicare.cielo.newRecebaRapido.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.newRecebaRapido.data.datasource.remote.ReceiveAutomaticRemoteDataSource
import br.com.mobicare.cielo.newRecebaRapido.util.ReceiveAutomaticFactory
import br.com.mobicare.cielo.newRecebaRapido.util.ReceiveAutomaticFactory.listOffer
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class ReceiveAutomaticOffersRepositoryTest {

    private val remoteDataSource = mockk<ReceiveAutomaticRemoteDataSource>()

    private val resultError = ReceiveAutomaticFactory.resultError
    private val resultRASuccess = CieloDataResult.Success(listOffer)

    private val repository = ReceiveAutomaticOffersRepositoryImpl(remoteDataSource)

    @Test
    fun `it should fetch Offers calling remote data source only once`() = runBlocking {
        // given
        coEvery { remoteDataSource.getReceiveAutomaticOffers(any(), any()) } returns resultRASuccess

        // when
        repository.getReceiveAutomaticOffers()

        // then
        coVerify(exactly = 1) { remoteDataSource.getReceiveAutomaticOffers(any(), any()) }
    }

    @Test
    fun `it should fetch Offers successfully`() = runBlocking {
        // given
        coEvery { remoteDataSource.getReceiveAutomaticOffers(any(), any()) } returns resultRASuccess

        // when
        val result = repository.getReceiveAutomaticOffers()

        // then
        Assert.assertEquals(resultRASuccess, result)
    }

    @Test
    fun `it should return the correct list size of Offers`() = runBlocking {
        // given
        coEvery { remoteDataSource.getReceiveAutomaticOffers(any(), any()) } returns resultRASuccess

        // when
        val result = repository.getReceiveAutomaticOffers()

        // then
        Truth.assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)
        Truth.assertThat((result as CieloDataResult.Success).value.size)
            .isEqualTo(resultRASuccess.value.size)
    }

    @Test
    fun `it should return a network error when fetching Offers`() = runBlocking {
        // given
        coEvery { remoteDataSource.getReceiveAutomaticOffers(any(), any()) } returns resultError

        // when
        val result = repository.getReceiveAutomaticOffers()

        // then
        Assert.assertEquals(resultError, result)
    }

}