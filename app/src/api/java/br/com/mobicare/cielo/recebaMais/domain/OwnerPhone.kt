package br.com.mobicare.cielo.recebaMais.domain


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OwnerPhone(
    @SerializedName("areaCode")
    val areaCode: String?,
    @SerializedName("number")
    val number: String?,
    @SerializedName("type")
    val type: String?)
    : Parcelable {



    companion object {
        fun convertPhoneIntoOwnerPhone(phone: Phone?): OwnerPhone {
            return OwnerPhone(phone?.areaCode, phone?.number, phone?.type)
        }
    }
}