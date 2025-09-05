package br.com.mobicare.cielo.extrato.data.managers

import br.com.mobicare.cielo.meusCartoes.domains.entities.CreditCardStatement
import io.reactivex.Observable

interface StatementRepository {

    fun statements(initialDt: String, finalDt: String, pageSize: Int, page: Int, merchantId: String?,
                   accessToken: String, proxyCard: String):
            Observable<CreditCardStatement>

}