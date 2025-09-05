package br.com.mobicare.cielo.esqueciSenha.presentation.ui

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage

interface EsqueciSenhaActivityContract {
    fun showProgress()
    fun hideProgress()
    fun showError(error: ErrorMessage)
    fun changeActivity()
    fun showErrorTapume(error: ErrorMessage)
    fun hideError()
    fun tapTextField(string: String)
    fun messageSuccess(message: String)
}