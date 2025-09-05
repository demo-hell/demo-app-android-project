package br.com.mobicare.cielo.pagamentoLink.managers

import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.pagamentoLink.clients.api.LinkDataSource
import br.com.mobicare.cielo.pagamentoLink.domain.CreateLinkBodyResponse
import br.com.mobicare.cielo.pagamentoLink.domains.PaymentLinkDTO
import io.reactivex.Observable

class LinkRepository(private val linkDataSource: LinkDataSource) {

    fun generateLink(
        token: String,
        dto: PaymentLinkDTO,
        quickFilter: QuickFilter?
    ): Observable<CreateLinkBodyResponse> {
        return linkDataSource.generateLink(token, dto, quickFilter)
    }

}