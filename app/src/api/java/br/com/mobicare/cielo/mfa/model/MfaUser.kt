package br.com.mobicare.cielo.mfa.model

import com.google.gson.annotations.SerializedName

data class MfaUser(
        @SerializedName("id")
        val id: String?,
        @SerializedName("username")
        val usernameInLogin: String?,
        @SerializedName("email")
        val email: String?,
        @SerializedName("cpf")
        val cpf: String?,
        @SerializedName("ec")
        val ec: String?,
        @SerializedName("seed")
        val mfaSeed: String?)