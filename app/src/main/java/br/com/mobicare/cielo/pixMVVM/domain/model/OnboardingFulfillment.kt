package br.com.mobicare.cielo.pixMVVM.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.pixMVVM.domain.enums.BlockType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixDocumentType
import br.com.mobicare.cielo.pixMVVM.domain.enums.ProfileType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixStatus
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class OnBoardingFulfillment(
    val isEligible: Boolean? = null,
    val profileType: ProfileType? = null,
    val isSettlementActive: Boolean? = null,
    val isEnabled: Boolean? = null,
    val status: PixStatus? = null,
    val blockType: BlockType? = null,
    val pixAccount: PixAccount? = null,
    val settlementScheduled: SettlementScheduled? = null,
    val document: String? = null,
    val documentType: PixDocumentType? = null
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
    ) : Parcelable {
        val accountWithDigit get() = "$account-$accountDigit"
    }

    @Keep
    @Parcelize
    data class SettlementScheduled(
        val isEnabled: Boolean? = null,
        val list: List<String>? = null
    ) : Parcelable

}