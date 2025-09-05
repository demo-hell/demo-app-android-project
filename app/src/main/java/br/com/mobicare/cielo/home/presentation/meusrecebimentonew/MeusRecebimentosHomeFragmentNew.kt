package br.com.mobicare.cielo.home.presentation.meusrecebimentonew

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.presentation.ArvNavigationFlowActivity
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.SIMPLE_HOUR_MINUTE_24h
import br.com.mobicare.cielo.commons.utils.hourMinuteToBrFormat
import br.com.mobicare.cielo.commons.utils.initCountDownResearch
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.ContentMeusRecebimentosBinding
import br.com.mobicare.cielo.home.presentation.analytics.HomeAnalytics
import br.com.mobicare.cielo.home.presentation.analytics.HomeAnalytics.Companion.DEPOSITED_YESTERDAY
import br.com.mobicare.cielo.home.presentation.analytics.HomeAnalytics.Companion.RECEIVABLES
import br.com.mobicare.cielo.home.presentation.analytics.HomeAnalytics.Companion.SALES_AND_RECEIVABLES
import br.com.mobicare.cielo.home.presentation.analytics.HomeAnalytics.Companion.VALUE_TO_RECEIVE
import br.com.mobicare.cielo.home.presentation.analytics.HomeGA4
import br.com.mobicare.cielo.meusrecebimentosnew.models.Summary
import br.com.mobicare.cielo.webView.presentation.WebViewContainerActivity
import br.com.mobicare.cielo.webView.utils.FLOW_NAME_PARAM
import br.com.mobicare.cielo.webView.utils.URL_PARAM
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MeusRecebimentosHomeFragmentNew : BaseFragment(), MeusRecebimentosHomeContract.View {

    private var binding: ContentMeusRecebimentosBinding? = null
    private val analytics: HomeAnalytics by inject()
    private val ga4: HomeGA4 by inject()
    private val myReceiptsHomePresenter: MyReceiptsHomePresenter by inject {
        parametersOf(this)
    }

    companion object {
        fun newInstance() = MeusRecebimentosHomeFragmentNew()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ContentMeusRecebimentosBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDefaultLayout()
        setupListeners()
    }

    private fun setupDefaultLayout() {
        if(isAttached()){
            binding?.apply {
                includeDepositedTodayStatus.apply {
                    tvTotalSalesQuantity.text = getString(R.string.tv_deposited_today)
                    tvTotalSalesValue.setTextColor(resources.getColor(R.color.alert_400))
                    ivCheck.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_clock_alert_500, null))

                    root.background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.background_solid_alert_50_rounded_8dp,
                        null
                    )
                }

                includeDepositedYesterdayStatus.tvTotalSalesQuantity.text = getString(R.string.tv_deposited_yesterday)

                containerSeeMore.apply {
                    tvActionButton.text = getString(R.string.anticipate_receivables)
                    root.background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.background_solid_alert_50_rounded_8dp,
                        null
                    )
                }
            }
        }
    }

    private fun refreshUpdatedTime() {
        binding?.containerReceivablesUpdatedAt?.tvUpdatedAt?.text = getString(
            R.string.updated_at,
            SimpleDateFormat(SIMPLE_HOUR_MINUTE_24h, Locale.getDefault()).format(Date())
        ).hourMinuteToBrFormat()
    }

    private fun setupListeners() {
        binding?.apply {
            containerSeeMore.root.setOnClickListener {
                goToArv()
                ga4.logServiceHomeClick(
                    contentComponent = HomeGA4.RECEIVABLES,
                    contentName = HomeGA4.ANTICIPATE_RECEIVABLES
                )
            }

            includeDepositedTodayStatus.root.setOnClickListener {
                setupReceivablesFlow(VALUE_TO_RECEIVE)
            }

            includeDepositedYesterdayStatus.root.setOnClickListener {
                setupReceivablesFlow(DEPOSITED_YESTERDAY)
            }

            containerReceivablesUpdatedAt.llRefreshSales.setOnClickListener {
                setupReceivablesRefreshFlow()
            }
            includeCardError.btTryAgain.setOnClickListener {
                setupReceivablesRefreshFlow()
            }
        }
    }

    private fun setupReceivablesFlow(labelName: String){
        startSatisfactionSurvey()
        goToMyReceivables()
        analytics.logScreenActions(
                actionName = SALES_AND_RECEIVABLES,
                flowName = RECEIVABLES,
                labelName = labelName
        )
    }

    private fun setupReceivablesRefreshFlow() {
        getReceivables()
        analytics.logScreenActions(
                actionName = SALES_AND_RECEIVABLES,
                flowName = RECEIVABLES,
                labelName = Action.REFRESH
        )
    }

    override fun onResume() {
        super.onResume()
        myReceiptsHomePresenter.onResume()
        getReceivables()
    }

    override fun onPause() {
        myReceiptsHomePresenter.onPause()
        super.onPause()
    }

    override fun showReceivablesInfo(yesterdayReceivables: Summary?, todayReceivables: Summary?, isByRefreshing: Boolean) {
        if(isByRefreshing) analytics.logCallbackRefreshButton(flowName = RECEIVABLES)

        binding?.apply {
            hideLoading()
            llGroupIncludes.visible()

            yesterdayReceivables?.let { itYesterdayReceivable ->
                includeDepositedYesterdayStatus.tvTotalSalesValue.text = itYesterdayReceivable.totalAmount.toPtBrRealString()
            } ?: run {
                showDefaultValue(includeDepositedYesterdayStatus.tvTotalSalesValue)
            }

            todayReceivables?.let {
                includeDepositedTodayStatus.tvTotalSalesValue.text = it.totalAmount.toPtBrRealString()
            } ?: run {
                showDefaultValue(includeDepositedTodayStatus.tvTotalSalesValue)
            }
        }
    }

    override fun unavailableReceivables(isByRefreshing: Boolean) {
        if (isAttached()) {
            if(isByRefreshing) analytics.logCallbackRefreshButton(flowName = RECEIVABLES)

            binding?.apply {
                containerSeeMore.root.setBackgroundColor(resources.getColor(R.color.white))
                includeCardError.root.visible()
            }
        }
    }

    private fun showDefaultValue(tvTotalSalesValue: TextView?) {
        tvTotalSalesValue?.text = ZERO_DOUBLE.toPtBrRealString()
    }

    override fun showLoading() {
        if (isAttached()) {
            binding?.apply {
                includeCardError.root.gone()
                llGroupIncludes.gone()
                includeShimmerDepositedYesterday.root.visible()
                includeShimmerDepositedYesterday.loadingShimmerDepositedYesterday.startShimmer()

                includeShimmerDepositedToday.root.visible()
                includeShimmerDepositedToday.loadingShimmerDepositedToday.startShimmer()

                containerSeeMore.root.background = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.background_solid_alert_50_rounded_8dp,
                    null
                )
            }
        }
    }

    override fun hideLoading() {
        if (isAttached()) {
            binding?.apply {
                includeShimmerDepositedYesterday.root.gone()
                includeShimmerDepositedYesterday.loadingShimmerDepositedYesterday.stopShimmer()

                includeShimmerDepositedToday.root.gone()
                includeShimmerDepositedToday.loadingShimmerDepositedToday.stopShimmer()
            }
        }
    }

    override fun showError(error: ErrorMessage?, isByRefreshing: Boolean) {
        if (isAttached()) {
            if(isByRefreshing) analytics.logCallbackRefreshButton(error, flowName = RECEIVABLES)

            setupErrorLayout()
        }
    }

    private fun setupErrorLayout() {
        binding?.apply {
            hideLoading()
            llGroupIncludes.gone()
            containerSeeMore.root.setBackgroundColor(resources.getColor(R.color.white))

            includeCardError.apply {
                root.visible()
                tvErrorInfo.text = getString(R.string.tv_error_title)
            }
        }
    }

    private fun goToArv() = requireActivity().startActivity<ArvNavigationFlowActivity>()
    private fun goToMyReceivables(){
        if (myReceiptsHomePresenter.checkShowNewReceivables()){
            Intent(requireContext(), WebViewContainerActivity::class.java).apply {
                putExtra(URL_PARAM, BuildConfig.RECEBIVEIS_URL)
                putExtra(FLOW_NAME_PARAM, "Recebíveis")
                startActivity(this)
            }
        }else{
            requireActivity().startActivity<MeusRecebimentosHomeActivityNew>()
        }
    }
    private fun startSatisfactionSurvey() {
        requireActivity().supportFragmentManager.apply {
            requireActivity().initCountDownResearch(
                requireContext(),
                this, Action.HOME_INICIO
            )
        }
    }

    private fun getReceivables() {
        showLoading()
        refreshUpdatedTime()
        myReceiptsHomePresenter.getAllReceivables()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}