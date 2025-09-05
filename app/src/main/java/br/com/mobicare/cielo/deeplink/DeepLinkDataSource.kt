package br.com.mobicare.cielo.deeplink

import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices

class DeepLinkDataSource(val api: CieloAPIServices){
    fun verificationEmailConfirmation(token: String?) = api.verificationEmailConfirmation(token)
    fun resendEmail(token: String?) = api.resendEmail(token)

}