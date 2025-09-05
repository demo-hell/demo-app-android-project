package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.trocaMaquina.fragments.openRequestResume

import br.com.mobicare.cielo.autoAtendimento.domain.MachineInstallAddressObj
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.machine.domain.Availability
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosMachine

interface OpenRequestResumeContract {

    interface View : IAttached {
        fun showLoading()
        fun hideLoading()
        fun logout(msg: ErrorMessage)
        fun showError(error: ErrorMessage)
        fun setRentSolicitation(machineName: String?, logicalNumber: String, versionNumber: String)
        fun setPurchaseSolicitation(machineName: String?, serialNumber: String, orderNumber: String)
        fun setDeliveryAddress(
            address: MachineInstallAddressObj,
            period: Availability,
            establishmentName: String,
            referencePoint: String
        )
        fun setContact(personName: String, personPhoneNumber: String)
        fun showSucessfull(protocol: String?, hours: Int?)
        fun showRentalMachineErrorMessage()
    }

    interface Presenter {
        fun onConfirmButtonClicked()
        fun setData(
                versionMachine: String?,
                machineItem: TaxaPlanosMachine,
                address: MachineInstallAddressObj,
                personName: String,
                personPhoneNumber: String,
                period: Availability,
                orderNumber: String?,
                serialNumber: String?,
                establishmentName: String,
                referencePoint: String)
    }

}