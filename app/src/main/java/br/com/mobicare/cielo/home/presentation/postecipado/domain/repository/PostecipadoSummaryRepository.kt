package br.com.mobicare.cielo.home.presentation.postecipado.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.postecipado.meuAluguel.PlanInformationResponse

interface PostecipadoSummaryRepository {
    suspend fun getPlanInformation(planName: String): CieloDataResult<PlanInformationResponse>
}