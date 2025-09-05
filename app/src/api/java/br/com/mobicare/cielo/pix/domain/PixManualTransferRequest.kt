package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PixManualTransferRequest(
    val finalAmount: Double,
    val message: String?,
    val payee: ManualPayee?,
    val fingerprint: String? = null,
    val schedulingDate: String? = null
) : Parcelable

@Keep
@Parcelize
data class ManualPayee(
    val bankName: String?,
    val bankAccountNumber: String?,
    val bankAccountType: String?,
    val bankBranchNumber: String?,
    val beneficiaryType: String?,
    val documentNumber: String?,
    val ispb: Int?,
    val name: String? = null
) : Parcelable