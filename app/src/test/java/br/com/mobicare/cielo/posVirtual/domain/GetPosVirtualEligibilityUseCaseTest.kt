package br.com.mobicare.cielo.posVirtual.domain

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.posVirtual.domain.repository.PosVirtualEligibilityRepository
import br.com.mobicare.cielo.posVirtual.domain.useCase.GetPosVirtualEligibilityUseCase
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
class GetPosVirtualEligibilityUseCaseTest {

    private val repository = mockk<PosVirtualEligibilityRepository>()

    private lateinit var getPosVirtualEligibilityUseCase: GetPosVirtualEligibilityUseCase

    private val posVirtualEntity = PosVirtualFactory.Eligibility.posVirtualEntity
    private val resultSuccess = CieloDataResult.Success(posVirtualEntity)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    @Before
    fun setup() {
        getPosVirtualEligibilityUseCase = GetPosVirtualEligibilityUseCase(repository)
    }

    @Test
    fun `it should call getEligibility of repository only once`() = runTest {
        // given
        coEvery { repository.getEligibility() } returns resultSuccess

        // when
        getPosVirtualEligibilityUseCase()

        // then
        coVerify(exactly = ONE) { repository.getEligibility() }
    }

    @Test
    fun `it should return a success result on getEligibility call`() = runTest {
        // given
        coEvery { repository.getEligibility() } returns resultSuccess

        // when
        val result = getPosVirtualEligibilityUseCase()

        // then
        assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)
        assertThat((result as CieloDataResult.Success).value).isEqualTo(posVirtualEntity)
    }

    @Test
    fun `it should return an error result on getEligibility call`() = runTest {
        // given
        coEvery { repository.getEligibility() } returns resultError

        // when
        val result = getPosVirtualEligibilityUseCase()

        // then
        assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)
    }

    @Test
    fun `it should return an empty result on getEligibility call`() = runTest {
        // given
        coEvery { repository.getEligibility() } returns resultEmpty

        // when
        val result = getPosVirtualEligibilityUseCase()

        // then
        assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)
    }

}