package br.com.mobicare.cielo.pagamentoLink.delivery.address.repository

import br.com.mobicare.cielo.meuCadastro.domains.entities.CepAddressResponse
import io.reactivex.Observable

class CollectAddressInteractorImpl : CollectAddressInteractor {

    val dataSource = CollectAddressDataSource()

    override fun getAddressByZipcode(zipCode: String): Observable<CepAddressResponse> {
        return dataSource.getAddressByZipcode(zipCode)
    }
}