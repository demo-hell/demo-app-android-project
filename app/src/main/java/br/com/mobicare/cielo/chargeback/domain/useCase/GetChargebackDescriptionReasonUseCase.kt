package br.com.mobicare.cielo.chargeback.domain.useCase

import br.com.mobicare.cielo.chargeback.domain.enum.DescriptionReasonEnum.*
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.repository.local.FeatureTogglePreferenceRepository

class GetChargebackDescriptionReasonUseCase(private val repository: FeatureTogglePreferenceRepository) {
    suspend operator fun invoke(
        key: String,
        descriptionReasonType: String,
        reasonType: Int
    ): Any? {
        val response = repository.getFeatureTogglePreference(key)
        var message: Any? = null

        response.onSuccess { isFeatureToggleEnabled ->
            message = if (isFeatureToggleEnabled) {
                descriptionReasonType
            } else {
                getMessageByReasonType(reasonType)
            }
        }.onError {
                getMessageByReasonType(reasonType)
            }

        return message
    }

    private fun getMessageByReasonType(reasonType: Int): Int {
        return when (reasonType) {
            ONE.position -> ONE.message
            TWO.position -> TWO.message
            THREE.position -> THREE.message
            else -> ONE.message
        }
    }
}
