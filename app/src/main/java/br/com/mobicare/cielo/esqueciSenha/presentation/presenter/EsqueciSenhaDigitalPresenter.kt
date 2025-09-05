package br.com.mobicare.cielo.esqueciSenha.presentation.presenter

import br.com.mobicare.cielo.commons.constants.ERROR_NOT_BOOTING
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.esqueciSenha.data.clients.managers.EsqueciSenhaNewRepository
import br.com.mobicare.cielo.esqueciSenha.domains.entities.RecoveryPassword
import br.com.mobicare.cielo.esqueciSenha.presentation.ui.EsqueciSenhaDigitalContract
import com.akamai.botman.CYFMonitor

class EsqueciSenhaDigitalPresenter(
    private val mView: EsqueciSenhaDigitalContract.View,
    private val repository: EsqueciSenhaNewRepository
) : EsqueciSenhaDigitalContract.Presenter {


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
        repository.recoveryPassword(data, handler, CYFMonitor.getSensorData())
    }


}
