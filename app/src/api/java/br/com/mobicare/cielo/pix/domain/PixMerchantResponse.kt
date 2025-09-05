package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PixMerchantResponse(
    val disabling: Boolean?,
    val documentNumber: String?,
    val documentType: String?,
    val inPreValidation: Boolean?,
    val merchantNumber: String?,
    val name: String?,
    val nonPixAccount: NonPixAccount?,
    val pixAccount: PixAccount?,
    val pixFullActive: Boolean?
) : Parcelable

@Keep
@Parcelize
data class NonPixAccount(
    val account: String?,
    val accountDigit: String?,
    val accountType: String?,
    val agency: String?,
    val agencyDigit: String?,
    val bank: String?,
    val bankName: String?,
    val beneficiaryName: String?,
    val ispb: String?
) : Parcelable

@Keep
@Parcelize
data class PixAccount(
    val account: String?,
    val accountDigit: String?,
    val agency: String?,
    val bank: String?,
    val bankName: String?,
    val cielo: Boolean?,
    val dockAccountId: String?,
    val pixId: String?
) : Parcelable
