package br.com.mobicare.cielo.meuCadastroNovo

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPI
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.meuCadastro.domains.entities.CepAddressResponse
import br.com.mobicare.cielo.meuCadastroNovo.domain.*
import br.com.mobicare.cielo.turboRegistration.data.model.response.AddressResponse

import io.reactivex.Observable
import retrofit2.Response

class MeuCadastroNovoDataSource (context: Context) {

    private val api: CieloAPIServices = CieloAPIServices.getInstance(context, BuildConfig.HOST_API)

    fun loadReceitaFederal(access_token: String) : Observable<ReceitaFederalResponse> {
        return api.loadReceitaFederal(access_token)
    }

    fun saveReceitaFederal(access_token: String) : Observable<ReceitaFederalResponse> {
        return api.saveReceitaFederal(access_token)
    }

    fun putMerchantOwner(
        accessToken: String,
        otpCode: String,
        owner: Owner
    ): Observable<Response<Void>> {
        return api.putMerchantOwner(accessToken, otpCode, owner)
    }

    fun putMerchantContact(
        accessToken: String,
        otpCode: String,
        contact: Contact
    ): Observable<Response<Void>> {
        return api.putMerchantContact(accessToken, otpCode, contact)
    }

    fun updateUserAddress(
        accessToken: String,
        otpCode: String,
        addressUpdateRequest: AddressUpdateRequest
    ): Observable<Response<Void>> {
        return api.updateUserAddress(accessToken, otpCode, addressUpdateRequest)
    }

    fun getAddressByCep(accessToken: String, cep: String) : Observable<AddressResponse> {
        return api.getAddressByCep(accessToken, cep)
    }

    fun fetchAddressByCep(accessToken: String, cep: String) : Observable<CepAddressResponse> {
        return api.fetchAddressByCep(accessToken, cep)
    }
}