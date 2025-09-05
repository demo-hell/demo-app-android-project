package br.com.mobicare.cielo.meusRecebimentos.presentation.ui

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.commons.utils.DataCustomNew
import br.com.mobicare.cielo.meusRecebimentos.domains.entities.IncomingObj
import com.github.mikephil.charting.data.Entry

/**
 * Created by benhur.souza on 26/06/2017.
 */
interface MeusRecebimentosGraficoContract {


    interface View : IAttached {
        fun showNextButton()
        fun hideNextButton()
        fun showPreviousButton()
        fun hidePreviousButton()
        fun setViewPagerCurrentItem(position: Int)
        fun setHighlightItem(position: Float)
        fun moveViewToX(position: Float)
        fun loadHeaderData(list: ArrayList<IncomingObj>)
        fun loadGraph()
        fun changeItem(position: Int = 0, obj: IncomingObj)
        fun sendClickGraphEventGA(label: String?)
        fun callSummary(date: String)
        fun showError(error: ErrorMessage)
    }

    interface Presenter {
        fun getPostingsGraph(mainDate: DataCustomNew?)
        fun getValues(): ArrayList<Entry>
        fun getXLabel(value: Int): String?
        fun getYLabel(position: Int, value: Float): String?
        fun initGraph(list: ArrayList<IncomingObj>)
        fun updateData(position: Float)
        fun onClickNext()
        fun onClickPrevious()
    }
}