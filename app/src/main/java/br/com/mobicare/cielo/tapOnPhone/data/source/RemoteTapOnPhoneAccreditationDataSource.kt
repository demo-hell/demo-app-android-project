package br.com.mobicare.cielo.tapOnPhone.data.source

import br.com.mobicare.cielo.component.requiredDataField.data.model.request.OrdersRequest
import br.com.mobicare.cielo.tapOnPhone.data.api.TapOnPhoneAPI

class RemoteTapOnPhoneAccreditationDataSource(private val api: TapOnPhoneAPI) {

    fun loadAllBrands() = api.loadBrands()

    fun loadOffers(additionalProduct: String? = null) = api.getTapOnPhoneOffer(additionalProduct)

    fun getSessionId() = api.getTapOnPhoneSessionId()

    fun requestTapOnPhoneOrder(request: OrdersRequest) =
        api.requestTapOnPhoneOrder(request)

}