package br.com.mobicare.cielo.arv.data.model.response

import androidx.annotation.Keep

@Keep
data class ArvAnticipationResponse(
    val acquirers: List<AcquirerResponse?>? = null,
    val discountAmount: Double? = null,
    val finalDate: String? = null,
    val grossAmount: Double? = null,
    val id: String? = null,
    val initialDate: String? = null,
    val negotiationType: String? = null,
    val netAmount: Double? = null,
    val nominalFee: Double? = null,
    val standardFee: Double? = null,
    val token: String? = null,
    val effectiveFee: Double? = null,
    val simulationType: String? = null,
    val eligibleTimeToReceiveToday: Boolean = false
)

@Keep
data class AcquirerResponse(
    val code: Int? = null,
    val cardBrands: List<CardBrandResponse?>? = null,
    val discountAmount: Double? = null,
    val grossAmount: Double? = null,
    val name: String? = null,
    val netAmount: Double? = null
)

@Keep
data class CardBrandResponse(
    val code: Int? = null,
    val discountAmount: Double? = null,
    val grossAmount: Double? = null,
    val name: String? = null,
    val netAmount: Double? = null
)