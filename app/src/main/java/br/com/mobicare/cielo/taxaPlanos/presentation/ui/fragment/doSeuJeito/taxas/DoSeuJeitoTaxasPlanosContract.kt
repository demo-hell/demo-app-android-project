package br.com.mobicare.cielo.taxaPlanos.presentation.ui.fragment.doSeuJeito.taxas

import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.taxaPlanos.componentes.taxas.BandeiraModelView
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosSolutionResponse

interface DoSeuJeitoTaxasPlanosContract {
    interface View {
        fun showMachinesLoading(isShow: Boolean)
        fun showMachine(response: TaxaPlanosSolutionResponse)
        fun showMachineError(error: ErrorMessage)
        fun hideMachinesCard()
        fun showChangeIncomingButton(isShow: Boolean)
        fun showIncomingFastLoading(isShow: Boolean)
        fun showIncomingFastError(error: ErrorMessage)
        fun showTaxesLoading(isShow: Boolean)
        fun showTaxes(taxes: ArrayList<BandeiraModelView>)
        fun showTaxesError(error: ErrorMessage)
        fun showIncomingWay(isEnabledCancelIncomingFastFT: Boolean)
        fun showWhatsAppCancellationDialog(whatsappLink: String?)
        fun showCancellationActivity()
    }

    interface Presenter {
        fun load()
        fun loadTaxes()
        fun loadMachines()
        fun loadEligibleIncomingFastOffer()
        fun confirmCancellation()
    }
}