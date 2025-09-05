package br.com.mobicare.cielo.centralDeAjuda.data.clients.api

import br.com.mobicare.cielo.centralDeAjuda.domains.entities.CentralAjudaObj
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPI
import io.reactivex.Observable

class CentralDeAjudaAPIDataSource(private val api: CieloAPI) {
    fun registrationData(): Observable<CentralAjudaObj> {
        return api.unloggedRegistrationData()
    }
}