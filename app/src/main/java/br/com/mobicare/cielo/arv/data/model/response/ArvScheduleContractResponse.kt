package br.com.mobicare.cielo.arv.data.model.response
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ArvScheduleContractResponse(
    val file: String? = null,
    val size: String? = null
) : Parcelable