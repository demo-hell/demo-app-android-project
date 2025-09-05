package br.com.mobicare.cielo.meusrecebimentosnew.calculationview.graph.repository

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.utils.Utils
import io.reactivex.Observable

class MeusRecebimentosGraficoRepository {

    private val api = CieloAPIServices.getInstance(CieloApplication.context!!, BuildConfig.HOST_API)
    private val token = UserPreferences.getInstance().token
    private val authorization = Utils.authorization()


    fun getPostingsGraph(initialDate: String, finalDate: String): Observable<PostingsResponse> {
        return api.getCaculationVisionGraph(authorization, token, BuildConfig.CLIENT_ID, initialDate, finalDate)
    }
}