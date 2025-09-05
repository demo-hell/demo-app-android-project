package br.com.mobicare.cielo.component.impersonate.presentation.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetMeInformationUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetMenuUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.domain.useCase.userPreferences.PutUserPreferencesUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.UILoadingState
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.component.impersonate.data.model.response.ImpersonateResponse
import br.com.mobicare.cielo.component.impersonate.domain.usecase.PostImpersonateUseCase
import br.com.mobicare.cielo.component.impersonate.presentation.viewModel.ImpersonateViewModel
import br.com.mobicare.cielo.component.impersonate.utils.ImpersonateFactory
import br.com.mobicare.cielo.component.impersonate.utils.TypeImpersonateEnum
import br.com.mobicare.cielo.component.impersonate.utils.UIImpersonateState
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.main.domain.AppMenuResponse
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

@OptIn(ExperimentalCoroutinesApi::class)
class ImpersonateViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val getMenuUseCase = mockk<GetMenuUseCase>()
    private val getFeatureTogglePreferenceUseCase = mockk<GetFeatureTogglePreferenceUseCase>()
    private val getMeInformationUseCase = mockk<GetMeInformationUseCase>()
    private val postImpersonateUseCase = mockk<PostImpersonateUseCase>()
    private val putUserPreferencesUseCase = mockk<PutUserPreferencesUseCase>()
    private val context = mockk<Context>()

    private lateinit var viewModel: ImpersonateViewModel

    private val resultUserObjSuccess = CieloDataResult.Success(UserObj())

    private val resultSuccessImpersonate =
        CieloDataResult.Success(ImpersonateFactory.impersonateResponse)
    private val resultSuccessSaveAccessToken = CieloDataResult.Success(true)
    private val resultSuccessMe = CieloDataResult.Success(ImpersonateFactory.meResponse)
    private val resultSuccessFTWhiteList = CieloDataResult.Success(true)
    private val resultSuccessMenu = CieloDataResult.Success(AppMenuResponse(menu = emptyList()))

    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    @Before
    fun setup() {
        viewModel = ImpersonateViewModel(
            getUserObjUseCase,
            getMenuUseCase,
            getFeatureTogglePreferenceUseCase,
            getMeInformationUseCase,
            postImpersonateUseCase,
            putUserPreferencesUseCase
        )

        viewModel.selectMerchant(ImpersonateFactory.ec)

        coEvery {
            getUserObjUseCase()
        } returns resultUserObjSuccess

        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context
    }

    @Test
    fun `it should set impersonateState with Success when post impersonate`() = runTest {
        coEvery { postImpersonateUseCase(any(), any(), any()) } returns resultSuccessImpersonate

        coEvery {
            putUserPreferencesUseCase(
                any(),
                any<String>(),
                any()
            )
        } returns resultSuccessSaveAccessToken

        coEvery { getMeInformationUseCase(any()) } returns resultSuccessMe

        coEvery { getFeatureTogglePreferenceUseCase(any()) } returns resultSuccessFTWhiteList

        coEvery { getMenuUseCase(any(), any()) } returns resultSuccessMenu

        val loadingStates = viewModel.loadingState.captureValues()
        val impersonateStates = viewModel.impersonateState.captureValues()

        viewModel.impersonate(ImpersonateFactory.fingerprint, TypeImpersonateEnum.HIERARCHY, false)

        dispatcherRule.advanceUntilIdle()

        assertEquals(TWO, loadingStates.size)
        assertThat(loadingStates[ZERO]).isInstanceOf(UILoadingState.ShowLoading::class.java)
        assertThat(loadingStates[ONE]).isInstanceOf(UILoadingState.HideLoading::class.java)

        assertEquals(TWO, impersonateStates.size)
        assertThat(impersonateStates[ZERO]).isInstanceOf(UIImpersonateState.SendMessageUpdateMainBottomNavigation::class.java)
        assertThat(impersonateStates[ONE]).isInstanceOf(UIImpersonateState.Success::class.java)
    }

    @Test
    fun `it should set impersonateState with ImpersonateError when API error post impersonate`() =
        runTest {
            coEvery { postImpersonateUseCase(any(), any(), any()) } returns resultError

            val loadingStates = viewModel.loadingState.captureValues()
            val impersonateStates = viewModel.impersonateState.captureValues()

            viewModel.impersonate(ImpersonateFactory.fingerprint, TypeImpersonateEnum.HIERARCHY, false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, loadingStates.size)
            assertThat(loadingStates[ZERO]).isInstanceOf(UILoadingState.ShowLoading::class.java)
            assertThat(loadingStates[ONE]).isInstanceOf(UILoadingState.HideLoading::class.java)

            assertEquals(ONE, impersonateStates.size)
            assertThat(impersonateStates[ZERO]).isInstanceOf(UIImpersonateState.ImpersonateError::class.java)
        }

    @Test
    fun `it should set impersonateState with ImpersonateError when Empty error post impersonate`() =
        runTest {
            coEvery { postImpersonateUseCase(any(), any(), any()) } returns resultEmpty

            val loadingStates = viewModel.loadingState.captureValues()
            val impersonateStates = viewModel.impersonateState.captureValues()

            viewModel.impersonate(ImpersonateFactory.fingerprint, TypeImpersonateEnum.HIERARCHY, false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, loadingStates.size)
            assertThat(loadingStates[ZERO]).isInstanceOf(UILoadingState.ShowLoading::class.java)
            assertThat(loadingStates[ONE]).isInstanceOf(UILoadingState.HideLoading::class.java)

            assertEquals(ONE, impersonateStates.size)
            assertThat(impersonateStates[ZERO]).isInstanceOf(UIImpersonateState.ImpersonateError::class.java)
        }

    @Test
    fun `it should set impersonateState with ImpersonateError when access token empty post impersonate`() =
        runTest {
            coEvery {
                postImpersonateUseCase(any(), any(), any())
            } returns CieloDataResult.Success(ImpersonateResponse())

            val loadingStates = viewModel.loadingState.captureValues()
            val impersonateStates = viewModel.impersonateState.captureValues()

            viewModel.impersonate(ImpersonateFactory.fingerprint, TypeImpersonateEnum.HIERARCHY,false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, loadingStates.size)
            assertThat(loadingStates[ZERO]).isInstanceOf(UILoadingState.ShowLoading::class.java)
            assertThat(loadingStates[ONE]).isInstanceOf(UILoadingState.HideLoading::class.java)

            assertEquals(ONE, impersonateStates.size)
            assertThat(impersonateStates[ZERO]).isInstanceOf(UIImpersonateState.ImpersonateError::class.java)
        }

    @Test
    fun `it should set impersonateState with ImpersonateError when success post impersonate but error save accessToken`() =
        runTest {
            coEvery { postImpersonateUseCase(any(), any(), any()) } returns resultSuccessImpersonate

            coEvery {
                putUserPreferencesUseCase(
                    any(),
                    any<String>(),
                    any()
                )
            } returns CieloDataResult.Success(false)

            val loadingStates = viewModel.loadingState.captureValues()
            val impersonateStates = viewModel.impersonateState.captureValues()

            viewModel.impersonate(ImpersonateFactory.fingerprint, TypeImpersonateEnum.HIERARCHY, false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, loadingStates.size)
            assertThat(loadingStates[ZERO]).isInstanceOf(UILoadingState.ShowLoading::class.java)
            assertThat(loadingStates[ONE]).isInstanceOf(UILoadingState.HideLoading::class.java)

            assertEquals(ONE, impersonateStates.size)
            assertThat(impersonateStates[ZERO]).isInstanceOf(UIImpersonateState.ImpersonateError::class.java)
        }

    @Test
    fun `it should set impersonateState with LogoutError when success post impersonate but API error get me`() =
        runTest {
            coEvery { postImpersonateUseCase(any(), any(), any()) } returns resultSuccessImpersonate

            coEvery {
                putUserPreferencesUseCase(
                    any(),
                    any<String>(),
                    any()
                )
            } returns resultSuccessSaveAccessToken

            coEvery { getMeInformationUseCase(any()) } returns resultError

            val loadingStates = viewModel.loadingState.captureValues()
            val impersonateStates = viewModel.impersonateState.captureValues()

            viewModel.impersonate(ImpersonateFactory.fingerprint, TypeImpersonateEnum.HIERARCHY, false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, loadingStates.size)
            assertThat(loadingStates[ZERO]).isInstanceOf(UILoadingState.ShowLoading::class.java)
            assertThat(loadingStates[ONE]).isInstanceOf(UILoadingState.HideLoading::class.java)

            assertEquals(ONE, impersonateStates.size)
            assertThat(impersonateStates[ZERO]).isInstanceOf(UIImpersonateState.LogoutError::class.java)
        }

    @Test
    fun `it should set impersonateState with LogoutError when success post impersonate but Empty error get me`() =
        runTest {
            coEvery { postImpersonateUseCase(any(), any(), any()) } returns resultSuccessImpersonate

            coEvery {
                putUserPreferencesUseCase(
                    any(),
                    any<String>(),
                    any()
                )
            } returns resultSuccessSaveAccessToken

            coEvery { getMeInformationUseCase(any()) } returns resultEmpty

            val loadingStates = viewModel.loadingState.captureValues()
            val impersonateStates = viewModel.impersonateState.captureValues()

            viewModel.impersonate(ImpersonateFactory.fingerprint, TypeImpersonateEnum.HIERARCHY, false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, loadingStates.size)
            assertThat(loadingStates[ZERO]).isInstanceOf(UILoadingState.ShowLoading::class.java)
            assertThat(loadingStates[ONE]).isInstanceOf(UILoadingState.HideLoading::class.java)

            assertEquals(ONE, impersonateStates.size)
            assertThat(impersonateStates[ZERO]).isInstanceOf(UIImpersonateState.LogoutError::class.java)
        }

    @Test
    fun `it should set impersonateState with LogoutError when success post impersonate but API error get menu`() =
        runTest {
            coEvery { postImpersonateUseCase(any(), any(), any()) } returns resultSuccessImpersonate

            coEvery {
                putUserPreferencesUseCase(
                    any(),
                    any<String>(),
                    any()
                )
            } returns resultSuccessSaveAccessToken

            coEvery { getMeInformationUseCase(any()) } returns resultSuccessMe

            coEvery { getFeatureTogglePreferenceUseCase(any()) } returns resultSuccessFTWhiteList

            coEvery { getMenuUseCase(any(), any()) } returns resultEmpty

            val loadingStates = viewModel.loadingState.captureValues()
            val impersonateStates = viewModel.impersonateState.captureValues()

            viewModel.impersonate(ImpersonateFactory.fingerprint, TypeImpersonateEnum.HIERARCHY, false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, loadingStates.size)
            assertThat(loadingStates[ZERO]).isInstanceOf(UILoadingState.ShowLoading::class.java)
            assertThat(loadingStates[ONE]).isInstanceOf(UILoadingState.HideLoading::class.java)

            assertEquals(TWO, impersonateStates.size)
            assertThat(impersonateStates[ZERO]).isInstanceOf(UIImpersonateState.SendMessageUpdateMainBottomNavigation::class.java)
            assertThat(impersonateStates[ONE]).isInstanceOf(UIImpersonateState.LogoutError::class.java)
        }

    @Test
    fun `it should set impersonateState with LogoutError when success post impersonate but Empty error get menu`() =
        runTest {
            coEvery { postImpersonateUseCase(any(), any(), any()) } returns resultSuccessImpersonate

            coEvery {
                putUserPreferencesUseCase(
                    any(),
                    any<String>(),
                    any()
                )
            } returns resultSuccessSaveAccessToken

            coEvery { getMeInformationUseCase(any()) } returns resultSuccessMe

            coEvery { getFeatureTogglePreferenceUseCase(any()) } returns resultSuccessFTWhiteList

            coEvery { getMenuUseCase(any(), any()) } returns resultEmpty

            val loadingStates = viewModel.loadingState.captureValues()
            val impersonateStates = viewModel.impersonateState.captureValues()

            viewModel.impersonate(ImpersonateFactory.fingerprint, TypeImpersonateEnum.HIERARCHY, false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, loadingStates.size)
            assertThat(loadingStates[ZERO]).isInstanceOf(UILoadingState.ShowLoading::class.java)
            assertThat(loadingStates[ONE]).isInstanceOf(UILoadingState.HideLoading::class.java)

            assertEquals(TWO, impersonateStates.size)
            assertThat(impersonateStates[ZERO]).isInstanceOf(UIImpersonateState.SendMessageUpdateMainBottomNavigation::class.java)
            assertThat(impersonateStates[ONE]).isInstanceOf(UIImpersonateState.LogoutError::class.java)
        }

}