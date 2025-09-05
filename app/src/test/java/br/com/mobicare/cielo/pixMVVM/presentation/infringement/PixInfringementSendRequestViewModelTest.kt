package br.com.mobicare.cielo.pixMVVM.presentation.infringement

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.pixMVVM.domain.model.PixCreateNotifyInfringement
import br.com.mobicare.cielo.pixMVVM.domain.usecase.PostPixInfringementUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.infringement.ui.sendRequest.PixInfringementSendRequestViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.infringement.utils.UIPixInfringementSendRequestState
import br.com.mobicare.cielo.pixMVVM.utils.PixInfringementFactory
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.reflect.Field

@OptIn(ExperimentalCoroutinesApi::class)
class PixInfringementSendRequestViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val postPixInfringementUseCase = mockk<PostPixInfringementUseCase>()
    private val context = mockk<Context>()

    private lateinit var viewModel: PixInfringementSendRequestViewModel

    private val resultUserObjSuccess = CieloDataResult.Success(UserObj())
    private val resultSuccessCreateInfringement =
        CieloDataResult.Success(PixInfringementFactory.pixCreateInfringementResponse)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    @Before
    fun setup() {
        viewModel = PixInfringementSendRequestViewModel(
            getUserObjUseCase,
            postPixInfringementUseCase
        )

        coEvery { getUserObjUseCase() } returns resultUserObjSuccess

        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context
    }

    @Test
    fun `check whether the data was set currently using the setData method`() = runTest {
        val field: Field =
            PixInfringementSendRequestViewModel::class.java.getDeclaredField("_pixCreateNotifyInfringement")
        field.isAccessible = true

        viewModel.setData(PixInfringementFactory.pixCreateNotifyInfringement)

        val pixCreateNotifyInfringement = field.get(viewModel) as PixCreateNotifyInfringement?

        assertEquals(PixInfringementFactory.pixCreateNotifyInfringement, pixCreateNotifyInfringement)
    }

    @Test
    fun `it should set uiState with UIPixInfringementSendRequestStateSuccess when sendRequest is called`() =
        runTest {
            coEvery {
                postPixInfringementUseCase(any())
            } returns resultSuccessCreateInfringement

            val states = viewModel.uiState.captureValues()

            viewModel.setData(PixInfringementFactory.pixCreateNotifyInfringement)

            viewModel.sendRequest()

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assert(states[ZERO] is UIPixInfringementSendRequestState.ShowLoading)
            assert(states[ONE] is UIPixInfringementSendRequestState.HideLoading)
            assert(states[TWO] is UIPixInfringementSendRequestState.Success)
        }

    @Test
    fun `it should set uiState with UIPixInfringementSendRequestStateError when sendRequest is called and return error`() =
        runTest {
            coEvery {
                postPixInfringementUseCase(any())
            } returns resultError

            val states = viewModel.uiState.captureValues()

            viewModel.setData(PixInfringementFactory.pixCreateNotifyInfringement)

            viewModel.sendRequest()

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assert(states[ZERO] is UIPixInfringementSendRequestState.ShowLoading)
            assert(states[ONE] is UIPixInfringementSendRequestState.HideLoading)
            assert(states[TWO] is UIPixInfringementSendRequestState.Error)
        }

    @Test
    fun `it should set uiState with UIPixInfringementSendRequestStateError when sendRequest is called and return empty error`() =
        runTest {
            coEvery {
                postPixInfringementUseCase(any())
            } returns resultEmpty

            val states = viewModel.uiState.captureValues()

            viewModel.setData(PixInfringementFactory.pixCreateNotifyInfringement)

            viewModel.sendRequest()

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assert(states[ZERO] is UIPixInfringementSendRequestState.ShowLoading)
            assert(states[ONE] is UIPixInfringementSendRequestState.HideLoading)
            assert(states[TWO] is UIPixInfringementSendRequestState.Error)
        }

}