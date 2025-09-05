package br.com.mobicare.cielo.adicaoEc.domain.api

import br.com.mobicare.cielo.adicaoEc.domain.model.BankAccountObj
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.BanksSet
import io.reactivex.Observable

class AddEcRepository(private val api: CieloAPIServices) {

    fun getAllBanks(): Observable<BanksSet> = api.allBanks()
    fun addNewEc(objEc: BankAccountObj, otpCode: String) = api.addNewEc(objEc, otpCode)
}