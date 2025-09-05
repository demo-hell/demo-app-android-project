package br.com.mobicare.cielo.eventTracking.data.mapper

import br.com.mobicare.cielo.commons.utils.convertToBrDateFormat
import br.com.mobicare.cielo.eventTracking.data.model.response.Detail
import br.com.mobicare.cielo.eventTracking.data.model.response.Equipment
import br.com.mobicare.cielo.eventTracking.data.model.response.MachineRequestResponseItem
import br.com.mobicare.cielo.eventTracking.domain.model.EventRequestStatus
import br.com.mobicare.cielo.eventTracking.domain.model.Machine
import br.com.mobicare.cielo.eventTracking.domain.model.MachineRequest
import br.com.mobicare.cielo.eventTracking.domain.model.MachineRequestContact
import br.com.mobicare.cielo.extensions.ifNullSimpleLine
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.gson.annotations.SerializedName


fun Equipment.toMachine(): Machine {
    return Machine(
        id = EMPTY,
        name = this.commercialDescription.ifNullSimpleLine(),
        logicalID = this.logicalNumber.ifNullSimpleLine(),
        modality = this.modality.ifNullSimpleLine()
    )
}

fun Detail.translateStatus(): EventRequestStatus {
    return when (this.equipments?.map { it?.status }?.first()) {
        EquipmentsStatus.NEW -> EventRequestStatus.IN_TRANSIT
        EquipmentsStatus.ATTENDED -> EventRequestStatus.ATTENDED
        EquipmentsStatus.WITHOUT_UPDATE -> EventRequestStatus.WITHOUT_UPDATE
        EquipmentsStatus.CANCELLED_WITHOUT_VISIT, EquipmentsStatus.CANCELLED_WITH_VISIT -> EventRequestStatus.UNREALIZED
        EquipmentsStatus.CIELO_RESCHEDULED, EquipmentsStatus.RESCHEDULED -> EventRequestStatus.RESCHEDULED

        else -> EventRequestStatus.IN_RESOLUTION
    }
}

object EquipmentsStatus {
    const val NEW = "Novo"
    const val ATTENDED = "Atendido"
    const val WITHOUT_UPDATE = "Sem atualização"
    const val CANCELLED_WITHOUT_VISIT = "Cancelado sem visita"
    const val CANCELLED_WITH_VISIT = "Cancelado com visita"
    const val RESCHEDULED = "Reagendado"
    const val CIELO_RESCHEDULED = "Reagendamento Cielo"
}

fun MachineRequestResponseItem.toMachineRequest(): MachineRequest {

    return MachineRequest(
        id = this.id.ifNullSimpleLine(),
        requestMachine = this.details?.first()?.equipments?.map { it?.toMachine() },
        requestType = this.serviceType?.translatedName.ifNullSimpleLine(),
        requestDate = this.createdDate?.convertToBrDateFormat().ifNullSimpleLine(),
        requestAttendedDate = this.details?.first()?.serviceForecastDate?.convertToBrDateFormat()
            .ifNullSimpleLine(),
        requestStatus = this.details?.map { it?.translateStatus() }?.first(),
        requestContact = MachineRequestContact(
            id = EMPTY,
            name = this.details?.first()?.contactName.ifNullSimpleLine(),
            telephone = this.details?.first()?.contactPhone.ifNullSimpleLine()
        ),
        requestEstablishment = this.merchant.ifNullSimpleLine(),
        requestReason = this.details?.first()?.equipments?.first()?.reason
    )
}

enum class MachineRequestServiceType(val translatedName: String) {
    @SerializedName("INSTALACAO")
    INSTALACAO("Entrega/Instalação"),

    @SerializedName("DESINSTALACAO")
    DESINSTALACAO("Desinstalação"),

    @SerializedName("MANUTENCAO")
    MANUTENCAO("Manutenção"),

    @SerializedName("TROCA DE TECNOLOGIA")
    TECH_CHANGE("Troca de Tecnologia")
}