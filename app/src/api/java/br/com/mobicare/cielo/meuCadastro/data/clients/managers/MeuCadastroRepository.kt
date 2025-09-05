package br.com.mobicare.cielo.meuCadastro.data.clients.managers

import android.annotation.SuppressLint
import br.com.mobicare.cielo.meuCadastro.data.clients.api.APICallbackPassword
import br.com.mobicare.cielo.meuCadastro.data.clients.api.MeuCadastroAPIDataSource
import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroEndereco
import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroObj
import br.com.mobicare.cielo.meuCadastro.presetantion.ui.MeuCadastroCallback
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.BodyChangePassword
import com.google.gson.internal.`$Gson$Preconditions`.checkNotNull
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by silvia.miranda on 25/04/2017.
 */

open class MeuCadastroRepository(remoteDataSource: MeuCadastroAPIDataSource) {
    private val remoteDataSource: MeuCadastroAPIDataSource = checkNotNull(remoteDataSource)
    private var ERROR = "ERROR"

    //TODO: Refazer
    @SuppressLint("CheckResult")
    fun getChangePassword(body: BodyChangePassword, callback: APICallbackPassword) {
        remoteDataSource.getChangePassword(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    when {
                        data.code() in 200..204 -> {
                            callback.onSuccess()
                        }
                        data.code() in 400..420 -> {
                            callback.onErrorAuthentication(data.message())
                        }
                        else -> {
                            callback.onError(data.message())
                        }
                    }
                },
                        { error ->
                            val erro = error?.message ?: ""
                            callback.onError(erro)
                        })
    }

    private fun checkAddress(response: MeuCadastroObj, callback: MeuCadastroCallback) {
        if (response.addresses != null) {
            val endFisico = response.getEndereco(MeuCadastroEndereco.Tipo.FISICO)
            val endContato = response.getEndereco(MeuCadastroEndereco.Tipo.CONTATO)

            if (endFisico == null && endContato == null) {
                //Caso nao tenha nenhum dos dois enderecos retorna error
                callback.onLoadContactAddress(null)
                callback.onLoadPhysicalAddress(null)
            } else if (endFisico == null) {
                //Caso nao tenha o endereco fisico
                getMapaUrl(endContato?.addressConcatenadoMap, MeuCadastroEndereco.Tipo.CONTATO, callback)
                callback.onLoadPhysicalAddress(null)
            } else if (endContato == null) {
                //Caso nao tenha o endereco de contato
                getMapaUrl(endFisico?.addressConcatenadoMap, MeuCadastroEndereco.Tipo.FISICO, callback)
                callback.onLoadContactAddress(null)
            } else {
                //Caso tenham os dois enderecos
                getMapaUrl(endFisico?.addressConcatenadoMap, MeuCadastroEndereco.Tipo.FISICO, callback)
                getMapaUrl(endContato?.addressConcatenadoMap, MeuCadastroEndereco.Tipo.CONTATO, callback)
            }
        }
    }

    fun getMapaUrl(address: String?, type: String, callback: MeuCadastroCallback) {
        remoteDataSource.getMap(address)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<MeuCadastroEndereco> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onComplete() {

                    }

                    override fun onError(e: Throwable) {
                        when (type) {
                            MeuCadastroEndereco.Tipo.FISICO -> callback.onLoadPhysicalAddress(ERROR)
                            MeuCadastroEndereco.Tipo.CONTATO -> callback.onLoadContactAddress(ERROR)
                        }
                    }


                    override fun onNext(response: MeuCadastroEndereco) {
                        when (type) {
                            MeuCadastroEndereco.Tipo.FISICO -> callback.onLoadPhysicalAddress(response.status)
                            MeuCadastroEndereco.Tipo.CONTATO -> callback.onLoadContactAddress(response.status)
                        }
                    }
                })
    }


    companion object {
        fun getInstance(remoteDataSource: MeuCadastroAPIDataSource): MeuCadastroRepository = MeuCadastroRepository(remoteDataSource)
    }
}
