package br.com.mobicare.cielo.pixMVVM.presentation.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixAccountBalanceUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixMasterKeyUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixUserDataUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.home.models.PixKeysStore
import br.com.mobicare.cielo.pixMVVM.presentation.home.utils.MasterKeyUiState
import br.com.mobicare.cielo.pixMVVM.presentation.home.utils.UserDataUiResult
import kotlinx.coroutines.launch

class PixHomeViewModel(
    private val userPreferences: UserPreferences,
    private val getPixUserDataUseCase: GetPixUserDataUseCase,
    getPixAccountBalanceUseCase: GetPixAccountBalanceUseCase,
    private val getPixMasterKeyUseCase: GetPixMasterKeyUseCase
) : PixAccountBalanceViewModel(getPixAccountBalanceUseCase) {

    private val _userDataUiResult = MutableLiveData<UserDataUiResult>()
    val userDataUiResult: LiveData<UserDataUiResult> = _userDataUiResult

    private val _masterKeyUiState = MutableLiveData<MasterKeyUiState>()
    val masterKeyUiState: LiveData<MasterKeyUiState> = _masterKeyUiState

    private var _keysStore = PixKeysStore()
    val keysStore get() = _keysStore

    val wasOnboardingPixKeysViewed get() = userPreferences.isOnboardingPixKeysWasViewed

    fun loadUserData() {
        _userDataUiResult.value = getPixUserDataUseCase()
    }

    fun loadMasterKey() {
        viewModelScope.launch {
            _masterKeyUiState.postValue(MasterKeyUiState.Loading)

            getPixMasterKeyUseCase()
                .onSuccess {
                    handleMasterKeyResult(it)
                }.onError {
                    _masterKeyUiState.postValue(MasterKeyUiState.Error(it.apiException.message))
                }.onEmpty {
                    _masterKeyUiState.postValue(MasterKeyUiState.Error())
                }
        }
    }

    private fun handleMasterKeyResult(result: GetPixMasterKeyUseCase.Result) {
        when (result) {
            is GetPixMasterKeyUseCase.Result.MasterKeyFound -> result.data.let {
                updateKeysStore(it)
                setMasterKeyFoundState(it)
            }
            is GetPixMasterKeyUseCase.Result.MasterKeyNotFound -> result.data.let {
                updateKeysStore(it)
                setMasterKeyNotFoundState(it)
            }
            is GetPixMasterKeyUseCase.Result.NoKeysFound -> setNoKeysFoundState()
        }
    }

    private fun updateKeysStore(data: GetPixMasterKeyUseCase.Data) {
        _keysStore = _keysStore.copy(
            keys = data.keys,
            masterKey = data.masterKey
        )
    }

    private fun setMasterKeyFoundState(data: GetPixMasterKeyUseCase.Data) {
        _masterKeyUiState.postValue(
            MasterKeyUiState.MasterKeyFound(
                keys = data.keys,
                masterKey = data.masterKey,
                showAlert = data.shouldShowAlert
            )
        )
    }

    private fun setMasterKeyNotFoundState(data: GetPixMasterKeyUseCase.Data) {
        _masterKeyUiState.postValue(
            MasterKeyUiState.MasterKeyNotFound(
                keys = data.keys,
                showAlert = data.shouldShowAlert
            )
        )
    }

    private fun setNoKeysFoundState() {
        _masterKeyUiState.postValue(MasterKeyUiState.NoKeysFound)
    }

}