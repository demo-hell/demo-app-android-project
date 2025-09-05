package br.com.mobicare.cielo.suporteTecnico.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UIStatePostOrdersReplacements<out T> {
    class Success<T>(val data: T? = null) : UIStatePostOrdersReplacements<T>()
    class Error(val error: NewErrorMessage?): UIStatePostOrdersReplacements<Nothing>()
    object Loading: UIStatePostOrdersReplacements<Nothing>()
    object UpdateLoadingMessage: UIStatePostOrdersReplacements<Nothing>()
    object Empty: UIStatePostOrdersReplacements<Nothing>()
}