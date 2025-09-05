package br.com.mobicare.cielo.pixMVVM.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PixAccountBalanceResponse(
    val idStatusAccount: Int? = null,
    val statusAccount: String? = null,
    val dataStatusAccount: String? = null,
    val balanceAvailableGlobal: Double? = null,
    val balanceAvailableWithdrawal: Double? = null,
    val finalCurrentBalance: Double? = null,
    val previousExtractBalance: Double? = null,
    val timeOfRequest: String? = null,
): Parcelable
