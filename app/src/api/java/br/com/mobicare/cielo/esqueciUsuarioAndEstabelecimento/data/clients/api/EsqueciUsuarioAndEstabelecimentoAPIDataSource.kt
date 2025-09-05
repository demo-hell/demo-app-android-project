package br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.data.clients.api

import android.content.Context
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.domains.entities.EsqueciEstabelecimentoObj
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.domains.entities.EsqueciUsuarioObj
import io.reactivex.Observable

/**
 * Created by benhur.souza on 12/04/2017.
 */

class EsqueciUsuarioAndEstabelecimentoAPIDataSource(context: Context) {
    var api: CieloAPIServices

    init {
        api = CieloAPIServices.getInstance(context)
    }

    fun recoveryUser(doc: String): Observable<EsqueciUsuarioObj> {
        return api.recoveryUser(doc)
    }

    fun sendEmail(doc: String?, ec : String?): Observable<EsqueciUsuarioObj> {
        return api.sendEmail(doc, ec)
    }

    fun recoveryEC(doc: String): Observable<EsqueciEstabelecimentoObj> {
        return api.recoveryEstableshiment(doc)
    }

    companion object {

        fun getInstance(context: Context): EsqueciUsuarioAndEstabelecimentoAPIDataSource {
            return EsqueciUsuarioAndEstabelecimentoAPIDataSource(context)
        }
    }
}
