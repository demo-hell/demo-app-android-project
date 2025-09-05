package br.com.mobicare.cielo.pixMVVM.presentation.home.models

import br.com.mobicare.cielo.commons.utils.SIMPLE_DT_FORMAT_MASK_DAY_MONTH
import br.com.mobicare.cielo.commons.utils.SIMPLE_HOUR_MINUTE_24h
import br.com.mobicare.cielo.commons.utils.parseToString
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import java.time.ZonedDateTime

data class PixAccountBalanceStore(
    val balance: Double? = null,
    val updatedAt: ZonedDateTime? = null
) {
    val formattedBalance get() = balance?.toPtBrRealString()
    val formattedUpdatedDate get() = updatedAt?.parseToString(pattern = SIMPLE_DT_FORMAT_MASK_DAY_MONTH)
    val formattedUpdatedTime get() = updatedAt?.parseToString(pattern = SIMPLE_HOUR_MINUTE_24h)
}
