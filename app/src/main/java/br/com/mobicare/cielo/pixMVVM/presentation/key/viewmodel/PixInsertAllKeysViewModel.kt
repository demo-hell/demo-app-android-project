package br.com.mobicare.cielo.pixMVVM.presentation.key.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.constants.BRAZIL_COUNTRY_CODE
import br.com.mobicare.cielo.commons.constants.CNPJ
import br.com.mobicare.cielo.commons.constants.CPF
import br.com.mobicare.cielo.commons.constants.ELEVEN
import br.com.mobicare.cielo.commons.constants.HTTP_ENHANCE
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.utils.removeNonNumbers
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixValidateKeyUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.key.enums.PixKeyTypeInput
import br.com.mobicare.cielo.pixMVVM.presentation.key.utils.PixInsertAllKeysUIState
import kotlinx.coroutines.launch

class PixInsertAllKeysViewModel(
    private val getUserObjUseCase: GetUserObjUseCase,
    private val getPixValidateKeyUseCase: GetPixValidateKeyUseCase,
) : ViewModel() {

    private val _uiState = MutableLiveData<PixInsertAllKeysUIState>()
    val uiState: LiveData<PixInsertAllKeysUIState> = _uiState

    private val _keyTypeInput = MutableLiveData<PixKeyTypeInput>()
    val keyTypeInput: LiveData<PixKeyTypeInput> = _keyTypeInput

    private var errorCounter = ZERO

    fun setKeyTypeButton(keyTypeInput: PixKeyTypeInput) {
        _keyTypeInput.value = keyTypeInput
    }

    fun validateKey(key: String) {
        viewModelScope.launch {
            _uiState.value = PixInsertAllKeysUIState.ShowLoading

            getPixValidateKeyUseCase.invoke(
                getKeyValue(key), getKeyType(key)
            ).onSuccess {
                errorCounter = ZERO
                setHideLoading()
                _uiState.value = PixInsertAllKeysUIState.Success(it)
            }.onEmpty {
                errorCounter++
                processError(null)
            }.onError {
                errorCounter++
                handleError(it.apiException.newErrorMessage)
            }
        }
    }

    private fun getKeyValue(key: String): String {
        return when (_keyTypeInput.value) {
            PixKeyTypeInput.EMAIL, PixKeyTypeInput.EVP -> key
            PixKeyTypeInput.PHONE -> BRAZIL_COUNTRY_CODE + key.removeNonNumbers()
            else -> key.removeNonNumbers()
        }
    }

    private fun getKeyType(key: String): String {
        return when (_keyTypeInput.value) {
            PixKeyTypeInput.EMAIL, PixKeyTypeInput.EVP, PixKeyTypeInput.PHONE -> _keyTypeInput.value?.name.orEmpty()
            else -> if (key.removeNonNumbers().length == ELEVEN) CPF else CNPJ
        }
    }

    private suspend fun handleError(error: NewErrorMessage) {
        newErrorHandler(
            getUserObjUseCase = getUserObjUseCase,
            newErrorMessage = error,
            onHideLoading = {
                setHideLoading()
            },
            onErrorAction = {
                processError(error)
            }
        )
    }

    private fun processError(error: NewErrorMessage?) {
        setHideLoading()
        _uiState.value = when {
            (error?.httpCode == HTTP_ENHANCE) -> PixInsertAllKeysUIState.InputError(error)
            (errorCounter < THREE) -> PixInsertAllKeysUIState.GenericError(error)
            else -> PixInsertAllKeysUIState.UnavailableService(error)
        }
    }

    private fun setHideLoading() {
        _uiState.value = PixInsertAllKeysUIState.HideLoading
    }

}