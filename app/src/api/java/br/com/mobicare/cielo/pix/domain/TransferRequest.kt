package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.pix.enums.PixQRCodeOperationTypeEnum
import br.com.mobicare.cielo.pix.enums.PixTransferTypeEnum
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class TransferRequest(
    var agentMode: String? = null,
    var agentWithdrawalIspb: String? = null,
    @SerializedName("finalAmount")
    var amount: Double? = null,
    var changeAmount: String? = null,
    @SerializedName("endToEndId")
    val idEndToEnd: String?,
    val idTx: String? = null,
    val payee: Payee?,
    val message: String? = null,
    val pixType: String? = PixQRCodeOperationTypeEnum.TRANSFER.name,
    var purchaseAmount: String? = null,
    val transferType: String = PixTransferTypeEnum.CHAVE.name,
    val fingerprint: String? = null,
    val schedulingDate: String? = null
) : Parcelable

@Keep
@Parcelize
data class Payee(
    val key: String?,
    val keyType: String?
) : Parcelable