package br.com.mobicare.cielo.meusRecebimentos.presentation.ui

import android.view.Menu
import android.view.MenuInflater
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceTextView
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.meusRecebimentos.domains.entities.BankDataObj
import br.com.mobicare.cielo.meusRecebimentos.domains.entities.IncomingObj
import br.com.mobicare.cielo.meusRecebimentos.domains.entities.MeusRecebimentosObj
import br.com.mobicare.cielo.meusrecebimentosnew.models.SummaryItems
import br.com.mobicare.cielo.meusrecebimentosnew.repository.SummaryResponse

/**
 * Created by silvia.miranda on 20/06/2017.
 */
interface MeusRecebimentosContract {

    interface View : IAttached {
        fun initiButtons()
        fun changeCardVisibility()
        fun loadDepositos(list: ArrayList<IncomingObj>)
        fun loadCalculationVision(summaryResponse: SummaryResponse, size: Int?)
        fun loadCardBancos(bankDatas: ArrayList<BankDataObj>)
        fun showAlert(message: String)
        fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
        fun showProgress()
        fun hideProgress()
        fun showAlertDetail(bankName: String?, list: Array<Double>)
        fun hideContent()
        fun logout(error: String)
        fun loadListReceipt(summaryItems: List<SummaryItems>, date: String?, cieloDate: String?, endDate: String? = null)
        fun callDetalhePendente(id: Int?, quantity: Int?, amount: Double, name: String?, date: String?, cieloDate: String?, endDate: String?)
        fun showEmpty(msgId: Int)
        fun showError(error: ErrorMessage)
        fun clickPosition(position: Int)
        fun clickFilterDaily()
        fun clickFilterDateInit()
        fun clickFilterDateEnd()
    }

    interface Presenter {
        fun callAPI(dailyDate: String? = null, initialDate: String? = null, finalDate: String? = null, period: String? = null, isConvivencia: Boolean)
        fun onStart()
        fun onError(error: ErrorMessage)
        fun onFinish()
        fun onSuccess(response: MeusRecebimentosObj)
        fun formatarValores(textValor: TypefaceTextView, valor: Double = 0.00, corValor: Boolean = true)
        fun hideValores(textValor: TypefaceTextView)
        fun managerValores(textValor: TypefaceTextView, valor: Double, corValor: Boolean)
        fun callAPIMeusLancamentos(date: String?, item: BankDataObj)
    }

}