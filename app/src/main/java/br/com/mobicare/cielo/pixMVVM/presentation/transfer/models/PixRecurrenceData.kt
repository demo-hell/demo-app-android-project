package br.com.mobicare.cielo.pixMVVM.presentation.transfer.models

import br.com.cielo.libflue.util.dateUtils.removeTimeAttributes
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.enums.PixPeriodRecurrence
import java.util.Calendar

data class PixRecurrenceData(
    var startDate: Calendar = Calendar.getInstance().removeTimeAttributes(),
    var period: PixPeriodRecurrence? = null,
    var endDate: Calendar? = null,
)
