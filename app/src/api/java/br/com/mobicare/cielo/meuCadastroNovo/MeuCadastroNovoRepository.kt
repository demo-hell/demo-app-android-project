package br.com.mobicare.cielo.meuCadastroNovo

import br.com.mobicare.cielo.commons.data.DisposableDefault
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.meuCadastro.domains.entities.CepAddressResponse
import br.com.mobicare.cielo.meuCadastroNovo.domain.*
import br.com.mobicare.cielo.turboRegistration.data.model.response.AddressResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import java.net.HttpURLConnection.HTTP_NO_CONTENT
import java.net.HttpURLConnection.HTTP_OK

class MeuCadastroNovoRepository(private val remoteDataSource: MeuCadastroNovoDataSource) :
    DisposableDefault {


    private var compositeDisp = CompositeDisposable()

    override fun disposable() {
        compositeDisp.clear()
    }

    fun loadReceitaFederal(
        access_token: String,
        callback: APICallbackDefault<ReceitaFederalResponse, String>
    ) {

        compositeDisp.add(remoteDataSource.loadReceitaFederal(access_token)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { callback.onStart() }
            .subscribe({
                callback.onSuccess(it)
            }, {
                callback.onError(APIUtils.convertToErro(it))
            })
        )
    }

    fun saveReceitaFederal(
        access_token: String,
        callback: APICallbackDefault<ReceitaFederalResponse, String>
    ) {

        compositeDisp.add(remoteDataSource.saveReceitaFederal(access_token)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { callback.onStart() }
            .subscribe({
                callback.onSuccess(it)
            }, {
                val errorMessage = APIUtils.convertToErro(it)
                callback.onError(errorMessage)
            })
        )
    }

    fun putOwner(
        accessToken: String,
        otpCode: String,
        owner: Owner,
        callback: APICallbackDefault<Response<Void>, String>
    ) {
        compositeDisp.add(remoteDataSource.putMerchantOwner(accessToken, otpCode, owner)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { callback.onStart() }
            .doFinally { callback.onFinish() }
            .subscribe({
                if (it.code() in HTTP_OK..HTTP_NO_CONTENT)
                    callback.onSuccess(it)
                else
                    callback.onError(APIUtils.convertToErro(it))
            }, {
                callback.onError(APIUtils.convertToErro(it))
            })
        )
    }

    fun putContact(
        accessToken: String,
        otpCode: String,
        contact: Contact,
        callback: APICallbackDefault<Response<Void>, String>
    ) {
        compositeDisp.add(remoteDataSource.putMerchantContact(accessToken, otpCode, contact)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { callback.onStart() }
            .subscribe({
                if (it.code() in HTTP_OK..HTTP_NO_CONTENT)
                    callback.onSuccess(it)
                else
                    callback.onError(APIUtils.convertToErro(it))
            }, {
                callback.onError(APIUtils.convertToErro(it))
            })
        )
    }

    fun updateUserAddress(
        accessToken: String,
        otpCode: String,
        updateAddressUpdateRequest: AddressUpdateRequest
    ): Observable<Response<Void>> {
        return remoteDataSource.updateUserAddress(
            accessToken,
            otpCode,
            updateAddressUpdateRequest
        )
    }

    fun getAddressByCep(accessToken: String, cep: String): Observable<AddressResponse> {
        return remoteDataSource.getAddressByCep(accessToken, cep)
    }

    fun fetchAddressByCep(accessToken: String, cep: String): Observable<CepAddressResponse> {
        return remoteDataSource.fetchAddressByCep(accessToken, cep)
    }

}