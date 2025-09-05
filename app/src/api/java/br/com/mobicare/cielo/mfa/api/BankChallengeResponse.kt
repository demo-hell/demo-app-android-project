package br.com.mobicare.cielo.mfa.api

import com.google.gson.annotations.SerializedName

data class BankChallengeResponse(@SerializedName("merchantId") val merchantId: String?,
                                 @SerializedName("merchantStatusEnum") val merchantStatusEnum: String?)