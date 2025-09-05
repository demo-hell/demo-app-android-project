package br.com.mobicare.cielo.autoAtendimento.domain.model

import android.os.Parcel
import android.os.Parcelable

data class SupliesResponse(
    val supplies: List<Supply>
)

data class Supply(
        val allowedQuantity: Boolean,
        val code: String,
        val description: String,
        val type: String
):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readByte() != 0.toByte(),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (allowedQuantity) 1 else 0)
        parcel.writeString(code)
        parcel.writeString(description)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Supply> {
        override fun createFromParcel(parcel: Parcel): Supply {
            return Supply(parcel)
        }

        override fun newArray(size: Int): Array<Supply?> {
            return arrayOfNulls(size)
        }
    }
}

data class SupplyDTO(
        val allowedQuantity: Boolean,
        val code: String,
        val description: String,
        val type: String,
        var quantidade: Int
): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readByte() != 0.toByte(),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (allowedQuantity) 1 else 0)
        parcel.writeString(code)
        parcel.writeString(description)
        parcel.writeString(type)
        parcel.writeInt(quantidade)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SupplyDTO> {
        override fun createFromParcel(parcel: Parcel): SupplyDTO {
            return SupplyDTO(parcel)
        }

        override fun newArray(size: Int): Array<SupplyDTO?> {
            return arrayOfNulls(size)
        }
    }
}
