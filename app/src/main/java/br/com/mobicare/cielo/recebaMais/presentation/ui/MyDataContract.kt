package br.com.mobicare.cielo.recebaMais.presentation.ui

import br.com.mobicare.cielo.commons.presentation.BasePresenter
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.recebaMais.domain.Bank
import br.com.mobicare.cielo.recebaMais.domain.BanksResponse
import br.com.mobicare.cielo.recebaMais.domain.Installment
import br.com.mobicare.cielo.recebaMais.domains.entities.Contract

interface MyDataContract {

    interface Presenter : BasePresenter<View> {
        fun loadMerchant(authorization: String, token: String)
        fun loadBanks()
        fun validatePhone(phone: String)
        fun validadeEmail(email: String) : String
        fun validadeBank(bank: Bank)
        fun setInstallment(installment: Installment)
        fun validate(): Boolean { return false }
        fun resumoContract(){}
    }


    interface View : BaseView, IAttached {
        fun merchantSuccess(email: String, phone: String){}
        fun banksSuccess(banksResponse: BanksResponse){}
        fun sucessBorrow() {}
        fun showMessageDomicilioObrigatorio() {}
        fun sucessSummary(contracts: List<Contract>) {}
        fun showErrorResponse(error: String){}
    }

}