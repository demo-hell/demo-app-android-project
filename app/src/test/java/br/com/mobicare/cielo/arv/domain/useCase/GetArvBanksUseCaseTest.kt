package br.com.mobicare.cielo.arv.domain.useCase

import br.com.mobicare.cielo.arv.domain.repository.ArvRepositoryNew
import br.com.mobicare.cielo.arv.utils.ArvFactory
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetArvBanksUseCaseTest {
    private val repository = mockk<ArvRepositoryNew>()

    private val arvBankList = ArvFactory.arvBankList
    private val resultError = ArvFactory.resultError
    private val resultArvBanksSuccess = CieloDataResult.Success(arvBankList)

    private val getArvBanksUseCase = GetArvBanksUseCase(repository)

    @Test
    fun `it should call repository only once`() = runBlocking {
        // given
        coEvery { repository.getArvBanks() } returns resultArvBanksSuccess

        // when
        getArvBanksUseCase()

        // then
        coVerify(exactly = 1) { repository.getArvBanks() }
    }

    @Test
    fun `it should return a list of ArvBanks successfully`() = runBlocking {
        // given
        coEvery { repository.getArvBanks() } returns resultArvBanksSuccess

        // when
        val result = getArvBanksUseCase()

        // then
        assertEquals(resultArvBanksSuccess, result)
    }

    @Test
    fun `it should return the correct list size of ArvBanks`() = runBlocking {
        // given
        coEvery { repository.getArvBanks() } returns resultArvBanksSuccess

        // when
        val result = getArvBanksUseCase()

        // then
        assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)
        assertThat((result as CieloDataResult.Success).value.size)
            .isEqualTo(resultArvBanksSuccess.value.size)
    }

    @Test
    fun `it should return a network error`() = runBlocking {
        // given
        coEvery { repository.getArvBanks() } returns resultError

        // when
        val result = getArvBanksUseCase()

        // then
        assertEquals(resultError, result)
    }

}