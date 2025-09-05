package br.com.mobicare.cielo.esqueciSenha.presentation.presenter

import br.com.mobicare.cielo.commons.constants.ERROR_NOT_BOOTING
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.component.bankData.BankDataRepository
import br.com.mobicare.cielo.esqueciSenha.data.clients.managers.EsqueciSenhaNewRepository
import br.com.mobicare.cielo.esqueciSenha.domains.entities.RecoveryPassword
import br.com.mobicare.cielo.esqueciSenha.presentation.ui.EsqueciSenhaContract
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.BanksSet
import com.akamai.botman.CYFMonitor

class EsqueciSenhaPresenter(
    private val mView: EsqueciSenhaContract.View,
    private val repository: BankDataRepository,
    private val repositoryNew: EsqueciSenhaNewRepository
) : EsqueciSenhaContract.Presenter {


    override fun resetPassword(data: RecoveryPassword) {

        val handler = object : APICallbackDefault<String, String> {
            override fun onStart() {}
            override fun onFinish() {}
            override fun onSuccess(response: String) {
                mView.hideProgress()
                mView.showSuccess(response)
            }

            override fun onError(error: ErrorMessage) {
                mView.hideProgress()
                if (error.errorCode == ERROR_NOT_BOOTING)
                    mView.onErrorNotBooting()
                else
                    mView.showError(error)
            }
        }
        mView.showProgress()
        repositoryNew.recoveryPassword(data, handler, CYFMonitor.getSensorData())
    }

    override fun loadBankList() {
        val handler = object : APICallbackDefault<BanksSet, String> {
            override fun onStart() {}
            override fun onFinish() {}
            override fun onSuccess(response: BanksSet) {
                mView.hideProgress()
                mView.populateBanks(Mapper.mapper(response))
                mView.hideError()

            }

            override fun onError(error: ErrorMessage) {
                mView.showErrorTapume(error)
                mView.hideProgress()
            }
        }
        mView.showProgress()
        repository.banks(handler)
    }

    override fun ecIsValid(ec: String): Boolean {
        return ec.length > 2
    }

    override fun userNameIsValid(userName: String): Boolean {
        return userName.length > 2
    }

    override fun pswISValid(psw: String): Boolean {
        return ValidationUtils.isValidPassword(psw)
    }

}
