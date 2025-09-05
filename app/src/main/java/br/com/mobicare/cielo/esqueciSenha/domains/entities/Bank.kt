package br.com.mobicare.cielo.esqueciSenha.domains.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by benhur.souza on 11/04/2017.
 */

@Parcelize
data class Bank(
        var code: String? = null,
        @SerializedName("agency") var branch: String? = null,
        var account: String? = null,
        var accountType: String? = null
) : Parcelable
