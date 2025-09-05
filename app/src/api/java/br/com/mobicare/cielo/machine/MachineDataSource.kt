package br.com.mobicare.cielo.machine

import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.machine.domain.MachineListOffersResponse
import br.com.mobicare.cielo.meuCadastro.domains.entities.CepAddressResponse
import br.com.mobicare.cielo.meuCadastroNovo.domain.MCMerchantResponse
import io.reactivex.Observable

class MachineDataSource(private var api: CieloAPIServices) {

    fun loadSolutionsOffers(
        token: String,
        imageType: String
    ): Observable<MachineListOffersResponse> {
        return api.loadSolutionsOffers(token, imageType)
    }

    fun loadMerchant(token: String): Observable<MCMerchantResponse> {
        return api.loadMerchant(token)
    }

    fun fetchAddressByCep(token: String, cep: String): Observable<CepAddressResponse> {
        return api.fetchAddressByCep(token, cep)
    }

    fun loadMarchine(token: String) = api.loadMarchine(token)

    fun loadMerchantSolutionsEquipments(token: String) = api.loadMerchantSolutionsEquipments(token)

    fun getOrderAffiliationDetail(orderId: Int)
            = this.api.getOrderAffiliationDetail(orderId)

}
