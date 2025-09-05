package br.com.mobicare.cielo.login.firstAccess.data.model.response

data class FirstAccessResponse(
    val email: String,
    val tokenExpirationInMinutes: Int
)

enum class FirstAccessType(val errorType: String) {
    REQUEST_ADM_PERMISSION("INELIGIBLE_MERCHANT_ID"),
    REQUEST_MANAGER_PERMISSION("UNAUTHORIZED_CHANNEL")
}