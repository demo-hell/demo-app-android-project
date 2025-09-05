package br.com.mobicare.cielo.chargeback.presentation.details.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackDetails
import br.com.mobicare.cielo.chargeback.domain.useCase.GetChargebackDescriptionReasonUseCase
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.pix.constants.EMPTY
import kotlinx.coroutines.launch

class ChargebackFeatureToggleViewModel(
    private val getChargebackDescriptionReasonUseCase: GetChargebackDescriptionReasonUseCase,
    private val getFeatureToggleViewModel: GetFeatureTogglePreferenceUseCase
) : ViewModel() {

    var descriptionReasonTypeMessage: Any? = null
    var showRDRCardInDetails: Boolean? = null

    fun getDescriptionReasonTypeFeatureToggle(chargebackDetails: ChargebackDetails?) {
        viewModelScope.launch {
            descriptionReasonTypeMessage = getChargebackDescriptionReasonUseCase(
                key = FeatureTogglePreference.USE_REASONMESSAGE_CHARGEBACK,
                descriptionReasonType = chargebackDetails?.descriptionReasonType ?: EMPTY,
                reasonType = chargebackDetails?.reasonType ?: ZERO
            )
        }
    }

    fun getShowRDRCardFeatureToggle() {
        viewModelScope.launch {
            val result = getFeatureToggleViewModel(
                key = FeatureTogglePreference.SHOW_CHARGEBACK_RDR_CARD_IN_DETAILS
            )
            result.onSuccess {
                showRDRCardInDetails = it
            }
            result.onError {
                showRDRCardInDetails = false
            }
            result.onEmpty {
                showRDRCardInDetails = false
            }
        }
    }
}
