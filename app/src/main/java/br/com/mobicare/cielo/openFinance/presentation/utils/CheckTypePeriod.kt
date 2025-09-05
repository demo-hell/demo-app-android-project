package br.com.mobicare.cielo.openFinance.presentation.utils

import android.content.Context
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.TYPE_DAYS
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.TYPE_MONTHS
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.TYPE_YEARS
import br.com.mobicare.cielo.pix.constants.EMPTY

object CheckTypePeriod {
    fun checkTypePeriod(context: Context, type: String): String {
        return when (type) {
            TYPE_DAYS -> context.getString(R.string.type_days)
            TYPE_MONTHS -> context.getString(R.string.type_months)
            TYPE_YEARS -> context.getString(R.string.type_years)
            else -> EMPTY
        }
    }
}