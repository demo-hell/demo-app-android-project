package br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.ui.state

import br.com.mobicare.cielo.commons.constants.EMPTY_STRING
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UiMigrationConfirmationState {
    object ShowLoading : UiMigrationConfirmationState()

    object HideLoading : UiMigrationConfirmationState()

    object AcceptSuccess : UiMigrationConfirmationState()

    object RejectSuccess : UiMigrationConfirmationState()

    data class Error(
        val error: NewErrorMessage? = null,
        val isAccepted: Boolean = false,
        val screenName: String = EMPTY_STRING,
    ) : UiMigrationConfirmationState()
}