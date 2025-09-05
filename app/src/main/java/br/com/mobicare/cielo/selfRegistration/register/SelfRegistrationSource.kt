package br.com.mobicare.cielo.selfRegistration.register

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.selfRegistration.domains.AccountRegistrationPayLoadRequest
import br.com.mobicare.cielo.selfRegistration.domains.SelfRegistrationResponse
import io.reactivex.Observable

class SelfRegistrationSource(context: Context) {

    private var api: CieloAPIServices = CieloAPIServices.getInstance(context, BuildConfig.HOST_API)

    fun registrationAccount(accountRegistrationPayLoadRequest: AccountRegistrationPayLoadRequest, inviteToken: String?, akamaiSensorData: String?):
            Observable<SelfRegistrationResponse> {
        return api.registrationAccount(accountRegistrationPayLoadRequest, inviteToken, akamaiSensorData)
    }

}