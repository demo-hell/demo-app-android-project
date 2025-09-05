package br.com.mobicare.cielo.pix.domain

import br.com.mobicare.cielo.commons.constants.ONE_HUNDRED
import br.com.mobicare.cielo.commons.constants.TWENTY_FIVE
import br.com.mobicare.cielo.pix.domain.ReceiptsTab.REVERSAL
import br.com.mobicare.cielo.pix.domain.ReceiptsTab.TRANSFER
import br.com.mobicare.cielo.pix.enums.PixExtractFilterEnum.*

class PixExtractFilterRequest(
    var idEndToEnd: String? = null,
    var startDate: String? = null,
    var endDate: String? = null,
    var schedulingCode: String? = null,
    var receiptsTab: ReceiptsTab = TRANSFER,
    period: String? = null,
    transferType: String? = null,
    cashFlowType: String? = null,
) {

    var limit = if (receiptsTab == REVERSAL) ONE_HUNDRED else TWENTY_FIVE
    var period: String? = if (period == RECENTS.name || period == OTHERS_PERIODS.name) null else period
    var transferType = if (transferType == ALL_TRANSACTIONS.name) null else transferType
    var cashFlowType = if (cashFlowType == ALL_RELEASES.name) null else cashFlowType
}
