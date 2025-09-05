package br.com.mobicare.cielo.suporteTecnico.domain.repo

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.suporteTecnico.data.EquipmentEligibilityResponse
import br.com.mobicare.cielo.suporteTecnico.data.ProblemEquipments

interface ProblemEquipmentsRepository {

    suspend fun getProblemEquipments(
    ) : CieloDataResult<List<ProblemEquipments>>

    suspend fun getEligibility(technology: String, code: String
    ) : CieloDataResult<EquipmentEligibilityResponse>
}