package br.com.mobicare.cielo.meuCadastro.data.clients.api

import android.annotation.SuppressLint
import android.content.Context
import android.util.Base64
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroEndereco
import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroObj
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.BodyChangePassword
import io.reactivex.Observable
import retrofit2.Response


class MeuCadastroAPIDataSource(var context: Context) {
    var api: CieloAPIServices? = null
    var apiBrands: CieloAPIServices? = null
    var token: String? = null
    var ec: String? = null
    var service: String? = null

    init {
        api = CieloAPIServices.getInstance(context)
        apiBrands = CieloAPIServices.getInstance(context, BuildConfig.HOST_API)
        token = UserPreferences.getInstance().token

        service = UserPreferences.getInstance().isConvivenciaUser.toString()

        MenuPreference.instance.getEC()?.let { ecNumber ->
            ec = ecNumber
        }
    }


//    fun cardBrandFees(): Observable<CardBrandFees> = apiBrands!!.getCardBrandFees(ec!!, service!!, token!!)
    fun getChangePassword(body: BodyChangePassword): Observable<Response<Void>> = apiBrands!!.getChangePassword(token!!, getAutorization(), body)


    fun getMap(address: String?): Observable<MeuCadastroEndereco> {
        api = CieloAPIServices.getInstance(context, BuildConfig.GOOGLE_MAPS_SERVER_URL)
        return api!!.getMapaURL(address)
    }

    @SuppressLint("NewApi")
    fun getAutorization(): String {

        val user = UserPreferences.getInstance().userName
        val password = UserPreferences.getInstance().keepUserPassword

        val auth = "${user}:${password}"
        val encodedParam = Base64.encodeToString(auth.toByteArray(), Base64.NO_WRAP)
        val authHeader = "$basic ${encodedParam}"
        return authHeader
    }

    companion object {
        fun getInstance(context: Context): MeuCadastroAPIDataSource = MeuCadastroAPIDataSource(context)
        const val basic = "Basic"
    }

}
