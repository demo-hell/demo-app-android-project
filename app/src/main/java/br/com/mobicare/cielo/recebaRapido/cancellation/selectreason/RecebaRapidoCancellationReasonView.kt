package br.com.mobicare.cielo.recebaRapido.cancellation.selectreason

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage

interface RecebaRapidoCancellationReasonView {

    fun showLoading()
    fun onCancellationSuccess()
    fun onCacelltionError(error: ErrorMessage?)
}