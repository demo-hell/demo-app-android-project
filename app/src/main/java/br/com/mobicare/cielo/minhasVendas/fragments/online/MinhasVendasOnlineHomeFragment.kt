package br.com.mobicare.cielo.minhasVendas.fragments.online

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Action.REFRESH
import br.com.mobicare.cielo.commons.constants.ELEVEN
import br.com.mobicare.cielo.commons.constants.MainConstants
import br.com.mobicare.cielo.commons.constants.SIXTEEN
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.helpers.BrandCardHelper
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.SIMPLE_HOUR_MINUTE_24h
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
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
import br.com.mobicare.cielo.home.presentation.analytics.HomeGA4
import br.com.mobicare.cielo.home.presentation.minhasVendas.ui.activity.MinhasVendasHomeActivity
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.mySales.data.model.Sale
import br.com.mobicare.cielo.mySales.data.model.Summary
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MinhasVendasOnlineHomeFragment : BaseFragment(), MinhasVendasOnlineContract.View {

    private var binding: MinhasVendasOnlineHomeBinding? = null
    private val analytics: HomeAnalytics by inject()
    private val ga4: HomeGA4 by inject()
    private val presenter: MinhasVendasOnlinePresenter by inject {
        parametersOf(this)
    }

    companion object {
        fun newInstance() = MinhasVendasOnlineHomeFragment()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        return MinhasVendasOnlineHomeBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        binding?.apply {
            containerSeeMoreSales.root.setOnClickListener {
                setupSalesActionFlow(SEE_MORE_SALES)
                ga4.logServiceHomeClick(
                    contentComponent = HomeGA4.SALES,
                    contentName = HomeGA4.SEE_MORE_SALES
                )
            }

            containerTotalSales.root.setOnClickListener {
                setupSalesActionFlow(TOTAL_SALES_TODAY)
            }

            containerLastSale.root.setOnClickListener {
                setupSalesActionFlow(LAST_SALES_TODAY)
            }

            includeCardError.btTryAgain.setOnClickListener {
                setupSalesRefreshFlow()
            }

            containerSalesUpdatedAt.llRefreshSales.setOnClickListener {
                setupSalesRefreshFlow()
            }
        }
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

    private fun setupSalesRefreshFlow() {
        getSales(isByRefreshing = true)
        analytics.logScreenActions(
                actionName = SALES_AND_RECEIVABLES,
                flowName = SALES,
                labelName = REFRESH
        )
    }

    override fun onStart() {
        super.onStart()
        getSales()
    }

    override fun onPause() {
        super.onPause()
        this.presenter.onClearRequests()
    }

    private fun getSales(isByRefreshing: Boolean = false) {
        showLoading()
        refreshUpdatedTime()

        this.presenter.loadInitial(QuickFilter.Builder().apply {
            initialDate(Calendar.getInstance().time)
            finalDate(Calendar.getInstance().time)
        }.build(), isByRefreshing)
    }

    private fun refreshUpdatedTime() {
        binding?.containerSalesUpdatedAt?.tvUpdatedAt?.text = getString(
                R.string.updated_at,
                SimpleDateFormat(SIMPLE_HOUR_MINUTE_24h, Locale.getDefault()).format(Date())
        ).hourMinuteToBrFormat()
    }

    override fun showLoading() {
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

    override fun hideLoading() {
        if (isAttached()) {
            binding?.apply {
                includeShimmerTodaySale.root.gone()
                includeShimmerLastSale.root.gone()
            }
        }
    }

    override fun showError(error: ErrorMessage?, isByRefreshing: Boolean) {
        if(isByRefreshing) analytics.logCallbackRefreshButton(error, flowName = SALES)

        error?.let {
            if (isAttached()) {
                binding?.apply {
                    containerTotalSales.root.gone()
                    containerLastSale.root.gone()
                    llNoSalesContainer.gone()
                    includeCardError.root.visible()

                    containerSeeMoreSales.root.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, null))
                }

                hideLoading()
            }
        }
    }

    override fun showSales(
        sales: ArrayList<Sale>,
        summary: Summary,
        saleStatus: Int,
        isNewLoad: Boolean,
        isByRefreshing: Boolean
    ) {
        if (isAttached()) {
            if(isByRefreshing) analytics.logCallbackRefreshButton(flowName = SALES)

            hideLoading()

            binding?.apply {
                includeCardError.root.gone()
                llNoSalesContainer.gone()
                containerTotalSales.root.visible()
                containerLastSale.root.visible()

                containerTotalSales.tvTotalSalesQuantity.text = resources.getQuantityString(R.plurals.totalSalesToday, summary.totalQuantity ?: ZERO,  summary.totalQuantity)
                containerTotalSales.tvTotalSalesValue.text = summary.totalAmount?.toPtBrRealString()

                sales.lastOrNull()?.let { itLastSale ->
                    containerLastSale.apply {
                        ivBrandIcon.contentDescription = getString(R.string.content_description_brand_name, itLastSale.cardBrandDescription)
                        tvLastSaleTime.text = getString(R.string.last_sale_time, itLastSale.date?.substring(ELEVEN, SIXTEEN)).hourMinuteToBrFormat()
                        tvSaleType.text = itLastSale.paymentType
                        tvSaleValue.text = itLastSale.amount?.toPtBrRealString()
                    }

                    itLastSale.cardBrand?.let { itCardBrand ->
                        BrandCardHelper.getUrlBrandImageByCode(itCardBrand)?.let { itUrl ->
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

    override fun showEmptyResult(isByRefreshing: Boolean) {
        if(isByRefreshing) analytics.logCallbackRefreshButton(flowName = SALES)

        if (isAttached()) {
            hideLoading()

            binding?.apply {
                llNoSalesContainer.visible()
                tvNoSalesMessage.text =
                        getString(R.string.home_minhas_vendas_quantidade_vendas_zero)

                containerSeeMoreSales.root.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, null))
            }
        }
    }

    override fun showFullsecError() {
        val hideHomeFragmentBroadcast = Intent(MainConstants.FRAGMENT_HIDE_MANAGER)
        hideHomeFragmentBroadcast.putExtra(MainConstants.FRAME_ID_TO_HIDE, R.id.clSalesContainer
        )
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(hideHomeFragmentBroadcast)
    }

    private fun goToMySalesFlow() = startActivity(Intent(activity, MinhasVendasHomeActivity::class.java))
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

    override fun logout(msg: ErrorMessage?) {
        SessionExpiredHandler.userSessionExpires(requireContext())
    }

    override fun onDestroy() {
        presenter.onDestroy()
        binding = null
        super.onDestroy()
    }
}