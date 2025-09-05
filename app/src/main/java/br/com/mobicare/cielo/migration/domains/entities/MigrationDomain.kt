package br.com.mobicare.cielo.migration.domains.entities

class MigrationDomain {
    var fullName = ""
    var cpf: String? = null
    var email = ""
    var emailConfirmation = ""
    var currentPassword = ""
    var password = ""
    var passwordConfirmation = ""
    var merchantId = ""

    // Response
    var tokenExpirationInMinutes: Int = 0
}