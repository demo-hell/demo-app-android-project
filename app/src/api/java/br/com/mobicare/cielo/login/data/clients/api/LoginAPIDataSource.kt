package br.com.mobicare.cielo.login.data.clients.api

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.login.domain.LoginMultichannelRequest
import br.com.mobicare.cielo.login.domains.entities.LoginObj
import br.com.mobicare.cielo.me.MeResponse
import io.reactivex.Observable

/**
 * Created by benhur.souza on 06/04/2017.
 */

class LoginAPIDataSource(context: Context) {

    var api = CieloAPIServices.getInstance(context, BuildConfig.SERVER_URL)

    var multichannelApi = CieloAPIServices.getInstance(context, BuildConfig.HOST_API)

    fun multichannelLogin(loginMultichannelRequest: LoginMultichannelRequest): Observable<LoginObj> {
        return multichannelApi.loginMultichannel(loginMultichannelRequest)
    }

    fun newLogin(token: String): Observable<MeResponse>{
        return multichannelApi.loadMe(token)
    }

    companion object {

        @JvmStatic
        fun getInstance(context: Context): LoginAPIDataSource {
            return LoginAPIDataSource(context)
        }
    }
}
