package br.com.mobicare.cielo.meusrecebimentosnew.calculationview.graph

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.DataCustomNew
import br.com.mobicare.cielo.home.presentation.meusrecebimentonew.MeusRecebimentosHomeActivityNew
import br.com.mobicare.cielo.meusRecebimentos.domains.entities.IncomingObj
import br.com.mobicare.cielo.meusRecebimentos.presentation.ui.MeusRecebimentosGraficoContract
import br.com.mobicare.cielo.meusRecebimentos.presentation.ui.adapters.MeusRecebimentosItemDataAdapter
import br.com.mobicare.cielo.meusRecebimentos.presentation.ui.grafico.CieloMarkerView
import br.com.mobicare.cielo.meusRecebimentos.presentation.ui.grafico.OnChangeDataListener
import br.com.mobicare.cielo.meusrecebimentosnew.calculationview.MeusRecebimentosFragmentNew
import br.com.mobicare.cielo.meusrecebimentosnew.calculationview.graph.repository.MeusRecebimentosGraficoRepository
import br.com.mobicare.cielo.meusrecebimentosnew.fragments.ComponentFilterListener
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.Utils
import kotlinx.android.synthetic.main.meus_recebimentos_grafico_fragment.*


class MeusRecebimentosGraficoFragmentNew : BaseFragment(), OnChartValueSelectedListener, MeusRecebimentosGraficoContract.View {
    private var blue: Int = 0
    private var gray: Int = 0
    var listener: OnChangeDataListener? = null
    var selectedPosition: Int = -1
    private val MAIN_DATE = "MAIN_DATE"

    var presenter: MeusRecebimentosGraficoPresenterNew? = null

    companion object {

        private var callbackCallSummary: ComponentFilterListener? = null

        fun newInstance(mainDate: DataCustomNew,
                        selectedPosition: Int,
                        callback: ComponentFilterListener) = MeusRecebimentosGraficoFragmentNew()
                .apply {
                    callbackCallSummary = callback
                    val extras = Bundle()
                    extras.putParcelable(MAIN_DATE, mainDate)
                    extras.putInt(MeusRecebimentosHomeActivityNew.CURRENT_POSITION, selectedPosition)
                    this.arguments = extras
                }

        fun newInstance(callback: OnChangeDataListener) = MeusRecebimentosGraficoFragmentNew()
                .apply { listener = callback }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.meus_recebimentos_grafico_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getisScreenRecebimentos()
        init()
    }

    private fun init() {
        presenter = MeusRecebimentosGraficoPresenterNew(this,  MeusRecebimentosGraficoRepository())
        val mainDate = arguments?.getParcelable<DataCustomNew>(MAIN_DATE)
        arguments?.let {
            selectedPosition = arguments?.getInt(MeusRecebimentosHomeActivityNew.CURRENT_POSITION)!!
        }
        presenter?.getPostingsGraph(mainDate)

        presenter?.currentPosition = selectedPosition

        imageview_meus_recebimentos_anterior.setOnClickListener {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Category.TAPICON),
                action = listOf(actionPath),
                label = listOf(String.format(Label.HOME_MEUS_RECEBIMENTOS, Label.HOME_MEUS_RECEBIMENTOS_DIA_ANTERIOR))
            )
            presenter?.onClickPrevious()
        }

        imageview_meus_recebimentos_proximo.setOnClickListener {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Category.TAPICON),
                action = listOf(actionPath),
                label = listOf(String.format(Label.HOME_MEUS_RECEBIMENTOS, Label.HOME_MEUS_RECEBIMENTOS_PROX_DIA))
            )
            presenter?.onClickNext()
        }

        layout_meus_recebimentos_header.visibility = View.GONE
        line_chart_meus_recebimentos.visibility = View.GONE
        blue = ContextCompat.getColor(activity as Activity, R.color.blue)
        gray = ContextCompat.getColor(activity as Activity, R.color.gray_light)
    }

    var isScreenRecebimentos: Boolean = false
    var actionPath: String = Action.HOME_INICIO

    fun getisScreenRecebimentos() {
        val bundle = this.arguments
        if (bundle != null) {
            isScreenRecebimentos = bundle.getBoolean("isScreenFragment", false)
        }
        actionPath = if (isScreenRecebimentos) "MeusRecebimentos" else Action.HOME_INICIO
    }

    fun changeBackgroundColor(@ColorRes color: Int) {
        if (this.isAttached()) {
            line_chart_meus_recebimentos.setBackgroundColor(ContextCompat.getColor(activity as Activity, color))
        }
    }

    override fun loadGraph() {
        if (this.isAttached()) {
            listener?.onGraphSuccess()

            layout_meus_recebimentos_header.visibility = View.VISIBLE
            line_chart_meus_recebimentos.visibility = View.VISIBLE
            line_chart_meus_recebimentos.setOnChartValueSelectedListener(this)
            line_chart_meus_recebimentos.setDrawGridBackground(false)
            line_chart_meus_recebimentos.setViewPortOffsets(0f, 40f, 0f, 125f)
            if (line_chart_meus_recebimentos.description != null) {
                line_chart_meus_recebimentos.description.isEnabled = true
            }
            line_chart_meus_recebimentos.axisRight.isEnabled = false
            line_chart_meus_recebimentos.axisLeft.isEnabled = false

            // if disabled, scaling can be done on x- and y-axis separately
            line_chart_meus_recebimentos.setPinchZoom(false)
            line_chart_meus_recebimentos.isDoubleTapToZoomEnabled = false

            // set an alternative background color
            line_chart_meus_recebimentos.setBackgroundColor(ContextCompat.getColor(activity as Activity, R.color.background))

            // newInstance a custom MarkerView (extend MarkerView) and specify the layout
            // to use for it
            val mv = CieloMarkerView(context as Context, R.layout.cielo_marker_view)
            mv.chartView = line_chart_meus_recebimentos // For bounds control
            line_chart_meus_recebimentos.marker = mv // Set the marker to the chart

            setData()

            //Quantidade de item que aparecerá na tela
            line_chart_meus_recebimentos.setVisibleXRange(5f, 5f)

            line_chart_meus_recebimentos.description = null    // Hide the description
            line_chart_meus_recebimentos.axisLeft.setDrawLabels(false)
            line_chart_meus_recebimentos.axisRight.setDrawLabels(false)
            line_chart_meus_recebimentos.xAxis.setDrawLabels(true)
            line_chart_meus_recebimentos.xAxis.axisLineColor = blue
            line_chart_meus_recebimentos.xAxis.gridColor = blue

            line_chart_meus_recebimentos.axisLeft.setDrawGridLines(false)
            line_chart_meus_recebimentos.xAxis.setDrawGridLines(false)
            line_chart_meus_recebimentos.axisRight.setDrawGridLines(false)
            line_chart_meus_recebimentos.xAxis.labelCount = 5
            line_chart_meus_recebimentos.xAxis.position = XAxis.XAxisPosition.BOTTOM
            line_chart_meus_recebimentos.xAxis.axisLineColor = blue
            line_chart_meus_recebimentos.xAxis.axisLineWidth = 1f
            line_chart_meus_recebimentos.xAxis.textColor = ContextCompat.getColor(activity as Activity,
                    R.color.gray_light)
            line_chart_meus_recebimentos.xAxis.setAvoidFirstLastClipping(false)
            line_chart_meus_recebimentos.xAxis.textSize = 12f
            line_chart_meus_recebimentos.xAxis.axisLineColor = blue

            //Cor e font quando não esta selecionado
            line_chart_meus_recebimentos.xAxis.typeface = Typeface.createFromAsset(context?.assets, "fonts/MuseoSans-500.ttf")
            line_chart_meus_recebimentos.xAxis.textColor = ContextCompat.getColor(activity as Activity, R.color.gray_light)

            //Mudanca da fonte e cor quando tiver formatado
            line_chart_meus_recebimentos.xAxis.selectedTypeface = Typeface.createFromAsset(context?.assets, "fonts/MuseoSans-700.ttf")
            line_chart_meus_recebimentos.xAxis.selectedTextColor = ContextCompat.getColor(activity as Activity, R.color.text_color_263238)

            line_chart_meus_recebimentos.xAxis.valueFormatter = IAxisValueFormatter { value, axis -> presenter?.getXLabel(value.toInt()) }

            line_chart_meus_recebimentos.legend.isEnabled = false

            // enable touch gestures
            line_chart_meus_recebimentos.setTouchEnabled(true)

            line_chart_meus_recebimentos.setClipValuesToContent(false)
            /**
             *  set start y axis in zero
             * */
//            val yAxis = line_chart_meus_recebimentos.axisLeft
//            yAxis.axisMinimum = 0f

            line_chart_meus_recebimentos.setOnChartScrollListener {
                if (line_chart_meus_recebimentos != null) {
                    val lowestVisibleX = line_chart_meus_recebimentos.lowestVisibleX
                    setHighlightItem(lowestVisibleX + 2)
                }
            }
        }
    }

    override fun onValueSelected(e: Entry, h: Highlight?) {
        if (this.isAttached()) {
            presenter?.updateData(e.x)

            val dayDescription = (view_pager_meus_recebimentos_data.adapter as MeusRecebimentosItemDataAdapter).list.get(view_pager_meus_recebimentos_data.currentItem).dayOfWeek
            dayDescription?.let {
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, Category.TAPICON),
                    action = listOf("${formatedScreenName()}${MeusRecebimentosFragmentNew.SCREEN_NAME}"),
                    label = listOf("Ponto do Gráfico", it)
                )
            }
        }
    }

    fun formatedScreenName(): String {
        return "Inicio"
    }

    override fun onNothingSelected() {
//
    }

    private fun setData() {

        val values = presenter?.getValues()

        val set1: LineDataSet

        // newInstance a dataset and give it a type
        set1 = LineDataSet(values, "Cielo")
        set1.mode = LineDataSet.Mode.CUBIC_BEZIER

        set1.setDrawIcons(false)

        set1.color = this.resources.getColor(R.color.blue)
        set1.setCircleColor(this.resources.getColor(R.color.blue))
        set1.setCircleColorHole(Color.WHITE)
        set1.circleRadius = 4f
        set1.setCircleColors(*intArrayOf(Color.TRANSPARENT, blue, blue, blue, blue, blue, blue, blue, blue, blue, blue, Color.TRANSPARENT))
        set1.circleHoleRadius = 3f
        set1.setDrawCircleHole(true)
        set1.valueTextSize = 12f
        set1.setDrawFilled(true)
        set1.lineWidth = 1f

        //Font e cor item não selecionado
        set1.valueTypeface = Typeface.createFromAsset(context?.assets, "fonts/MuseoSans-500.ttf")
        set1.valueTextColor = ContextCompat.getColor(activity as Activity, R.color.gray_light)
        set1.valueTextSize = 12f

        //Font e cor item selecionado
        set1.valueSelectedTextColor = ContextCompat.getColor(activity as Activity, R.color.text_color_263238)
        set1.selectedValueTypeface = Typeface.createFromAsset(context?.assets, "fonts/MuseoSans-700.ttf")

        set1.setDrawHorizontalHighlightIndicator(false)
        set1.highLightColor = ContextCompat.getColor(activity as Activity, R.color.blue)
        set1.highlightLineWidth = 1.5f

        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            val drawable = ContextCompat.getDrawable(context as Context, R.drawable.grafico_bg)
            set1.fillDrawable = drawable
        } else {
            set1.fillColor = blue
        }

        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(set1) // addInFrame the datasets

        val data = LineData(dataSets)
        data.setValueFormatter({ value, entry, dataSetIndex, viewPortHandler -> presenter?.getYLabel(entry.x.toInt(), value) })

        // set data
        line_chart_meus_recebimentos.data = data
        line_chart_meus_recebimentos.animateY(1000, Easing.EasingOption.EaseInCubic)
    }

    override fun showNextButton() {
        imageview_meus_recebimentos_proximo.visibility = View.VISIBLE
    }

    override fun hideNextButton() {
        imageview_meus_recebimentos_proximo.visibility = View.INVISIBLE
    }

    override fun showPreviousButton() {
        imageview_meus_recebimentos_anterior.visibility = View.VISIBLE
    }

    override fun hidePreviousButton() {
        imageview_meus_recebimentos_anterior.visibility = View.INVISIBLE
    }

    override fun setViewPagerCurrentItem(position: Int) {
        view_pager_meus_recebimentos_data.currentItem = position
    }

    override fun setHighlightItem(position: Float) {
        line_chart_meus_recebimentos.highlightValue(position, 0)
    }

    override fun moveViewToX(position: Float) {
        line_chart_meus_recebimentos.moveViewToAnimated(position, line_chart_meus_recebimentos.y, YAxis.AxisDependency.RIGHT, 500)
    }

    override fun loadHeaderData(list: ArrayList<IncomingObj>) {
        if (view_pager_meus_recebimentos_data == null) {
            return
        }
        view_pager_meus_recebimentos_data.adapter =
                MeusRecebimentosItemDataAdapter(fragmentManager as androidx.fragment.app.FragmentManager, list = list)
        view_pager_meus_recebimentos_data.setOnTouchListener { v, event ->
            true
        }
    }

    override fun sendClickGraphEventGA(label: String?) {
    }

    override fun callSummary(date: String) {
        callbackCallSummary?.let {
            it.onClickDate(date, date, true)
        }
    }

    override fun showError(error: ErrorMessage) {
        if (isAttached()) {
            Analytics.trackError(Action.HOME_INICIO, error)
        }
        listener?.showGraphError(error)
    }

    override fun changeItem(position: Int, obj: IncomingObj) {
        listener?.onItemChange(position, obj)
    }

    override fun isAttached(): Boolean {
        return line_chart_meus_recebimentos != null && view_pager_meus_recebimentos_data != null
    }
}