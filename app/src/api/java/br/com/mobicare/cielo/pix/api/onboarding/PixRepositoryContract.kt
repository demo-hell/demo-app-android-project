package br.com.mobicare.cielo.pix.api.onboarding

import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidResponse
import br.com.mobicare.cielo.pix.domain.ResponseEligibilityPix
import br.com.mobicare.cielo.pix.domain.ResponsePixDataQuery
import retrofit2.Response

interface PixRepositoryContract {
    fun destroyDisposable()
    fun createDisposable()
    fun pixEligibility(callBack: APICallbackDefault<ResponseEligibilityPix, String>)
    fun sendTerm(callBack: APICallbackDefault<Response<Void>, String>)
    fun sendTermPixPartner(callBack: APICallbackDefault<Response<Void>, String>)
    fun pixDataQuery(apiCallbackDefault: APICallbackDefault<ResponsePixDataQuery, String>)
    fun statusPix(callBack: APICallbackDefault<PrepaidResponse, String>, token: String)
}