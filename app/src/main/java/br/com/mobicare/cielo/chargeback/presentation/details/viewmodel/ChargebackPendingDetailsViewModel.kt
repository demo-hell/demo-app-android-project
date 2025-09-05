package br.com.mobicare.cielo.chargeback.presentation.details.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackAcceptItemRequest
import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackAcceptRequest
import br.com.mobicare.cielo.chargeback.domain.model.Chargeback
import br.com.mobicare.cielo.chargeback.domain.useCase.PutChargebackAcceptUseCase
import br.com.mobicare.cielo.chargeback.utils.UiAcceptState
import br.com.mobicare.cielo.chargeback.utils.UiLoadingState
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import kotlinx.coroutines.launch

class ChargebackPendingDetailsViewModel constructor(
    private val putChargebackAcceptUseCase: PutChargebackAcceptUseCase,
    private val userObjUseCase: GetUserObjUseCase
) : ViewModel() {

    private val _chargebackAcceptUiState = MutableLiveData<UiAcceptState>()
    val chargebackAcceptUiState: LiveData<UiAcceptState> get() = _chargebackAcceptUiState

    private val _loadingState = MutableLiveData<UiLoadingState>()
    val loadingState: LiveData<UiLoadingState> get() = _loadingState

    fun chargebackAccept(
        context: Context?,
        otpCode: String,
        chargeback: Chargeback?
    ) {
        viewModelScope.launch {
            if (chargeback?.merchantId != null && chargeback.chargebackId != null)
                putChargebackAcceptUseCase.invoke(
                    otpCode,
                    ChargebackAcceptRequest(
                        chargebacks = listOf(
                            ChargebackAcceptItemRequest(
                                chargebackId = chargeback.chargebackId.toString(),
                                merchantId = chargeback.merchantId.toString(),
                            )
                        )
                    )
                ).onSuccess {
                    _chargebackAcceptUiState.value = UiAcceptState.Success
                }.onEmpty {
                    _chargebackAcceptUiState.value = UiAcceptState.Success
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
        if (error?.flagErrorCode?.contains(Text.OTP) == true)
            _chargebackAcceptUiState.value = UiAcceptState.ErrorToken(error)
        else
            _chargebackAcceptUiState.value = UiAcceptState.Error(error)
    }

}