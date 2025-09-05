package br.com.mobicare.cielo.mfa.activation

interface PutValuePresenter {

    fun activationCode(value1: String, value2: String)
    fun onCreate()
    fun onPause()
    fun onResume()
}