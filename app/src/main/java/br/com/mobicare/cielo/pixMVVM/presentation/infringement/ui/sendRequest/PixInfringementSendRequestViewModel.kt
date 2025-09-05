package br.com.mobicare.cielo.pixMVVM.presentation.infringement.ui.sendRequest

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
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixCreateNotifyInfringementRequest
import br.com.mobicare.cielo.pixMVVM.domain.model.PixCreateNotifyInfringement
import br.com.mobicare.cielo.pixMVVM.domain.usecase.PostPixInfringementUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.infringement.utils.UIPixInfringementSendRequestState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PixInfringementSendRequestViewModel(
    private val getUserObjUseCase: GetUserObjUseCase,
    private val postPixInfringementUseCase: PostPixInfringementUseCase
) : ViewModel() {

    private val _uiState = MutableLiveData<UIPixInfringementSendRequestState>()
    val uiState: LiveData<UIPixInfringementSendRequestState> get() = _uiState

    private var _pixCreateNotifyInfringement: PixCreateNotifyInfringement? = null

    private val _pixCreateNotifyInfringementRequest
        get() = PixCreateNotifyInfringementRequest(
            idEndToEnd = _pixCreateNotifyInfringement?.idEndToEnd,
            message = _pixCreateNotifyInfringement?.message,
            situationType = _pixCreateNotifyInfringement?.situationType,
            reasonType = _pixCreateNotifyInfringement?.reasonType,
            amount = _pixCreateNotifyInfringement?.amount,
            merchantId = _pixCreateNotifyInfringement?.merchantId
        )

    fun setData(
        pixCreateNotifyInfringement: PixCreateNotifyInfringement?
    ) {
        _pixCreateNotifyInfringement = pixCreateNotifyInfringement
    }

    fun sendRequest() {
        _uiState.value = UIPixInfringementSendRequestState.ShowLoading

        viewModelScope.launch {
            postPixInfringementUseCase.invoke(_pixCreateNotifyInfringementRequest)
                .onSuccess {
                    _uiState.value = UIPixInfringementSendRequestState.HideLoading
                    _uiState.value = UIPixInfringementSendRequestState.Success
                }
                .onEmpty {
                    _uiState.value = UIPixInfringementSendRequestState.HideLoading
                    _uiState.value = UIPixInfringementSendRequestState.Error()
                }
                .onError {
                    handleError(it.apiException.newErrorMessage)
                }
        }
    }

    private suspend fun handleError(error: NewErrorMessage) {
        newErrorHandler(
            getUserObjUseCase = getUserObjUseCase,
            newErrorMessage = error,
            onHideLoading = {
                _uiState.value = UIPixInfringementSendRequestState.HideLoading
            },
            onErrorAction = {
                _uiState.value = UIPixInfringementSendRequestState.HideLoading
                _uiState.value = UIPixInfringementSendRequestState.Error(error)
            }
        )
    }

}