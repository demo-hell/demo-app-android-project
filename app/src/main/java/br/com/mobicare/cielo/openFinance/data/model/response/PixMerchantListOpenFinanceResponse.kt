package br.com.mobicare.cielo.openFinance.data.model.response

import androidx.annotation.Keep

@Keep
data class PixMerchantListOpenFinanceResponse(
    val pixMerchantList: List<PixMerchantOpenFinanceResponse>
)

@Keep
data class PixMerchantOpenFinanceResponse(
    val id: String,
    val name: String,
    val merchantNumber: String? = null,
    val documentType: String? = null,
    val documentNumber: String? = null,
    val pixAccountInfo: OpenFinancePixAccountResponse? = null
)

@Keep
data class OpenFinancePixAccountResponse(
    val dockAccountId: String? = null,
    val isCielo: Boolean? = null
)
