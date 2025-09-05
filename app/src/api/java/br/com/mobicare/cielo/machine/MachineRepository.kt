package br.com.mobicare.cielo.machine

import br.com.mobicare.cielo.commons.data.DisposableDefault
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.machine.domain.MachineListOffersResponse
import br.com.mobicare.cielo.meuCadastro.domains.entities.CepAddressResponse
import br.com.mobicare.cielo.meuCadastroNovo.domain.MCMerchantResponse
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosSolutionResponse
import br.com.mobicare.cielo.taxaPlanos.domain.TerminalsResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MachineRepository(private val remoteDataSource: MachineDataSource) : DisposableDefault {

    private var compositeDisp = CompositeDisposable()

    override fun disposable() {
        compositeDisp.clear()
    }

    fun loadOffers(token: String, imageType: String,
                   callback: APICallbackDefault<MachineListOffersResponse, String>) {
        compositeDisp.add(remoteDataSource.loadSolutionsOffers(token, imageType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe({
                    callback.onSuccess(it)
                }, {
                    val errorMessage = APIUtils.convertToErro(it)
                    callback.onError(errorMessage)
                }))

    }

    fun loadMerchant(token: String,
                     callback: APICallbackDefault<MCMerchantResponse, String>) {
        compositeDisp.add(remoteDataSource.loadMerchant(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe({
                    callback.onSuccess(it)
                }, {
                    val errorMessage = APIUtils.convertToErro(it)
                    callback.onError(errorMessage)
                }))

    }

    fun fetchAddressByCep(token: String, cep: String,
                          callback: APICallbackDefault<CepAddressResponse, String>) {
        compositeDisp.add(remoteDataSource.fetchAddressByCep(token, cep)
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

    fun loadMarchine(token: String, callback: APICallbackDefault<TaxaPlanosSolutionResponse, String>) {
        compositeDisp.add(remoteDataSource.loadMarchine(token)
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

    fun loadMerchantSolutionsEquipments(token: String,
                                        callback: APICallbackDefault<TerminalsResponse, String>) {
        compositeDisp.add(remoteDataSource.loadMerchantSolutionsEquipments(token)
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

    fun getOrderAffiliationDetail(orderId: Int)
            = this.remoteDataSource.getOrderAffiliationDetail(orderId)

}
