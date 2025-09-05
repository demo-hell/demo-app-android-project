package br.com.mobicare.cielo.login.domain

import com.google.gson.annotations.SerializedName

data class User(
        @SerializedName("nameLogin") val nameLogin: String,
        @SerializedName("birthdayDate") val birthdayDate: Int,
        @SerializedName("cpf") val cpf: String,
        @SerializedName("rg") val rg: String,
        @SerializedName("name") val name: String,
        @SerializedName("email") val email: String,
        @SerializedName("registerDate") val registerDate: Int,
        @SerializedName("inactive") val inactive: Int,
        @SerializedName("lastAccessDate") val lastAccessDate: Int,
        @SerializedName("QuantityAttemptReset") val quantityAttemptReset: Int,
        @SerializedName("entity") val entity: Int,
        @SerializedName("ec") val ec: String
)