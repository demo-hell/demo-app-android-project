package br.com.mobicare.cielo.pix.helpers

import android.content.Context
import android.widget.ViewFlipper
import androidx.fragment.app.Fragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.SIX
import br.com.mobicare.cielo.pix.enums.PixTimeManagementEnum

fun displayedChild(value: Int, vf: ViewFlipper){
    vf.displayedChild = value
}

fun Context.pixLimitDayTimeRangeText(timeManagement: PixTimeManagementEnum) =
    getString(R.string.text_pix_my_limits_transaction_hours_range, SIX, timeManagement.hour)

fun Context.pixLimitNightTimeRangeText(timeManagement: PixTimeManagementEnum) =
    getString(R.string.text_pix_my_limits_transaction_hours_range, timeManagement.hour, SIX)
