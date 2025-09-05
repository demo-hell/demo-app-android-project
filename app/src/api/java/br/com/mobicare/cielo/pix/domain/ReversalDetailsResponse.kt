package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ReversalDetailsResponse(
    val idAccount: String?,
    val idEndToEndOriginal: String?,
    val idEndToEndReturn: String?,
    val transactionDate: String?,
    val transactionType: String?,
    val errorType: String?,
    val transactionStatus: String?,
    val creditParty: ReversalParty?,
    val debitParty: ReversalParty?,
    val amount: Double?,
    val tariffAmount: Double?,
    val finalAmount: Double?,
    val reversalCode: String?,
    val reversalReason: String?,
    val idAdjustment: String?,
    val transactionCode: String?,
    val transactionCodeOriginal: String?,
    val payerAnswer: String?,
) : Parcelable

@Keep
@Parcelize
data class ReversalParty(
    val ispb: String?,
    val bankName: String?,
    val nationalRegistration: String?,
    val name: String?,
    val bankBranchNumber: String?,
    val bankAccountNumber: String?,
    val bankAccountType: String?
) : Parcelable

