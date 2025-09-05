package br.com.mobicare.cielo.eventTracking.domain.model

import br.com.cielo.libflue.util.FOUR
import br.com.cielo.libflue.util.THREE
import br.com.cielo.libflue.util.TWO
import br.com.mobicare.cielo.commons.constants.EIGHTY_NINE_NEGATIVE
import br.com.mobicare.cielo.commons.constants.FIVE
import br.com.mobicare.cielo.commons.constants.ONE_NEGATIVE
import br.com.mobicare.cielo.commons.constants.SIX_NEGATIVE
import br.com.mobicare.cielo.commons.constants.TWENTY_NINE_NEGATIVE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.utils.DataCustomNew
import br.com.mobicare.cielo.eventTracking.domain.model.CieloFilterType.DESINSTALACAO
import br.com.mobicare.cielo.eventTracking.domain.model.CieloFilterType.INSTALACAO
import br.com.mobicare.cielo.eventTracking.domain.model.CieloFilterType.MANUTENCAO
import br.com.mobicare.cielo.eventTracking.domain.model.CieloFilterType.TECH_CHANGE

data class CieloFilterChip(
    val id: Int = ZERO,
    val filterName: String? = null,
    val filterBottomSheetTitle: String? = null,
    val filterPossibilities: List<String>,
    val filterType: CieloFilterChipType,
    var currentSelected: Int = ONE_NEGATIVE,
    var searchQuery: String? = null
) {
    var isChecked: Boolean = filterName == null || currentSelected != ONE_NEGATIVE
    var initialDate: String? = if (currentSelected > ZERO && filterType == CieloFilterChipType.DATE) {
        DataCustomNew().apply {
            when (currentSelected) {
                TWO -> setDate(CieloFilterDay.YESTERDAY)
                THREE -> setDate(CieloFilterDay.LAST_SEVEN_DAYS)
                FOUR -> setDate(CieloFilterDay.LAST_THIRTY_DAYS)
                FIVE -> setDate(CieloFilterDay.LAST_NINETY_DAYS)
                else -> setDate(CieloFilterDay.TODAY)
            }
        }.formatDateToAPI()
    } else null

    var endDate: String? = if (currentSelected > ZERO && filterType == CieloFilterChipType.DATE && initialDate != null) {
        DataCustomNew().apply {
            if (currentSelected == TWO) {
                setDate(ONE_NEGATIVE)
            }
        }.formatDateToAPI()
    } else null

    var serviceType: String? = if (currentSelected > ZERO && filterType == CieloFilterChipType.REQUEST) {
        when (currentSelected) {
            TWO -> DESINSTALACAO
            THREE -> MANUTENCAO
            FOUR -> TECH_CHANGE
            else -> INSTALACAO
        }
    } else null

    var searchRequest: String? = if(!searchQuery.isNullOrBlank() && filterType == CieloFilterChipType.SEARCH){
        searchQuery
    } else{
        null
    }
}

object CieloFilterType {
    const val INSTALACAO = "INSTALACAO"
    const val DESINSTALACAO = "DESINSTALACAO"
    const val MANUTENCAO = "MANUTENCAO"
    const val TECH_CHANGE = "TROCA DE TECNOLOGIA"
}

object CieloFilterDay {
    const val TODAY = ZERO
    const val YESTERDAY = ONE_NEGATIVE
    const val LAST_SEVEN_DAYS = SIX_NEGATIVE
    const val LAST_THIRTY_DAYS = TWENTY_NINE_NEGATIVE
    const val LAST_NINETY_DAYS = EIGHTY_NINE_NEGATIVE
}

enum class CieloFilterChipType {
    STATUS,
    DATE,
    REQUEST,
    SEARCH
}