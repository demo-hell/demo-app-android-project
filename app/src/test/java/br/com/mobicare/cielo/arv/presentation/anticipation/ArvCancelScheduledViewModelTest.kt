package br.com.mobicare.cielo.arv.presentation.anticipation

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.arv.domain.model.ArvScheduleContract
import br.com.mobicare.cielo.arv.domain.useCase.CancelArvScheduledAnticipationUseCase
import br.com.mobicare.cielo.arv.domain.useCase.ConfirmArvScheduledAnticipationUseCase
import br.com.mobicare.cielo.arv.domain.useCase.GetArvScheduledContractUseCase
import br.com.mobicare.cielo.arv.utils.ArvFactory
import br.com.mobicare.cielo.arv.utils.ArvFactory.resultMfaTokenError
import br.com.mobicare.cielo.arv.utils.UiArvCancelScheduledAnticipationState
import br.com.mobicare.cielo.arv.utils.UiArvConfirmScheduledAnticipationState
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.login.domains.entities.UserObj
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArvCancelScheduledViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val context = mockk<Context>(relaxed = true)
    private val resultError = ArvFactory.resultError
    private val arvBank = ArvFactory.emptyArvBank
    private val cancelArvScheduledAnticipationUseCase = mockk<CancelArvScheduledAnticipationUseCase>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()

    private lateinit var viewModel: ArvCancelScheduledViewModel

    @Before
    fun setUp() {
        mockkObject(CieloApplication)
        every { CieloApplication.Companion.context } returns context

        viewModel = ArvCancelScheduledViewModel(
            cancelArvScheduledAnticipationUseCase,
            getUserObjUseCase,
        )

        coEvery { getUserObjUseCase() } returns CieloDataResult.Success(UserObj())
    }


    @Test
    fun `it should set success state on success result of cancelAnticipation call`() = runTest {
        // given
        coEvery { cancelArvScheduledAnticipationUseCase(any()) } returns CieloDataResult.Empty()

        // when
        viewModel.cancelAnticipation("CIELO")

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.arvCancelScheduledAnticipationState.value is UiArvCancelScheduledAnticipationState.Success)
    }

    @Test
    fun `it should set error state on network error result of cancelAnticipation call`() = runTest {
        // given
        coEvery { cancelArvScheduledAnticipationUseCase(any()) } returns resultError

        // when
        viewModel.cancelAnticipation("CIELO")

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.arvCancelScheduledAnticipationState.value is UiArvCancelScheduledAnticipationState.Error)
    }

    @Test
    fun `it should set token error state on MFA token error result of confirmAnticipation call`() = runTest {
        // given
        coEvery { cancelArvScheduledAnticipationUseCase(any()) } returns resultMfaTokenError

        // when
        viewModel.cancelAnticipation("CIELO")

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.arvCancelScheduledAnticipationState.value is UiArvCancelScheduledAnticipationState.ErrorToken)
    }

}