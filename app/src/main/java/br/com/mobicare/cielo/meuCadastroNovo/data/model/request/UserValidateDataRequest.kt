package br.com.mobicare.cielo.meuCadastroNovo.data.model.request

data class UserValidateDataRequest(
    var email: String? = null,
    var password: String? = null,
    var passwordConfirmation: String? = null,
    var cellphone: String? = null
)