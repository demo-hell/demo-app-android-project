package br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.machine

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.machine.MachineRepository
import br.com.mobicare.cielo.machine.domain.MachineListOffersResponse

class RequestMachinePresenter(
        private val _View: RequestMachineContract.View,
        private val _Repository: MachineRepository) : RequestMachineContract.Presenter {

    override fun onCleared() {
        _Repository.disposable()
    }

    override fun loadOffers(imageType: String) {

        val token = UserPreferences.getInstance().token
        if (token.isNullOrEmpty()) {
            _View.logout(ErrorMessage())
            return
        }

        _Repository.loadOffers(token, imageType,
                object : APICallbackDefault<MachineListOffersResponse, String> {

                    override fun onStart() {
                        _View.showLoading()
                    }

                    override fun onSuccess(response: MachineListOffersResponse) {
                        _View.showOffers(response)
                    }

                    override fun onError(error: ErrorMessage) {
                        if(error.logout) {
                            _View.logout(error)
                        } else {
                            _View.showError(error)
                        }
                    }

                })
    }


}