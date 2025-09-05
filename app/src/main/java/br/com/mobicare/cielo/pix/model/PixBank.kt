package br.com.mobicare.cielo.pix.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PixBank(val code: Int, val ispb: String, val shortName: String, val name: String) : Parcelable