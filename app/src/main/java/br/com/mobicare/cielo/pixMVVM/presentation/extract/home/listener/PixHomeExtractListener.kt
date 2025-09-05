package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.listener

interface PixHomeExtractListener {

    fun onLoadAccountBalance()

    fun getCurrentTab(): Int

    fun resetLayoutToolbar()

}