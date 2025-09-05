package br.com.mobicare.cielo.openFinance.data.model.request

import com.google.errorprone.annotations.Keep

@Keep
data class RejectConsentRequest(
    val consentId: String?,
    val detail: String?,
    val code: String?,
)
