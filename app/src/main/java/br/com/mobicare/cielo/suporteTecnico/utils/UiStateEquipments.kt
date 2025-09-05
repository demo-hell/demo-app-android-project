package br.com.mobicare.cielo.suporteTecnico.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.taxaPlanos.domain.TerminalsResponse

sealed class UiStateEquipments<out T> {

    object ShowLoading : UiStateEquipments<Nothing>()

    object HideLoading : UiStateEquipments<Nothing>()

    object Error : UiStateEquipments<Nothing>()

    class Success<T>(val data: T? = null) : UiStateEquipments<T>()

    class ErrorWithoutMachine(val error: NewErrorMessage?) : UiStateEquipments<Nothing>()
    object Empty : UiStateEquipments<Nothing>()
}