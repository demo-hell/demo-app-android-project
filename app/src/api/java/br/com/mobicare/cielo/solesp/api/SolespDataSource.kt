package br.com.mobicare.cielo.solesp.api

import br.com.mobicare.cielo.solesp.domain.SolespRequest

class SolespDataSource(private val api: SolespAPI) {

    fun sendSolespRequest(solespRequest: SolespRequest) = api.sendSolespRequest(solespRequest)

}