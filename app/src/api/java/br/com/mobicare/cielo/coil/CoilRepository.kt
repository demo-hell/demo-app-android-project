package br.com.mobicare.cielo.coil

import br.com.mobicare.cielo.coil.domain.MerchantAddressResponse
import br.com.mobicare.cielo.coil.domain.MerchantBuySupplyChosenResponse
import br.com.mobicare.cielo.coil.domain.MerchantSuppliesResponde
import br.com.mobicare.cielo.coil.domain.MerchantSupplyChosenRequest
import br.com.mobicare.cielo.commons.data.DisposableDefault
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class CoilRepository(private val remoteDataSource: CoilDataDataSource) : DisposableDefault {

    private var compositeDisp = CompositeDisposable()

    override fun disposable() {
        compositeDisp.clear()
    }

    fun merchantSupplies(token: String, callback: APICallbackDefault<MerchantSuppliesResponde, String>) {
        compositeDisp.add(remoteDataSource.merchantSupplies(token)
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


    fun merchantAddress(token: String, callback: APICallbackDefault<MerchantAddressResponse, String>) {
        compositeDisp.add(remoteDataSource.merchantAddress(token)
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

    fun merchantBuySupply(token: String, supplies: MerchantSupplyChosenRequest,
                          callback: APICallbackDefault<MerchantBuySupplyChosenResponse, String>) {

        compositeDisp.add(remoteDataSource.merchantBuySupply(token, supplies.suplies)
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
}