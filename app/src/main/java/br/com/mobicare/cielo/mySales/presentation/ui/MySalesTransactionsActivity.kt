package br.com.mobicare.cielo.mySales.presentation.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias.CentralAjudaSubCategoriasEngineActivity
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_SUBCATEGORY_NAME
import br.com.mobicare.cielo.commons.constants.THOUSAND
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.enums.EnrollmentType
import br.com.mobicare.cielo.commons.ui.widget.MfaBaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.AccessibilityUtils
import br.com.mobicare.cielo.commons.utils.bottomSheetGeneric
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.LayoutMinhasVendasTransacoesActivityBinding
import br.com.mobicare.cielo.extensions.backToHome
import br.com.mobicare.cielo.extensions.finishP2
import br.com.mobicare.cielo.extensions.genericError
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.successConfiguringMfa
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.idOnboarding.router.IDOnboardingRouter
import br.com.mobicare.cielo.mfa.router.MfaRouteHandler
import br.com.mobicare.cielo.mfa.router.userWithP2.MfaTokenConfigurationBottomSheet
import br.com.mobicare.cielo.minhasVendas.activities.CANCELAR_VENDAS_BUTTON_NAME
import br.com.mobicare.cielo.minhasVendas.activities.CANCELAR_VENDAS_CATEGORY
import br.com.mobicare.cielo.minhasVendas.activities.CANCELAR_VENDAS_EVENT
import br.com.mobicare.cielo.minhasVendas.activities.CancelDetailActivity
import br.com.mobicare.cielo.mySales.analytics.MySalesGA4
import br.com.mobicare.cielo.minhasVendas.constants.INELEGIBLE_SALE
import br.com.mobicare.cielo.minhasVendas.constants.NOT_FOUND
import br.com.mobicare.cielo.minhasVendas.constants.WITHOUT_BALANCE
import br.com.mobicare.cielo.mySales.data.model.Sale
import br.com.mobicare.cielo.mySales.data.model.Summary
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.CancelamentoPresenter
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.OnCancelamentoContract
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.ResponseBanlanceInquiry
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.minhasVendas.fragments.common.ScrollControlledLinearManager
import br.com.mobicare.cielo.mySales.analytics.MySalesGA4.Companion.SCREEN_NAME_SALES_MADE
import br.com.mobicare.cielo.mySales.data.model.bo.SummarySalesBO
import br.com.mobicare.cielo.mySales.presentation.ui.adapter.recyclerViewOnScrollListener
import br.com.mobicare.cielo.mySales.presentation.ui.adapter.transactionSales.TransactionSaleAdapter
import br.com.mobicare.cielo.mySales.presentation.ui.adapter.transactionSales.TransactionSalesCancelAdapter
import br.com.mobicare.cielo.mySales.presentation.viewmodel.MySalesTransactionsViewModel
import br.com.mobicare.cielo.mySales.presentation.utils.IS_SALE_TODAY_ARGS
import br.com.mobicare.cielo.mySales.presentation.utils.MINHAS_VENDAS_FILTER
import br.com.mobicare.cielo.mySales.presentation.utils.MySalesViewState
import br.com.mobicare.cielo.mySales.presentation.utils.SalesStatementStatus
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy.dialog.BottomSheetGenericFragment
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat


//TODO [MYSALES] - IMPORTANTE
// Esta tela conta com cancelamento de vendas, porem por conta do escopo da migracao, a parte de cancelamento ficara
// como debito tecnico. Futuramente e necessario verificar o codigo antigo (MinhasVendasTransacoesActivity)
// e aplicar as acoes relacionadas ao cancelamento de vendas. Tambem e necessario verificar a recyclerview por
// conta do cancelamento.


class MySalesTransactionsActivity: MfaBaseLoggedActivity(), OnCancelamentoContract.View {

    private val ARG_PARAM_QUICK_FILTER = "ARG_PARAM_QUICK_FILTER"

    private var binding: LayoutMinhasVendasTransacoesActivityBinding? = null

    private val viewModel: MySalesTransactionsViewModel by viewModel()
    private var scrollControlledLinearManager: ScrollControlledLinearManager? = null
    private lateinit var quickFilter: QuickFilter
    private lateinit var cancelAdapter: TransactionSalesCancelAdapter
    private lateinit var adapter: TransactionSaleAdapter
    private var menu: Menu? = null
    private val ga4: MySalesGA4 by inject()


    private val presenterCancel: CancelamentoPresenter by inject {
        parametersOf(this)
    }

    private var elegibility: Boolean = false
    private var selectedSale: Sale? = null

    private val mfaRouteHandler: MfaRouteHandler by inject {
        parametersOf(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutMinhasVendasTransacoesActivityBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        getIntentExtras()
        configureSwipeRefreshLayout()
        setupErrorViewRefreshListener()
        configureToolbar()
        setSalesDayDateBarText()
        setupRecyclerView()

    }

    override fun onResume() {
        super.onResume()
        handleMySalesTransactionsAPIService()
        presenterMfa.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenterMfa.onPause()
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


    private fun getIntentExtras() {
        this.intent?.extras?.let { bundle ->
            bundle.getSerializable(ARG_PARAM_QUICK_FILTER)?.let { serializable ->
                (serializable as QuickFilter).let {
                    this.quickFilter = it
                    viewModel.getMySalesTransactions(quickFilter)
                }
            }
        }
        setupMfaRouterHandler()
    }

    private fun handleMySalesTransactionsAPIService() {
        viewModel.getSalesTransactionViewState.observe(this) { state ->
            when(state) {
                is MySalesViewState.SUCCESS -> configureSuccessView(state.data)
                is MySalesViewState.SUCCESS_PAGINATION -> showMoreSales(state.data)
                is MySalesViewState.EMPTY -> showEmptySales()
                is MySalesViewState.LOADING -> showLoading()
                is MySalesViewState.LOADING_MORE -> showLoadingMoreSales()
                is MySalesViewState.ERROR -> showError(state.newErrorMessage)
            }
        }
    }

    //region - funcoes para atualizar / configura views

    @SuppressLint("LogNotTimber")
    private fun setupRecyclerView() {
        scrollControlledLinearManager =
            ScrollControlledLinearManager(this@MySalesTransactionsActivity)

        binding?.apply {
            recyclerView.layoutManager = scrollControlledLinearManager
            recyclerView.itemAnimator = null

            recyclerViewOnScrollListener(
                recyclerView = recyclerView,
                linearLayoutManager = scrollControlledLinearManager,
                isLastPageListener = { viewModel.isLastPage ?: false },
                isLoadingListener = { viewModel.isLoadingMorePagingData },
                loadMoreItems = { viewModel.loadMoreSalesTransactions() }
            )
        }

        if(viewModel.cancelSaleFeatureToggle){
            cancelAdapter = TransactionSalesCancelAdapter(
                clickListener = {
                    goToDetailsScreen(it)
                },
                cancelClickListener = {
                    gaSendButtonCancelDetail(CANCELAR_VENDAS_BUTTON_NAME)
                    ga4.beginCancel(SCREEN_NAME_SALES_MADE, Label.SWIPE)
                    showLoading()
                    selectedSale = it
                    cancelSale()
                }
            )
            binding?.recyclerView?.adapter = cancelAdapter
        }
        else {
            adapter = TransactionSaleAdapter {
                goToDetailsScreen(it)
            }
            binding?.recyclerView?.adapter = adapter
        }

    }

    private fun configureSwipeRefreshLayout() {
        binding?.apply {
            swipeRefreshLayout.setOnRefreshListener {
                scrollControlledLinearManager?.setIsCanScroll(false)
                viewModel.refresh()
            }
        }
    }


    private fun setupErrorViewRefreshListener() {
        binding?.apply {
            errorLayout.apply {
                buttonErrorTry.setOnClickListener {
                    viewModel.retry()
                }
            }
        }
    }


    private fun configureSuccessView(summarySalesBO: SummarySalesBO?) {
        summarySalesBO?.let {
            hideRefreshLoading()
            hideLoading()
            hideEmptySales()

            scrollControlledLinearManager?.setIsCanScroll(true)
            showSummary(it.summary)

            if(viewModel.cancelSaleFeatureToggle)
                cancelAdapter.setSales(it.items.toMutableList())
            else
                adapter.setSales(it.items.toMutableList())

        }
    }


    private fun showMoreSales(summarySalesBO: SummarySalesBO?) {
        summarySalesBO?.let {
            val sales = it.items
            binding?.apply {
                salesSummaryFooter.root.visible()
                waitingAddMoreItensLayout.gone()
                if(sales.isEmpty().not()){
                    recyclerView.stopScroll()
                    if(viewModel.cancelSaleFeatureToggle)
                        cancelAdapter.addSales(sales)
                    else
                        adapter.addSales(sales)
                    scrollControlledLinearManager?.setIsCanScroll(true)
                }
            }
        }
    }

    private fun showEmptySales() {
        binding?.apply {
            recyclerView.recycledViewPool.clear()
            swipeRefreshLayout.gone()
            emptyResultHandler.configureButtonVisible(false)
            emptyResultHandler.visible()
        }
    }

    private fun hideEmptySales(){
        binding?.apply {
            swipeRefreshLayout.visible()
            emptyResultHandler.configureButtonVisible(false)
            emptyResultHandler.gone()
        }
    }


    private fun showLoading() {
        binding?.apply {
            salesSummaryFooter.root.gone()
            waitingAddMoreItensLayout.gone()
            errorLayout.root.gone()
            waitingLayout.visible()
            recyclerView.gone()
        }
    }

    private fun showLoadingMoreSales() {
        binding?.apply {
            waitingAddMoreItensLayout.visible()
            salesSummaryFooter.root.gone()
        }
    }


    private fun hideLoading() {
        binding?.apply {
            waitingLayout.gone()
            salesSummaryFooter.root.gone()
            waitingAddMoreItensLayout.gone()
            recyclerView.visible()
        }
    }

    private fun showError(errorMessage: NewErrorMessage?){
        exceptionGA4(newErrorMessage = errorMessage)
        binding?.apply {
            swipeRefreshLayout.gone()
            recyclerView.gone()
            waitingLayout.gone()
            errorLayout.apply{
                root.visible()
                containerError.visible()
                errorLayout.textViewErrorMsg.text = errorMessage?.message
            }
        }
    }


    private fun hideRefreshLoading() {
        binding?.swipeRefreshLayout?.isRefreshing = false
        viewModel.isRefreshing = false
    }

    private fun showSummary(summary: Summary){
        val totalQuantity = summary.totalQuantity ?: ZERO
        val totalAmount = summary.totalAmount ?: ZERO_DOUBLE
        val totalNetAmount = summary.totalNetAmount ?: ZERO_DOUBLE
        val isShowFooter = totalAmount > ZERO_DOUBLE
        val isShowNetAmount = totalNetAmount > ZERO_DOUBLE

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

    private fun setSalesDayDateBarText() {
        quickFilter.initialDate?.let {
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val sdfPtBr = SimpleDateFormat("dd/MM/yyyy")
            val dateFormatted = sdfPtBr.format(sdf.parse(it))
            binding?.textYourDaySales?.text = getString(R.string.text_your_day_sales, dateFormatted)

        }
    }

    //endregion

    //region - funcoes para configurar a toolbar

    private fun configureToolbar() {
        binding?.toolbar?.toolbarMain?.let { setupToolbar(it,toolbarTitle = getString(R.string.ga_extrato)) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_common_filter_faq, menu)
        this.menu = menu
        setupVisibleItemMenuHelp()
        changeColorFilter(true)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_filter -> {
                showSalesFilters()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupVisibleItemMenuHelp() {
        this.menu?.findItem(R.id.action_help_extrato)?.isVisible = false
    }

    private fun changeColorFilter(isFilterNotSelected: Boolean) {
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

    //endregion

    //region - metodos para navegacao e chamada de telas

    private fun showSalesFilters() {
        val filterBottomSheet = MySalesFiltersBottomSheetFragment.newInstance(
            quickFilter = this.quickFilter,
            listener = object : MySalesFiltersBottomSheetFragment.OnResultListener {
                override fun onResult(quickFilter: QuickFilter) {
                    this@MySalesTransactionsActivity.quickFilter = quickFilter
                    changeColorFilter(viewModel.isFilterNotSelected(quickFilter))
                    this@MySalesTransactionsActivity.viewModel.refresh(quickFilter)
                }

            }, isLoadingPaymentTypes = true, isFilterBrandsData = true
        )
        filterBottomSheet.show(this.supportFragmentManager, MINHAS_VENDAS_FILTER)
    }

    private fun goToDetailsScreen(selectedSale: Sale) {
        selectedSale.statusCode = SalesStatementStatus.checkSaleStatus(selectedSale.status)
        val intent = MySaleDetailsActivity.newInstance(this, selectedSale)
        intent.putExtra(IS_SALE_TODAY_ARGS, false)
        startActivity(intent)
    }

    //endregion


    //region - chamada do fluxo de token e cancelamento
    private fun gaSendButtonCancelDetail(name: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, CANCELAR_VENDAS_CATEGORY),
                action = listOf(CANCELAR_VENDAS_EVENT),
                label = listOf(Label.BOTAO, name)
            )
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
        cancelAdapter.viewBinderHelper.closeLayout(selectedSale?.toString())
        Handler().postDelayed({
            hideLoading()
        }, THOUSAND)
    }

    override fun onError(error: ErrorMessage) {
        gaSendCallback(Label.ERRO)
        hideLoading()
        exceptionGA4(errorMessage = error)
        when {
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

    private fun mfaReady() {
        elegibility = true
        showLoading()
        selectedSale?.let { presenterCancel.balanceInquiry(it) }
        delayHideLoading()
    }

    override fun showTokenGenerator() {
        mfaReady()
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


    private fun startHelpCenter() {
        startActivity<CentralAjudaSubCategoriasEngineActivity>(
            ConfigurationDef.TAG_KEY_HELP_CENTER to ConfigurationDef.TAG_HELP_CENTER_MFA,
            ARG_PARAM_SUBCATEGORY_NAME to getString(R.string.text_token),
            CentralAjudaSubCategoriasEngineActivity.NOT_CAME_FROM_HELP_CENTER to true
        )
    }


    override fun onErrorConfiguringMfa(error: ErrorMessage?) {
        genericErrorMfa(error)
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
    }

    private fun showOnboardingID() {
        IDOnboardingRouter(
            activity = this,
            showLoadingCallback = {},
            hideLoadingCallback = {}
        ).showOnboarding()
    }


    override fun onShowSuccessConfiguringMfa(isShowMessage: Boolean) {
        if (isShowMessage)
            successConfiguringMfa {
                mfaReady()
            }
        else
            mfaReady()
    }

    override fun bottomSheetConfiguringMfaDismiss() {
        hideLoading()
    }

    override fun showDifferentDevice() {
        MfaTokenConfigurationBottomSheet.onCreate(
            listener = this, isResend = true
        ).show(this.supportFragmentManager,
            br.com.mobicare.cielo.minhasVendas.fragments.trasacoes.MINHAS_VENDAS_FILTER
        )
    }

    override fun showUserWithP2(type: EnrollmentType) {
        MfaTokenConfigurationBottomSheet.onCreate(
            listener = this, type = type.name, isResend = false
        ).show(this.supportFragmentManager,
            br.com.mobicare.cielo.minhasVendas.fragments.trasacoes.MINHAS_VENDAS_FILTER
        )
    }

    private fun cancelSale(){
        mfaRouteHandler.runWithMfaToken{
            elegibility = true
            showLoading()
            selectedSale?.let { presenterCancel.balanceInquiry(it) }
        }
    }

    private fun setupMfaRouterHandler() {
        mfaRouteHandler.showLoadingCallback = { show ->
            if (show)
                showLoading()
            else
                hideLoading()
        }
    }
    //endregion

    private fun exceptionGA4(newErrorMessage: NewErrorMessage? = null, errorMessage: ErrorMessage? = null
    ) {
        ga4.logException(SCREEN_NAME_SALES_MADE, newErrorMessage = newErrorMessage, errorMessage = errorMessage)
    }
}