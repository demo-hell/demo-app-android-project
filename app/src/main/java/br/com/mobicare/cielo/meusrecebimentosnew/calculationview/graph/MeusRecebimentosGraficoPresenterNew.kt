package br.com.mobicare.cielo.meusrecebimentosnew.calculationview.graph

import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.utils.DataCustomNew
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.meusRecebimentos.domains.entities.IncomingObj
import br.com.mobicare.cielo.meusRecebimentos.presentation.ui.MeusRecebimentosGraficoContract
import br.com.mobicare.cielo.meusrecebimentosnew.calculationview.graph.repository.MeusRecebimentosGraficoRepository
import br.com.mobicare.cielo.meusrecebimentosnew.calculationview.graph.repository.PostingGraphMapper
import com.github.mikephil.charting.data.Entry
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by benhur.souza on 26/06/2017.
 */
class MeusRecebimentosGraficoPresenterNew(var mView: MeusRecebimentosGraficoContract.View,
                                          val repository: MeusRecebimentosGraficoRepository) : MeusRecebimentosGraficoContract.Presenter {

    var incomingObjList: ArrayList<IncomingObj> = ArrayList()
    var entriesList: ArrayList<Entry> = ArrayList()
    var selected: Float = 0f
    var currentPosition: Int = -1
    var labelMap = HashMap<Int, String>()

    private val disposible = CompositeDisposable()

    override fun getValues(): ArrayList<Entry> {
        return entriesList
    }

    override fun getYLabel(position: Int, value: Float): String? {
        if (position == 0 || position >= (entriesList.size - 1)) {
            return null
        }
        val formatter = NumberFormat.getNumberInstance()
        formatter.minimumFractionDigits = 0
        formatter.maximumFractionDigits = 0

        var label = "R$ " + formatter.format(value.toInt()).replace(",", ".")

        if (position == selected.toInt()) {
            label = "$label*"
        }

        return label
    }

    override fun getXLabel(value: Int): String? {
        var label = labelMap[value]
        if (selected.toInt() == value) {
            label = "$label*"
        }
        return label
    }

   override fun getPostingsGraph(mainDate: DataCustomNew?) {
        var date = mainDate
        if (mainDate == null) date = DataCustomNew()

        repository.getPostingsGraph(getInitialDate(date!!), getFinalDate(date))
                .configureIoAndMainThread()
                .subscribe({
                    initGraph(PostingGraphMapper.mapper(it, date))
                }, {
                    mView.showError(APIUtils.convertToErro(it))
                })
                .addTo(disposible)
    }

    private fun getInitialDate(date: DataCustomNew): String {
        val initialDate = DataCustomNew()
        initialDate.setDate(date.toDate())
        initialDate.setDateByDate(-7)
        return initialDate.formatDateToAPI()
    }

    private fun getFinalDate(date: DataCustomNew): String {
        val finalDate = DataCustomNew()
        finalDate.setDate(date.toDate())
        finalDate.setDateByDate(7)
        return finalDate.formatDateToAPI()
    }

    override fun initGraph(list: ArrayList<IncomingObj>) {
        if (!mView.isAttached()) {
            return
        }

        incomingObjList = list
        createList(list)
        mView.loadGraph()
        mView.loadHeaderData(list)
        mView.setHighlightItem(selected)
    }

    fun createList(list: ArrayList<IncomingObj>) {
        //Adicionando os 3 primeiros itens
        entriesList.add(0, Entry(0f, 0f))
        labelMap.put(0, "")

        entriesList.add(0, Entry(0f, 0f))
        labelMap.put(1, "")

        entriesList.add(0, Entry(0f, 0f))
        labelMap.put(2, "")

        var x: Float = 3f
        for (item: IncomingObj in list) {

            //Adicionando valor do dia atual ou o que foi selecionado na home
            if (item.mainDay) {
                if (currentPosition == -1) {
                    selected = x
                    currentPosition = selected.toInt() - 1
                } else {
                    selected = currentPosition + 1f
                }

                mView.setViewPagerCurrentItem(currentPosition)
            }

            //Adicionando valores do gráfico
            entriesList.add(Entry(x, item.totalDeposited?.toFloat()))

            //Adicionando labels do eixo X
            labelMap.put(x.toInt(), "${item.dayOfMonth}#${item.dayOfWeek}")
            x += 1f
        }

        //Adicionando os 3 ultimos itens
        entriesList.add(Entry(x + 2, 0f))
        labelMap.put(x.toInt() + 2, "")
    }

    override fun updateData(position: Float) {
        selected = position
        currentPosition = selected.toInt() - 1
        mView.moveViewToX(position - 2.5f)
        mView.setViewPagerCurrentItem(currentPosition - 2)

        if (currentPosition >= incomingObjList.size + 1) {
            mView.hideNextButton()
            mView.showPreviousButton()
        } else if (currentPosition <= 2) {
            mView.showNextButton()
            mView.hidePreviousButton()
        } else {
            mView.showNextButton()
            mView.showPreviousButton()
        }

        //Enviar notificacao para mudar os dados da tela principal
        val incomingObj = incomingObjList[currentPosition - 2]
        mView.changeItem(currentPosition, incomingObj)
        callSummary(incomingObj)
    }

    override fun onClickNext() {
        mView.setHighlightItem(currentPosition + 2f)
    }

    override fun onClickPrevious() {
        mView.setHighlightItem(currentPosition.toFloat())
    }

    private fun callSummary(incomingObj: IncomingObj) {
        mView.callSummary(incomingObj.cieloDate!!)
    }
}