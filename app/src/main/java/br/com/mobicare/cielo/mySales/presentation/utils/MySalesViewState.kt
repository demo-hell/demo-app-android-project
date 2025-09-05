package br.com.mobicare.cielo.mySales.presentation.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class  MySalesViewState<out T> {
    data class SUCCESS<T>(val data: T? = null) :  MySalesViewState<T>()
    data class SUCCESS_PAGINATION<T>(val data: T? = null): MySalesViewState<T>()
    data class ERROR(val message: String? = null, val newErrorMessage: NewErrorMessage? = null):
        MySalesViewState<Nothing>()
    data class ERROR_FULL_SCREEN(val message: String? = null, val newErrorMessage: NewErrorMessage? = null): MySalesViewState<Nothing>()
    object LOADING :  MySalesViewState<Nothing>()
    object HIDE_LOADING :  MySalesViewState<Nothing>()
    object EMPTY :  MySalesViewState<Nothing>()
    object LOADING_MORE :  MySalesViewState<Nothing>()
    object HIDE_LOADING_MORE:  MySalesViewState<Nothing>()

}