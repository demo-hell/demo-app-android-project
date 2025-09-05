package br.com.mobicare.cielo.accessManager.presenter.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.accessManager.domain.model.CustomProfiles
import br.com.mobicare.cielo.accessManager.domain.usecase.GetCustomActiveProfilesUseCase
import br.com.mobicare.cielo.accessManager.domain.usecase.PostAssignRoleUseCase
import br.com.mobicare.cielo.accessManager.presentation.batchProfileChange.AccessManagerBatchChangeProfileViewModel
import br.com.mobicare.cielo.accessManager.utils.AccessManagerBatchChangeProfileUiState
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.forgotMyPassword.utils.ForgotMyPasswordInsertInfoFactory
import br.com.mobicare.cielo.forgotMyPassword.utils.ForgotPasswordUiState
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AccessManagerBatchChangeProfileViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: AccessManagerBatchChangeProfileViewModel
    private val getCustomActiveProfilesUseCase: GetCustomActiveProfilesUseCase = mockk()
    private val postAssignRoleUseCase: PostAssignRoleUseCase = mockk()
    private val featureTogglePreference: FeatureTogglePreference = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        MockKAnnotations.init(this)

        viewModel = AccessManagerBatchChangeProfileViewModel(
            getCustomActiveProfilesUseCase,
            postAssignRoleUseCase,
            featureTogglePreference
        )
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `test showTechnicalProfile returns true when feature is enabled`() {
        coEvery { featureTogglePreference.getFeatureTogle(FeatureTogglePreference.PERFIL_TECNICO) } returns true

        val result = viewModel.showTechnicalProfile()

        assert(result)
    }

    @Test
    fun `test showTechnicalProfile returns false when feature is disabled`() {
        coEvery { featureTogglePreference.getFeatureTogle(FeatureTogglePreference.PERFIL_TECNICO) } returns false

        val result = viewModel.showTechnicalProfile()

        assertFalse(result)
    }

    @Test
    fun `test getCustomActiveProfiles fetches data when feature is enabled`() = runTest {
        val customProfilesList = listOf(CustomProfiles())

        coEvery {
            featureTogglePreference.getFeatureTogle(FeatureTogglePreference.PERFIL_PERSONALIZADO)
        } returns true

        coEvery {
            getCustomActiveProfilesUseCase("CUSTOM", "ACTIVE")
        } coAnswers  {
            CieloDataResult.Success(customProfilesList)
        }

        viewModel.getCustomActiveProfiles(true)

        assert(viewModel.showCustomProfiles())
    }

    @Test
    fun `test getCustomActiveProfiles does not fetch data when feature is disabled`() = runTest {
        coEvery { featureTogglePreference.getFeatureTogle(FeatureTogglePreference.PERFIL_PERSONALIZADO) } returns false

        viewModel.getCustomActiveProfiles(true)

        coVerify(exactly = 0) { getCustomActiveProfilesUseCase(any(), any()) }
    }

    @Test
    fun `test getCustomActiveProfiles does not fetch data when customProfileEnabled is false`() = runTest {
        coEvery { featureTogglePreference.getFeatureTogle(FeatureTogglePreference.PERFIL_PERSONALIZADO) } returns true

        viewModel.getCustomActiveProfiles(false)

        coVerify(exactly = 0) { getCustomActiveProfilesUseCase(any(), any()) }
    }

    @Test
    fun `test assignRole success`() = runTest {

        coEvery {
            postAssignRoleUseCase(any(), any(), any())
        } coAnswers {
            CieloDataResult.Empty()
        }

        viewModel.assignRole(listOf("1", "2"), "ADMIN", "1234")

        coVerify { postAssignRoleUseCase(any(), any(), any()) }

        viewModel.accessManagerBatchChangeProfileLiveData.value.let {
            assert(it is AccessManagerBatchChangeProfileUiState.AssignRoleSuccess)
        }
    }

    @Test
    fun `test sendRequestRecoveryPassword error`() = runTest {

        coEvery {
            postAssignRoleUseCase(any(), any(), any())
        } coAnswers {
            CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
        }

        viewModel.assignRole(listOf("1", "2"), "ADMIN", "1234")

        coVerify { postAssignRoleUseCase(any(), any(), any()) }

        viewModel.accessManagerBatchChangeProfileLiveData.value.let {
            assert(it is AccessManagerBatchChangeProfileUiState.AssignRoleError)
        }
    }
}