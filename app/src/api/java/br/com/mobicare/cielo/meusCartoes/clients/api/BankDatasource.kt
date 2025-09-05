package br.com.mobicare.cielo.meusCartoes.clients.api

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.*
import io.reactivex.Observable

class BankDatasource(val context: Context) {

    val api: CieloAPIServices = CieloAPIServices.getInstance(context,
            BuildConfig.HOST_API)

    fun allBanks(): Observable<BanksSet> {
        return api.allBanks()
    }


    fun beginTransfer(cardProxy: String, accessToken: String,
                      bankTransferRequest: BankTransferRequest): Observable<TransferResponse> {
        return api.beginTransfer(cardProxy, accessToken, bankTransferRequest)
    }

    fun confirmTransfer(cardProxy: String, accessToken: String, transferId: String,
                        transferAuthorization: String):
            Observable<TransferConfirmationResponse> {
        return api.confirmTransfer(cardProxy, accessToken, transferId, transferAuthorization)
    }


}