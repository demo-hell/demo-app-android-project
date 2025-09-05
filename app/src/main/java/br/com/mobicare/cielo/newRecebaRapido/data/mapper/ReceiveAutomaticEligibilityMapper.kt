package br.com.mobicare.cielo.newRecebaRapido.data.mapper

import br.com.mobicare.cielo.newRecebaRapido.data.model.EligibilityDetailsResponse
import br.com.mobicare.cielo.newRecebaRapido.data.model.FastRepayRuleResponse
import br.com.mobicare.cielo.newRecebaRapido.data.model.ReceiveAutomaticEligibilityResponse
import br.com.mobicare.cielo.newRecebaRapido.domain.entity.EligibilityDetails
import br.com.mobicare.cielo.newRecebaRapido.domain.entity.FastRepayRule
import br.com.mobicare.cielo.newRecebaRapido.domain.entity.ReceiveAutomaticEligibility

object ReceiveAutomaticEligibilityMapper {
    fun mapToReceiveAutomaticEligibility(
        receiveAutomaticEligibilityResponse: ReceiveAutomaticEligibilityResponse,
    ): ReceiveAutomaticEligibility {
        return ReceiveAutomaticEligibility(
            eligible = receiveAutomaticEligibilityResponse.eligible,
            eligibilityDetails = mapEligibilityDetails(receiveAutomaticEligibilityResponse.eligibilityDetails),
        )
    }

    private fun mapEligibilityDetails(eligibilityDetailsResponse: EligibilityDetailsResponse): EligibilityDetails {
        return EligibilityDetails(
            fastRepayRules = eligibilityDetailsResponse.fastRepayRules.map { mapFastRepayRule(it) },
        )
    }

    private fun mapFastRepayRule(fastRepayRuleResponse: FastRepayRuleResponse): FastRepayRule {
        return FastRepayRule(
            ruleCode = fastRepayRuleResponse.ruleCode,
            ruleDescription = fastRepayRuleResponse.ruleDescription,
            ruleEligible = fastRepayRuleResponse.ruleEligible,
            ruleContractRestricted = fastRepayRuleResponse.ruleContractRestricted,
        )
    }
}
