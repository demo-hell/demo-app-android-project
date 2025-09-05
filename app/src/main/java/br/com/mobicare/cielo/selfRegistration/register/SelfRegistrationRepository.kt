package br.com.mobicare.cielo.selfRegistration.register

import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.selfRegistration.domains.AccountRegistrationPayLoadRequest
import br.com.mobicare.cielo.selfRegistration.domains.SelfRegistrationResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SelfRegistrationRepository(private var remoteSource: SelfRegistrationSource) {

    private var compositeDisp = CompositeDisposable()

    fun registrationAccount(accountRegistrationPayLoadRequest: AccountRegistrationPayLoadRequest,
                            callback: APICallbackDefault<SelfRegistrationResponse, String>,
                            inviteToken: String? = null,
                            akamaiSensorData: String? = null) {
        compositeDisp.add(remoteSource.registrationAccount(accountRegistrationPayLoadRequest, inviteToken, akamaiSensorData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }

                .doOnComplete {
                    callback.onFinish()
                }
                .subscribe({
                    callback.onSuccess(it)
                }, {
                    val errorMessage = APIUtils.convertToErro(it)
                    callback.onError(errorMessage)
                }))
    }

}