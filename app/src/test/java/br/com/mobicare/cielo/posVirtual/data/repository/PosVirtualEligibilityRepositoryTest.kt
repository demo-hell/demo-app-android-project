package br.com.mobicare.cielo.posVirtual.data.repository

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.posVirtual.data.dataSource.PosVirtualEligibilityDataSource
import br.com.mobicare.cielo.posVirtual.domain.repository.PosVirtualEligibilityRepository
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PosVirtualEligibilityRepositoryTest {

    private val remoteDataSource = mockk<PosVirtualEligibilityDataSource>()

    private lateinit var repository: PosVirtualEligibilityRepository

    private val posVirtualEntity = PosVirtualFactory.Eligibility.posVirtualEntity
    private val resultSuccess = CieloDataResult.Success(posVirtualEntity)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    @Before
    fun setup() {
        repository = PosVirtualEligibilityRepositoryImpl(remoteDataSource)
    }

    @Test
    fun `it should call getEligibility of remote data source only once`() = runTest {
        // given
        coEvery { remoteDataSource.getEligibility() } returns resultSuccess

        // when
        repository.getEligibility()

        // then
        coVerify(exactly = ONE) { remoteDataSource.getEligibility() }
    }

    @Test
    fun `it should return a success result on getEligibility call`() = runTest {
        // given
        coEvery { remoteDataSource.getEligibility() } returns resultSuccess

        // when
        val result = repository.getEligibility()

        // then
        assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)
        assertThat((result as CieloDataResult.Success).value).isEqualTo(posVirtualEntity)
    }

    @Test
    fun `it should return an error result on getEligibility call`() = runTest {
        // given
        coEvery { remoteDataSource.getEligibility() } returns resultError

        // when
        val result = repository.getEligibility()

        // then
        assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)
    }

    @Test
    fun `it should return an empty result on getEligibility call`() = runTest {
        // given
        coEvery { remoteDataSource.getEligibility() } returns resultEmpty

        // when
        val result = repository.getEligibility()

        // then
        assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)
    }

}