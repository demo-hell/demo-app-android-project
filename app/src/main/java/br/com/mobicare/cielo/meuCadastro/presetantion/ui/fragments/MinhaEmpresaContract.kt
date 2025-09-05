package br.com.mobicare.cielo.meuCadastro.presetantion.ui.fragments

interface MinhaEmpresaContract {

    interface View {
        fun onSucess()
        fun onError()
        fun onErrorAuthentication()
        fun onChancePasswordSuccess()
        fun onChancePasswordError(error: String)
        fun onPasswordPoliticError(error: String)
        fun logout()
    }

}