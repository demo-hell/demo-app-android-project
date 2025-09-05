package br.com.mobicare.cielo.taxaPlanos.presentation.ui.api

import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.postecipado.meuAluguel.PlanInformationResponse
import io.reactivex.Observable

class PostecipadoRepository(private val api: CieloAPIServices) {

    fun getPlanInformation(planName: String): Observable<PlanInformationResponse> =
        api.getPlanInformation(planName)
}