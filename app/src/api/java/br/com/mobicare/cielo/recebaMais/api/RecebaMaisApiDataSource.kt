package br.com.mobicare.cielo.recebaMais.api

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.recebaMais.domain.BanksResponse
import br.com.mobicare.cielo.recebaMais.domain.HelpCenterResponse
import br.com.mobicare.cielo.recebaMais.domain.UserOwnerResponse
import br.com.mobicare.cielo.recebaMais.domains.entities.ContratacaoResponse
import br.com.mobicare.cielo.recebaMais.domains.entities.ContratarEmprestimoRecebaMaisRequest
import br.com.mobicare.cielo.recebaMais.domains.entities.ResumoResponse
import io.reactivex.Observable

class RecebaMaisApiDataSource(context: Context) {

    private val apiApollo: CieloAPIServices = CieloAPIServices.getInstance(context, BuildConfig.HOST_API)

    // Dados cadastrais
    fun getMerchant(autorization: String, token: String): Observable<UserOwnerResponse> {
        return apiApollo.getMerchat(autorization, token)
    }

    fun getBanks(): Observable<BanksResponse> {
        return apiApollo.getBanks()
    }

    fun getHelpCenter(): Observable<HelpCenterResponse> {
        return apiApollo.getHelpCenter()
    }

    fun setBorrow(token: String, contratarEmprestimo: ContratarEmprestimoRecebaMaisRequest, accessToken: String): Observable<ContratacaoResponse>{
        return apiApollo.setBorrow(token, contratarEmprestimo, accessToken)
    }

    fun summary(accessToken: String): Observable<ResumoResponse> {
        return apiApollo.summary(accessToken)
    }

    fun keepInterestOffer(offerId: String, accessToken: String, authorization: String): Observable<ContratacaoResponse> {
        return  apiApollo.keepInterestOffer(offerId, accessToken, authorization)
    }

}