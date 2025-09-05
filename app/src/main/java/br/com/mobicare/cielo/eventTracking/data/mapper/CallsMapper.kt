package br.com.mobicare.cielo.eventTracking.data.mapper

import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.utils.formatDateFromYYMMDD
import br.com.mobicare.cielo.eventTracking.data.mapper.CallsMapper.translateStatus
import br.com.mobicare.cielo.eventTracking.data.model.response.CallResponseItem
import br.com.mobicare.cielo.eventTracking.domain.model.CallRequest
import br.com.mobicare.cielo.eventTracking.domain.model.EventRequestStatus
import br.com.mobicare.cielo.extensions.ifNullSimpleLine

object CallsMapper {

    fun CallResponseItem.translateStatus(): EventRequestStatus {
        return when (this.status) {
            CallStatus.OPEN -> EventRequestStatus.IN_RESOLUTION
            CallStatus.ATTENDED -> EventRequestStatus.ATTENDED

            else -> EventRequestStatus.IN_RESOLUTION
        }
    }

    object CallStatus {
        const val OPEN = "P"
        const val ATTENDED = "S"
    }
}

fun CallResponseItem.toCallRequest() =
    CallRequest(
        code = this.code.ifNullSimpleLine(),
        description = this.description.ifNullSimpleLine(),
        createdDate = this.createdDate?.formatDateFromYYMMDD().ifNullSimpleLine(),
        solutionDeadline = this.solutionDeadline ?: ZERO,
        referCode = this.referCode.ifNullSimpleLine(),
        eventRequestStatus = this.translateStatus(),
        dependencyCode = this.dependencyCode
    )


