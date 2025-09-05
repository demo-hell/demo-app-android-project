package br.com.mobicare.cielo.pixMVVM.presentation.account.viewmodel

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
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixProfileRequest
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixScheduledSettlementRequest
import br.com.mobicare.cielo.pixMVVM.domain.usecase.ChangePixProfileUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.CreatePixScheduledSettlementUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.UpdatePixScheduledSettlementUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.account.utils.PixProfileUiState
import br.com.mobicare.cielo.pixMVVM.presentation.account.utils.PixScheduledTransferUiState
import kotlinx.coroutines.launch

class PixAccountChangeViewModel(
    private val getUserObjUseCase: GetUserObjUseCase,
    private val changePixProfileUseCase: ChangePixProfileUseCase,
    private val createPixScheduledSettlementUseCase: CreatePixScheduledSettlementUseCase,
    private val updatePixScheduledSettlementUseCase: UpdatePixScheduledSettlementUseCase
) : ViewModel() {

    private val _profileState = MutableLiveData<PixProfileUiState>()
    val profileState: LiveData<PixProfileUiState> get() = _profileState

    private val _scheduledTransferState = MutableLiveData<PixScheduledTransferUiState>()
    val scheduledTransferState: LiveData<PixScheduledTransferUiState> get() = _scheduledTransferState

    fun changeProfile(token: String, settlementActive: Boolean) {
        viewModelScope.launch {
            changePixProfileUseCase(
                ChangePixProfileUseCase.Params(
                    token = token,
                    request = PixProfileRequest(
                        settlementActive = settlementActive
                    )
                )
            )
                .onSuccess {
                    setProfileState(PixProfileUiState.Success)
                }.onError {
                    handleError(it.apiException.newErrorMessage) {
                        setProfileState(PixProfileUiState.Error)
                    }
                }.onEmpty {
                    setProfileState(PixProfileUiState.Error)
                }
        }
    }

    fun toggleScheduledTransfer(
        token: String,
        enableScheduledTransfer: Boolean,
        scheduledHours: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            createPixScheduledSettlementUseCase(
                CreatePixScheduledSettlementUseCase.Params(
                    token = token,
                    request = PixScheduledSettlementRequest(
                        settlementScheduled = enableScheduledTransfer,
                        listScheduled = scheduledHours
                    )
                )
            )
                .onSuccess {
                    setScheduledTransferState(PixScheduledTransferUiState.Success(it))
                }.onError {
                    handleError(it.apiException.newErrorMessage) {
                        setScheduledTransferState(PixScheduledTransferUiState.Error)
                    }
                }.onEmpty {
                    setScheduledTransferState(PixScheduledTransferUiState.Error)
                }
        }
    }

    fun updateScheduledTransfer(
        token: String,
        scheduledHours: List<String>
    ) {
        viewModelScope.launch {
            updatePixScheduledSettlementUseCase(
                UpdatePixScheduledSettlementUseCase.Params(
                    token = token,
                    scheduledList = scheduledHours
                )
            )
                .onSuccess {
                    setScheduledTransferState(PixScheduledTransferUiState.Success(it))
                }.onError {
                    handleError(it.apiException.newErrorMessage) {
                        setScheduledTransferState(PixScheduledTransferUiState.Error)
                    }
                }.onEmpty {
                    setScheduledTransferState(PixScheduledTransferUiState.Error)
                }
        }
    }

    private suspend fun handleError(error: NewErrorMessage, onErrorAction: () -> Unit) {
        newErrorHandler(
            getUserObjUseCase = getUserObjUseCase,
            newErrorMessage = error,
            onErrorAction = onErrorAction
        )
    }

    private fun setProfileState(state: PixProfileUiState) {
        _profileState.postValue(state)
    }

    private fun setScheduledTransferState(state: PixScheduledTransferUiState) {
        _scheduledTransferState.postValue(state)
    }

}