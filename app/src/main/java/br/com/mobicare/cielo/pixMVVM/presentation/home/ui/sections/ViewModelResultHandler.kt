package br.com.mobicare.cielo.pixMVVM.presentation.home.ui.sections

interface ViewModelResultHandler<V> {

    fun handleObservableResult(value: V)

    fun reloadSetup() {}

}