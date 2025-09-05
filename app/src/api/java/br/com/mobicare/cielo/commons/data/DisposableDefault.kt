package br.com.mobicare.cielo.commons.data

interface DisposableDefault {
    fun disposable()
    fun onDestroy() {}
    fun onResume() {}
}