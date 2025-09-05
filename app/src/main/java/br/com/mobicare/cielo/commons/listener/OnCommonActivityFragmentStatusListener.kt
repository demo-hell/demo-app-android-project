package br.com.mobicare.cielo.commons.listener

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage

interface OnCommonActivityFragmentStatusListener {
    fun onError()
    fun onError(message: ErrorMessage)
    fun onErrorHandlerRetryWithMessage(message: ErrorMessage) {}
    fun onErrorAndClose(message: ErrorMessage)
    fun onShowLoading()
    fun onHideLoading()
    fun onExpiredSession()
    fun onSetTitleToolbar(title: String)
    fun onSuccess(result: String)
}
