package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.address.newAddress

import br.com.mobicare.cielo.autoAtendimento.domain.MachineInstallAddressObj
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.machine.MachineRepository
import br.com.mobicare.cielo.meuCadastro.domains.entities.CepAddressResponse

class InstalacaoMaquinaChooseaddressNewPresenter(
        private val mView: InstalacaoMaquinaChooseAddressNewContract.View,
        private val mRepository: MachineRepository) : InstalacaoMaquinaChooseAddressNewContract.Presenter {


    override fun nextStep(addressObj: MachineInstallAddressObj) {
        var error = false
        if (addressObj.streetAddress.trim().isEmpty()) {
            mView.showErrorAddress()
            error = true
        }
        if (addressObj.city.trim().isEmpty()) {
            mView.showErrorCity()
            error = true
        }

        if (addressObj.numberAddress.trim().isEmpty()) {
            mView.showErrorNumberAddress()
            error = true
        }

        if (addressObj.neighborhood.trim().isEmpty()) {
            mView.showErrorNeighborhood()
            error = true
        }

        if (addressObj.referencePoint.trim().isEmpty()) {
            mView.showErrorReferencePoint()
            error = true
        }

        if (addressObj.state.trim().isEmpty()) {
            mView.showErrorState()
            error = true
        }

        if (addressObj.zipcode.trim().isEmpty()) {
            mView.showErrorZipcode()
            error = true
        }
        if (!error) {
            mView.nextStep(addressObj)
        }
    }


    override fun onCleared() {
        mRepository.disposable()
    }

    fun fetchAddressByCep(addressCode: String) {

        val token: String = UserPreferences.getInstance().token ?: ""

        if (token.isEmpty()) {
            mView.logout(ErrorMessage())
            return
        }

        mView.clearAddressFields()

        mRepository.fetchAddressByCep(token, addressCode, object : APICallbackDefault<CepAddressResponse, String> {
            override fun onStart() {
                mView.showLoading()
            }

            override fun onError(error: ErrorMessage) {
                if (error.logout) {
                    mView.logout(error)
                } else {
                    mView.showError(error)
                }
            }

            override fun onSuccess(response: CepAddressResponse) {
                mView.showAddress(response.addresses)
                mView.hideLoading()
            }
        })
    }

}