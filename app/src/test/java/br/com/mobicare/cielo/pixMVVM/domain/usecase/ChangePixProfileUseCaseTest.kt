package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.utils.EMPTY_VALUE
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixProfileRepository
import br.com.mobicare.cielo.pixMVVM.utils.PixProfileFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ChangePixProfileUseCaseTest {
    private val repository = mockk<PixProfileRepository>()

    private val params = PixProfileFactory.let {
        ChangePixProfileUseCase.Params(it.otpCode, it.pixProfileRequest)
    }
    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val successResult = CieloDataResult.Success(EMPTY_VALUE)

    private val changePixProfileUseCase = ChangePixProfileUseCase(repository)

    @Test
    fun `it should call method update of repository only once`() = runBlocking {
        // given
        coEvery { repository.update(any(), any()) } returns successResult

        // when
        changePixProfileUseCase(params)

        // then
        coVerify(exactly = 1) { repository.update(any(), any()) }
    }

    @Test
    fun `it should return a success result on changePixProfileUseCase call`() = runBlocking {
        // given
        coEvery { repository.update(any(), any()) } returns successResult

        // when
        val result = changePixProfileUseCase(params)

        // then
        assertEquals(successResult, result)
    }

    @Test
    fun `it should return a network error on changePixProfileUseCase call`() = runBlocking {
        // given
        coEvery { repository.update(any(), any()) } returns errorResult

        // when
        val result = changePixProfileUseCase(params)

        // then
        assertEquals(errorResult, result)
    }

}