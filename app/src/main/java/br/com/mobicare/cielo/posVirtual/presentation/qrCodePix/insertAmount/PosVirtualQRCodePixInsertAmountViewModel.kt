package br.com.mobicare.cielo.posVirtual.presentation.qrCodePix.insertAmount

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.chargeback.utils.UiLoadingState
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.posVirtual.data.model.request.PosVirtualCreateQRCodeRequest
import br.com.mobicare.cielo.posVirtual.domain.useCase.PostPosVirtualCreateQRCodePixUseCase
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualConstants.POS_VIRTUAL_ERROR_CODE_INTEGRATION_ERROR
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualConstants.POS_VIRTUAL_ERROR_CODE_INVALID_AMOUNT
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualConstants.POS_VIRTUAL_ERROR_CODE_LIMIT_EXCEEDED
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualConstants.POS_VIRTUAL_ERROR_CODE_TIME_OUT
import br.com.mobicare.cielo.posVirtual.utils.UIPosVirtualQRCodePixState
import kotlinx.coroutines.launch
import java.math.BigDecimal

class PosVirtualQRCodePixInsertAmountViewModel constructor(
    private val postPosVirtualCreateQRCodePixUseCase: PostPosVirtualCreateQRCodePixUseCase,
    private val userObjUseCase: GetUserObjUseCase
) : ViewModel() {

    private val _uiPosVirtualQRCodePixStateLiveData = MutableLiveData<UIPosVirtualQRCodePixState>()
    val uiPosVirtualQRCodePixStateLiveData: LiveData<UIPosVirtualQRCodePixState> get() = _uiPosVirtualQRCodePixStateLiveData

    private val _loadingState = MutableLiveData<UiLoadingState>()
    val loadingState: LiveData<UiLoadingState> get() = _loadingState

    fun generateQRCode(
        context: Context?,
        otpCode: String,
        amount: BigDecimal,
        logicalNumber: String
    ) {
        viewModelScope.launch {
            postPosVirtualCreateQRCodePixUseCase.invoke(
                otpCode,
                PosVirtualCreateQRCodeRequest(logicalNumber = logicalNumber, amount = amount)
            ).onSuccess {
                _uiPosVirtualQRCodePixStateLiveData.value = UIPosVirtualQRCodePixState.Success(it)
            }.onEmpty {
                _uiPosVirtualQRCodePixStateLiveData.value = UIPosVirtualQRCodePixState.GenericError(null)
            }.onError {
                val error = it.apiException.newErrorMessage

                context?.let { itContext ->
                    newErrorHandler(context = itContext,
                        getUserObjUseCase = userObjUseCase,
                        newErrorMessage = error,
                        onHideLoading = {
                            _loadingState.value = UiLoadingState.HideLoading
                        },
                        onErrorAction = {
                            showError(error)
                        })
                } ?: showError(error)
            }
        }
    }

    private fun showError(error: NewErrorMessage? = null) {
        if (error?.flagErrorCode?.contains(Text.OTP) == true){
            _uiPosVirtualQRCodePixStateLiveData.value =
                UIPosVirtualQRCodePixState.TokenError(error)
        }else{
            when (error?.flagErrorCode) {
                POS_VIRTUAL_ERROR_CODE_TIME_OUT -> {
                    _uiPosVirtualQRCodePixStateLiveData.value =
                        UIPosVirtualQRCodePixState.TimeOutError(error)
                }
                POS_VIRTUAL_ERROR_CODE_INVALID_AMOUNT -> {
                    _uiPosVirtualQRCodePixStateLiveData.value =
                        UIPosVirtualQRCodePixState.InvalidAmountError(error)
                }
                POS_VIRTUAL_ERROR_CODE_INTEGRATION_ERROR -> {
                    _uiPosVirtualQRCodePixStateLiveData.value =
                        UIPosVirtualQRCodePixState.IntegrationError(error)
                }
                POS_VIRTUAL_ERROR_CODE_LIMIT_EXCEEDED -> {
                    _uiPosVirtualQRCodePixStateLiveData.value =
                        UIPosVirtualQRCodePixState.LimitExceededError(error)
                }
                else -> {
                    _uiPosVirtualQRCodePixStateLiveData.value =
                        UIPosVirtualQRCodePixState.GenericError(error)
                }
            }
        }
    }

}