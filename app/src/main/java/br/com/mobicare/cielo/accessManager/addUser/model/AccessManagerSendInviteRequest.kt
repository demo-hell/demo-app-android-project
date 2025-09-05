package br.com.mobicare.cielo.accessManager.addUser.model

data class AccessManagerSendInviteRequest (
    var cpf: String? = null,
    var email: String? = null,
    var role: String? = null,
    var foreign: Boolean? = null,
    var countryCode: String? = null
)