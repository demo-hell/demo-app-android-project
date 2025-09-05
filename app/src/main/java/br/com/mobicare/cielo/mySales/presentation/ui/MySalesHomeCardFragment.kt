package br.com.mobicare.cielo.mySales.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.constants.ELEVEN
import br.com.mobicare.cielo.commons.constants.MainConstants
import br.com.mobicare.cielo.commons.constants.SIXTEEN
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.helpers.BrandCardHelper
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.SIMPLE_HOUR_MINUTE_24h
import br.com.mobicare.cielo.commons.utils.hourMinuteToBrFormat
import br.com.mobicare.cielo.commons.utils.initCountDownResearch
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.MinhasVendasOnlineHomeBinding
import br.com.mobicare.cielo.home.presentation.analytics.HomeAnalytics
import br.com.mobicare.cielo.home.presentation.analytics.HomeAnalytics.Companion.LAST_SALES_TODAY
import br.com.mobicare.cielo.home.presentation.analytics.HomeAnalytics.Companion.SALES
import br.com.mobicare.cielo.home.presentation.analytics.HomeAnalytics.Companion.SALES_AND_RECEIVABLES
import br.com.mobicare.cielo.home.presentation.analytics.HomeAnalytics.Companion.SEE_MORE_SALES
import br.com.mobicare.cielo.home.presentation.analytics.HomeAnalytics.Companion.TOTAL_SALES_TODAY
import br.com.mobicare.cielo.home.presentation.minhasVendas.ui.activity.MinhasVendasHomeActivity
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.mySales.data.model.bo.HomeCardSummarySaleBO
import br.com.mobicare.cielo.mySales.presentation.viewmodel.HomeSalesCardViewModel
import br.com.mobicare.cielo.mySales.presentation.utils.MySalesViewState
import br.com.mobicare.cielo.webView.presentation.WebViewContainerActivity
import br.com.mobicare.cielo.webView.utils.FLOW_NAME_PARAM
import br.com.mobicare.cielo.webView.utils.URL_PARAM
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MySalesHomeCardFragment(): BaseFragment() {

    private var _binding: MinhasVendasOnlineHomeBinding? = null
    private val binding get() = _binding

    private val analytics: HomeAnalytics by inject()
    private val viewModel: HomeSalesCardViewModel by viewModel()

    companion object {
        fun newInstance() = MySalesHomeCardFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MinhasVendasOnlineHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        setupButtonsListeners()
        handleMySalesAPIService()
        getHomeCardSummarySaleData()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun configureQuickFilter(): QuickFilter {
         return  QuickFilter.Builder().apply {
            initialDate(Calendar.getInstance().time)
            finalDate(Calendar.getInstance().time)
        }.build()
    }

    private fun getHomeCardSummarySaleData() {
        refreshUpdateTime()
        viewModel.getHomeCardSummarySale(configureQuickFilter())
    }

    private fun handleMySalesAPIService() {
        viewModel.getHomeCardSummarySalesViewState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is MySalesViewState.SUCCESS -> configureSuccessView(state.data)
                is MySalesViewState.EMPTY -> showEmptyView()
                is MySalesViewState.ERROR -> showErrorView(state.newErrorMessage)
                is MySalesViewState.LOADING -> showLoadingView()
                is MySalesViewState.ERROR_FULL_SCREEN -> showFullScreenError()
            }
        }
    }


    private fun setupButtonsListeners() {
        binding?.apply {
            containerSeeMoreSales.root.setOnClickListener {
                setupSalesActionFlow(SEE_MORE_SALES)
            }

            containerTotalSales.root.setOnClickListener {
                setupSalesActionFlow(TOTAL_SALES_TODAY)
            }

            containerLastSale.root.setOnClickListener {
                setupSalesActionFlow(LAST_SALES_TODAY)
            }

            includeCardError.btTryAgain.setOnClickListener {
                setupSalesRefresh()
            }

            containerSalesUpdatedAt.llRefreshSales.setOnClickListener {
                setupSalesRefresh()
            }
        }
    }


    private fun refreshUpdateTime() {
        binding?.containerSalesUpdatedAt?.tvUpdatedAt?.text = getString(
            R.string.updated_at,
            SimpleDateFormat(SIMPLE_HOUR_MINUTE_24h, Locale.getDefault()).format(Date())
        ).hourMinuteToBrFormat()
    }

    private fun showEmptyView() {
        if(viewModel.isRefreshing) analytics.logCallbackRefreshButton(flowName = SALES)
        if (isAttached()) {
            hideLoadingView()
            binding?.apply {
                llNoSalesContainer.visible()
                tvNoSalesMessage.text =
                    getString(R.string.home_minhas_vendas_quantidade_vendas_zero)
                containerSeeMoreSales.root.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, null))
            }
        }
    }

    private fun showErrorView(newErrorMessage: NewErrorMessage?) {
        if(viewModel.isRefreshing) analytics.logCallbackForRefreshButton(
            newErrorMessage = newErrorMessage, flowName = SALES)

        if(isAttached()){
            hideLoadingView()
            binding?.apply {
                containerTotalSales.root.gone()
                containerLastSale.root.gone()
                llNoSalesContainer.gone()
                includeCardError.root.visible()
                containerSeeMoreSales.root.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, null))
            }
        }
    }

    private fun showLoadingView() {
        if (isAttached()) {
            binding?.apply {
                includeShimmerTodaySale.root.visible()
                includeShimmerLastSale.root.visible()
                llNoSalesContainer.gone()
                includeCardError.root.gone()
                containerTotalSales.root.gone()
                containerLastSale.root.gone()
                containerSeeMoreSales.root.background = ResourcesCompat.getDrawable(resources, R.drawable.background_solid_pistachio_100_rounded_8dp, null)
            }
        }
    }


    private fun hideLoadingView() {
        if(isAttached()){
            binding?.apply {
                includeShimmerTodaySale.root.gone()
                includeShimmerLastSale.root.gone()
            }
        }
    }

    private fun configureSuccessView(cardSummary: HomeCardSummarySaleBO?) {
        if(isAttached()){
            hideLoadingView()
            if(viewModel.isRefreshing) analytics.logCallbackRefreshButton(flowName = SALES)

            binding?.apply {
                cardSummary?.let {
                    includeCardError.root.gone()
                    llNoSalesContainer.gone()
                    containerTotalSales.root.visible()
                    containerLastSale.root.visible()
                    containerTotalSales.tvTotalSalesQuantity.text =
                        resources.getQuantityString(R.plurals.totalSalesToday, cardSummary.summary.totalQuantity ?: ZERO,  cardSummary.summary.totalQuantity)
                    containerTotalSales.tvTotalSalesValue.text = cardSummary.summary.totalAmount?.toPtBrRealString()

                    containerLastSale.apply {
                        ivBrandIcon.contentDescription = getString(R.string.content_description_brand_name, cardSummary.lastSale.cardBrandDescription)
                        tvLastSaleTime.text = getString(R.string.last_sale_time, cardSummary.lastSale.date?.substring(
                            ELEVEN, SIXTEEN
                        )).hourMinuteToBrFormat()
                        tvSaleType.text = cardSummary.lastSale.paymentType
                        tvSaleValue.text = cardSummary.lastSale.amount?.toPtBrRealString()

                    }

                    cardSummary.lastSale.cardBrand?.let {cardbrand->
                        BrandCardHelper.getUrlBrandImageByCode(cardbrand)?.let { itUrl ->
                            ImageUtils.loadImage(
                                containerLastSale.ivBrandIcon,
                                itUrl,
                                R.drawable.ic_generic_brand
                            )
                        }
                    }
                }
                containerSeeMoreSales.root.background = ResourcesCompat.getDrawable(resources, R.drawable.background_solid_pistachio_100_rounded_8dp, null)
            }
        }
    }

    private fun goToMySalesFlow() {
        if (viewModel.checkShowSalesWebPage()) goToSalesWebPage()
        else startActivity(Intent(activity, MinhasVendasHomeActivity::class.java))
    }

    private fun goToSalesWebPage() {
        val intent = Intent(requireContext(), WebViewContainerActivity::class.java)
        intent.putExtra(URL_PARAM, BuildConfig.SALES_URL)
        intent.putExtra(FLOW_NAME_PARAM, "Vendas")
        startActivity(intent)
    }

    private fun startSatisfactionSurvey() {
        if (isAttached()) {
            requireActivity().supportFragmentManager.apply {
                requireActivity().initCountDownResearch(
                    requireContext(),
                    this,
                    Action.HOME_INICIO
                )
            }
        }
    }

    fun showFullScreenError() {
        val hideHomeFragmentBroadcast = Intent(MainConstants.FRAGMENT_HIDE_MANAGER)
        hideHomeFragmentBroadcast.putExtra(
            MainConstants.FRAME_ID_TO_HIDE, R.id.clSalesContainer
        )
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(hideHomeFragmentBroadcast)
    }

    private fun setupSalesActionFlow(labelName: String){
        startSatisfactionSurvey()
        goToMySalesFlow()
        analytics.logScreenActions(
            actionName = SALES_AND_RECEIVABLES,
            flowName = SALES,
            labelName = labelName
        )
    }

    private fun setupSalesRefresh() {
        viewModel.isRefreshing = true
        getHomeCardSummarySaleData()
        showLoadingView()
        analytics.logScreenActions(
            actionName = SALES_AND_RECEIVABLES,
            flowName = SALES,
            labelName = Action.REFRESH
        )
    }
}