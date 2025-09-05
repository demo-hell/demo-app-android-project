package br.com.mobicare.cielo.meuCadastro.data.clients.api

interface APICallbackPassword {
    fun onError(error: String)
    fun onSuccess()
    fun onErrorAuthentication(error: String)
}