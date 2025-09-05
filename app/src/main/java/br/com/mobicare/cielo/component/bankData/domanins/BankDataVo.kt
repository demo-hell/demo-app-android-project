package br.com.mobicare.cielo.component.bankData.domanins

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class BankDataVo(
        val code: String,
        val agency: String,
        val account: String,
        val accountType: String
) : Parcelable