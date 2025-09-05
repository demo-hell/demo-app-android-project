package br.com.mobicare.cielo.chargeback.presentation.refuse

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackRefuseRequest
import br.com.mobicare.cielo.chargeback.domain.model.Chargeback
import br.com.mobicare.cielo.chargeback.domain.useCase.PutChargebackRefuseUseCase
import br.com.mobicare.cielo.chargeback.utils.UiLoadingState
import br.com.mobicare.cielo.chargeback.utils.UiRefuseState
import br.com.mobicare.cielo.commons.constants.Intent.JPG
import br.com.mobicare.cielo.commons.constants.Intent.PDF
import br.com.mobicare.cielo.commons.constants.Text.OTP
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import kotlinx.coroutines.launch

class ChargebackRefuseViewModel constructor(
    private val chargebackRefuseUseCase: PutChargebackRefuseUseCase,
    private val userObjUseCase: GetUserObjUseCase
) : ViewModel() {

    private val _chargebackRefuseLiveData = MutableLiveData<UiRefuseState>()
    val chargebackRefuseLiveData: LiveData<UiRefuseState> get() = _chargebackRefuseLiveData

    private val _loadingState = MutableLiveData<UiLoadingState>()
    val loadingState: LiveData<UiLoadingState> get() = _loadingState

    fun checkFileType(type: String) {
        viewModelScope.launch {
            val fileType = type.lowercase()
            if (fileType != JPG && fileType != PDF)
                _chargebackRefuseLiveData.value = UiRefuseState.FileExtensionIsNotAccepted
            else
                _chargebackRefuseLiveData.value = UiRefuseState.FileExtensionIsAccepted
        }
    }

    fun chargebackRefuse(
        context: Context?,
        otpCode: String,
        reasonToRefuse: String?,
        fileName: String?,
        fileBase64: String?,
        chargeback: Chargeback?
    ) {
        viewModelScope.launch {
            if (fileName.isNullOrEmpty().not() &&
                fileBase64.isNullOrEmpty().not() &&
                reasonToRefuse.isNullOrEmpty().not() &&
                chargeback?.caseId != null &&
                chargeback.merchantId != null &&
                chargeback.chargebackId != null
            )
                chargebackRefuseUseCase.invoke(
                    otpCode,
                    ChargebackRefuseRequest(
                        fileName = fileName,
                        reasonToRefuse = reasonToRefuse,
                        chargebackId = chargeback.chargebackId.toString(),
                        merchantId = chargeback.merchantId.toString(),
                        fileBase64 = fileBase64
                    )
                ).onSuccess {
                    _chargebackRefuseLiveData.value = UiRefuseState.Success
                }.onEmpty {
                    _chargebackRefuseLiveData.value = UiRefuseState.Success
                }.onError {
                    val error = it.apiException.newErrorMessage
                    context?.let { itContext ->
                        newErrorHandler(
                            context = itContext,
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
            else showError()
        }
    }

    private fun showError(error: NewErrorMessage? = null) {
        if (error?.flagErrorCode?.contains(OTP) == true)
            _chargebackRefuseLiveData.value = UiRefuseState.ErrorToken(error)
        else
            _chargebackRefuseLiveData.value = UiRefuseState.Error(error)
    }
}