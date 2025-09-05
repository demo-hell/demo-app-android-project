package br.com.mobicare.cielo.cieloFarol.domain.repository

import br.com.mobicare.cielo.cieloFarol.data.model.response.CieloFarolResponse
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

interface CieloFarolRepository{
   suspend fun getCieloFarol(
            authorization: String,
            merchant: String?
    ): CieloDataResult<CieloFarolResponse>

}