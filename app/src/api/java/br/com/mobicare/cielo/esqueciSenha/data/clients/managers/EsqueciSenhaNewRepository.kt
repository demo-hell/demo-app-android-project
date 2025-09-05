package br.com.mobicare.cielo.esqueciSenha.data.clients.managers

import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.esqueciSenha.data.clients.api.EsqueciSenhaNewAPIDataSource
import br.com.mobicare.cielo.esqueciSenha.domains.entities.RecoveryPassword
import br.com.mobicare.cielo.esqueciSenha.domains.entities.RecoveryPasswordResponse
import com.google.gson.internal.`$Gson$Preconditions`.checkNotNull
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class EsqueciSenhaNewRepository(remoteDataSource: EsqueciSenhaNewAPIDataSource) {
    private val remoteDataSource: EsqueciSenhaNewAPIDataSource = checkNotNull(remoteDataSource)


    fun recoveryPassword(data: RecoveryPassword, callback: APICallbackDefault<String, String>, akamaiSensorData: String?) {

        remoteDataSource.recoveryPassword(data, akamaiSensorData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe(object : Observer<RecoveryPasswordResponse> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onComplete() {
                        callback.onFinish()
                    }

                    override fun onError(e: Throwable) {
                        val errorMessage = APIUtils.convertToErro(e)
                        callback.onError(errorMessage)
                    }

                    override fun onNext(response: RecoveryPasswordResponse) {
                        callback.onSuccess(response.email)
                    }
                })
    }

    companion object {

        private var instance: EsqueciSenhaNewRepository? = null

        fun getInstance(remoteDataSource: EsqueciSenhaNewAPIDataSource): EsqueciSenhaNewRepository {
            if (instance == null) {
                instance = EsqueciSenhaNewRepository(remoteDataSource)
            }

            return instance as EsqueciSenhaNewRepository
        }
    }

}
