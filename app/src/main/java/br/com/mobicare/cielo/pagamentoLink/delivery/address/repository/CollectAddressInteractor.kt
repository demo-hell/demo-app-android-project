package br.com.mobicare.cielo.pagamentoLink.delivery.address.repository

import br.com.mobicare.cielo.meuCadastro.domains.entities.CepAddressResponse
import io.reactivex.Observable

interface CollectAddressInteractor {

    fun getAddressByZipcode(zipCode: String): Observable<CepAddressResponse>
}