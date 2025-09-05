package br.com.mobicare.cielo.contactCielo.data.repository

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.contactCielo.data.datasource.SegmentCodeRemoteSource
import br.com.mobicare.cielo.contactCielo.data.datasource.local.SegmentCodeLocalSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SegmentCodeRepositoryTest {

    private val remoteDataSource = mockk<SegmentCodeRemoteSource>()
    private val localDataSource = mockk<SegmentCodeLocalSource>()
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultSuccess = CieloDataResult.Success(EMPTY)

    private val repository = SegmentCodeRepositoryImpl(remoteDataSource, localDataSource)

    @Test
    fun `it should get segmentCode from remote only once`() = runTest {
        coEvery { repository.getRemoteSegmentCode() } returns resultSuccess

        repository.getRemoteSegmentCode()

        coVerify(exactly = 1) { repository.getRemoteSegmentCode() }
    }

    @Test
    fun `it should get segmentCode successfully`() = runTest {
        coEvery { repository.getRemoteSegmentCode() } returns resultSuccess

        val result = repository.getRemoteSegmentCode()

        assert(result == resultSuccess)
    }

    @Test
    fun `it should return an error on get segmentCode`() = runTest {
        coEvery { repository.getRemoteSegmentCode() } returns resultError

        val result = repository.getRemoteSegmentCode()

        assert(result == resultError)
    }
}