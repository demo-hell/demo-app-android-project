package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentPixHomeExtractBinding
import br.com.mobicare.cielo.databinding.LayoutPixExtractTabsBinding
import br.com.mobicare.cielo.pixMVVM.domain.enums.ProfileType
import br.com.mobicare.cielo.pixMVVM.presentation.account.PixAccountNavigationFlowActivity
import br.com.mobicare.cielo.pixMVVM.presentation.extract.PixNewExtractNavigationFlowActivity
import br.com.mobicare.cielo.pixMVVM.presentation.extract.PixNewExtractNavigationFlowActivity.NavArgs.SCHEDULED_PIX_WAS_CANCELED
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.listener.PixHomeExtractListener
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.dialog.PixBankDomicileBottomSheet
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.dialog.PixScheduledSettlementBottomSheet
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.pager.adapter.PixExtractPagerAdapter
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.views.collapsable.PixAutomaticTransferCollapsableViewSection
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.views.collapsable.PixExtractCollapsableViewBase
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.views.collapsable.PixFreeMovementCollapsableViewSection
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.views.collapsable.PixScheduledSettlementCollapsableViewSection
import br.com.mobicare.cielo.pixMVVM.utils.bottomSheets.pixAlertNewLayout.PixAlertNewLayoutBottomSheetHandler
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import com.google.android.material.tabs.TabLayoutMediator
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PixHomeExtractFragment :
    BaseFragment(),
    CieloNavigationListener,
    PixHomeExtractListener {
    private val viewModel: PixHomeExtractViewModel by viewModel()

    private var binding: FragmentPixHomeExtractBinding? = null
    private var tabsBinding: LayoutPixExtractTabsBinding? = null

    private val data: PixNewExtractNavigationFlowActivity.NavArgs.Data by lazy {
        (requireActivity() as PixNewExtractNavigationFlowActivity).data
    }

    private val tabTitles =
        listOf(
            R.string.pix_extract_tab_all,
            R.string.pix_extract_tab_returns,
            R.string.pix_extract_tab_scheduled,
        )

    private var pagerAdapter: PixExtractPagerAdapter? = null

    private var navigation: CieloNavigation? = null

    private val pixAlertNewLayoutBottomSheetHandler: PixAlertNewLayoutBottomSheetHandler by inject()

    private var collapsableViewSection: PixExtractCollapsableViewBase? = null

    private val isFreeMovementType
        get() = data.profileType == ProfileType.FREE_MOVEMENT

    private val isAutomaticTransferType
        get() = data.profileType == ProfileType.AUTOMATIC_TRANSFER

    private val isScheduledSettlementEnabled
        get() = data.settlementScheduled?.isEnabled == true

    private val scheduledPixWasCanceled
        get() = navigation?.getSavedData()?.getBoolean(SCHEDULED_PIX_WAS_CANCELED) ?: false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        tabsBinding = LayoutPixExtractTabsBinding.inflate(inflater, container, false)

        return FragmentPixHomeExtractBinding
            .inflate(inflater, container, false)
            .also {
                binding = it
            }.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializeCollapsableViewSection()
        setupViewPager()
        setupObserver()
        viewModel.checkShowNewPixSchedulingExtract()
        pixAlertNewLayoutBottomSheetHandler.verifyShowBottomSheet(requireActivity())
    }

    override fun onResume() {
        super.onResume()
        setupNavigation()
        checkIfScheduledPixWasCanceled()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        tabsBinding = null
        collapsableViewSection?.onDestroyView()
    }

    override fun onBackButtonClicked() = isFreeMovementType.not()

    override fun onLoadAccountBalance() {
        collapsableViewSection?.loadAccountBalance()
    }

    override fun getCurrentTab() = tabsBinding?.tabLayout?.selectedTabPosition ?: ZERO

    override fun resetLayoutToolbar() {
        navigation?.configureCollapsingToolbar(generateCollapsingToolbar())
        collapsableViewSection?.refreshView()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation =
                (requireActivity() as CieloNavigation).also {
                    it.configureCollapsingToolbar(generateCollapsingToolbar())
                }
        }
    }

    private fun generateCollapsingToolbar() =
        CieloCollapsingToolbarLayout.Configurator(
            layoutMode = CieloCollapsingToolbarLayout.LayoutMode.NOT_SCROLLABLE,
            toolbar =
                CieloCollapsingToolbarLayout.Toolbar(
                    title =
                        getString(
                            if (isFreeMovementType) {
                                R.string.pix_extract_title_toolbar
                            } else {
                                R.string.pix_home_toolbar_title
                            },
                        ),
                    menu =
                        CieloCollapsingToolbarLayout.ToolbarMenu(
                            menuRes = R.menu.menu_help,
                            onOptionsItemSelected = ::onMenuOptionSelected,
                        ),
                ),
            floatingTopSectionView =
                CieloCollapsingToolbarLayout.FloatingTopSectionView(
                    collapsableContentView = collapsableViewSection?.view,
                    fixedContentView = tabsBinding?.root,
                ),
        )

    private fun onMenuOptionSelected(menuItem: MenuItem) {
        if (menuItem.itemId == R.id.menuActionHelp) {
            requireActivity().openFaq(
                tag = ConfigurationDef.TAG_HELP_CENTER_PIX,
                subCategoryName = getString(R.string.cielo_facilita_central_de_ajuda_pix),
            )
        }
    }

    // TODO: REMOVER APÓS ESTABELECER O NOVO EXTRATO PARA AGENDADOS, POIS SERÁ ADICIONADO DIRETAMENTE NA LISTA O NOVO FRAGMENT
    private fun setupObserver() {
        viewModel.isShowNewPixSchedulingExtract.observe(viewLifecycleOwner) { isShowNewPixSchedulingExtract ->
            if (isShowNewPixSchedulingExtract) {
                pagerAdapter?.changeSchedulingPageToNewScheduling()
            }
        }
    }

    private fun initializeCollapsableViewSection() {
        collapsableViewSection =
            when {
                isScheduledSettlementEnabled ->
                    PixScheduledSettlementCollapsableViewSection(
                        fragment = this,
                        viewModel = viewModel,
                        onAccountManagementTap = ::onAccountManagementTap,
                        onBankDomicileTap = if (data.pixAccount?.isCielo == true) ::onBankDomicileTap else null,
                        onScheduledTransferTap = ::onScheduledTransferTap,
                    )
                isAutomaticTransferType ->
                    PixAutomaticTransferCollapsableViewSection(
                        fragment = this,
                        viewModel = viewModel,
                        onAccountManagementTap = ::onAccountManagementTap,
                        onBankDomicileTap = if (data.pixAccount?.isCielo == true) ::onBankDomicileTap else null,
                    )
                else -> PixFreeMovementCollapsableViewSection(this, viewModel)
            }
    }

    private fun onAccountManagementTap() {
        requireActivity().startActivity<PixAccountNavigationFlowActivity>()
    }

    private fun onBankDomicileTap() {
        data.pixAccount?.let {
            PixBankDomicileBottomSheet(
                context = requireContext(),
                pixAccount = it,
            ).show(requireActivity().supportFragmentManager)
        }
    }

    private fun onScheduledTransferTap() {
        data.settlementScheduled?.list?.let {
            PixScheduledSettlementBottomSheet(
                context = requireContext(),
                hours = it,
            ).show(requireActivity().supportFragmentManager)
        }
    }

    private fun setupViewPager() {
        binding?.viewPager?.apply {
            pagerAdapter =
                PixExtractPagerAdapter(
                    fragmentManager = requireActivity().supportFragmentManager,
                    lifecycle = lifecycle,
                    pixHomeExtractListener = this@PixHomeExtractFragment,
                )

            adapter = pagerAdapter

            tabsBinding?.tabLayout?.let {
                TabLayoutMediator(it, this) { tab, position ->
                    tab.text = getString(tabTitles[position])
                    currentItem = position
                }.attach()
            }

            currentItem = ZERO
        }
    }

    private fun checkIfScheduledPixWasCanceled() {
        if (scheduledPixWasCanceled) {
            pagerAdapter?.reloadExtractPage(getCurrentTab())
            navigation?.apply {
                showContent()
                clearData()
            }
        }
    }
}
