package br.com.mobicare.cielo.recebaMais.domain


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContractsResponse(
        @SerializedName("contracts")
        val contracts: List<Contract>
): Parcelable