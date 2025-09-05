package br.com.mobicare.cielo.changeEc

import br.com.mobicare.cielo.changeEc.domain.HierachyResponse
import br.com.mobicare.cielo.changeEc.domain.Impersonate
import br.com.mobicare.cielo.changeEc.domain.ImpersonateRequest
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.merchants.MerchantsResponse
import br.com.mobicare.cielo.recebaMais.domain.UserOwnerResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class ChangeEcRepository(private val remoteDataSource: ChangeEcDataSource) {

    private var compositeDisp = CompositeDisposable()


    enum class HierarchyType(type: String) {
        NODE("NODE"),
        MERCHANT("MERCHANT")
    }


    fun impersonate(
        ec: String,
        token: String,
        hierarchyType: HierarchyType,
        callback: APICallbackDefault<Impersonate, String>,
        fingerprint: String
    ) {
        UserPreferences.getInstance().saveTokenImpersonate(token)
        compositeDisp.add(remoteDataSource.impersonate(
            ec, token, hierarchyType.name,
            ImpersonateRequest(fingerprint)
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { callback.onStart() }
            .doFinally { callback.onFinish() }
            .subscribe({ mImpersonate ->
                val userPreferences = UserPreferences.getInstance()
                userPreferences.saveTokenImpersonate(mImpersonate?.accessToken)
                userPreferences.clearMenuCache()
                callback.onSuccess(mImpersonate)
            }, {
                val errorMessage = APIUtils.convertToErro(it)
                callback.onError(errorMessage)
            })
        )
    }

    fun children(
        token: String,
        pageSize: Int? = null,
        pageNumber: Int? = null,
        searchCriteria: String?,
        callback: APICallbackDefault<HierachyResponse, String>
    ) {
        compositeDisp.add(remoteDataSource.children(token, pageSize, pageNumber, searchCriteria)
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

    fun getMerchants(token: String, callback: APICallbackDefault<MerchantsResponse, String>) {
        compositeDisp.add(remoteDataSource.getMerchants(token)
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

    fun loadMerchant(token: String, callback: APICallbackDefault<UserOwnerResponse, String>) {
        compositeDisp.add(remoteDataSource.loadMerchant(token, Utils.authorization())
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

}