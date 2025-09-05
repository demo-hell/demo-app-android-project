package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.utils

import br.com.mobicare.cielo.pixMVVM.data.model.request.PixExtractFilterRequest
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.PixReceiptsTab
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.AccountEntriesFilterTypeEnum
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.PeriodFilterTypeEnum
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.StatusFilterTypeEnum
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.TransactionFilterTypeEnum
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.model.PixExtractFilterModel

object PixExtractFilterRequestGenerate {

    fun generate(
        idEndToEnd: String,
        tab: PixReceiptsTab,
        filterData: PixExtractFilterModel?
    ): PixExtractFilterRequest {
        return PixExtractFilterRequest(
            idEndToEnd = idEndToEnd.ifEmpty { null },
            period = getPeriod(filterData?.periodType, tab),
            receiptsTab = tab,
            schedulingStatus = getSchedulingStatus(filterData?.statusType),
            transferType = getTransferType(filterData?.transactionType),
            cashFlowType = getCashFlowType(filterData?.accountEntriesType)
        )
    }

    private fun getPeriod(periodType: PeriodFilterTypeEnum?, tab: PixReceiptsTab): String? {
        return if (tab != PixReceiptsTab.SCHEDULES) {
            periodType?.name
        } else {
            null
        }
    }

    private fun getSchedulingStatus(statusType: StatusFilterTypeEnum?): String? {
        return statusType?.takeIf { it != StatusFilterTypeEnum.ALL_STATUS }?.name
    }

    private fun getCashFlowType(accountEntriesFilterType: AccountEntriesFilterTypeEnum?): String? {
        return accountEntriesFilterType?.takeIf { it != AccountEntriesFilterTypeEnum.ALL_ACCOUNT_ENTRIES }?.name
    }

    private fun getTransferType(transactionFilterTypeEnum: TransactionFilterTypeEnum?): String? {
        return transactionFilterTypeEnum?.takeIf { it != TransactionFilterTypeEnum.ALL_TRANSACTIONS }?.name
    }

}