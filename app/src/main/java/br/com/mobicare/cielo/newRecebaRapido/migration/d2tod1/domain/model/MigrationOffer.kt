package br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.domain.model

import br.com.mobicare.cielo.extensions.toNineDigitString
import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers

data class MigrationOffer(
    val bannerId: Int?,
    val name: String?,
    val apiId: String?,
    val creditRateBefore: Double?,
    val creditRateAfter: Double?,
    val rateInstallmentsBefore: Double?,
    val rateInstallmentsAfter: Double?,
)

fun HiringOffers?.toMigrationOffer() =
    MigrationOffer(
        bannerId = this?.id,
        name = this?.name,
        apiId = this?.id?.toNineDigitString().orEmpty(),
        creditRateBefore = this?.creditRateBefore,
        creditRateAfter = this?.creditRateAfter,
        rateInstallmentsBefore = this?.rateInstallmentsBefore,
        rateInstallmentsAfter = this?.rateInstallmentsAfter

    )