package br.com.mobicare.cielo.mdr.domain.model

data class MdrBrandsInformation(
    val id: Int?,
    val name: String?,
    val apiId: String?,
    val cardFees: List<CardFees>,
    val defaultRentValue: Double?,
    val equipmentQuantity: Int?,
    val creditFactorGetFastMensal: Double?,
    val installmentFactorGetFastMensal: Double?,
    val billingGoal: Double?,
    val surplusTarget: Double?,
)

data class CardFees(
    val cardType: String,
    val debitFee: Double?,
    val creditFee: Double?,
    val fewInstallmentsFee: Double?,
    val installmentsFee: Double?,
    val icon: Int,
)
