package br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.term.adapter

import br.com.mobicare.cielo.component.requiredDataField.data.model.response.Term

class TermLinkContract {

    abstract class View {
        abstract fun onTermClick(item: Term)
    }
    
}
