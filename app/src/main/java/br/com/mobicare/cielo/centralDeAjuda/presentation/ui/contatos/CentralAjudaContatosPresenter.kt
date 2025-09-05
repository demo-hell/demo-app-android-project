package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.contatos

import br.com.mobicare.cielo.centralDeAjuda.data.clients.CentralAjudaLogadoRepository
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.Contact
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage

class CentralAjudaContatosPresenter(private val repository: CentralAjudaLogadoRepository) : CentralAjudaContatosContract.Presenter {

    private lateinit var view: CentralAjudaContatosContract.View

    override fun setView(view: CentralAjudaContatosContract.View) {
        this.view = view
    }

    override fun loadContacts() {
        val accessToken = UserPreferences.getInstance().token
        this.repository.getFaqContacts(accessToken,
                object : APICallbackDefault<List<Contact>, String> {
                    override fun onStart() {
                        this@CentralAjudaContatosPresenter.view.showLoading()
                    }

                    override fun onSuccess(response: List<Contact>) {
                        this@CentralAjudaContatosPresenter.view.showContacts(response)
                        this@CentralAjudaContatosPresenter.view.hideLoading()
                    }

                    override fun onError(error: ErrorMessage) {
                        if (error.logout) {
                            this@CentralAjudaContatosPresenter.view.logout(error)
                        } else {
                            this@CentralAjudaContatosPresenter.view.showError(error)
                        }
                    }
                })
    }

}