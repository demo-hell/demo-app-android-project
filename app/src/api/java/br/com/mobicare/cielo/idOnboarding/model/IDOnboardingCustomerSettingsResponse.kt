package br.com.mobicare.cielo.idOnboarding.model

import androidx.annotation.Keep
import br.com.mobicare.cielo.pix.constants.EMPTY

@Keep
data class IDOnboardingCustomerSettingsResponse (
    var allowedEmailDomains: Array<String>? = null,
    val passwordExpirationDays: String? = EMPTY,
    var allowedCellphoneValidationChannels: Array<String>? = null,
    val foreignFlowAllowed: Boolean? = false,
    val customProfileEnabled: Boolean? = false
)