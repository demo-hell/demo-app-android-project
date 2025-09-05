package br.com.mobicare.cielo.eventTracking

import br.com.cielo.libflue.util.ONE
import br.com.cielo.libflue.util.TWO
import br.com.mobicare.cielo.commons.constants.Text.SIMPLE_LINE
import br.com.mobicare.cielo.eventTracking.domain.model.CallRequest
import br.com.mobicare.cielo.eventTracking.domain.model.CieloFilterChip
import br.com.mobicare.cielo.eventTracking.domain.model.CieloFilterChipType
import br.com.mobicare.cielo.eventTracking.domain.model.EventRequestStatus

object CallsMockedData {

    val callsMockedList = listOf(
        CallRequest(
            code = "1111",
            description = "CONTESTEÇÃO DE COMPRA",
            createdDate = "19/01/2024",
            solutionDeadline = 0,
            referCode = SIMPLE_LINE,
            eventRequestStatus = EventRequestStatus.ATTENDED
        ),
        CallRequest(
            code = "2222",
            description = "ALTERAÇÃO DE CONTA",
            createdDate = "18/01/2024",
            solutionDeadline = 1,
            referCode = SIMPLE_LINE,
            eventRequestStatus = EventRequestStatus.IN_RESOLUTION
        ),
        CallRequest(
            code = "3333",
            description = "REQUISIÇÃO DE MATERIAL",
            createdDate = "17/01/2024",
            solutionDeadline = 2,
            referCode = SIMPLE_LINE,
            eventRequestStatus = EventRequestStatus.ATTENDED
        ),
        CallRequest(
            code = "4444",
            description = "CHAMADO ALEATÓRIO",
            createdDate = "16/01/2024",
            solutionDeadline = 3,
            referCode = SIMPLE_LINE,
            eventRequestStatus = EventRequestStatus.IN_RESOLUTION
        )
    )

    val firstFilter = listOf(
        CieloFilterChip(
            id = ONE,
            filterBottomSheetTitle = null,
            filterPossibilities = listOf(),
            filterType = CieloFilterChipType.STATUS
        ),
        CieloFilterChip(
            id = TWO,
            filterName = null,
            filterBottomSheetTitle = null,
            filterPossibilities = listOf(),
            filterType = CieloFilterChipType.SEARCH
        )
    )
    val updateFilter = firstFilter.mapIndexed { index, cieloFilterChip -> cieloFilterChip.copy(id = index, filterName = index.toString()) }
}