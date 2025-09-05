package br.com.mobicare.cielo.newLogin.domain

data class LoginRequest(val username: String? = null, val password: String? = null, val merchant: String?=null,val fingerprint:String?=null)