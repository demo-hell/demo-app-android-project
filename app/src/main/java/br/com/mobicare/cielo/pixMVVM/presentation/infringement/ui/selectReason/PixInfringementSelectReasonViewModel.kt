package br.com.mobicare.cielo.pixMVVM.presentation.infringement.ui.selectReason

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixEligibilityInfringementResponse
import br.com.mobicare.cielo.pixMVVM.domain.model.PixCreateNotifyInfringement
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixEligibilityInfringementUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.infringement.utils.UIPixInfringementSelectReasonState
import kotlinx.coroutines.launch

class PixInfringementSelectReasonViewModel(
    private val getUserObjUseCase: GetUserObjUseCase,
    private val getPixEligibilityInfringementUseCase: GetPixEligibilityInfringementUseCase,
) : ViewModel() {
    private val _uiState = MutableLiveData<UIPixInfringementSelectReasonState>()
    val uiState: LiveData<UIPixInfringementSelectReasonState> get() = _uiState

    private var _pixInfringementResponse: PixEligibilityInfringementResponse? = null
    private var _situationSelected: PixEligibilityInfringementResponse.Situation? = null

    fun start(idEndToEnd: String) {
        if (uiState.value == null) {
            _uiState.value = UIPixInfringementSelectReasonState.ShowLoading

            viewModelScope.launch {
                getPixEligibilityInfringement(idEndToEnd)
            }
        } else {
            _uiState.value = UIPixInfringementSelectReasonState.Success
        }
    }

    fun reloadGetPixEligibilityInfringement(idEndToEnd: String)  {
        _uiState.value = UIPixInfringementSelectReasonState.ShowLoading

        viewModelScope.launch {
            getPixEligibilityInfringement(idEndToEnd)
        }
    }

    fun getSituations(): List<PixEligibilityInfringementResponse.Situation> = _pixInfringementResponse?.situations ?: emptyList()

    fun getPixCreateNotifyInfringement(): PixCreateNotifyInfringement =
        PixCreateNotifyInfringement(
            idEndToEnd = _pixInfringementResponse?.idEndToEnd,
            amount = _pixInfringementResponse?.amount,
            date = _pixInfringementResponse?.transactionDate,
            institution = _pixInfringementResponse?.payee?.bank?.name,
            merchantId = _pixInfringementResponse?.merchantId,
            reasonType = _pixInfringementResponse?.reasonType,
            situationType = _situationSelected?.type,
            situationDescription = _situationSelected?.description,
        )

    fun setSituation(situation: PixEligibilityInfringementResponse.Situation) {
        _situationSelected = situation
        _uiState.value = UIPixInfringementSelectReasonState.NavigateToDetailWhatHappened
    }

    private suspend fun getPixEligibilityInfringement(idEndToEnd: String) {
        getPixEligibilityInfringementUseCase
            .invoke(idEndToEnd)
            .onSuccess {
                processEligibilityInfringement(it)
            }.onEmpty {
                onEmpty()
            }.onError {
                handleError(it.apiException.newErrorMessage)
            }
    }

    private fun processEligibilityInfringement(response: PixEligibilityInfringementResponse) {
        _pixInfringementResponse = response
        _uiState.value = UIPixInfringementSelectReasonState.HideLoading
        _uiState.value =
            if (response.isEligible == true) {
                UIPixInfringementSelectReasonState.Success
            } else {
                UIPixInfringementSelectReasonState.Ineligible(response.details.orEmpty())
            }
    }

    private fun onEmpty() {
        _uiState.value = UIPixInfringementSelectReasonState.HideLoading
        _uiState.value = UIPixInfringementSelectReasonState.Error()
    }

    private suspend fun handleError(error: NewErrorMessage) {
        newErrorHandler(
            getUserObjUseCase = getUserObjUseCase,
            newErrorMessage = error,
            onHideLoading = {
                _uiState.value = UIPixInfringementSelectReasonState.HideLoading
            },
            onErrorAction = {
                _uiState.value = UIPixInfringementSelectReasonState.HideLoading
                _uiState.value = UIPixInfringementSelectReasonState.Error(error)
            },
        )
    }
}
