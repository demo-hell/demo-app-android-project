package br.com.mobicare.cielo.featureToggle.api

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleParams
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleResponse
import io.reactivex.Observable


class FeatureToggleAPIDataSource(var context: Context) {
    var api: CieloAPIServices = CieloAPIServices.getInstance(context, BuildConfig.HOST_API)

    fun getFeatureToggle(params: FeatureToggleParams, page: Int? = null): Observable<FeatureToggleResponse> {
        return api.getFeatureToggle(system = params.system, version = params.version, platform = params.platform, page = page)
    }


    companion object {

        fun getInstance(context: Context): FeatureToggleAPIDataSource {
            return FeatureToggleAPIDataSource(context)
        }
    }
}