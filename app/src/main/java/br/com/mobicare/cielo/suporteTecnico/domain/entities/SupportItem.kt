package br.com.mobicare.cielo.suporteTecnico.domain.entities

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*

@Keep
@Parcelize
data class SupportItem(
    val id: String?,
    val categoryName: String?,
    val imageUrl: String?,
    val active: Boolean = false,
    val problems: ArrayList<Problem>) : Parcelable