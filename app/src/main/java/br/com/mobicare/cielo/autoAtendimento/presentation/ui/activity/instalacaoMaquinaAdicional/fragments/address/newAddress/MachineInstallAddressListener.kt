package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.address.newAddress

import br.com.mobicare.cielo.autoAtendimento.domain.MachineInstallAddressObj

interface MachineInstallAddressListener {
    fun onAddressChosen(address: MachineInstallAddressObj?)
}