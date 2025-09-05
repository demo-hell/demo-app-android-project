package br.com.mobicare.cielo.meusCartoes

import br.com.mobicare.cielo.commons.data.DisposableDefault
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.CardActivationCatenoRequest
import br.com.mobicare.cielo.meusCartoes.domains.entities.CardActivationStatusResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CreditCardsNewRespository(private val remoteDataSource: CreditCardsNewDataSource) : DisposableDefault {

    private var compositeDisp = CompositeDisposable()

    override fun disposable() {
        compositeDisp.clear()
    }


    fun changePasswordCard(proxy: String, cardActivation: CardActivationCatenoRequest,
                           token: String, xAuthorization: String,
                           callback: APICallbackDefault<Int, String>) {

        compositeDisp.add(remoteDataSource.activateCardCateno(proxy, cardActivation, token, xAuthorization)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe({data ->
                    when {
                        data.code() in 200..204 -> {
                            callback.onSuccess(200)
                        }
                        data.code() == 401 -> {
                            val errorMessage = ErrorMessage()
                            errorMessage.logout = true
                            errorMessage.httpStatus = 401
                            errorMessage.code = "401"
                            callback.onError(errorMessage)

                        }
                        data.code() in 402..420 -> {
                            val errorMessage = ErrorMessage()
                            errorMessage.logout = false
                            errorMessage.httpStatus = 402
                            errorMessage.code = "420"
                            callback.onError(errorMessage)
                        }
                        else -> {
                            callback.onError(ErrorMessage())
                        }
                    }
                }, {
                    val errorMessage = APIUtils.convertToErro(it)

                    if (errorMessage.code.toInt() in 402..499) {
                        errorMessage.message = "Verifique se os dados foram preenchidos corretamente."
                    }
                    callback.onError(errorMessage)
                }))
    }

    fun activateCard(proxy: String, ec: String, token: String, callback: APICallbackDefault<CardActivationStatusResponse, String>) {

        compositeDisp.add(remoteDataSource.activateCreditCard(ec, token, proxy)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe({ response ->
                    if (response.isSuccessful){
                        callback.onSuccess(CardActivationStatusResponse(active = true))
                    } else {
                        val errorMessage = APIUtils.convertToErro(response)

                        if (errorMessage.code.toInt() in 402..499) {
                            errorMessage.message = "Verifique se os dados foram preenchidos corretamente."
                        }
                        callback.onError(errorMessage)
                    }
                }, {
                    val errorMessage = APIUtils.convertToErro(it)

                    if (errorMessage.code.toInt() in 402..499) {
                        errorMessage.message = "Verifique se os dados foram preenchidos corretamente."
                    }
                    callback.onError(errorMessage)
                }))

    }


}