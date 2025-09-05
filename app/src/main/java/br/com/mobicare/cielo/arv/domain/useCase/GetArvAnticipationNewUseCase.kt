package br.com.mobicare.cielo.arv.domain.useCase

import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.domain.repository.ArvRepositoryNew
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.successValueOrNull

class GetArvAnticipationNewUseCase(
    private val repository: ArvRepositoryNew
) {
    suspend operator fun invoke(
        negotiationType: String?,
        receiveToday: Boolean? = null
    ): CieloDataResult<ArvAnticipation> =
        repository.getAnticipation(negotiationType, getReceiveTodayValue(receiveToday))

    private suspend fun getReceiveTodayValue(receiveValue: Boolean?): Boolean {
        return receiveValue ?: retrieveReceiveTodayValue()
    }

    private suspend fun retrieveReceiveTodayValue(): Boolean {
        val arvBanksResult = repository.getArvBanks()
        return arvBanksResult.successValueOrNull?.firstOrNull()?.receiveToday ?: false
    }
}