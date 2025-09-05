package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.resumoEfetivacao

import br.com.mobicare.cielo.autoAtendimento.domain.MachineInstallAddressObj
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.machine.domain.Availability
import br.com.mobicare.cielo.machine.domain.MachineItemOfferResponse

interface InstalacaoMaquinaAdicionalResumoEfetivacaoContract {

    interface View : IAttached {
        fun showLoading()
        fun hideLoading()
        fun logout(msg: ErrorMessage)
        fun showError(error: ErrorMessage)
        fun setSolicitation(machine: MachineItemOfferResponse, amount: Int)
        fun setDeliveryAddress(address: MachineInstallAddressObj, period: Availability, establishmentName: String, referencePoint: String)
        fun setContact(personName: String, personPhoneNumber: String)
        fun showSucessfull(protocol: String?, days: Int?, amount: Int?, rentalPrice: Double?)
    }

    interface Presenter {
        fun onConfirmButtonClicked()
        fun setData(
                machineItem: MachineItemOfferResponse,
                amount: Int,
                address: MachineInstallAddressObj,
                personName: String,
                personPhoneNumber: String,
                period: Availability,
                establishmentName: String,
                referencePoint: String)
    }

}