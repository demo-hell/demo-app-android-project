package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.contatos

import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.Contact
import br.com.mobicare.cielo.commons.presentation.BasePresenter
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached

interface CentralAjudaContatosContract {
    interface View : BaseView, IAttached {
        fun showContacts(contacts: List<Contact>)
    }

    interface Presenter : BasePresenter<View> {
        fun loadContacts()
    }
}