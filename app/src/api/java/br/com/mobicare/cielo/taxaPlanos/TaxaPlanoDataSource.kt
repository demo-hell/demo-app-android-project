package br.com.mobicare.cielo.taxaPlanos

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.meuCadastro.domains.entities.CardBrandFees
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosOverviewResponse
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosSolutionResponse
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosStatusPlanResponse
import io.reactivex.Observable

class TaxaPlanoDataSource(context: Context) {
    private var api: CieloAPIServices = CieloAPIServices.getInstance(context, BuildConfig.HOST_API)

    fun loadStatusPlan(token: String): Observable<TaxaPlanosStatusPlanResponse> {
        return api.loadStatusPlan(token)
    }

    fun loadPlanDetails(planName: String)
            = this.api.loadPlanDetails(planName)


    fun loadOverview(token: String, type: String): Observable<TaxaPlanosOverviewResponse> {
        return api.loadOverview(token, type)
    }

    fun getOfferIncomingFastDetail() = api.getOfferIncomingFastDetail()

    fun getEligibleToOffer() = api.getEligibleToOffer()

    fun loadMarchine(token: String): Observable<TaxaPlanosSolutionResponse> {
        return api.loadMarchine(token)
    }

    fun isEnabledIncomingFastFT() = FeatureTogglePreference
            .instance.getFeatureTogle(FeatureTogglePreference.RECEBA_RAPIDO_CONTRATACAO)
    fun isEnabledCancelIncomingFastFT() = FeatureTogglePreference
            .instance.getFeatureTogle(FeatureTogglePreference.RECEBA_RAPIDO_CANCELAMENTO)
}