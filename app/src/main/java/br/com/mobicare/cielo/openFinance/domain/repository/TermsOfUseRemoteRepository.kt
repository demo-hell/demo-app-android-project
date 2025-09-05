package br.com.mobicare.cielo.openFinance.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.domain.model.TermsOfUse

interface TermsOfUseRemoteRepository {
    suspend fun getTermsOfUse(): CieloDataResult<TermsOfUse>
}