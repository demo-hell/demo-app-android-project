package br.com.mobicare.cielo.component.impersonate.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class MerchantResponse(
    val id: String? = null,
    val name: String? = null,
    val document: String? = null
): Parcelable