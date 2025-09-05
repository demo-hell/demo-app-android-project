package br.com.mobicare.cielo.main

import br.com.mobicare.cielo.login.domain.SendDeviceTokenResponse
import br.com.mobicare.cielo.login.domain.TokenFCM
import br.com.mobicare.cielo.me.AnticipationEligibilityResponse
import br.com.mobicare.cielo.me.MeResponse
import io.reactivex.Observable

interface UserInformationDataSource {

    fun getUserInformation(accessToken: String)
            : Observable<MeResponse>

    fun sendTokenFCM(tokenFCM: TokenFCM): Observable<SendDeviceTokenResponse>
}