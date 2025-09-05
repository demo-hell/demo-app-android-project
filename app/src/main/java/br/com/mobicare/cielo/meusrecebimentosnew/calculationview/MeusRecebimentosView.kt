package br.com.mobicare.cielo.meusrecebimentosnew.calculationview

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.meusrecebimentosnew.models.Summary
import br.com.mobicare.cielo.meusrecebimentosnew.repository.AlertsResponse
import br.com.mobicare.cielo.meusrecebimentosnew.repository.BankAccountItem
import br.com.mobicare.cielo.meusrecebimentosnew.repository.FileResponse
import br.com.mobicare.cielo.meusrecebimentosnew.repository.SummaryResponse
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter

interface MeusRecebimentosView {

    fun onCalculationVisionSuccess(summaryResponse: SummaryResponse, quickFilter: QuickFilter)
    fun onClickPendingAmount(summary: Summary, quickFilter: QuickFilter)
    fun onCalculationVisionError(error: ErrorMessage)
    fun onShowLoadingReceivablesBankAccounts(isShow: Boolean)
    fun onShowReceivablesBankAccounts(items: List<BankAccountItem>, isPrevisto: Boolean)
    fun onHideReceivablesBankAccounts()
    fun onShowLoadingCalculationVision(isShow: Boolean)
    fun onLoadAlertsSuccess(it: AlertsResponse?)
    fun onLoadAlertsError(convertToErro: ErrorMessage)

    fun onLoadAlertsPdfSuccess(it: FileResponse?)
    fun onLoadAlertsPdfError(convertToErro: ErrorMessage)

    fun hideAlerts()
    fun showAlerts(retorno: String)
}