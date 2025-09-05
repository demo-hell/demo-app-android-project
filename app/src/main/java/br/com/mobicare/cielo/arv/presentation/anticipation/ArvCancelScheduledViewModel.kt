package br.com.mobicare.cielo.arv.presentation.anticipation

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.*
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.arv.data.model.request.ArvScheduledAnticipationCancelRequest
import br.com.mobicare.cielo.arv.data.model.request.CancelNegotiationType
import br.com.mobicare.cielo.arv.domain.useCase.CancelArvScheduledAnticipationUseCase
import br.com.mobicare.cielo.arv.utils.UiArvCancelScheduledAnticipationState
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import kotlinx.coroutines.launch

class ArvCancelScheduledViewModel(
    private val cancelArvScheduledAnticipationUseCase: CancelArvScheduledAnticipationUseCase,
    private val getUserObjUseCase: GetUserObjUseCase
) : ViewModel() {

    private val _arvCancelScheduledAnticipationState = MutableLiveData<UiArvCancelScheduledAnticipationState>()
    val arvCancelScheduledAnticipationState: LiveData<UiArvCancelScheduledAnticipationState> get() = _arvCancelScheduledAnticipationState


    fun cancelAnticipation(negotiationType: String) {
        viewModelScope.launch {
            _arvCancelScheduledAnticipationState.value = UiArvCancelScheduledAnticipationState.ShowLoading
            cancelArvScheduledAnticipationUseCase(
                ArvScheduledAnticipationCancelRequest(CancelNegotiationType.valueOf(negotiationType))
            ).onSuccess {
                _arvCancelScheduledAnticipationState.value =  UiArvCancelScheduledAnticipationState.HideLoading
                _arvCancelScheduledAnticipationState.value =  UiArvCancelScheduledAnticipationState.Success
            }.onEmpty {
                _arvCancelScheduledAnticipationState.value =  UiArvCancelScheduledAnticipationState.HideLoading
                _arvCancelScheduledAnticipationState.value =  UiArvCancelScheduledAnticipationState.Success
            }.onError {
                val error = it.apiException.newErrorMessage
                newErrorHandler(
                    getUserObjUseCase = getUserObjUseCase,
                    newErrorMessage = error,
                    onHideLoading = {
                        _arvCancelScheduledAnticipationState.value =
                            UiArvCancelScheduledAnticipationState.HideLoading
                    },
                    onErrorAction = {
                        setCancelAnticipationError(error)
                    }
                )
            }
        }
    }

    private fun setCancelAnticipationError(error: NewErrorMessage? = null) {
        _arvCancelScheduledAnticipationState.value = UiArvCancelScheduledAnticipationState.HideLoading
        _arvCancelScheduledAnticipationState.value =
            if (error?.flagErrorCode?.contains(Text.OTP) == true)
                UiArvCancelScheduledAnticipationState.ErrorToken(error)
            else
                UiArvCancelScheduledAnticipationState.Error(error)
    }
}
