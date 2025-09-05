package br.com.mobicare.cielo.pagamentoLink.delivery.address.repository

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.CieloApplication.Companion.context
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.meuCadastro.domains.entities.CepAddressResponse
import io.reactivex.Observable

class CollectAddressDataSource {
    private var api: CieloAPIServices = CieloAPIServices.getInstance(context!!, BuildConfig.HOST_API)
    private val token = UserPreferences.getInstance().token

    fun getAddressByZipcode(zipcode: String): Observable<CepAddressResponse> {
        return api.fetchAddressByCep(token, zipcode)
    }
}