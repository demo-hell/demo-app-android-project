package br.com.mobicare.cielo.minhasVendas.fragments.trasacoes

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias.CentralAjudaSubCategoriasEngineActivity
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.analytics.Label.SWIPE
import br.com.mobicare.cielo.commons.constants.*
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.enums.EnrollmentType
import br.com.mobicare.cielo.commons.helpers.BrandCardHelper
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.ui.adapter.InfiniteScrollOnDefaultViewListAdapter
import br.com.mobicare.cielo.commons.ui.widget.MfaBaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.AccessibilityUtils
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.bottomSheetGeneric
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.LayoutMinhasVendasTransacoesActivityBinding
import br.com.mobicare.cielo.extensions.*
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.idOnboarding.router.IDOnboardingRouter
import br.com.mobicare.cielo.mfa.router.userWithP2.MfaTokenConfigurationBottomSheet
import br.com.mobicare.cielo.minhasVendas.activities.CANCELAR_VENDAS_BUTTON_NAME
import br.com.mobicare.cielo.minhasVendas.activities.CANCELAR_VENDAS_CATEGORY
import br.com.mobicare.cielo.minhasVendas.activities.CANCELAR_VENDAS_EVENT
import br.com.mobicare.cielo.minhasVendas.activities.CancelDetailActivity
import br.com.mobicare.cielo.mySales.analytics.MySalesGA4
import br.com.mobicare.cielo.mySales.analytics.MySalesGA4.Companion.SCREEN_NAME_SALES_MADE
import br.com.mobicare.cielo.minhasVendas.constants.INELEGIBLE_SALE
import br.com.mobicare.cielo.minhasVendas.constants.IS_SALE_TODAY_ARGS
import br.com.mobicare.cielo.minhasVendas.constants.NOT_FOUND
import br.com.mobicare.cielo.minhasVendas.constants.WITHOUT_BALANCE
import br.com.mobicare.cielo.minhasVendas.detalhe.MinhasVendasDetalhesActvity
import br.com.mobicare.cielo.mySales.data.model.Sale
import br.com.mobicare.cielo.mySales.data.model.Summary
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.CancelamentoPresenter
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.OnCancelamentoContract
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.ResponseBanlanceInquiry
import br.com.mobicare.cielo.mySales.presentation.utils.MySalesStatusHelper
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.minhasVendas.fragments.common.ScrollControlledLinearManager
import br.com.mobicare.cielo.minhasVendas.fragments.filter.MinhasVendasFilterBottomSheetFragment
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy.dialog.BottomSheetGenericFragment
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.layout_item_minhas_vendas.view.*
import kotlinx.android.synthetic.main.layout_minhas_vendas_transacoes_activity.*
import kotlinx.android.synthetic.main.layout_option_delete.view.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

const val MINHAS_VENDAS_FILTER = "MinhasVendasFilterBottomSheetFragment"


//TODO [MYSALES] - Remover posteriormente - Migracao MVVM
class MinhasVendasTransacoesActivity : MfaBaseLoggedActivity(), MinhasVendasTransacoesContract.View,
    OnCancelamentoContract.View {

    private val presenter: MinhasVendasTransacoesPresenter by inject {
        parametersOf(this)
    }

    private val presenterCancel: CancelamentoPresenter by inject {
        parametersOf(this)
    }

    var elegibility: Boolean = false

    private val ga4: MySalesGA4 by inject()
    private var selectedSale: Sale? = null
    private var menu: Menu? = null
    private var adapter: InfiniteScrollOnDefaultViewListAdapter<Sale>? = null
    private var scrollControlledLinearManager: ScrollControlledLinearManager? = null
    private var quickFilter: QuickFilter? = null
    private var binding: LayoutMinhasVendasTransacoesActivityBinding? = null

    companion object {
        private val ELEGIBILITY = "ELEGIBILITY"
        private val ARG_PARAM_QUICK_FILTER = "ARG_PARAM_QUICK_FILTER"
        fun create(context: Context, quickFilter: QuickFilter) {
            context.startActivity(
                Intent(
                    context,
                    MinhasVendasTransacoesActivity::class.java
                ).apply {
                    putExtra(ARG_PARAM_QUICK_FILTER, quickFilter)
                })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LayoutMinhasVendasTransacoesActivityBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        lifecycle.addObserver(presenterCancel)
        configureToolbar()
        configureRecyclerView()
        configureSwipeRefreshLayout()
        configureListeners()
        loadParameters()
    }

    private fun loadParameters() {
        this.intent?.extras?.let {
            it.getSerializable(ARG_PARAM_QUICK_FILTER)?.let { itQuickFilter ->
                (itQuickFilter as? QuickFilter)?.let {
                    this.quickFilter = it
                    this.presenter.loadInitial(it)
                }
            }
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        presenterMfa.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenterMfa.onPause()
        this.presenter.onClearRequests()
    }

    override fun showLoading() {
        binding?.apply {
            salesSummaryFooter.root.gone()
            waitingAddMoreItensLayout.gone()
            errorLayout.root.gone()
            waitingLayout.visible()
            recyclerView.gone()
        }
    }

    override fun hideLoading() {
        binding?.apply {
            waitingLayout.gone()
            salesSummaryFooter.root.gone()
            waitingAddMoreItensLayout.gone()
            recyclerView.visible()
        }
    }

    override fun hideRefreshLoading() {
        binding?.swipeRefreshLayout?.isRefreshing = false
    }

    override fun showLoadingMoreItens() {
        binding?.apply {
            waitingAddMoreItensLayout.visible()
            salesSummaryFooter.root.gone()
        }
    }

    override fun logout(msg: ErrorMessage) {
        SessionExpiredHandler.userSessionExpires(this, true)
    }

    override fun showError(error: ErrorMessage) {
       exceptionGA4(error)
        binding?.apply {
            swipeRefreshLayout.gone()
            recyclerView.gone()
            waitingLayout.gone()
            errorLayout.apply{
                root.visible()
                containerError.visible()
                errorLayout.textViewErrorMsg.text = error.message
            }
        }
    }

    override fun showSales(sales: ArrayList<Sale>) {
        this.swipeRefreshLayout?.visible()
        if (isAttached()) {
            binding?.recyclerView?.itemAnimator = null
            if (adapter == null) {
                if (FeatureTogglePreference.instance
                        .getFeatureTogle(FeatureTogglePreference.EFETIVAR_CANCELAMENTO)
                ) {
                    adapter = InfiniteScrollOnDefaultViewListAdapter(
                        sales,
                        R.layout.layout_item_minhas_vendas_man
                    )
                } else {
                    adapter = InfiniteScrollOnDefaultViewListAdapter(
                        sales,
                        R.layout.layout_item_minhas_vendas
                    )
                }

                binding?.swipeRefreshLayout?.let { adapter?.attachSwipeLayout(it) }
                adapter?.setOnLoadNextPageListener(object :
                    InfiniteScrollOnDefaultViewListAdapter.OnLoadNextPageListener {
                    override fun onLoadNextPage() {
                        this@MinhasVendasTransacoesActivity.presenter.loadMore()
                    }
                })
                adapter?.setBindViewHolderCallback(object :
                    DefaultViewListAdapter.OnBindViewHolder<Sale> {
                    override fun onBind(item: Sale, holder: DefaultViewHolderKotlin) {
                        holder.mView.tvTime?.text = item.date?.substring(11, 16)
                        holder.mView.tvValue?.text = item.grossAmount?.toPtBrRealString()
                        holder.mView.tvPaymentType?.text = item.paymentType
                        holder.mView.tvStatus?.text = "Aprovada"

                        item.cardBrandCode?.let { itCardBrandCode ->
                            BrandCardHelper.getUrlBrandImageByCode(itCardBrandCode)?.let { itUrl ->
                                ImageUtils.loadImage(
                                    holder.mView.ivBrandType,
                                    itUrl,
                                    R.drawable.ic_generic_brand
                                )
                                holder.mView.ivBrandType.contentDescription =
                                    getString(
                                        R.string.description_focused_flag_card,
                                        item.cardBrand
                                    )
                            }
                                ?: holder.mView.ivBrandType?.setImageResource(R.drawable.ic_generic_brand)
                        }

                        // Foi chapado como aprovado porque não esta retornando da api
                        MySalesStatusHelper.setSalesStatus(
                            this@MinhasVendasTransacoesActivity,
                            1,
                            null,
                            holder.mView.tvStatus
                        )

                        holder.mView.ll_mv.setOnClickListener {
                            item.statusCode = 1
                            val intent = MinhasVendasDetalhesActvity.newIntent(
                                this@MinhasVendasTransacoesActivity,
                                item
                            )
                            intent.putExtra(IS_SALE_TODAY_ARGS, false)
                            startActivity(intent)
                        }

                        if (FeatureTogglePreference.instance
                                .getFeatureTogle(FeatureTogglePreference.EFETIVAR_CANCELAMENTO)
                        ) {
                            holder.mView.ll_cancel.setOnClickListener {
                                gaSendButtonCancelDetail(CANCELAR_VENDAS_BUTTON_NAME)
                                showLoading()
                                presenterMfa.load()
                                selectedSale = item
                                ga4.beginCancel(SCREEN_NAME_SALES_MADE, SWIPE)
                            }

                        }

                    }
                })
                binding?.recyclerView?.adapter = adapter
            } else {
                val wrapSales = arrayListOf(*sales.toTypedArray())
                adapter?.setNewDataSet(wrapSales)
                Handler().postDelayed({
                    scrollControlledLinearManager?.setIsCanScroll(true)
                }, FIVE_HUNDRED)
            }
        }
    }

    override fun showMoreSales(sales: List<Sale>) {
        binding?.apply {
            salesSummaryFooter.root.visible()
            waitingAddMoreItensLayout.gone()
        }

        if (sales.isEmpty()) {
            adapter?.setEndOfTheList(true)
        } else {
            adapter?.addMoreInList(sales)
        }
    }

    override fun showSummary(summary: Summary) {
        val totalQuantity = summary.totalQuantity ?: ZERO
        val totalAmount = summary.totalAmount ?: ZERO_DOUBLE
        val totalNetAmount = summary.totalNetAmount ?: ZERO_DOUBLE
        val isShowFooter = totalAmount > ZERO_DOUBLE
        val isShowNetAmount = totalNetAmount > ZERO_DOUBLE

        if (isAttached()) {
            binding?.apply {
                emptyResultHandler.gone()
                salesSummaryFooter.apply {
                    root.visible(isShowFooter)
                    if (isShowFooter) {
                        txtValueApprovedSales.text = totalQuantity.toString()
                        txtValueGrossValue.text = totalAmount.toPtBrRealString()
                        txtValueGrossValue.contentDescription =
                            AccessibilityUtils.convertAmount(totalAmount, baseContext)
                        txtValueNetValue.text = totalNetAmount.toPtBrRealString()
                        txtValueNetValue.contentDescription =
                            AccessibilityUtils.convertAmount(totalNetAmount, baseContext)
                        rowNetValue.visible(isShowNetAmount)
                    }
                }
            }
        }
    }

    override fun showEmptyResult() {
        binding?.apply {
            recyclerView.recycledViewPool.clear()
            swipeRefreshLayout.gone()
            emptyResultHandler.configureButtonVisible(false)
            emptyResultHandler.visible()
        }
    }

    override fun setToolbarText(text: String) {
        binding?.textYourDaySales?.text = getString(R.string.text_your_day_sales, text)
    }

    private fun configureToolbar() {
        setupToolbar(this.toolbar as Toolbar, toolbarTitle = getString(R.string.ga_extrato))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_common_filter_faq, menu)

        setupVisibleItemMenuHelp(menu)
        this.menu = menu
        this.changeColorFilter(this.presenter.isFilterNotSelected())

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_filter -> {
                presenter.showMoreFilters()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupVisibleItemMenuHelp(menu: Menu?) {
        menu?.findItem(R.id.action_help_extrato)?.isVisible = false
    }

    private fun configureRecyclerView() { //feito
        scrollControlledLinearManager =
            ScrollControlledLinearManager(this@MinhasVendasTransacoesActivity)
        binding?.apply {
            recyclerView.layoutManager = scrollControlledLinearManager
            recyclerView.itemAnimator = null
        }
    }

    private fun configureSwipeRefreshLayout() {
        binding?.swipeRefreshLayout?.setOnRefreshListener {
            scrollControlledLinearManager?.setIsCanScroll(false)
            presenter.refresh(quickFilter)
        }
    }

    private fun configureListeners() {
        binding?.errorLayout?.buttonErrorTry?.setOnClickListener {
            presenter.retry()
        }
    }

    private fun gaSendCallback(callbackType: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.MINHAS_VENDAS),
            action = listOf(Action.VENDA, Action.CANCELAR),
            label = listOf(Label.CALLBACK, callbackType)
        )
    }

    private fun createDialog(
        @StringRes title: Int,
        @StringRes message: Int,
        @DrawableRes drawable: Int
    ) {

        CieloDialog.create(
            getString(title),
            getString(message),
        )
            .setImage(drawable)
            .setTitleColor(R.color.color_204986)
            .setMessageColor(R.color.color_5A646E)
            .setCloseButtonIcon(R.drawable.ic_close_blue)
            .setPrimaryButton(getString(R.string.entendi))
            .show(supportFragmentManager, null)
    }

    override fun onSucess(response: ResponseBanlanceInquiry) {
        gaSendCallback(Label.SUCESSO)

        try {
            val intent = CancelDetailActivity.newIntent(baseContext, response, elegibility)
            startActivity(intent)
        } catch (exception: Exception) {
            exception.message?.let { error ->
                FirebaseCrashlytics.getInstance().log(error)
            }
        }
        adapter?.viewBinderHelper?.closeLayout(selectedSale?.toString())
        Handler().postDelayed({
            hideLoading()
        }, THOUSAND)
    }

    override fun onError(error: ErrorMessage) {
        gaSendCallback(Label.ERRO)
        hideLoading()
       exceptionGA4(error)

        when {
            (error.logout) -> logout(error)

            (error.errorCode == WITHOUT_BALANCE) -> createDialog(
                R.string.error_cancel_sem_saldo_titulo,
                R.string.error_cancel_sem_saldo_msg,
                R.drawable.img_link_pgto_empty_state
            )
            (error.errorCode == INELEGIBLE_SALE) -> createDialog(
                R.string.error_cancel_error,
                R.string.error_cancel_error_msg,
                R.drawable.img_no_profile_access
            )
            (error.errorCode == NOT_FOUND) -> createDialog(
                R.string.dialog_unable_cancel,
                R.string.text_try_again,
                R.drawable.img_no_profile_access
            )
            else -> createDialog(
                R.string.error_cancel_error,
                R.string.error_cancel_error_msg,
                R.drawable.img_no_profile_access
            )
        }
    }

    private fun gaSendButtonCancelDetail(name: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, CANCELAR_VENDAS_CATEGORY),
                action = listOf(CANCELAR_VENDAS_EVENT),
                label = listOf(Label.BOTAO, name)
            )
        }
    }

    private fun mfaReady() {
        elegibility = true
        showLoading()
        selectedSale?.let { presenterCancel.balanceInquiry(it) }
        delayHideLoading()
    }

    override fun showTokenGenerator() {
        mfaReady()
    }

    override fun onRestart() {
        super.onRestart()
        hideLoading()
    }

    override fun showNotEligible() {
        hideLoading()
        bottomSheetGeneric(
            EMPTY,
            R.drawable.ic_generic_error_image,
            getString(R.string.text_funcionality_dont_free_title),
            getString(R.string.text_funcionality_dont_free_subtitle),
            getString(R.string.text_lgpd_saiba_mais),
            statusBtnClose = false,
            statusBtnOk = true,
            statusViewLine = false,
            isResizeToolbar = true,
        ).apply {
            this.onClick = object : BottomSheetGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnOk(dialog: Dialog) {
                    startHelpCenter()
                    dismiss()
                }
            }
        }.show(supportFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    override fun showMFAStatusPending() {
        hideLoading()
        bottomSheetGeneric(
            EMPTY,
            R.drawable.ic_37,
            getString(R.string.text_mfa_status_pending_title),
            getString(R.string.text_mfa_status_pending_subtitle),
            getString(R.string.ok),
            statusBtnClose = false,
            statusBtnOk = true,
            statusViewLine = false,
            isResizeToolbar = true
        ).apply {
            this.onClick = object : BottomSheetGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnOk(dialog: Dialog) {
                    dismiss()
                }
            }
        }.show(supportFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    override fun showMFAStatusErrorPennyDrop() {
        hideLoading()
        bottomSheetGeneric(
            EMPTY,
            R.drawable.ic_42,
            getString(R.string.text_mfa_status_error_penny_drop_title),
            getString(R.string.text_mfa_status_error_penny_drop_subtitle),
            getString(R.string.text_lgpd_saiba_mais),
            statusBtnClose = false,
            statusBtnOk = true,
            statusViewLine = false,
            isResizeToolbar = true
        ).apply {
            this.onClick = object : BottomSheetGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnOk(dialog: Dialog) {
                    startHelpCenter()
                    dismiss()
                }
            }
        }.show(supportFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    private fun startHelpCenter() {
        startActivity<CentralAjudaSubCategoriasEngineActivity>(
            ConfigurationDef.TAG_KEY_HELP_CENTER to ConfigurationDef.TAG_HELP_CENTER_MFA,
            ARG_PARAM_SUBCATEGORY_NAME to getString(R.string.text_token),
            CentralAjudaSubCategoriasEngineActivity.NOT_CAME_FROM_HELP_CENTER to true
        )
    }

    override fun showMoreFilters(quickFilter: QuickFilter?) {
        quickFilter?.let {
            val filterBottomSheet = MinhasVendasFilterBottomSheetFragment.create(
                it,
                object : MinhasVendasFilterBottomSheetFragment.OnResultListener {
                    override fun onResult(quickFilter: QuickFilter) {
                        this@MinhasVendasTransacoesActivity.quickFilter = quickFilter
                        this@MinhasVendasTransacoesActivity.presenter.refresh(quickFilter)
                    }
                }, isLoadingPaymentTypes = true, isFilterBrandsData = true
            )

            filterBottomSheet
                .show(this.supportFragmentManager, MINHAS_VENDAS_FILTER)

        }
    }

    override fun changeColorFilter(isFilterNotSelected: Boolean) { 
        if (isFilterNotSelected)
            menu?.findItem(R.id.action_filter)
                ?.icon = ContextCompat.getDrawable(
                this,
                R.drawable.ic_filter
            )
        else
            menu?.findItem(R.id.action_filter)
                ?.icon = ContextCompat.getDrawable(
                this,
                R.drawable.ic_filter_filled
            )
    }

    private fun genericErrorMfa(error: ErrorMessage?) {
        genericError(
            error = error,
            onFirstAction = {
                presenterMfa.load()
            },
            onSecondAction = {
                backToHome()
            },
            onSwipeAction = {
                backToHome()
            },
            isErrorMFA = true
        )
       exceptionGA4(error)
    }

    override fun bottomSheetConfiguringMfaDismiss() {
        hideLoading()
    }

    override fun showDifferentDevice() {
        MfaTokenConfigurationBottomSheet.onCreate(
            listener = this, isResend = true
        ).show(this.supportFragmentManager, MINHAS_VENDAS_FILTER)
    }

    override fun showUserWithP2(type: EnrollmentType) {
        MfaTokenConfigurationBottomSheet.onCreate(
            listener = this, type = type.name, isResend = false
        ).show(this.supportFragmentManager, MINHAS_VENDAS_FILTER)
    }

    override fun onErrorResendPennyDrop(error: ErrorMessage?) {
        genericError(
            error = error,
            onFirstAction = {
                presenterMfa.resendPennyDrop()
            },
            onSecondAction = {
                backToHome()
            },
            onSwipeAction = {
                backToHome()
            },
            isErrorMFA = true
        )
       exceptionGA4(error)
    }

    override fun onShowSuccessConfiguringMfa(isShowMessage: Boolean) {
        if (isShowMessage)
            successConfiguringMfa {
                mfaReady()
            }
        else
            mfaReady()
    }

    override fun onErrorConfiguringMfa(error: ErrorMessage?) {
        genericErrorMfa(error)
        exceptionGA4(error)
    }

    override fun showUserNeedToFinishP2(error: ErrorMessage?) {
        finishP2(
            onFirstAction = {
                backToHome()
            },
            onSecondAction = {
                showOnboardingID()
            },
            onSwipeAction = {
                backToHome()
            },
            error
        )
        exceptionGA4(error)
    }

    private fun showOnboardingID() {
        IDOnboardingRouter(
            activity = this,
            showLoadingCallback = {},
            hideLoadingCallback = {}
        ).showOnboarding()
    }

    private fun exceptionGA4(error: ErrorMessage?){
        ga4.logException(SCREEN_NAME_SALES_MADE, errorMessage = error)
    }
}