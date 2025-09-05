package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.trocaMaquina.fragments.openRequestMachines

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosMachine
import br.com.mobicare.cielo.taxaPlanos.domain.TerminalsResponse

interface OpenRequestMachinesContract {
    interface View : BaseView {
        fun renderState(state: OpenRequestMachineViewState)
        fun showMachines(solutionResponse: TerminalsResponse)
        fun onNextStep(versionMachine: String?, serialNumber: String?, orderNumber: String?, taxaPlanosMachine: TaxaPlanosMachine)
    }

    interface Presenter {
        var taxaPlanosMachineSeleted: TaxaPlanosMachine?
        fun onNextButtonClicked(versionMachine: String?, serialNumber: String?, orderNumber: String?)
        fun loadMerchantSolutionsEquipments()
        fun onCleared()
        fun onVersionNumberChanged(text: String)
        fun onOrderAndSerialNumberChanged(orderNumberText: String, serialNumberText: String)
        fun onTaxaPlanosMarchineSelected(item: TaxaPlanosMachine, orderNumberText: String, serialNumberText: String, versionNumberText: String)
    }
}