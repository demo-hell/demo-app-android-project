package br.com.mobicare.cielo.taxaPlanos.presentation.ui.myPlan

import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosOverviewResponse
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosSolutionResponse

interface TaxaPlanosPlanContract {

    interface Presenter {
        fun onClieared()
        fun loadOverview(type: String)
        fun loadMarchine()
        fun getEligibleToOffer()
        fun confirmCancellation()
    }

    interface View : BaseView, IAttached {
        fun showOverview(response: TaxaPlanosOverviewResponse)
        fun showMachine(response: TaxaPlanosSolutionResponse)
        fun showOverviewError(error: ErrorMessage)
        fun showMachineError(error: ErrorMessage)
        fun showLoadMachines()
        fun hideLoadMachines()
        fun hideEmptyMachines()
        fun showChangeIncomingButton()
        fun showIncomingWay(isEnabledCancelIncomingFastFT: Boolean)
        fun showWhatsAppCancellationDialog(whatsappLink: String?)
        fun showCancellationActivity()
    }
}