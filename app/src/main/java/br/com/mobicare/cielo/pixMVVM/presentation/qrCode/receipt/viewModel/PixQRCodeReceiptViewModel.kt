package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.receipt.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferResult
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixTransferDetailsUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixTransferScheduleDetailUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.utils.PixReceiptQRCodeUIState
import kotlinx.coroutines.launch

class PixQRCodeReceiptViewModel(
    private val getUserObjUseCase: GetUserObjUseCase,
    private val getPixTransferDetailsUseCase: GetPixTransferDetailsUseCase,
    private val getPixTransferScheduleDetailUseCase: GetPixTransferScheduleDetailUseCase,
) : ViewModel() {
    private val _uiState = MutableLiveData<PixReceiptQRCodeUIState>()
    val uiState get() = _uiState

    fun getTransferOrSchedulingDetails(transferResult: PixTransferResult) {
        setState(PixReceiptQRCodeUIState.ShowLoading)

        viewModelScope.launch {
            if (transferResult.schedulingCode.isNullOrBlank().not()) {
                getScheduleDetail(transferResult.schedulingCode)
            } else {
                getTransferDetail(transferResult.endToEndId, transferResult.transactionCode)
            }
        }
    }

    private suspend fun getTransferDetail(
        endToEndID: String?,
        transactionCode: String?,
    ) {
        getPixTransferDetailsUseCase
            .invoke(
                GetPixTransferDetailsUseCase.Params(endToEndID, transactionCode),
            ).onSuccess {
                setState(PixReceiptQRCodeUIState.HideLoading)
                setState(PixReceiptQRCodeUIState.TransactionExecutedSuccess(it))
            }.onEmpty {
                setState(PixReceiptQRCodeUIState.HideLoading)
                setState(PixReceiptQRCodeUIState.Error)
            }.onError {
                handleError(it.apiException.newErrorMessage)
            }
    }

    private suspend fun getScheduleDetail(schedulingCode: String?) {
        getPixTransferScheduleDetailUseCase
            .invoke(
                GetPixTransferScheduleDetailUseCase.Params(schedulingCode),
            ).onSuccess {
                setState(PixReceiptQRCodeUIState.HideLoading)
                setState(PixReceiptQRCodeUIState.TransactionScheduledSuccess(it))
            }.onEmpty {
                setState(PixReceiptQRCodeUIState.HideLoading)
                setState(PixReceiptQRCodeUIState.Error)
            }.onError {
                handleError(it.apiException.newErrorMessage)
            }
    }

    private suspend fun handleError(error: NewErrorMessage) {
        newErrorHandler(
            getUserObjUseCase = getUserObjUseCase,
            newErrorMessage = error,
            onErrorAction = {
                setState(PixReceiptQRCodeUIState.HideLoading)
                setState(PixReceiptQRCodeUIState.Error)
            },
            onHideLoading = {
                setState(PixReceiptQRCodeUIState.ReturnBackScreen)
            },
        )
    }

    private fun setState(state: PixReceiptQRCodeUIState) {
        _uiState.value = state
    }
}
