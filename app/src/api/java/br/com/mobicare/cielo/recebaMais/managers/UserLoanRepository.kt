package br.com.mobicare.cielo.recebaMais.managers

import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.recebaMais.domain.LoanSimulationResponse
import br.com.mobicare.cielo.recebaMais.domain.OfferSet
import br.com.mobicare.cielo.recebaMais.domains.entities.ResumoResponse
import io.reactivex.Observable
import java.math.BigDecimal

class UserLoanRepository(val cieloApi: CieloAPIServices) {


    fun offers(userToken: String): Observable<OfferSet> {
        return cieloApi.offers(userToken)
    }

    fun simulate(offerId: String, loanAmount: BigDecimal, firstInstallmentDt: String,
                 token: String):
            Observable<LoanSimulationResponse> {
        return cieloApi.simulation(offerId, loanAmount, firstInstallmentDt, token)
    }

    fun fetchContracts(userToken: String): Observable<ResumoResponse> {
        return cieloApi.fetchContracts(userToken)
    }

}