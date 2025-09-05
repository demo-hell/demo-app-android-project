package br.com.mobicare.cielo.pix.ui.extract.filter

import androidx.annotation.StringRes
import br.com.mobicare.cielo.commons.utils.DataCustom
import br.com.mobicare.cielo.pix.domain.FilterExtract
import java.util.*

interface PixExtractFilterBottomSheetContract {
    interface View {
        fun showCalendar(
            cal: Calendar,
            @StringRes idTitleRes: Int,
            type: Int,
            startDate: Calendar,
            endDate: Calendar
        )

        fun fillStartPeriod(data: DataCustom?)
        fun fillEndPeriod(data: DataCustom?)
        fun applyFilter(filter: FilterExtract?)
    }

    interface Presenter {
        fun onStartPeriodoClicked()
        fun onEndPeriodClicked()
        fun onChangePeriod(type: Int, data: DataCustom)
        fun applyFilter(inputFilterExtract: FilterExtract? = null)
    }
}