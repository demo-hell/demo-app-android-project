package br.com.mobicare.cielo.openFinance.domain.model

import androidx.annotation.Keep

@Keep
data class PixMerchantListResponse(
    val id: String,
    val name: String,
    val merchantNumber: String? = null,
    val documentType: String? = null,
    val documentNumber: String? = null,
    val pixAccount: OpenFinancePixAccount? = null
)

@Keep
data class OpenFinancePixAccount(
    val dockAccountId: String? = null,
    val isCielo: Boolean? = null
)