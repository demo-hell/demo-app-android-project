package br.com.mobicare.cielo.pixMVVM.data.model.request

import br.com.mobicare.cielo.commons.constants.TWENTY_FIVE
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.PixReceiptsTab

data class PixExtractFilterRequest(
    val idEndToEnd: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val schedulingCode: String? = null,
    val schedulingStatus: String? = null,
    val receiptsTab: PixReceiptsTab = PixReceiptsTab.TRANSFER,
    val period: String? = null,
    val transferType: String? = null,
    val cashFlowType: String? = null,
    val limit: Int = TWENTY_FIVE
)