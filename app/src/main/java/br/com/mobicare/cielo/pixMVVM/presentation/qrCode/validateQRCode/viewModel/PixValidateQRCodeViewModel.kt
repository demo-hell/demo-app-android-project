package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.validateQRCode.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.constants.EMPTY_STRING
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.pixMVVM.domain.model.PixDecodeQRCode
import br.com.mobicare.cielo.pixMVVM.domain.usecase.PostPixDecodeQRCodeUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.enums.PixQRCodeScreenEnum
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.utils.PixDecodeQRCodeUIState
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.utils.PixQRCodeUtils
import br.com.mobicare.cielo.pixMVVM.utils.PixConstants
import kotlinx.coroutines.launch

class PixValidateQRCodeViewModel(
    private val getUserObjUseCase: GetUserObjUseCase,
    private val postPixDecodeQRCodeUseCase: PostPixDecodeQRCodeUseCase,
) : ViewModel() {
    private var screenOriginDecode = PixQRCodeScreenEnum.DECODE

    private val _qrCode = MutableLiveData<String>()
    val qrCode: LiveData<String> get() = _qrCode

    private val _uiState = MutableLiveData<PixDecodeQRCodeUIState>()
    val uiState: LiveData<PixDecodeQRCodeUIState> get() = _uiState

    fun getScreenOriginDecode(): PixQRCodeScreenEnum = screenOriginDecode

    fun setScreenOriginDecode(screenOriginDecode: PixQRCodeScreenEnum) {
        this.screenOriginDecode = screenOriginDecode
    }

    fun setQRCode(qrCode: String) {
        _qrCode.value = qrCode
    }

    fun validateQRCode() {
        if (_qrCode.value.isNullOrEmpty() || _qrCode.value == PixConstants.ERROR_READ_QR_CODE) {
            _uiState.value = PixDecodeQRCodeUIState.GenericError(null)
            setStateDoNothing()
            return
        }

        _uiState.value = PixDecodeQRCodeUIState.ShowLoading
        viewModelScope.launch {
            postPixDecodeQRCodeUseCase
                .invoke(
                    PostPixDecodeQRCodeUseCase.Params(
                        qrCode = qrCode.value.orEmpty(),
                    ),
                ).onSuccess {
                    processDecodeQRCodeSuccess(it)
                }.onEmpty {
                    setStateHideLoading()
                    _uiState.value = PixDecodeQRCodeUIState.GenericError(null)
                    setStateDoNothing()
                }.onError {
                    handleError(it.apiException.newErrorMessage)
                }
        }
    }

    private fun processDecodeQRCodeSuccess(decodeQRCode: PixDecodeQRCode) {
        val amountIsZero = PixQRCodeUtils.getPaymentAmount(decodeQRCode) == ZERO_DOUBLE
        val isAllowedChangeValue =
            PixQRCodeUtils.isAllowedChangePaymentValue(decodeQRCode) || PixQRCodeUtils.isAllowedChangeChangeValue(decodeQRCode)

        setStateHideLoading()
        _uiState.value =
            when {
                isAllowedChangeValue.not() && amountIsZero -> PixDecodeQRCodeUIState.GenericError(null)
                amountIsZero ->
                    PixDecodeQRCodeUIState.NavigateToPixQRCodePaymentInsertAmount(
                        decodeQRCode,
                        PixQRCodeUtils.isPixTypeChange(decodeQRCode),
                    )
                else -> PixDecodeQRCodeUIState.NavigateToPixQRCodePaymentSummary(decodeQRCode)
            }
        setStateDoNothing()
    }

    private suspend fun handleError(error: NewErrorMessage) {
        newErrorHandler(
            getUserObjUseCase = getUserObjUseCase,
            newErrorMessage = error,
            onErrorAction = {
                setStateHideLoading()
                _uiState.value = PixDecodeQRCodeUIState.GenericError(error)
                setStateDoNothing()
            },
            onHideLoading = {
                setStateHideLoading()
                _uiState.value = PixDecodeQRCodeUIState.CloseActivity
                setStateDoNothing()
            },
        )
    }

    private fun setStateHideLoading() {
        _uiState.value = PixDecodeQRCodeUIState.HideLoading
    }

    private fun setStateDoNothing() {
        _uiState.value = PixDecodeQRCodeUIState.DoNothing
        setQRCode(EMPTY_STRING)
    }
}
