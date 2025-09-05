package br.com.mobicare.cielo.component.impersonate.presentation.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MerchantUI(
    val id: String,
    val name: String? = null,
    val document: String? = null
) : Parcelable