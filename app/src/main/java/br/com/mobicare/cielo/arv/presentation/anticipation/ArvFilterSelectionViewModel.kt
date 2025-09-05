package br.com.mobicare.cielo.arv.presentation.anticipation


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.domain.useCase.GetArvSingleAnticipationWithFilterUseCase
import br.com.mobicare.cielo.arv.presentation.anticipation.adapter.ArvSelectableItem
import br.com.mobicare.cielo.arv.utils.ArvConstants
import br.com.mobicare.cielo.arv.utils.UiArvBrandsSelectionState
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import kotlinx.coroutines.launch

class ArvFilterSelectionViewModel(
    private val arvSingleAnticipationByBrandsUseCase: GetArvSingleAnticipationWithFilterUseCase,
    private val getUserObjUseCase: GetUserObjUseCase
) : ViewModel() {

    private val _arvBrandsSelectionMutableLiveData = MutableLiveData<UiArvBrandsSelectionState>()
    val arvBrandsSelectionLiveData: LiveData<UiArvBrandsSelectionState> get() = _arvBrandsSelectionMutableLiveData

    fun updateAnticipation(
        previousAnticipation: ArvAnticipation,
        selectedList: List<ArvSelectableItem>,
        receiveToday: Boolean
    ) {
        viewModelScope.launch {
            _arvBrandsSelectionMutableLiveData.value =
                UiArvBrandsSelectionState.ShowLoadingAnticipation

            val codesList = selectedList.filter {
                it.isSelected
            }.mapNotNull { it.code }

            arvSingleAnticipationByBrandsUseCase(
                negotiationType = previousAnticipation.negotiationType,
                initialDate = previousAnticipation.initialDate,
                endDate = previousAnticipation.finalDate,
                brandCodes = if(previousAnticipation.negotiationType == ArvConstants.CIELO_NEGOTIATION_TYPE) codesList else null,
                acquirerCode = if(previousAnticipation.negotiationType == ArvConstants.MARKET_NEGOTIATION_TYPE) codesList else null,
                receiveToday = receiveToday
            ).onSuccess { anticipation ->
                _arvBrandsSelectionMutableLiveData.value =
                    UiArvBrandsSelectionState.HideLoadingAnticipation

                _arvBrandsSelectionMutableLiveData.value =
                    UiArvBrandsSelectionState.SuccessLoadArvAnticipation(
                        anticipation.appendNotSelected(selectedList).mapDates(previousAnticipation)
                    )
            }.onError { error ->
                _arvBrandsSelectionMutableLiveData.value =
                    UiArvBrandsSelectionState.HideLoadingAnticipation

                newErrorHandler(
                    getUserObjUseCase = getUserObjUseCase,
                    newErrorMessage = error.apiException.newErrorMessage,
                    onErrorAction = {
                        _arvBrandsSelectionMutableLiveData.value =
                            UiArvBrandsSelectionState.ShowError(
                                error.apiException.newErrorMessage
                            )
                    }
                )
            }
        }
    }

    private fun ArvAnticipation.mapDates(previousAnticipation: ArvAnticipation): ArvAnticipation {
        return this.copy(
            initialDate = this.initialDate ?: previousAnticipation.initialDate,
            finalDate = this.finalDate ?: previousAnticipation.finalDate
        )
    }
}