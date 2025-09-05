package br.com.mobicare.cielo.commons.utils.dialog

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage

interface BottomSheetValidationTokenView {

    fun onSuccess(response: String)
    fun onError(error: ErrorMessage)
}