package br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.offer.accountSelector

import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneAccount

interface AccountSelectorContract {

    interface View {
        fun onAccountSelected(account: TapOnPhoneAccount)
    }

    interface Result {
        fun onAccountConfirm(account: TapOnPhoneAccount)
    }
}