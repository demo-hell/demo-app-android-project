package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.address.newAddress

import br.com.mobicare.cielo.autoAtendimento.domain.MachineInstallAddressObj
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.meuCadastro.domains.entities.CepAdress

interface InstalacaoMaquinaChooseAddressNewContract {
    interface View : BaseView, IAttached {
        fun showAddress(addresses: List<CepAdress>?)
        fun showErrorZipcode()
        fun showErrorState()
        fun showErrorReferencePoint()
        fun showErrorNeighborhood()
        fun showErrorCity()
        fun showErrorAddress()
        fun showErrorNumberAddress()
        fun nextStep(addressObj: MachineInstallAddressObj)
        fun clearAddressFields()
    }

    interface Presenter {
        fun onCleared()
        fun nextStep(addressObj: MachineInstallAddressObj)
    }
}