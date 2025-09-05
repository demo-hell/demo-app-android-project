package br.com.mobicare.cielo.login.data.managers

import android.content.Context
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.login.data.clients.api.LoginAPIDataSource
import br.com.mobicare.cielo.login.domain.LoginMultichannelRequest
import br.com.mobicare.cielo.login.domains.entities.LoginObj
import br.com.mobicare.cielo.me.MeResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


open class LoginRepository(var context: Context, var remoteDataSource: LoginAPIDataSource) {

    private var compositeDisp = CompositeDisposable()
    fun multichannelLogin(username: String,
                          merchantId: String?,
                          password: String): Observable<LoginObj> {
        return remoteDataSource.multichannelLogin(
                LoginMultichannelRequest(username, merchantId, password))
    }

    fun newLogin(token: String, callback: APICallbackDefault<MeResponse, String>){

        compositeDisp.add(remoteDataSource.newLogin(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe({
                    callback.onSuccess(it)
                },{
                    val errorMessage = APIUtils.convertToErro(it)
                    callback.onError(errorMessage)
                }))


    }

    fun login(username: String, password: String, merchant: String?) {
        compositeDisp
    }

    companion object {

        fun getInstance(context: Context, remoteDataSource: LoginAPIDataSource): LoginRepository {
            return LoginRepository(context, remoteDataSource)
        }
    }

}
