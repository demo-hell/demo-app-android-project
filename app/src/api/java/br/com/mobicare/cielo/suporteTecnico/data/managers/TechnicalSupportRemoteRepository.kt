package br.com.mobicare.cielo.suporteTecnico.data.managers

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.suporteTecnico.domain.entities.SupportItem
import br.com.mobicare.cielo.suporteTecnico.domain.repo.TechnicalSupportRepository
import io.reactivex.Observable


class TechnicalSupportRemoteRepository private constructor(val context: Context) :
        TechnicalSupportRepository {

    private val api: CieloAPIServices = CieloAPIServices.getInstance(context, BuildConfig.HOST_API)


    companion object {
        fun createTechnicalSupportRemote(context: Context) =
                TechnicalSupportRemoteRepository(context)
    }

    override fun fetchTechnicalSupportRepository(): Observable<List<SupportItem>> {
        return api.fetchTechnicalSupport()
    }

}