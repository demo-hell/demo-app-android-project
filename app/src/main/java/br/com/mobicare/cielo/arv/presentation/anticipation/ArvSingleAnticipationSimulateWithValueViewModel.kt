package br.com.mobicare.cielo.arv.presentation.anticipation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.domain.useCase.GetArvSingleAnticipationWithValueNewUseCase
import br.com.mobicare.cielo.arv.utils.ArvConstants
import br.com.mobicare.cielo.arv.utils.UiArvSingleWithValueState
import br.com.mobicare.cielo.commons.constants.HTTP_UNKNOWN
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.utils.getErrorMessage
import kotlinx.coroutines.launch

class ArvSingleAnticipationSimulateWithValueViewModel(
    private val arvSingleAnticipationWithValueNewUseCase: GetArvSingleAnticipationWithValueNewUseCase,
    private val getUserObjUseCase: GetUserObjUseCase
) : ViewModel() {
    private val _arvSingleAnticipationWithValueMutableLiveData =
        MutableLiveData<UiArvSingleWithValueState?>()
    val arvSingleAnticipationWithValueLiveData: LiveData<UiArvSingleWithValueState?> get() = _arvSingleAnticipationWithValueMutableLiveData

    var receivableType: String? = ArvConstants.CIELO_NEGOTIATION_TYPE

    fun getArvSingleAnticipationWithValue(
        value: Double,
        initialDate: String?,
        finalDate: String?,
    ) {
        viewModelScope.launch {
            _arvSingleAnticipationWithValueMutableLiveData.value =
                UiArvSingleWithValueState.ShowLoadingArvSingleWithValue
            arvSingleAnticipationWithValueNewUseCase(
                negotiationType = receivableType,
                value = value,
                initialDate = initialDate,
                finalDate = finalDate,
            )
                .onSuccess { anticipation ->
                    _arvSingleAnticipationWithValueMutableLiveData.value =
                        UiArvSingleWithValueState.HideLoadingArvSingleWithValue
                    _arvSingleAnticipationWithValueMutableLiveData.value =
                        anticipation.let { arvAnticipation ->
                            UiArvSingleWithValueState.SuccessArvSingleWithValue(
                                mapNegotiationType(
                                    arvAnticipation,
                                    receivableType
                                )
                            )
                        }

                }.onEmpty {
                    _arvSingleAnticipationWithValueMutableLiveData.value =
                        UiArvSingleWithValueState.ErrorArvSingleWithValueMessage(R.string.anticipation_error)

                }.onError { apiError ->
                    _arvSingleAnticipationWithValueMutableLiveData.value =
                        UiArvSingleWithValueState.HideLoadingArvSingleWithValue
                    val error = apiError.apiException.newErrorMessage
                    val message =
                        if (error.httpCode == HTTP_UNKNOWN) error.message else error.getErrorMessage(
                            R.string.anticipation_error
                        )
                    newErrorHandler(
                        getUserObjUseCase = getUserObjUseCase,
                        newErrorMessage = error,
                        onErrorAction = {
                            _arvSingleAnticipationWithValueMutableLiveData.value =
                                UiArvSingleWithValueState.ErrorArvSingleWithValue(error, message)
                        }
                    )
                }
        }
    }

    private fun mapNegotiationType(
        anticipation: ArvAnticipation,
        negotiationType: String?
    ): ArvAnticipation {
        return anticipation.copy(negotiationType = negotiationType)
    }

    fun resetState() {
        _arvSingleAnticipationWithValueMutableLiveData.value = null
    }
}
