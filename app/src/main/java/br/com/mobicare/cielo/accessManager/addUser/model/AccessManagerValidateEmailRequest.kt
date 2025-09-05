package br.com.mobicare.cielo.accessManager.addUser.model

data class AccessManagerValidateEmailRequest (
    var cpf: String? = null,
    var email: String? = null,
    var foreign: Boolean? = false
)