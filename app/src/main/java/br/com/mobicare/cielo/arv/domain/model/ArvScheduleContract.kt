package br.com.mobicare.cielo.arv.domain.model
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ArvScheduleContract(
    val file: String? = null,
    val size: String? = null
) : Parcelable