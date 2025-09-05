package br.com.mobicare.cielo.mfa.selecioneBanco

import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.mfa.BankEnrollmentResponse
import br.com.mobicare.cielo.mfa.MfaAccount
import br.com.mobicare.cielo.mfa.MfaRepository
import br.com.mobicare.cielo.mfa.commons.EnrollmentStatus

class SelecioneBancoMfaPresenter(
        private val view: SelecioneBancoMfaContract.View,
        private val repository: MfaRepository) : SelecioneBancoMfaContract.Presenter {

    private var accountSelected: MfaAccount? = null

    override fun load() {
        this.view.showLoading(true)
        this.repository.getMfaBanks(object : APICallbackDefault<ArrayList<MfaAccount>, String> {
            override fun onError(error: ErrorMessage) {
                this@SelecioneBancoMfaPresenter.view.showLoading(false)
                if (error.httpStatus != 401) {
                    this@SelecioneBancoMfaPresenter.view.showError(error)
                }
            }

            override fun onSuccess(response: ArrayList<MfaAccount>) {
                if (response.isNotEmpty()) {
                    if (response.isNotEmpty()) {
                        this@SelecioneBancoMfaPresenter.view.show(response)
                    }
                }
                else {
                    this@SelecioneBancoMfaPresenter.view.showIneligible()
                }
            }
        })
    }

    override fun selectedItem(account: MfaAccount) {
        this.accountSelected = account
        this.view.enableNextButton(true)
    }

    override fun send() {
        this.accountSelected?.let { itAccount ->
            this.view.showLoading(true)
            this.repository.postBankEnrollment(itAccount, object: APICallbackDefault<BankEnrollmentResponse, String> {
                override fun onError(error: ErrorMessage) {
                    this@SelecioneBancoMfaPresenter.view.showLoading(false)
                    if (error.httpStatus != 401) {
                        when (error.errorCode) {
                            EnrollmentStatus.PENNY_DROP_TEMPORARILY_BLOCKED.name ->  view.showTemporarilyBlockError(error)
                            else -> this@SelecioneBancoMfaPresenter.view.showError(error)
                        }
                    }
                }

                override fun onSuccess(response: BankEnrollmentResponse) {
                    this@SelecioneBancoMfaPresenter.view.showSuccessful()
                }
            })
        }
    }
}