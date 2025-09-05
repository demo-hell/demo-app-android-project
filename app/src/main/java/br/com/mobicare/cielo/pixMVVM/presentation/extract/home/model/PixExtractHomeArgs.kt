package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PixExtractHomeArgs(
    val transactionCode: String? = null,
    val idEndToEnd: String? = null,
    val schedulingCode: String? = null,
) : Parcelable
