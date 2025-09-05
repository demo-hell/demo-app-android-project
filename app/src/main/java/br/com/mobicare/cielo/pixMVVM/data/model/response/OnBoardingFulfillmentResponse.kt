package br.com.mobicare.cielo.pixMVVM.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class OnBoardingFulfillmentResponse(
    val eligible: Boolean? = null,
    val profileType: String? = null,
    val settlementActive: Boolean? = null,
    val enabled: Boolean? = null,
    val status: String? = null,
    val blockType: String? = null,
    val pixAccount: PixAccount? = null,
    val settlementScheduled: SettlementScheduled? = null,
    val document: String? = null,
    val documentType: String? = null
) : Parcelable {

    @Keep
    @Parcelize
    data class PixAccount(
        val pixId: String? = null,
        val bank: String? = null,
        val agency: String? = null,
        val account: String? = null,
        val accountDigit: String? = null,
        val dockAccountId: String? = null,
        val isCielo: Boolean? = null,
        val bankName: String? = null
    ) : Parcelable

    @Keep
    @Parcelize
    data class SettlementScheduled(
        val enabled: Boolean? = null,
        val list: List<String>? = null
    ) : Parcelable

}