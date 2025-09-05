package br.com.mobicare.cielo.cieloFarol.presentation.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.cieloFarol.domain.useCase.GetCieloFarolUseCase
import br.com.mobicare.cielo.cieloFarol.presentation.CieloFarolViewModel
import br.com.mobicare.cielo.cieloFarol.utils.CieloFarolFactory
import br.com.mobicare.cielo.cieloFarol.utils.uiState.FarolUiState
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CieloFarolViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val getCieloFarolUseCase = mockk<GetCieloFarolUseCase>()
    private val userPreferences = mockk<UserPreferences>()
    private val farolCompleted = CieloFarolFactory.farolCompleted
    private val merchantId = CieloFarolFactory.farolRequestMerchantId
    private val authorization = CieloFarolFactory.farolRequestAuthorization
    private val resultSuccess = CieloDataResult.Success(farolCompleted)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

    private lateinit var viewModel: CieloFarolViewModel

    @Before
    fun setUp() {
        viewModel = CieloFarolViewModel(getCieloFarolUseCase, userPreferences)
        coEvery { userPreferences.token } returns authorization
        every { userPreferences.userInformation?.merchant?.id } returns merchantId
    }

    @Test
    fun `getCieloFarol should update FarolUiState with Success when use case succeeds`() = runTest {
        coEvery { getCieloFarolUseCase(authorization, merchantId) } returns resultSuccess

        viewModel.getCieloFarol()

        dispatcherRule.advanceUntilIdle()

        viewModel.farolUiState.value.let {
            assert(it is FarolUiState.Success && farolCompleted == it.data)
        }
    }

    @Test
    fun `getCieloFarol should update FarolUiState with Error when use case returns an error`() = runTest {
        coEvery { getCieloFarolUseCase(authorization, merchantId) } returns resultError

        viewModel.getCieloFarol()

        dispatcherRule.advanceUntilIdle()

        assert(viewModel.farolUiState.value is FarolUiState.Error)
    }

    @Test
    fun `getCieloFarol should update FarolUiState with Empty when use case returns an empty result`() = runTest {
        coEvery { getCieloFarolUseCase(authorization, merchantId) } returns CieloDataResult.Empty()

        viewModel.getCieloFarol()

        dispatcherRule.advanceUntilIdle()

        assert(viewModel.farolUiState.value is FarolUiState.Empty)
    }
}