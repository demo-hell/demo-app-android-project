package br.com.mobicare.cielo.main

import android.content.Context
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.login.domain.SendDeviceTokenResponse
import br.com.mobicare.cielo.login.domain.TokenFCM
import br.com.mobicare.cielo.me.MeResponse
import io.reactivex.Observable

class UserInformationRemoteDataSource(val context: Context) : UserInformationDataSource {

    private val api: CieloAPIServices = CieloAPIServices.getCieloBackInstance(context)

    override fun getUserInformation(accessToken: String): Observable<MeResponse> {
        return api.loadMe(accessToken)
    }

    override fun sendTokenFCM(tokenFCM: TokenFCM): Observable<SendDeviceTokenResponse> {
        return api.sendTokenFCM(tokenFCM)
    }


}