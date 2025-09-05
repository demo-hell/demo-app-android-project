package br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.extensions.isHigherThanZero
import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers
import br.com.mobicare.cielo.mdr.analytics.ArvAnalyticsGA4Constants
import br.com.mobicare.cielo.mdr.domain.usecase.PostContractUseCase
import br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.domain.model.MigrationOffer
import br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.domain.model.toMigrationOffer
import br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.ui.state.UiMigrationConfirmationState
import br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.ui.state.UiMigrationOfferState
import kotlinx.coroutines.launch

class MigrationOfferViewModel(
    private val getUserObjUseCase: GetUserObjUseCase,
    private val postContractUseCase: PostContractUseCase,
) : ViewModel() {
    private var migrationOffer: MigrationOffer? = null
    private val _migrationConfirmationState = MutableLiveData<UiMigrationConfirmationState>()
    val migrationConfirmationState: LiveData<UiMigrationConfirmationState> get() = _migrationConfirmationState
    private val _migrationOfferState = MutableLiveData<UiMigrationOfferState>()
    val migrationOfferState: LiveData<UiMigrationOfferState> get() = _migrationOfferState

    fun updateMigrationOfferState(offer: HiringOffers?) {
        migrationOffer = offer?.toMigrationOffer()
        _migrationOfferState.value = getMigrationOfferState(migrationOffer)
    }

    fun postContractUserDecision(
        isAccepted: Boolean,
    ) {
        _migrationConfirmationState.value = UiMigrationConfirmationState.ShowLoading
        viewModelScope.launch {
            val apiId = migrationOffer?.apiId ?: return@launch
            val bannerId = migrationOffer?.bannerId ?: return@launch
            postContractUseCase.invoke(apiId, bannerId, isAccepted)
                .onEmpty { response ->
                    verifyEmptyResponse(response, isAccepted)
                }
                .onError { handleError(it.apiException.newErrorMessage, isAccepted) }
        }
    }

    private fun getMigrationOfferState(offer: MigrationOffer?): UiMigrationOfferState {
        return when {
            offer?.creditRateBefore.isHigherThanZero() && offer?.rateInstallmentsBefore.isHigherThanZero() -> {
                UiMigrationOfferState.Both(offer?.creditRateBefore, offer?.rateInstallmentsBefore)
            }

            offer?.creditRateBefore.isHigherThanZero() -> {
                UiMigrationOfferState.Credit(offer?.creditRateBefore)
            }

            offer?.rateInstallmentsBefore.isHigherThanZero() -> {
                UiMigrationOfferState.Installment(offer?.rateInstallmentsBefore)
            }

            else -> UiMigrationOfferState.NoOfferError
        }
    }

    private suspend fun verifyEmptyResponse(
        response: CieloDataResult.Empty,
        isAccepted: Boolean,
    ) {
        when (response.code) {
            NetworkConstants.HTTP_STATUS_200, NetworkConstants.HTTP_STATUS_202 ->
                handleSuccessResponse(isAccepted)
            else -> handleError(isAccepted = isAccepted)
        }
    }

    private fun handleSuccessResponse(isAccepted: Boolean) =
        updateState(
            if (isAccepted) {
                UiMigrationConfirmationState.AcceptSuccess
            } else {
                UiMigrationConfirmationState.RejectSuccess
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
                    UiMigrationConfirmationState.Error(
                        error,
                        isAccepted,
                        if (isAccepted) ArvAnalyticsGA4Constants.SCREEN_VIEW_MDR_HOME_ACCEPT else ArvAnalyticsGA4Constants.SCREEN_VIEW_MDR_HOME_REJECT,
                    ),
                )
            },
        )
    }

    private fun updateState(state: UiMigrationConfirmationState) {
        _migrationConfirmationState.value = UiMigrationConfirmationState.HideLoading
        _migrationConfirmationState.value = state
    }
}
