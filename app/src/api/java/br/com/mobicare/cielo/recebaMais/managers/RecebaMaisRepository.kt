package br.com.mobicare.cielo.recebaMais.managers

import br.com.mobicare.cielo.recebaMais.api.RecebaMaisApiDataSource
import br.com.mobicare.cielo.recebaMais.domains.entities.ContratarEmprestimoRecebaMaisRequest

class RecebaMaisRepository(private val dataSource: RecebaMaisApiDataSource) {

    fun getMerchant(authorization: String, token: String) = dataSource.getMerchant(authorization, token)

    fun getBanks() = dataSource.getBanks()

    fun setBorrow(token: String, contratarEmprestimo: ContratarEmprestimoRecebaMaisRequest, accessToken: String) = dataSource.setBorrow(token, contratarEmprestimo, accessToken)
    fun summary(accessToken: String) = dataSource.summary(accessToken)
    fun keepInterestOffer(offerId: String, accessToken: String, authorization: String) = dataSource.keepInterestOffer(offerId, accessToken, authorization)
}
