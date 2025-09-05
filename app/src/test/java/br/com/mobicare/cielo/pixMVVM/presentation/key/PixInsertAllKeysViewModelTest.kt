package br.com.mobicare.cielo.pixMVVM.presentation.key

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.constants.HTTP_ENHANCE
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixValidateKeyUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.key.utils.PixInsertAllKeysUIState
import br.com.mobicare.cielo.pixMVVM.presentation.key.viewmodel.PixInsertAllKeysViewModel
import br.com.mobicare.cielo.pixMVVM.utils.PixKeysFactory
import com.google.common.truth.Truth.assertThat
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
class PixInsertAllKeysViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val getPixValidateKeyUseCase = mockk<GetPixValidateKeyUseCase>()
    private val context = mockk<Context>()

    private lateinit var viewModel: PixInsertAllKeysViewModel

    private val userObjResult = CieloDataResult.Success(UserObj())
    private val validateKeySuccessResult =
        CieloDataResult.Success(PixKeysFactory.pixValidateKey)

    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val emptyResult = CieloDataResult.Empty()
    private val errorEnhanceResult =
        CieloDataResult.APIError(
            CieloAPIException.httpError(
                response = null,
                httpStatusCode = HTTP_ENHANCE,
                actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
                newErrorMessage = NewErrorMessage(
                    httpCode = HTTP_ENHANCE
                )
            ),
        )

    @Before
    fun setup() {
        viewModel = PixInsertAllKeysViewModel(getUserObjUseCase, getPixValidateKeyUseCase)

        coEvery { getUserObjUseCase() } returns userObjResult

        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context
    }

    @Test
    fun `check function to validate key with success return`() =
        runTest {
            coEvery { getPixValidateKeyUseCase(any(), any()) } returns validateKeySuccessResult

            val states = viewModel.uiState.captureValues()
            viewModel.validateKey(PixKeysFactory.key)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(PixInsertAllKeysUIState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(PixInsertAllKeysUIState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(PixInsertAllKeysUIState.Success::class.java)
        }

    @Test
    fun `check function to validate key with generic error return`() =
        runTest {
            coEvery { getPixValidateKeyUseCase(any(), any()) } returns errorResult

            val states = viewModel.uiState.captureValues()
            viewModel.validateKey(PixKeysFactory.key)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(PixInsertAllKeysUIState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(PixInsertAllKeysUIState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(PixInsertAllKeysUIState.GenericError::class.java)
        }

    @Test
    fun `check function to validate key with generic error return with errorCounter greater than two`() =
        runTest {
            val field: Field = PixInsertAllKeysViewModel::class.java.getDeclaredField("errorCounter")
            field.isAccessible = true
            field.set(viewModel, TWO)

            coEvery { getPixValidateKeyUseCase(any(), any()) } returns errorResult

            val states = viewModel.uiState.captureValues()
            viewModel.validateKey(PixKeysFactory.key)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(PixInsertAllKeysUIState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(PixInsertAllKeysUIState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(PixInsertAllKeysUIState.UnavailableService::class.java)
        }

    @Test
    fun `check function to validate key with http enhance error return`() =
        runTest {
            coEvery { getPixValidateKeyUseCase(any(), any()) } returns errorEnhanceResult

            val states = viewModel.uiState.captureValues()
            viewModel.validateKey(PixKeysFactory.key)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(PixInsertAllKeysUIState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(PixInsertAllKeysUIState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(PixInsertAllKeysUIState.InputError::class.java)
        }

    @Test
    fun `check function to validate key with http enhance error return with errorCounter greater than two`() =
        runTest {
            val field: Field = PixInsertAllKeysViewModel::class.java.getDeclaredField("errorCounter")
            field.isAccessible = true
            field.set(viewModel, TWO)

            coEvery { getPixValidateKeyUseCase(any(), any()) } returns errorEnhanceResult

            val states = viewModel.uiState.captureValues()
            viewModel.validateKey(PixKeysFactory.key)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(PixInsertAllKeysUIState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(PixInsertAllKeysUIState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(PixInsertAllKeysUIState.InputError::class.java)
        }

    @Test
    fun `check function to validate key with empty error return`() =
        runTest {
            coEvery { getPixValidateKeyUseCase(any(), any()) } returns emptyResult

            val states = viewModel.uiState.captureValues()
            viewModel.validateKey(PixKeysFactory.key)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(PixInsertAllKeysUIState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(PixInsertAllKeysUIState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(PixInsertAllKeysUIState.GenericError::class.java)
        }

    @Test
    fun `check function to validate key with empty error return with errorCounter greater than two`() =
        runTest {
            val field: Field = PixInsertAllKeysViewModel::class.java.getDeclaredField("errorCounter")
            field.isAccessible = true
            field.set(viewModel, TWO)

            coEvery { getPixValidateKeyUseCase(any(), any()) } returns emptyResult

            val states = viewModel.uiState.captureValues()
            viewModel.validateKey(PixKeysFactory.key)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(PixInsertAllKeysUIState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(PixInsertAllKeysUIState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(PixInsertAllKeysUIState.UnavailableService::class.java)
        }

}