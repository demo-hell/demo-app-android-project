package br.com.mobicare.cielo.openFinance.data.model.request

import com.google.errorprone.annotations.Keep

@Keep
data class ConsentIdRequest(
    val consentId: String
)