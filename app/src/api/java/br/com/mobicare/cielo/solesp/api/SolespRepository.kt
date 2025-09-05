package br.com.mobicare.cielo.solesp.api

import br.com.mobicare.cielo.solesp.domain.SolespRequest

class SolespRepository(private val dataSource: SolespDataSource) {

    fun sendSolespRequest(solespRequest: SolespRequest) = dataSource.sendSolespRequest(solespRequest)

}