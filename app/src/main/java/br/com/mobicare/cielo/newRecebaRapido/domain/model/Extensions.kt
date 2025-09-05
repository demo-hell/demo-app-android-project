package br.com.mobicare.cielo.newRecebaRapido.domain.model

import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.extensions.orZero
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.VALIDITY_TYPE_FIXED_DATE
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.VALIDITY_TYPE_MONTHS
import com.salesforce.marketingcloud.sfmcsdk.util.orElse

fun List<Offer>.mapToOfferSummary() =
    flatMap { offer ->
        offer.items.orEmpty()
    }.map {
        val firstCreditOffer = it.items?.firstOrNull { creditOffer ->
            creditOffer.type == ConstantsReceiveAutomatic.CREDIT
        }
        val firstInstallmentOffer = it.items?.firstOrNull { creditOffer ->
            creditOffer.type == ConstantsReceiveAutomatic.INSTALLMENT
        }
        OfferSummary(
            it.brandCode?.toInt().orZero,
            it.brandDescription.orEmpty(),
            firstCreditOffer?.summarizedMdr,
            firstInstallmentOffer?.installments?.map { installment ->
                InstallmentSummary(installment.number?.toInt().orZero,
                    installment.summarizedMdr.orElse { ZERO_DOUBLE })
            }
        )
    }

fun List<Offer>.mapToCreditOffer(): CreditOfferItem? {
    val orderedOffers = this.orderedOffers().first()
    val firstCreditOffer = orderedOffers.items?.firstOrNull { creditOffer ->
        creditOffer.type == ConstantsReceiveAutomatic.CREDIT
    } ?: return null

    return firstCreditOffer
}

fun List<Offer>.mapToInstallmentOffer(): CreditOfferItem? {
    val orderedOffers = this.orderedOffers().first()
    val firstInstallmentOffer = orderedOffers.items?.firstOrNull { creditOffer ->
        creditOffer.type == ConstantsReceiveAutomatic.INSTALLMENT
    } ?: return null

    return firstInstallmentOffer
}

fun List<Offer>.mapToGeneralOfferSummary(): GeneralOfferSummary {
    val orderedOffer = this.orderedOffers().first()
    val offer = this.first()
    val periodType = when {
        offer.validityPeriodDate.isNullOrEmpty().not() -> VALIDITY_TYPE_FIXED_DATE
        offer.validityPeriodMonths.isNullOrEmpty().not() -> VALIDITY_TYPE_MONTHS
        else -> null
    }

    val period = when (periodType) {
        VALIDITY_TYPE_FIXED_DATE -> offer.validityPeriodDate
        VALIDITY_TYPE_MONTHS -> offer.validityPeriodMonths
        else -> null
    }
    return GeneralOfferSummary(
        referenceBrand = orderedOffer.brandDescription,
        validityPeriodType = periodType,
        validityPeriod = period
    )
}

private fun List<Offer>.orderedOffers(): List<OfferItem> {
    return flatMap { offer ->
        offer.items.orEmpty()
    }.sortedBy {
        it.brandCode
    }
}