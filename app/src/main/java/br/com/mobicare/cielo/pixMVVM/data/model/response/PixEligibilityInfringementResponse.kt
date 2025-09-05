package br.com.mobicare.cielo.pixMVVM.data.model.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PixEligibilityInfringementResponse(
    val isEligible: Boolean? = null,
    val details: String? = null,
    val merchantId: String? = null,
    val idEndToEnd: String? = null,
    val amount: Double? = null,
    val reasonType: String? = null,
    val transactionDate: String? = null,
    val payee: Payee? = null,
    val situations: List<Situation>? = null
) {

    @Keep
    data class Payee(
        val name: String? = null,
        val document: String? = null,
        val key: String? = null,
        val bank: Bank? = null
    ) {

        @Keep
        data class Bank(
            @SerializedName("ispb")
            val iSPB: String? = null,
            val name: String? = null,
            val accountType: String? = null,
            val accountNumber: String? = null,
            val branchNumber: String? = null,
        )

    }

    @Keep
    data class Situation(
        val type: String? = null,
        val description: String? = null,
    )

}