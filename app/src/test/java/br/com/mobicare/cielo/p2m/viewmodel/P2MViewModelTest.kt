package br.com.mobicare.cielo.chargeback.presentation.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.p2m.domain.usecase.GetFeatureToggleMessageUseCase
import br.com.mobicare.cielo.p2m.domain.usecase.PutP2mAcceptUseCase
import br.com.mobicare.cielo.p2m.presentation.viewmodel.P2mAcreditationViewModel
import br.com.mobicare.cielo.p2m.utils.P2mFactory
import br.com.mobicare.cielo.p2m.utils.UiP2mAcceptState
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class P2MViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val putP2mAcceptUseCase = mockk<PutP2mAcceptUseCase>()
    private val featureToggleMessageUseCase = mockk<GetFeatureToggleMessageUseCase>()
    private val userObjUseCase = mockk<GetUserObjUseCase>()
    private val context = mockk<Context>(relaxed = true)

    private val p2mKeyCode = P2mFactory.p2mKeyCode
    private val p2mKeyCodeInvalid = P2mFactory.p2mInvalid

    private val resultEmpty = CieloDataResult.Empty()
    private val resultNetworkError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

    private val resultUserObjSuccess = CieloDataResult.Success(UserObj())

    private lateinit var viewModel: P2mAcreditationViewModel

    @Before
    fun setUp() {
        viewModel = P2mAcreditationViewModel(
            putP2mAcceptUseCase,
            userObjUseCase,
            featureToggleMessageUseCase
        )
    }

    @Test
    fun `it should set success state on empty result of P2mAccept call`() = runTest {
        // given
        coEvery { putP2mAcceptUseCase(any()) } returns resultEmpty

        // when
        viewModel.p2mAccept(context, p2mKeyCode)

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.p2mAcceptUiState.value is UiP2mAcceptState.Success)
    }

    @Test
    fun `it should set error state on getP2mLifecycleUseCase call result`() = runTest {
        // given
        coEvery { putP2mAcceptUseCase(any()) } returns resultNetworkError
        coEvery { userObjUseCase() } returns resultUserObjSuccess

        // when
        viewModel.p2mAccept(context, p2mKeyCodeInvalid)

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.p2mAcceptUiState.value is UiP2mAcceptState.Error)
    }

}

