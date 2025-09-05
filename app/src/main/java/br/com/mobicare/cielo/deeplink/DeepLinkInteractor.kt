package br.com.mobicare.cielo.deeplink

import br.com.mobicare.cielo.commons.data.domain.MultichannelUserTokenResponse
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * create by Enzo Teles
 * */
class DeepLinkInteractor(val dataSource: DeepLinkDataSource) : DeepLinkContract.Interactor {

    private var compositeDisp = CompositeDisposable()

    /**
     * método para limpar o disposable
     * */
    override fun disposable() {
        compositeDisp.clear()
    }

    /**
     * método que chama a validação do email do usuário na api
     * @param token
     * */
    override fun verificationEmailConfirmation(token: String?,
                                               apiCallbackDefault: APICallbackDefault<Unit, String>) {

        compositeDisp.add(dataSource.verificationEmailConfirmation(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    apiCallbackDefault.onSuccess(Unit)
                },{
                    val errorMessage = APIUtils.convertToErro(it)
                    apiCallbackDefault.onError(errorMessage)
                }))
    }

    /**
     * método que reenvia a validação do email do usuário na api
     * @param token
     * */
    override fun resendEmail(token: String?,
                             apiCallbackDefault: APICallbackDefault<MultichannelUserTokenResponse, String>) {
        compositeDisp.add(dataSource.resendEmail(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    apiCallbackDefault.onSuccess(it)
                },{
                    val errorMessage = APIUtils.convertToErro(it)
                    apiCallbackDefault.onError(errorMessage)
                }))
    }



}