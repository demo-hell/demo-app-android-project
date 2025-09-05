package br.com.mobicare.cielo.recebaMais.managers

import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.recebaMais.domains.entities.ContractDetailsResponse
import io.reactivex.Observable

class MyResumeRepository(val cieloApi: CieloAPIServices) {

    fun getContractsDetails(userToken: String): Observable<ContractDetailsResponse> {
        return cieloApi.getContractDetails(userToken)
    }
}