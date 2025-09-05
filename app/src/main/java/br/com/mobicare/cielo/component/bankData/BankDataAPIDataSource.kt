package br.com.mobicare.cielo.component.bankData

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.BanksSet
import io.reactivex.Observable

/**
 * Created by benhur.souza on 11/04/2017.
 */

class BankDataAPIDataSource(context: Context) {
    private val api: CieloAPIServices = CieloAPIServices.getInstance(context, BuildConfig.HOST_API)

    fun allBanksNew(): Observable<BanksSet> {
        return api.allBanks()
    }

    companion object {

        private var instance: BankDataAPIDataSource? = null

        fun getInstance(context: Context): BankDataAPIDataSource {
            if (instance == null) {
                instance = BankDataAPIDataSource(context)
            }

            return instance as BankDataAPIDataSource
        }
    }
}
