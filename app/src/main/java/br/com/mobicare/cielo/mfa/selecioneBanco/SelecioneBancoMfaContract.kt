package br.com.mobicare.cielo.mfa.selecioneBanco

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.mfa.MfaAccount

interface SelecioneBancoMfaContract {
    interface View {
        fun show(contas: List<MfaAccount>)
        fun showLoading(isVisible: Boolean)
        fun enableNextButton(isEnabled: Boolean)
        fun showSuccessful()
        fun showError(error: ErrorMessage)
        fun showTemporarilyBlockError(error: ErrorMessage)
        fun showIneligible()
    }

    interface Presenter {
        fun load()
        fun selectedItem(account: MfaAccount)
        fun send()
    }
}