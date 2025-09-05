package br.com.mobicare.cielo.esqueciSenha.presentation.ui

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.esqueciSenha.domains.entities.BankListResponse
import br.com.mobicare.cielo.esqueciSenha.domains.entities.BankMaskVO
import br.com.mobicare.cielo.esqueciSenha.domains.entities.RecoveryPassword

interface EsqueciSenhaContract {

    interface View : IAttached {
        fun showProgress()
        fun hideProgress()
        fun populateBanks(bankListResponse: BankListResponse)
        fun populateAccount(bank: BankMaskVO?, typeperfilName: String)
        fun showError(error: ErrorMessage)
        fun showSuccess(message: String)
        fun changeActivity()
        fun showErrorTapume(error: ErrorMessage)
        fun hideError()
        fun onErrorNotBooting()
//        fun verificationStatusLogin(status: String)
    }

    interface Presenter {
        fun resetPassword(data: RecoveryPassword)
        fun loadBankList()
        fun ecIsValid(ec: String): Boolean
        fun userNameIsValid(userName: String): Boolean
        fun pswISValid(psw: String): Boolean
//        fun getLoginVerificationStatus(ec: String)
//        fun getBaseContext(baseContext: Context)
    }
}
