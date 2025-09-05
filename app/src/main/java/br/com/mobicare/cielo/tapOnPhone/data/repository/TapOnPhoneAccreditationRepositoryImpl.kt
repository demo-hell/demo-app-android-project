package br.com.mobicare.cielo.tapOnPhone.data.repository

import br.com.mobicare.cielo.component.requiredDataField.data.model.request.OrdersRequest
import br.com.mobicare.cielo.meuCadastroNovo.domain.Solution
import br.com.mobicare.cielo.tapOnPhone.data.source.RemoteTapOnPhoneAccreditationDataSource
import br.com.mobicare.cielo.tapOnPhone.domain.repository.TapOnPhoneAccreditationRepository
import io.reactivex.Observable

class TapOnPhoneAccreditationRepositoryImpl(
    private val dataSource: RemoteTapOnPhoneAccreditationDataSource
) : TapOnPhoneAccreditationRepository {

    override fun loadAllBrands(): Observable<List<Solution>> = dataSource.loadAllBrands()

    override fun loadOffers(additionalProduct: String?) = dataSource.loadOffers(additionalProduct)

    override fun getSessionId() = dataSource.getSessionId()

    override fun requestTapOnPhoneOrder(request: OrdersRequest) =
        dataSource.requestTapOnPhoneOrder(request)

}