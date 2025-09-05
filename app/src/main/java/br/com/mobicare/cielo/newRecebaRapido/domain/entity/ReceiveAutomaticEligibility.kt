package br.com.mobicare.cielo.newRecebaRapido.domain.entity

data class ReceiveAutomaticEligibility(
    val eligible: Boolean,
    val eligibilityDetails: EligibilityDetails,
)

data class EligibilityDetails(
    val fastRepayRules: List<FastRepayRule>,
)

data class FastRepayRule(
    val ruleCode: Int,
    val ruleDescription: String,
    val ruleEligible: Boolean,
    val ruleContractRestricted: Boolean,
)
