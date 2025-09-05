package br.com.mobicare.cielo.component.requiredDataField.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.component.requiredDataField.data.model.request.Field
import br.com.mobicare.cielo.component.requiredDataField.data.model.request.Order
import br.com.mobicare.cielo.component.requiredDataField.data.model.request.OrdersRequest
import br.com.mobicare.cielo.component.requiredDataField.domain.useCase.PostUpdateDataRequiredDataFieldUseCase
import br.com.mobicare.cielo.component.requiredDataField.utils.RequiredDataFieldConstants.REQUIRED_DATA_FIELD_INVALID_DATA_ERROR
import br.com.mobicare.cielo.component.requiredDataField.utils.RequiredDataFieldConstants.REQUIRED_DATA_FIELD_ORDER_TYPE
import br.com.mobicare.cielo.component.requiredDataField.utils.UiRequiredDataFieldState
import kotlinx.coroutines.launch

class RequiredDataFieldViewModel(
    private val getUserObjUseCase: GetUserObjUseCase,
    private val postUpdateDataRequiredDataFieldUseCase: PostUpdateDataRequiredDataFieldUseCase
) : ViewModel() {

    private val _requiredDataFieldState = MutableLiveData<UiRequiredDataFieldState>()
    val requiredDataFieldState: LiveData<UiRequiredDataFieldState> get() = _requiredDataFieldState

    fun sendDataField(context: Context?, otpCode: String, fields: List<Field>, order: Order?) {
        viewModelScope.launch {
            postUpdateDataRequiredDataFieldUseCase(
                otpCode,
                generateRequest(fields, order)
            )
                .onSuccess {
                    _requiredDataFieldState.value =
                        UiRequiredDataFieldState.Success(it.orderId.orEmpty())
                }
                .onEmpty {
                    _requiredDataFieldState.value = UiRequiredDataFieldState.GenericError()
                }
                .onError {
                    handleError(context, it.apiException.newErrorMessage)
                }
        }
    }

    private fun generateRequest(fields: List<Field>, order: Order?): OrdersRequest {
        return OrdersRequest(
            type = REQUIRED_DATA_FIELD_ORDER_TYPE,
            order = order,
            registrationData = fields
        )
    }

    private suspend fun handleError(
        context: Context?,
        error: NewErrorMessage
    ) {
        context?.let { itContext ->
            newErrorHandler(
                context = itContext,
                getUserObjUseCase = getUserObjUseCase,
                newErrorMessage = error,
                onErrorAction = {
                    setError(error)
                }
            )
        } ?: run {
            setError(error)
        }
    }

    private fun setError(error: NewErrorMessage) {
        with(error.flagErrorCode) {
            when {
                this.contains(Text.OTP) -> {
                    _requiredDataFieldState.value = UiRequiredDataFieldState.TokenError(error)
                }
                this == REQUIRED_DATA_FIELD_INVALID_DATA_ERROR -> {
                    _requiredDataFieldState.value =
                        UiRequiredDataFieldState.InvalidDataError(R.string.required_data_field_error_invalid_data_message)
                }
                else -> {
                    _requiredDataFieldState.value = UiRequiredDataFieldState.GenericError(error)
                }
            }
        }
    }

}