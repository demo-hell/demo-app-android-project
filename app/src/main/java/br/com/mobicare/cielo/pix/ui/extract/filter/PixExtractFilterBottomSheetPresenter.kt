package br.com.mobicare.cielo.pix.ui.extract.filter

import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.FIVE_NEGATIVE
import br.com.mobicare.cielo.commons.constants.TWO_NEGATIVE
import br.com.mobicare.cielo.commons.utils.CalendarCustom
import br.com.mobicare.cielo.commons.utils.DataCustom
import br.com.mobicare.cielo.commons.utils.addYears
import br.com.mobicare.cielo.pix.domain.FilterExtract
import java.util.*

class PixExtractFilterBottomSheetPresenter(private val view: PixExtractFilterBottomSheetContract.View) :
    PixExtractFilterBottomSheetContract.Presenter {

    private var startPeriod: DataCustom? = null
    private var endPeriod: DataCustom? = null
    private var filter: FilterExtract? = null

    val START_PERIOD: Int = 0
    val END_PERIOD: Int = 1

    override fun onStartPeriodoClicked() {
        val endDate = CalendarCustom.now
        val period = this.startPeriod?.toCalendar() ?: endDate
        val startDate = CalendarCustom.now

        startDate.addYears(TWO_NEGATIVE)

        this.view.showCalendar(period, R.string.periodo_inicio, START_PERIOD, startDate, endDate)
    }

    override fun onEndPeriodClicked() {
        val endDate = CalendarCustom.now
        val period = this.endPeriod?.toCalendar() ?: endDate
        var startDate = CalendarCustom.now

        startPeriod?.let {
            startDate = it.toCalendar()
        } ?: startDate.addYears(TWO_NEGATIVE)

        this.view.showCalendar(period, R.string.periodo_fim, END_PERIOD, startDate, endDate)
    }


    private fun setStartPeriod(data: DataCustom) {
        if (data != this.startPeriod) {
            this.endPeriod?.let {
                if (data.isGreaterThen(it)) {
                    this.endPeriod = null
                    this.view.fillEndPeriod(null)
                }
            }
            this.startPeriod = data
            this.startPeriod?.let { itStartPeriod ->
                this.view.fillStartPeriod(itStartPeriod)
            }
        }
    }

    private fun setEndPeriod(data: DataCustom) {
        if (data != this.endPeriod) {
            this.startPeriod?.let {
                if (data.isBelowThen(it)) {
                    this.startPeriod = null
                    this.view.fillStartPeriod(null)
                }
            }
            this.endPeriod = data
            this.endPeriod?.let {
                this.view.fillEndPeriod(it)
                this.startPeriod?.let {
                }
            }
        }
    }

    override fun onChangePeriod(type: Int, data: DataCustom) {
        if (type == START_PERIOD)
            this.setStartPeriod(data)
        else
            this.setEndPeriod(data)
    }

    override fun applyFilter(inputFilterExtract: FilterExtract?) {
        this.view.applyFilter(
            FilterExtract
                .Builder()
                .from(filter)
                .initialDate(inputFilterExtract?.startDate)
                .finalDate(inputFilterExtract?.endDate)
                .cashFlowType(inputFilterExtract?.cashFlowType)
                .transferType(inputFilterExtract?.transferType)
                .period(inputFilterExtract?.period)
                .qtdFilters(inputFilterExtract?.qtdFilters)
                .build()
        )
    }
}