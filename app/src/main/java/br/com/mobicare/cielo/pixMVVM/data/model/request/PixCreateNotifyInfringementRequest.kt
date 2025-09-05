package br.com.mobicare.cielo.pixMVVM.data.model.request

import androidx.annotation.Keep

@Keep
data class PixCreateNotifyInfringementRequest(
    val idEndToEnd: String? = null,
    val message: String? = null,
    val situationType: String? = null,
    val reasonType: String? = null,
    val amount: Double? = null,
    val merchantId: String? = null,
)