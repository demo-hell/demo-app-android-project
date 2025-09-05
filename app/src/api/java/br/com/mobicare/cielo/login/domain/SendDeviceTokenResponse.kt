package br.com.mobicare.cielo.login.domain

data class SendDeviceTokenResponse (
         val device: DeviceResponse?,
         val code: String?,
         val notNowCount: String?
)