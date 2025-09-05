package br.com.mobicare.cielo.pix.ui.mylimits.transactions.helpers

import androidx.fragment.app.FragmentManager
import br.com.mobicare.cielo.databinding.LayoutPixMyLimitsDaytimeBinding
import br.com.mobicare.cielo.databinding.LayoutPixMyLimitsMonthlyBinding
import br.com.mobicare.cielo.databinding.LayoutPixMyLimitsNighttimeBinding
import br.com.mobicare.cielo.pix.domain.Limits
import br.com.mobicare.cielo.pix.enums.PixLimitTypeEnum

abstract class LimitTypeViewSelector(
    private val limit: Limits,
    private val fragmentManager: FragmentManager,
) {

    abstract val containerDayTime: LayoutPixMyLimitsDaytimeBinding
    abstract val containerNightTime: LayoutPixMyLimitsNighttimeBinding
    abstract val containerMonthly: LayoutPixMyLimitsMonthlyBinding

    operator fun invoke() {
        when (limit.type) {
            PixLimitTypeEnum.DAYTIME_TRANSACTION_LIMIT.name ->
                configureDayTimeTransactionLimit()
            PixLimitTypeEnum.TOTAL_DAYTIME_TRANSACTION_LIMIT.name ->
                configureTotalDayTimeTransactionLimit()
            PixLimitTypeEnum.NIGHTTIME_TRANSACTION_LIMIT.name ->
                configureNightTimeTransactionLimit()
            PixLimitTypeEnum.TOTAL_NIGHTTIME_TRANSACTION_LIMIT.name ->
                configureTotalNightTimeTransactionLimit()
            PixLimitTypeEnum.TOTAL_MONTH_TRANSACTION_LIMIT.name ->
                configureTotalMonthTransactionLimit()
        }
    }

    private fun configureDayTimeTransactionLimit() = LimitTypeViewConfigurator(
        limit = limit,
        amountTextView = containerDayTime.tvMyLimitsDaytimeValue,
        layoutLimitsInformation = containerDayTime.containerPixMyLimitsDaytimeInfo,
        fragmentManager = fragmentManager
    ).invoke()

    private fun configureTotalDayTimeTransactionLimit() = LimitTypeViewConfigurator(
        limit = limit,
        amountTextView = containerDayTime.tvMyLimitsDaytimeTotalValue,
        layoutLimitsInformation = containerDayTime.containerPixMyLimitsDaytimeTotalInfo,
        layoutTransactionLimit = containerDayTime.containerPixMyLimitsDaytimeTransactionLimit,
        fragmentManager = fragmentManager
    ).invoke()

    private fun configureNightTimeTransactionLimit() = LimitTypeViewConfigurator(
        limit = limit,
        amountTextView = containerNightTime.tvMyLimitsNighttimeValue,
        layoutLimitsInformation = containerNightTime.containerPixMyLimitsNighttimeInfo,
        fragmentManager = fragmentManager
    ).invoke()

    private fun configureTotalNightTimeTransactionLimit() = LimitTypeViewConfigurator(
        limit = limit,
        amountTextView = containerNightTime.tvMyLimitsNighttimeTotalValue,
        layoutLimitsInformation = containerNightTime.containerPixMyLimitsNighttimeTotalInfo,
        layoutTransactionLimit = containerNightTime.containerPixMyLimitsNighttimeTransactionLimit,
        fragmentManager = fragmentManager
    ).invoke()

    private fun configureTotalMonthTransactionLimit() = LimitTypeViewConfigurator(
        limit = limit,
        amountTextView = containerMonthly.tvMyLimitsMonthlyValue,
        layoutLimitsInformation = containerMonthly.containerPixMyLimitsMonthlyInfo,
        layoutTransactionLimit = containerMonthly.containerPixMyLimitsMonthlyTransactionLimit,
        fragmentManager = fragmentManager
    ).invoke()

}