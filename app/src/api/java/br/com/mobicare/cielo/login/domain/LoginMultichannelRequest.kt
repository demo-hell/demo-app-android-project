package br.com.mobicare.cielo.login.domain

import com.google.gson.annotations.SerializedName

data class LoginMultichannelRequest(@SerializedName("username") var username: String,
                                    @SerializedName("merchant") var merchantId: String?,
                                    @SerializedName("password") var password: String)
