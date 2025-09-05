package br.com.mobicare.cielo.contactCielo.domain.useCase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.contactCielo.domain.repository.SegmentCodeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetRemoteSegmentCodeUseCaseTest {
    private val repository = mockk<SegmentCodeRepository>()

    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultSuccess = CieloDataResult.Success(EMPTY)

    private val getRemoteSegmentCodeUseCase = GetRemoteSegmentCodeUseCase(repository)

    @Test
    fun `it should call getSegmentCode only once`() = runTest {
        coEvery { repository.getRemoteSegmentCode() } returns resultSuccess

        getRemoteSegmentCodeUseCase()

        coVerify(exactly = 1) { repository.getRemoteSegmentCode() }
    }

    @Test
    fun `it should return a string for segmentCode`() = runTest {
        coEvery { repository.getRemoteSegmentCode() } returns resultSuccess

        val result = getRemoteSegmentCodeUseCase()

        assert(result == resultSuccess)
    }

    @Test
    fun `it should return a network error`() = runTest {
        coEvery { repository.getRemoteSegmentCode() } returns resultError

        val result = getRemoteSegmentCodeUseCase()

        assert(result == resultError)
    }
}