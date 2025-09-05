package br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.data.clients.managers

import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.data.clients.api.EsqueciUsuarioAndEstabelecimentoAPIDataSource
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.domains.entities.EsqueciEstabelecimentoObj
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.domains.entities.EsqueciUsuarioObj
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.presentation.ui.EsqueciUsuarioAndEstabelecimentoCallback
import com.google.gson.internal.`$Gson$Preconditions`.checkNotNull
import io.reactivex.Observer

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class EsqueciUsuarioAndEstabelecimentoRepository(remoteDataSource: EsqueciUsuarioAndEstabelecimentoAPIDataSource) {
    private val remoteDataSource: EsqueciUsuarioAndEstabelecimentoAPIDataSource

    init {
        this.remoteDataSource = checkNotNull(remoteDataSource)
    }


    fun recoveryUser(doc: String, callback: EsqueciUsuarioAndEstabelecimentoCallback) {
        remoteDataSource.recoveryUser(doc)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe(object : Observer<EsqueciUsuarioObj> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onComplete() {
                        callback.onFinish()
                    }

                    override fun onError(e: Throwable) {
                        val errorMessage = APIUtils.convertToErro(e)
                        callback.onError(errorMessage)
                    }

                    override fun onNext(users: EsqueciUsuarioObj) {
                        callback.onSuccess(users)
                    }
                })
    }

    fun sendEmail(doc: String?, ec: String?, callback: EsqueciUsuarioAndEstabelecimentoCallback) {
        remoteDataSource.sendEmail(doc, ec)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe(object : Observer<EsqueciUsuarioObj> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onComplete() {
                        callback.onFinish()
                    }

                    override fun onError(e: Throwable) {
                        val errorMessage = APIUtils.convertToErro(e)
                        callback.onError(errorMessage)
                    }

                    override fun onNext(user: EsqueciUsuarioObj) {
                        callback.onSuccess(user)
                    }
                })
    }

    fun recoveryEC(cnpj: String, callback: EsqueciUsuarioAndEstabelecimentoCallback) {
        remoteDataSource.recoveryEC(cnpj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe(object : Observer<EsqueciEstabelecimentoObj> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onComplete() {
                        callback.onFinish()
                    }

                    override fun onError(e: Throwable) {
                        val errorMessage = APIUtils.convertToErro(e)
                        callback.onError(errorMessage)
                    }

                    override fun onNext(ecs: EsqueciEstabelecimentoObj) {
                        callback.onSuccess(ecs)
                    }
                })
    }

    companion object {
        fun getInstance(remoteDataSource: EsqueciUsuarioAndEstabelecimentoAPIDataSource): EsqueciUsuarioAndEstabelecimentoRepository {
            return EsqueciUsuarioAndEstabelecimentoRepository(remoteDataSource)
        }
    }
}
