package br.com.mobicare.cielo.mdr.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_200
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_202
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.extensions.orZero
import br.com.mobicare.cielo.mdr.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_MDR_HOME_ACCEPT
import br.com.mobicare.cielo.mdr.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_MDR_HOME_REJECT
import br.com.mobicare.cielo.mdr.domain.usecase.PostContractUseCase
import br.com.mobicare.cielo.mdr.ui.state.UiMdrConfirmationState
import br.com.mobicare.cielo.mdr.ui.state.UiMdrOfferState
import br.com.mobicare.cielo.mdr.utils.Constants.REATIVACAO_MDR_ALUGUEL
import br.com.mobicare.cielo.mdr.utils.Constants.REATIVACAO_MDR_RR_ALUGUEL
import br.com.mobicare.cielo.mdr.utils.Constants.RETENCAO_MDR_ALUGUEL
import br.com.mobicare.cielo.mdr.utils.Constants.RETENCAO_MDR_ALUGUEL_RR_S_POSTECIPADO
import br.com.mobicare.cielo.mdr.utils.Constants.RETENCAO_MDR_ALUGUEL_S_POSTECIPADO
import br.com.mobicare.cielo.mdr.utils.Constants.RETENCAO_MDR_RR_ALUGUEL
import br.com.mobicare.cielo.mdr.utils.Constants.RETENCAO_MDR_RR_S_MAQUINA
import br.com.mobicare.cielo.mdr.utils.Constants.RETENCAO_MDR_S_MAQUINA
import kotlinx.coroutines.launch

class MdrOfferViewModel(
    private val getUserObjUseCase: GetUserObjUseCase,
    private val postContractUseCase: PostContractUseCase,
) : ViewModel() {
    private val _mdrConfirmationState = MutableLiveData<UiMdrConfirmationState>()
    val mdrConfirmationState: LiveData<UiMdrConfirmationState> get() = _mdrConfirmationState
    private val _mdrOfferState = MutableLiveData<UiMdrOfferState>()
    val mdrOfferState: LiveData<UiMdrOfferState> get() = _mdrOfferState

    fun updateMdrOfferState(offerId: Int?) {
        _mdrOfferState.value = getMdrOfferState(offerId)
    }

    fun postContractUserDecision(
        apiId: String?,
        bannerId: Int?,
        isAccepted: Boolean,
    ) {
        _mdrConfirmationState.value = UiMdrConfirmationState.ShowLoading
        viewModelScope.launch {
            postContractUseCase.invoke(apiId.orEmpty(), bannerId.orZero, isAccepted)
                .onEmpty { response ->
                    verifyEmptyResponse(response, isAccepted)
                }
                .onError { handleError(it.apiException.newErrorMessage, isAccepted) }
        }
    }

    private fun getMdrOfferState(offerId: Int?): UiMdrOfferState {
        val offerStateMap =
            mapOf(
                REATIVACAO_MDR_ALUGUEL to UiMdrOfferState.ShowPostponedWithoutRR,
                REATIVACAO_MDR_RR_ALUGUEL to UiMdrOfferState.ShowPostponedWithRR,
                RETENCAO_MDR_ALUGUEL to UiMdrOfferState.ShowPostponedWithoutRR,
                RETENCAO_MDR_RR_ALUGUEL to UiMdrOfferState.ShowPostponedWithRR,
                RETENCAO_MDR_ALUGUEL_S_POSTECIPADO to UiMdrOfferState.ShowWithoutPostponedWithoutRR,
                RETENCAO_MDR_ALUGUEL_RR_S_POSTECIPADO to UiMdrOfferState.ShowWithoutPostponedWithRR,
                RETENCAO_MDR_S_MAQUINA to UiMdrOfferState.ShowWithoutEquipmentWithoutRR,
                RETENCAO_MDR_RR_S_MAQUINA to UiMdrOfferState.ShowWithoutEquipmentWithRR,
            )
        return offerStateMap[offerId] ?: UiMdrOfferState.Error
    }

    private suspend fun verifyEmptyResponse(
        response: CieloDataResult.Empty,
        isAccepted: Boolean,
    ) {
        when (response.code) {
            HTTP_STATUS_200, HTTP_STATUS_202 ->
                handleSuccessResponse(isAccepted)
            else -> handleError(isAccepted = isAccepted)
        }
    }

    private fun handleSuccessResponse(isAccepted: Boolean) =
        updateState(
            if (isAccepted) {
                UiMdrConfirmationState.AcceptSuccess
            } else {
                UiMdrConfirmationState.RejectSuccess
            },
        )

    private suspend fun handleError(
        error: NewErrorMessage? = null,
        isAccepted: Boolean,
    ) {
        newErrorHandler(
            getUserObjUseCase = getUserObjUseCase,
            newErrorMessage = error ?: NewErrorMessage(),
            onErrorAction = {
                updateState(
                    UiMdrConfirmationState.Error(
                        error,
                        isAccepted,
                        if (isAccepted) SCREEN_VIEW_MDR_HOME_ACCEPT else SCREEN_VIEW_MDR_HOME_REJECT,
                    ),
                )
            },
        )
    }

    private fun updateState(state: UiMdrConfirmationState) {
        _mdrConfirmationState.value = UiMdrConfirmationState.HideLoading
        _mdrConfirmationState.value = state
    }
}
