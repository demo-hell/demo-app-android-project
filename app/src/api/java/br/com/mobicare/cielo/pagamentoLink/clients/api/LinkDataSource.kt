package br.com.mobicare.cielo.pagamentoLink.clients.api

import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.pagamentoLink.domain.CreateLinkBodyResponse
import br.com.mobicare.cielo.pagamentoLink.domains.PaymentLinkDTO
import io.reactivex.Observable

interface LinkDataSource {

    fun generateLink(
        token: String,
        dto: PaymentLinkDTO,
        quickFilter: QuickFilter?
    ): Observable<CreateLinkBodyResponse>

}