package br.com.mobicare.cielo.commons.presentation

interface BasePresenter<T> {
    fun setView(view: T)
}