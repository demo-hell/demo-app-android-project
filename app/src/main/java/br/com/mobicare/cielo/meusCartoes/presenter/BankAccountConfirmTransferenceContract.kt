package br.com.mobicare.cielo.meusCartoes.presenter

import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceEditTextView

interface BankAccountConfirmTransferenceContract {

    interface View {
        fun confirmTransference(textInputEditDt: TypefaceEditTextView,
                                textInputEditCvv: TypefaceEditTextView)
    }


}