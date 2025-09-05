package br.com.mobicare.cielo.pixMVVM.presentation.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixAccountBalanceUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixMasterKeyUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixUserDataUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.home.models.PixAccountBalanceStore
import br.com.mobicare.cielo.pixMVVM.presentation.home.models.PixKeysStore
import br.com.mobicare.cielo.pixMVVM.presentation.home.utils.AccountBalanceUiState
import br.com.mobicare.cielo.pixMVVM.presentation.home.utils.MasterKeyUiState
import br.com.mobicare.cielo.pixMVVM.presentation.home.utils.UserDataUiResult
import br.com.mobicare.cielo.pixMVVM.presentation.home.viewmodel.PixHomeViewModel
import br.com.mobicare.cielo.pixMVVM.utils.PixAccountBalanceFactory
import br.com.mobicare.cielo.pixMVVM.utils.PixKeysFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PixHomeViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val getPixUserDataUseCase = mockk<GetPixUserDataUseCase>()
    private val getPixAccountBalanceUseCase = mockk<GetPixAccountBalanceUseCase>()
    private val getPixMasterKeyUseCase = mockk<GetPixMasterKeyUseCase>()
    private val userPreferences = mockk<UserPreferences>()

    private val pixAccountBalanceEntity = PixAccountBalanceFactory.pixAccountBalanceEntity
    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val emptyResult = CieloDataResult.Empty()

    private lateinit var userDataUiResult: List<UserDataUiResult?>
    private lateinit var accountBalanceUiState: List<AccountBalanceUiState?>
    private lateinit var masterKeyUiState: List<MasterKeyUiState?>

    private lateinit var viewModel: PixHomeViewModel

    @Before
    fun setUp() {
        viewModel = PixHomeViewModel(
            userPreferences,
            getPixUserDataUseCase,
            getPixAccountBalanceUseCase,
            getPixMasterKeyUseCase
        )

        userDataUiResult = viewModel.userDataUiResult.captureValues()
        accountBalanceUiState = viewModel.accountBalanceUiState.captureValues()
        masterKeyUiState = viewModel.masterKeyUiState.captureValues()
    }

    private fun assertAccountBalanceLoadingState(state: AccountBalanceUiState?) {
        assertThat(state).isInstanceOf(AccountBalanceUiState.Loading::class.java)
    }

    private fun assertMasterKeyLoadingState(state: MasterKeyUiState?) {
        assertThat(state).isInstanceOf(MasterKeyUiState.Loading::class.java)
    }

    @Test
    fun `it should set UserDataUiState to liveData on loadUserData call`() = runTest {
        // given
        coEvery { getPixUserDataUseCase() } returns UserDataUiResult.WithOnlyOptionalUserName("")

        // when
        viewModel.loadUserData()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(userDataUiResult[0]).isInstanceOf(UserDataUiResult::class.java)
    }

    @Test
    fun `it should set success state and update account balance store correctly on loadAccountBalance call`() = runTest {
        // given
        val expectedAccountBalanceStore = PixAccountBalanceStore(
            balance = pixAccountBalanceEntity.currentBalance,
            updatedAt = pixAccountBalanceEntity.timeOfRequest
        )
        coEvery { getPixAccountBalanceUseCase() } returns CieloDataResult.Success(pixAccountBalanceEntity)

        // when
        viewModel.loadAccountBalance()

        // then
        dispatcherRule.advanceUntilIdle()

        assertAccountBalanceLoadingState(accountBalanceUiState[0])

        assertThat(accountBalanceUiState[1])
            .isInstanceOf(AccountBalanceUiState.Success::class.java)

        assertEquals(expectedAccountBalanceStore, viewModel.accountBalanceStore)
    }

    @Test
    fun `it should set error state on error result of loadAccountBalance call`() = runTest {
        // given
        coEvery { getPixAccountBalanceUseCase() } returns errorResult

        // when
        viewModel.loadAccountBalance()

        // then
        dispatcherRule.advanceUntilIdle()

        assertAccountBalanceLoadingState(accountBalanceUiState[0])
        assertThat(accountBalanceUiState[1]).isInstanceOf(AccountBalanceUiState.Error::class.java)
    }

    @Test
    fun `it should set empty state on error result of loadAccountBalance call`() = runTest {
        // given
        coEvery { getPixAccountBalanceUseCase() } returns emptyResult

        // when
        viewModel.loadAccountBalance()

        // then
        dispatcherRule.advanceUntilIdle()

        assertAccountBalanceLoadingState(accountBalanceUiState[0])
        assertThat(accountBalanceUiState[1]).isInstanceOf(AccountBalanceUiState.Error::class.java)
    }

    @Test
    fun `it should set MasterKeyFound state with correct data and update keysStore on loadMasterKey call`() = runTest {
        // given
        val masterKeyFoundResult = GetPixMasterKeyUseCase.Result.MasterKeyFound(
            GetPixMasterKeyUseCase.Data(
                keys = PixKeysFactory.WithMasterKey.keyItems,
                masterKey = PixKeysFactory.WithMasterKey.masterKey
            )
        )
        val expectedKeysStore = PixKeysStore(
            keys = PixKeysFactory.WithMasterKey.keyItems,
            masterKey = PixKeysFactory.WithMasterKey.masterKey
        )
        coEvery { getPixMasterKeyUseCase() } returns CieloDataResult.Success(masterKeyFoundResult)

        // when
        viewModel.loadMasterKey()

        // then
        dispatcherRule.advanceUntilIdle()

        assertMasterKeyLoadingState(masterKeyUiState[0])

        assertThat(masterKeyUiState[1])
            .isInstanceOf(MasterKeyUiState.MasterKeyFound::class.java)

        val actualState = masterKeyUiState[1] as MasterKeyUiState.MasterKeyFound

        assertEquals(masterKeyFoundResult.data.keys, actualState.keys)
        assertEquals(masterKeyFoundResult.data.masterKey, actualState.masterKey)
        assertEquals(masterKeyFoundResult.data.shouldShowAlert, actualState.showAlert)

        assertEquals(expectedKeysStore, viewModel.keysStore)
    }

    @Test
    fun `it should set MasterKeyNotFound state with correct data and update keysStore on loadMasterKey call`() = runTest {
        // given
        val masterKeyNotFoundResult = GetPixMasterKeyUseCase.Result.MasterKeyNotFound(
            GetPixMasterKeyUseCase.Data(
                keys = PixKeysFactory.WithoutMasterKey.keyItems,
            )
        )
        val expectedKeysStore = PixKeysStore(
            keys = PixKeysFactory.WithoutMasterKey.keyItems,
        )
        coEvery { getPixMasterKeyUseCase() } returns CieloDataResult.Success(masterKeyNotFoundResult)

        // when
        viewModel.loadMasterKey()

        // then
        dispatcherRule.advanceUntilIdle()

        assertMasterKeyLoadingState(masterKeyUiState[0])

        assertThat(masterKeyUiState[1])
            .isInstanceOf(MasterKeyUiState.MasterKeyNotFound::class.java)

        val actualState = masterKeyUiState[1] as MasterKeyUiState.MasterKeyNotFound

        assertEquals(masterKeyNotFoundResult.data.keys, actualState.keys)
        assertEquals(masterKeyNotFoundResult.data.shouldShowAlert, actualState.showAlert)

        assertEquals(expectedKeysStore, viewModel.keysStore)
    }

    @Test
    fun `it should set NoKeysFound state with correct data on loadMasterKey call`() = runTest {
        // given
        coEvery { getPixMasterKeyUseCase() } returns CieloDataResult.Success(GetPixMasterKeyUseCase.Result.NoKeysFound)

        // when
        viewModel.loadMasterKey()

        // then
        dispatcherRule.advanceUntilIdle()

        assertMasterKeyLoadingState(masterKeyUiState[0])

        assertThat(masterKeyUiState[1])
            .isInstanceOf(MasterKeyUiState.NoKeysFound::class.java)
    }

    @Test
    fun `it should set error state on error result of loadMasterKey call`() = runTest {
        // given
        coEvery { getPixMasterKeyUseCase() } returns errorResult

        // when
        viewModel.loadMasterKey()

        // then
        dispatcherRule.advanceUntilIdle()

        assertMasterKeyLoadingState(masterKeyUiState[0])
        assertThat(masterKeyUiState[1]).isInstanceOf(MasterKeyUiState.Error::class.java)
    }

    @Test
    fun `it should set empty state on error result of loadMasterKey call`() = runTest {
        // given
        coEvery { getPixMasterKeyUseCase() } returns emptyResult

        // when
        viewModel.loadMasterKey()

        // then
        dispatcherRule.advanceUntilIdle()

        assertMasterKeyLoadingState(masterKeyUiState[0])
        assertThat(masterKeyUiState[1]).isInstanceOf(MasterKeyUiState.Error::class.java)
    }

}