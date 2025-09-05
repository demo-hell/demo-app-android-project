package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.contatos.ombudsman.message

import br.com.mobicare.cielo.centralDeAjuda.data.clients.CentralAjudaLogadoRepository
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.OmbudsmanRequest
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.OmbudsmanResponse
import br.com.mobicare.cielo.commons.constants.HelpCenter.FIFTY
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage

class OmbudsmanMessagePresenter(
        private val view: OmbudsmanMessageContract.View,
        private val repository: CentralAjudaLogadoRepository,
) : OmbudsmanMessageContract.Presenter {

    override fun onSendProtocol(
            ombudsmanRequest: OmbudsmanRequest?,
            subject: String?,
            protocol: String?,
            message: String?,
    ) {
        view.showLoading()

        if (ombudsmanRequest == null)
            view.showError()
        else {
            var userName = ombudsmanRequest.contactPerson
            userName?.let {
                if (it.length > FIFTY)
                    userName = it.subSequence(ZERO, FIFTY).toString()
            }

            ombudsmanRequest.contactPerson = userName
            ombudsmanRequest.protocol = protocol
            ombudsmanRequest.message = message
            ombudsmanRequest.subject = subject

            repository.sendProtocol(ombudsmanRequest, object : APICallbackDefault<OmbudsmanResponse, String> {

                override fun onError(error: ErrorMessage) {
                    view.hideLoading()
                    view.showError(error)
                }

                override fun onSuccess(response: OmbudsmanResponse) {
                    view.hideLoading()
                    view.onSuccess(response)
                }
            })
        }
    }

    override fun onPause() {
        repository.disposable()
    }

    override fun onResume() {
        repository.onResume()
    }
}