package br.com.mobicare.cielo.pix.api.onboarding

import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidResponse
import br.com.mobicare.cielo.pix.domain.ResponseEligibilityPix
import br.com.mobicare.cielo.pix.domain.ResponsePixDataQuery
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Response

class PixRepository(private val api: CieloAPIServices) : PixRepositoryContract {

    private var disposable = CompositeDisposable()

    override fun destroyDisposable() {
        disposable.dispose()
    }

    override fun createDisposable() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun pixEligibility(callBack: APICallbackDefault<ResponseEligibilityPix, String>) {
        disposable.add(api.pixEligibility()
                .configureIoAndMainThread()
                .subscribe({
                    callBack.onSuccess(it)
                }, {
                    val errorMessage = APIUtils.convertToErro(it)
                    callBack.onError(errorMessage)
                })
        )
    }

    override fun sendTerm(callBack: APICallbackDefault<Response<Void>, String>) {
        disposable.add(api.sendTerm()
                .configureIoAndMainThread()
                .subscribe({
                    if (it.code() in 200..204)
                    callBack.onSuccess(it)
                    else
                        callBack.onError(APIUtils.convertToErro(it))
                }, {
                    callBack.onError(APIUtils.convertToErro(it))
                })
        )
    }

    override fun sendTermPixPartner(callBack: APICallbackDefault<Response<Void>, String>) {
        disposable.add(api.sendTermPixPartner()
                .configureIoAndMainThread()
                .subscribe({
                    if (it.code() in 200..204)
                        callBack.onSuccess(it)
                    else
                        callBack.onError(APIUtils.convertToErro(it))
                }, {
                    callBack.onError(APIUtils.convertToErro(it))
                })
        )
    }

    override fun pixDataQuery(apiCallbackDefault: APICallbackDefault<ResponsePixDataQuery, String>) {
        disposable.add(api.pixDataQuery()
                .configureIoAndMainThread()
                .subscribe({
                    apiCallbackDefault.onSuccess(it)
                }, {
                    val errorMessage = APIUtils.convertToErro(it)
                    apiCallbackDefault.onError(errorMessage)
                })
        )
    }

    override fun statusPix(callBack: APICallbackDefault<PrepaidResponse, String>, token: String) {
        disposable.add(api.getUserStatusPrepago(token)
            .configureIoAndMainThread()
            .subscribe({
                callBack.onSuccess(it)
            }, {
                val errorMessage = APIUtils.convertToErro(it)
                callBack.onError(errorMessage)
            })
        )
    }
}