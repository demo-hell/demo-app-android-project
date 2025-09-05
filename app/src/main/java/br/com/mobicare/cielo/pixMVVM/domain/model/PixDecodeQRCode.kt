package br.com.mobicare.cielo.pixMVVM.domain.model

import android.os.Parcelable
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixAllowsChangeValueEnum
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixQRCodeType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixQrCodeOperationType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixOwnerType
import kotlinx.android.parcel.Parcelize
import java.time.ZonedDateTime
import java.util.Calendar

@Parcelize
data class PixDecodeQRCode(
    val endToEndId: String,
    val type: PixQRCodeType,
    val pixType: PixQrCodeOperationType,
    val participant: Int?,
    val participantName: String,
    val revision: Int?,
    val receiverName: String,
    val receiverTradingName: String,
    val receiverPersonType: PixOwnerType?,
    val receiverDocument: String,
    val idTx: String,
    val payerName: String,
    val payerDocument: String,
    val city: String,
    val address: String,
    val state: String,
    val zipCode: String,
    val originalAmount: Double?,
    val interest: Double?,
    val penalty: Double?,
    val discount: Double?,
    val abatement: Double?,
    val finalAmount: Double?,
    val withDrawAmount: Double?,
    val changeAmount: Double?,
    val allowsChange: Boolean?,
    val expireDate: ZonedDateTime?,
    val dueDate: String,
    val daysAfterDueDate: Int?,
    val creationDate: ZonedDateTime?,
    val decodeDate: ZonedDateTime?,
    val url: String,
    val reusable: Boolean?,
    val branch: String,
    val accountType: String,
    val accountNumber: String,
    val key: String,
    val keyType: String,
    val category: String,
    val additionalData: String,
    val payerType: PixOwnerType?,
    val modalityAlteration: PixAllowsChangeValueEnum,
    val description: String,
    val ispbWithDraw: Int?,
    val ispbWithDrawName: String,
    val modalityAltWithDraw: PixAllowsChangeValueEnum,
    val modalityWithDrawAgent: String,
    val ispbChange: Int?,
    val ispbChangeName: String,
    val modalityAltChange: PixAllowsChangeValueEnum,
    val modalityChangeAgent: String,
    val status: Int?,
    val qrCode: String,
    val isSchedulable: Boolean?,
) : Parcelable
