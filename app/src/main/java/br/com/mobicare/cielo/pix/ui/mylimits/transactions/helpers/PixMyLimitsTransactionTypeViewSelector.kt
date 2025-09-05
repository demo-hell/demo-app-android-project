package br.com.mobicare.cielo.pix.ui.mylimits.transactions.helpers

import androidx.fragment.app.FragmentManager
import br.com.mobicare.cielo.databinding.FragmentPixMyLimitsTransactionsBinding
import br.com.mobicare.cielo.pix.domain.Limits

class PixMyLimitsTransactionTypeViewSelector(
    limit: Limits,
    fragmentManager: FragmentManager,
    private val binding: FragmentPixMyLimitsTransactionsBinding,
) : LimitTypeViewSelector(limit, fragmentManager) {

    override val containerDayTime get() = binding.containerPixMyLimitsDaytime
    override val containerNightTime get() = binding.containerPixMyLimitsNighttime
    override val containerMonthly get() = binding.containerPixMyLimitsMonthly

}