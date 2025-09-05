package br.com.mobicare.cielo.meusCartoes

import br.com.mobicare.cielo.meusCartoes.clients.api.BankDatasource
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.*
import io.reactivex.Observable

class BankTransactionRepository(private val bankDatasource: BankDatasource) {

    fun allBanks(): Observable<BanksSet> {
        return bankDatasource.allBanks()
    }


    fun beginTransfer(cardProxy: String, accessToken: String,
                      bankTransferRequest: BankTransferRequest): Observable<TransferResponse> {
        return bankDatasource.beginTransfer(cardProxy, accessToken, bankTransferRequest)
    }


    fun confirmTransfer(cardProxy: String, accessToken: String, transferId: String,
                        transferAuthorization: String):
            Observable<TransferConfirmationResponse> {
        return bankDatasource.confirmTransfer(cardProxy, accessToken, transferId, transferAuthorization)
    }

}