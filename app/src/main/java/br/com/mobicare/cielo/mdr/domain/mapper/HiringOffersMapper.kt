package br.com.mobicare.cielo.mdr.domain.mapper

import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.extensions.toNineDigitString
import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers
import br.com.mobicare.cielo.mdr.domain.model.CardFees
import br.com.mobicare.cielo.mdr.domain.model.MdrBrandsInformation
import br.com.mobicare.cielo.mdr.utils.Constants.ELO
import br.com.mobicare.cielo.mdr.utils.Constants.MASTER
import br.com.mobicare.cielo.mdr.utils.Constants.VISA

object HiringOffersMapper {
    fun HiringOffers?.toMdrBrandsInformation(): MdrBrandsInformation {
        return MdrBrandsInformation(
            id = this?.id,
            name = this?.name,
            apiId = this?.id?.toNineDigitString().orEmpty(),
            cardFees =
                listOf(
                    CardFees(
                        cardType = VISA,
                        debitFee = this?.debitVisaFee,
                        creditFee = this?.creditVisaFee,
                        fewInstallmentsFee = this?.fewInstallmentsVisaFee,
                        installmentsFee = this?.installmentsVisaFee,
                        icon = R.drawable.ic_visa_regular,
                    ),
                    CardFees(
                        cardType = MASTER,
                        debitFee = this?.debitMasterFee,
                        creditFee = this?.creditMasterFee,
                        fewInstallmentsFee = this?.fewInstallmentsMasterFee,
                        installmentsFee = this?.installmentsMasterFee,
                        icon = R.drawable.ic_mastercard_regular,
                    ),
                    CardFees(
                        cardType = ELO,
                        debitFee = this?.debitEloFee,
                        creditFee = this?.creditEloFee,
                        fewInstallmentsFee = this?.fewInstallmentsEloFee,
                        installmentsFee = this?.installmentsEloFee,
                        icon = R.drawable.ic_elo_regular,
                    ),
                ),
            defaultRentValue = this?.defaultRentValue,
            equipmentQuantity = this?.equipmentQuantity,
            creditFactorGetFastMensal = this?.creditFactorGetFastMensal,
            installmentFactorGetFastMensal = this?.installmentFactorGetFastMensal,
            billingGoal = this?.billingGoal,
            surplusTarget = this?.surplusTarget,
        )
    }
}
