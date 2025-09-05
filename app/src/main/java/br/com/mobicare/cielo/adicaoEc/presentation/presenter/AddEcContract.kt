package br.com.mobicare.cielo.adicaoEc.presentation.presenter

import br.com.mobicare.cielo.adicaoEc.domain.model.BankAccountObj
import br.com.mobicare.cielo.adicaoEc.domain.model.ParamsEc
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.component.selectBottomSheet.SelectItem
import br.com.mobicare.cielo.esqueciSenha.domains.entities.BankMaskVO

interface AddEcContract {
    interface Presenter {
        fun getBankList()
        fun fetchAccountTypes(bankCode: String? = null, profileType: String, params: ParamsEc)
        fun prepareObjectToSubmit(objEc: BankAccountObj, profileType: String, params: ParamsEc): BankAccountObj
        fun addNewEc(objEc: BankAccountObj, otpCode: String)
        fun onResume()
        fun onStop()
    }

    interface View : BaseView {
        fun prepareBottomSheetBankList(bankList: ArrayList<SelectItem<BankMaskVO>>)
        fun prepareBottomSheetAccountTypeList(accountTypes: ArrayList<SelectItem<BankMaskVO>>)
        fun showBottomSheetSuccess()
    }
}