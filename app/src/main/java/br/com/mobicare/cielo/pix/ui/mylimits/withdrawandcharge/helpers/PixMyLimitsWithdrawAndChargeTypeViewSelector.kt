package br.com.mobicare.cielo.pix.ui.mylimits.withdrawandcharge.helpers

import androidx.fragment.app.FragmentManager
import br.com.mobicare.cielo.databinding.FragmentPixMyLimitsWithdrawAndChargeBinding
import br.com.mobicare.cielo.pix.domain.Limits
import br.com.mobicare.cielo.pix.ui.mylimits.transactions.helpers.LimitTypeViewSelector

class PixMyLimitsWithdrawAndChargeTypeViewSelector(
    limit: Limits,
    fragmentManager: FragmentManager,
    private val binding: FragmentPixMyLimitsWithdrawAndChargeBinding,
) : LimitTypeViewSelector(limit, fragmentManager) {

    override val containerDayTime get() = binding.containerPixMyLimitsDaytime
    override val containerNightTime get() = binding.containerPixMyLimitsNighttime
    override val containerMonthly get() = binding.containerPixMyLimitsMonthly

}