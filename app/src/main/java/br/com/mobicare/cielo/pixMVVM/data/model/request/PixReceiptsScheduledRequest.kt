package br.com.mobicare.cielo.pixMVVM.data.model.request

import br.com.mobicare.cielo.commons.constants.TWENTY_FIVE

data class PixReceiptsScheduledRequest(
    val limit: Int = TWENTY_FIVE,
    val schedulingStartDate: String? = null,
    val schedulingEndDate: String? = null,
    val lastSchedulingIdentifierCode: String? = null,
    val lastNextDateTimeScheduled: String? = null,
)
