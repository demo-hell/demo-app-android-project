package br.com.mobicare.cielo.pagamentoLink.domains

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import io.reactivex.Completable

class PgLinkDataDataSource(context: Context)  {
    private var api: CieloAPIServices = CieloAPIServices.getInstance(context, BuildConfig.HOST_API)

    fun deleteLink(token: String?, linkId: DeleteLink): Completable {
        return api.deleleLink(token, linkId)
    }

}