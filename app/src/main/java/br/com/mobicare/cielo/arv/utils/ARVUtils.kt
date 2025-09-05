package br.com.mobicare.cielo.arv.utils

import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.utils.DataCustomNew
import br.com.mobicare.cielo.commons.utils.addYears

object ARVUtils {
    val minAnticipationRangeDate = DataCustomNew()
    val maxAnticipationRangeDate =
        DataCustomNew().apply {
            setDate(
                date = toCalendar().apply {
                    addYears(TWO)
                }.time
            )
        }
}