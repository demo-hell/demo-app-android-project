package br.com.mobicare.cielo.extrato.presentation.presenter

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.util.Preconditions
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.helpers.DateTimeHelper
import br.com.mobicare.cielo.commons.utils.DataCustom
import br.com.mobicare.cielo.commons.utils.toDate
import br.com.mobicare.cielo.commons.utils.toLocalDateTime
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoStatusDef
import br.com.mobicare.cielo.extrato.presentation.ui.ExtratoContract
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.CHARGEBACK_REMOVE
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.SOLESP_REMOVE
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.mySales.presentation.ui.MySalesConsolidatedFragment
import br.com.mobicare.cielo.mySales.presentation.ui.MySalesHomeFragment
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


class ExtratoPresenter(
    var mView: ExtratoContract.View,
    var context: Context,
    private val featureTogglePreference: FeatureTogglePreference,
) : ExtratoContract.Presenter {

    var quickFilter: QuickFilter? = null
    private var isFilterByPeriod = false
    var isFilterByCanceledStatus = false

    init {
        this.mView = Preconditions.checkNotNull(mView, "View não pode ser null.")
        this.context = Preconditions.checkNotNull(context, "Context não pode ser null.")

        this.quickFilter = QuickFilter
            .Builder()
            .initialDate(Calendar.getInstance().time)
            .finalDate(Calendar.getInstance().time)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun callGA(label: String, botao: String, tela: String) {
    }

    override fun resetFilter() {
        if (!isFilterByCanceledStatus) {
            this.quickFilter = QuickFilter.Builder().build()
        } else {
            this.quickFilter = QuickFilter.Builder().status(listOf(ExtratoStatusDef.CANCELADA))
                .build()
        }
        this.mView.changeColorFilter(true)
    }

    override fun loadExtrato(checkedId: Int, dataInicio: String, dataFim: String) {
        when (checkedId) {
            R.id.radio_filtro_hoje -> {
                loadExtratoByDailyDate(DataCustom(Calendar.getInstance().time))
            }
            R.id.radio_filtro_7 -> {
                showByPeriod(7)
            }
            R.id.radio_filtro_15 -> {
                showByPeriod(15)
            }
            R.id.radio_filtro_30 -> {
                showByPeriod(30)
            }
            R.id.radio_filtro_outros -> {
                if (!mView.validaOutrosPeriodos()) {
                    showByPeriod(dataInicio, dataFim)
                } else {
                    mView.showErrorData()
                    return
                }
            }
        }
        mView.hideFiltro()
    }

    private fun showByPeriod(days: Int) {
        val endDate = Calendar.getInstance().time
        showByPeriod(
            DateTimeHelper.decreaseDateByNumberDays(Calendar.getInstance().time, days),
            endDate
        )
    }

    private fun showByPeriod(initialDate: String, finalDate: String) {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        try {
            showByPeriod(sdf.parse(initialDate), sdf.parse(finalDate))
        } catch (error: Throwable) {
            Timber.tag(ExtratoPresenter::class.java.simpleName)
                .e("#### Falha para fazer o parser da data do filtro por perido, initialData=$initialDate, finalDate=$finalDate")
        }
    }

    private fun showByPeriod(initialDate: Date, finalDate: Date) {
        this.quickFilter = QuickFilter
            .Builder()
            .from(this.quickFilter!!)
            .initialDate(initialDate)
            .finalDate(finalDate)
            .build()
        showByPeriod(this.quickFilter!!)
    }

    override fun showByPeriod(quickFilter: QuickFilter) {

        this.isFilterByPeriod = true

        if (!isFilterByCanceledStatus) {
            mView.attachedFragment(MySalesConsolidatedFragment.newInstance(quickFilter))
        } else {
            mView.attachedFragment(MySalesHomeFragment.newInstance(quickFilter))
        }
    }

    override fun loadExtratoByDailyDate(date: DataCustom) {
        var initialDt: Date = date.toDate()
        var finalDt: Date = LocalDateTime.now().toDate()

        //Na tela de cancelamento deve exibir vendas canceladas do dia anterior
        if (isFilterByCanceledStatus)
            initialDt = initialDt.toLocalDateTime().minusDays(1).toDate()

        this.showByToday(
            QuickFilter
                .Builder()
                .from(this.quickFilter!!)
                .initialDate(initialDt)
                .finalDate(finalDt)
                .build()
        )
    }

    override fun showByToday(filter: QuickFilter) {
        this.quickFilter = filter
        this.isFilterByPeriod = false

        mView.attachedFragment(MySalesHomeFragment.newInstance(filter))

        if (FeatureTogglePreference.instance
                .getFeatureTogle(FeatureTogglePreference.EFETIVAR_CANCELAMENTO)
            && UserPreferences.getInstance().cancelTutorialExibitionCount == ZERO
        ) {
            mView.showCancelTutorial()
        }
    }

    override fun loadExtratoByPeriod(period: String) {
        showByPeriod(period.toInt())
    }

    override fun loadExtratoByInterval(startDate: DataCustom, endDate: DataCustom) {
        showByPeriod(startDate.toDate(), endDate.toDate())
    }

    override fun showMoreFilters() {
        mView.showMoreFilters(this.quickFilter!!, this.isFilterByPeriod)
    }

    override fun refresh(filter: QuickFilter) {
        this.quickFilter = filter
        this.mView.changeColorFilter(isFilterNotSelected(filter))

        if (isFilterByPeriod)
            showByPeriod(this.quickFilter!!)
        else
            showByToday(this.quickFilter!!)
    }

    private fun isFilterNotSelected(filter: QuickFilter) =
        ((filter.cardBrand.isNullOrEmpty()
                && filter.paymentType.isNullOrEmpty())
                && (filter.nsu.isNullOrEmpty()
                && filter.authorizationCode.isNullOrEmpty()
                && filter.grossAmount == null
                && filter.saleGrossAmount == null
                && filter.tid.isNullOrEmpty()))

    override fun openBottomSheetMoreOptions() {
        val enabledOptionSolesp = featureTogglePreference.getFeatureTogle(SOLESP_REMOVE)
        val enabledOptionChargeback = featureTogglePreference.getFeatureTogle(CHARGEBACK_REMOVE)
        mView.openBottomSheetMoreOptions(enabledOptionSolesp, enabledOptionChargeback)
    }

}