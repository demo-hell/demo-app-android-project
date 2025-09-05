package br.com.mobicare.cielo.tapOnPhone.domain.repository

import br.com.mobicare.cielo.component.requiredDataField.data.model.request.OrdersRequest
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.OfferResponse
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.OrdersResponse
import br.com.mobicare.cielo.meuCadastroNovo.domain.Solution
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneSessionIdResponse
import io.reactivex.Observable

interface TapOnPhoneAccreditationRepository {
    fun loadAllBrands(): Observable<List<Solution>>
    fun loadOffers(additionalProduct: String? = null): Observable<OfferResponse>
    fun getSessionId(): Observable<TapOnPhoneSessionIdResponse>
    fun requestTapOnPhoneOrder(request: OrdersRequest): Observable<OrdersResponse>
}