package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.model

import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.AccountEntriesFilterTypeEnum
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.AccountEntriesFilterTypeEnum.ALL_ACCOUNT_ENTRIES
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.PeriodFilterTypeEnum
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.PeriodFilterTypeEnum.THIRTY_DAYS
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.StatusFilterTypeEnum
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.StatusFilterTypeEnum.ALL_STATUS
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.TransactionFilterTypeEnum
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.TransactionFilterTypeEnum.ALL_TRANSACTIONS

data class PixExtractFilterModel(
    var accountEntriesType: AccountEntriesFilterTypeEnum = ALL_ACCOUNT_ENTRIES,
    var periodType: PeriodFilterTypeEnum = THIRTY_DAYS,
    var statusType: StatusFilterTypeEnum = ALL_STATUS,
    var transactionType: TransactionFilterTypeEnum = ALL_TRANSACTIONS,
)