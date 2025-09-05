package br.com.mobicare.cielo.commons.data.managers

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage

interface APICallbackDefault<in RESPONSE, ERRROR> {
    fun onStart() {}
    fun onError(error: ErrorMessage){}
    fun onFinish() {}
    fun onSuccess(response: RESPONSE)
}

