package br.com.mobicare.cielo.commons.presentation

interface CommonPresenter {

    fun onResume()
    fun onDestroy()
    fun onPause() {}

}