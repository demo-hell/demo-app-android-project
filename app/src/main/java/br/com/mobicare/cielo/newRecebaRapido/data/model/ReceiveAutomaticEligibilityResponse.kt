package br.com.mobicare.cielo.newRecebaRapido.data.model

import androidx.annotation.Keep

@Keep
data class ReceiveAutomaticEligibilityResponse(
    val eligible: Boolean,
    val eligibilityDetails: EligibilityDetailsResponse,
)

@Keep
data class EligibilityDetailsResponse(
    val fastRepayRules: List<FastRepayRuleResponse>,
)

@Keep
data class FastRepayRuleResponse(
    val ruleCode: Int,
    val ruleDescription: String,
    val ruleEligible: Boolean,
    val ruleContractRestricted: Boolean,
)
