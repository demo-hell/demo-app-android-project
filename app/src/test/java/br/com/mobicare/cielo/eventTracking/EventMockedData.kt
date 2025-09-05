package br.com.mobicare.cielo.eventTracking

import br.com.cielo.libflue.util.ONE
import br.com.cielo.libflue.util.THREE
import br.com.cielo.libflue.util.TWO
import br.com.mobicare.cielo.commons.constants.Text.SIMPLE_LINE
import br.com.mobicare.cielo.eventTracking.domain.model.CieloFilterChip
import br.com.mobicare.cielo.eventTracking.domain.model.CieloFilterChipType
import br.com.mobicare.cielo.eventTracking.domain.model.EventRequestStatus
import br.com.mobicare.cielo.eventTracking.domain.model.Machine
import br.com.mobicare.cielo.eventTracking.domain.model.MachineRequest
import br.com.mobicare.cielo.eventTracking.domain.model.MachineRequestContact

object EventMockedData {
    val myRequestMockedList = listOf(
        MachineRequest(
            id = SIMPLE_LINE,
            requestMachine = listOf(
                Machine(
                    id = SIMPLE_LINE,
                    name = "LIO ON",
                    logicalID = "1234",
                    modality = "ALUGUEL"
                )
            ),
            requestType = "INSTALAÇÃO",
            requestDate = "21/12/2023",
            requestStatus = EventRequestStatus.ATTENDED,
            requestEstablishment = "TESTE",
            requestContact = MachineRequestContact(
                id = SIMPLE_LINE,
                name = "MARIA",
                telephone = "629999ABC1"
            ),
            requestAttendedDate = "21/12/2023"
        ),
        MachineRequest(
            id = SIMPLE_LINE,
            requestMachine = listOf(
                Machine(
                    id = SIMPLE_LINE,
                    name = "LIO ON",
                    logicalID = "1234",
                    modality = "ALUGUEL"
                )
            ),
            requestType = "DESINSTALACAO",
            requestDate = "21/12/2023",
            requestStatus = EventRequestStatus.WITHOUT_UPDATE,
            requestEstablishment = "TESTE",
            requestContact = MachineRequestContact(
                id = SIMPLE_LINE,
                name = "MARIA",
                telephone = "62999999999"
            ),
            requestAttendedDate = "21/12/2023"
        ),
        MachineRequest(
            id = SIMPLE_LINE,
            requestMachine = listOf(
                Machine(
                    id = SIMPLE_LINE,
                    name = "LIO ON",
                    logicalID = "1234",
                    modality = "ALUGUEL"
                )
            ),
            requestType = "INSTALACAO",
            requestDate = "21/12/2023",
            requestStatus = EventRequestStatus.IN_RESOLUTION,
            requestEstablishment = "TESTE",
            requestContact = MachineRequestContact(
                id = SIMPLE_LINE,
                name = "MARIA",
                telephone = "62999999999"
            ),
            requestAttendedDate = "21/12/2023"
        ),
        MachineRequest(
            id = SIMPLE_LINE,
            requestMachine = listOf(
                Machine(
                    id = SIMPLE_LINE,
                    name = "LIO ON",
                    logicalID = "1234",
                    modality = "ALUGUEL"
                ), Machine(
                    id = SIMPLE_LINE,
                    name = "LIO ON V2",
                    logicalID = "1234",
                    modality = "ALUGUEL"
                )
            ),
            requestType = "TROCA DE TECNOLOGIA",
            requestDate = "21/12/2023",
            requestStatus = EventRequestStatus.IN_TRANSIT,
            requestEstablishment = "TESTE",
            requestContact = MachineRequestContact(
                id = SIMPLE_LINE,
                name = "MARIA",
                telephone = "62999999999"
            ),
            requestAttendedDate = "21/12/2023"
        ),
        MachineRequest(
            id = SIMPLE_LINE,
            requestMachine = listOf(
                Machine(
                    id = SIMPLE_LINE,
                    name = "LIO ON",
                    logicalID = "1234",
                    modality = "ALUGUEL"
                )
            ),
            requestType = "DESINSTALACAO",
            requestDate = "21/12/2023",
            requestStatus = EventRequestStatus.RESCHEDULED,
            requestEstablishment = "TESTE",
            requestContact = MachineRequestContact(
                id = SIMPLE_LINE,
                name = "MARIA",
                telephone = "62999999999"
            ),
            requestAttendedDate = "21/12/2023"
        ),
        MachineRequest(
            id = SIMPLE_LINE,
            requestMachine = listOf(
                Machine(
                    id = SIMPLE_LINE,
                    name = "LIO ON",
                    logicalID = "1234",
                    modality = "ALUGUEL"
                )
            ),
            requestType = "DESINSTALACAO",
            requestDate = "21/12/2023",
            requestStatus = EventRequestStatus.UNREALIZED,
            requestEstablishment = "TESTE",
            requestContact = MachineRequestContact(
                id = SIMPLE_LINE,
                name = "MARIA",
                telephone = "62999999999"
            ),
            requestAttendedDate = "21/12/2023"
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
            filterType = CieloFilterChipType.DATE
        ),
        CieloFilterChip(
            id = THREE,
            filterName = null,
            filterBottomSheetTitle = null,
            filterPossibilities = listOf(),
            filterType = CieloFilterChipType.REQUEST
        )
    )
    val updateFilter = firstFilter.mapIndexed { index, cieloFilterChip -> cieloFilterChip.copy(id = index, filterName = index.toString()) }
}