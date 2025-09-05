package br.com.mobicare.cielo.pixMVVM.presentation.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.allowme.presentation.presenter.AllowMePresenter
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentPixHomeBinding
import br.com.mobicare.cielo.databinding.IncludePixHomeSectionHeaderBinding
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.dialog.PixBankDomicileBottomSheet
import br.com.mobicare.cielo.pixMVVM.presentation.home.PixHomeNavigationFlowActivity
import br.com.mobicare.cielo.pixMVVM.presentation.home.factories.PixTransactionsMenuFactory
import br.com.mobicare.cielo.pixMVVM.presentation.home.ui.sections.PixAccountBalanceViewSection
import br.com.mobicare.cielo.pixMVVM.presentation.home.ui.sections.PixHeaderViewSection
import br.com.mobicare.cielo.pixMVVM.presentation.home.ui.sections.PixMenuViewSection
import br.com.mobicare.cielo.pixMVVM.presentation.home.ui.sections.PixMyKeysViewSection
import br.com.mobicare.cielo.pixMVVM.presentation.home.ui.sections.PixTransactionsViewSection
import br.com.mobicare.cielo.pixMVVM.presentation.home.viewmodel.PixHomeViewModel
import br.com.mobicare.cielo.pixMVVM.utils.bottomSheets.pixAlertNewLayout.PixAlertNewLayoutBottomSheetHandler
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PixHomeFragment : BaseFragment(), CieloNavigationListener, AllowMeContract.View {

    private val viewModel: PixHomeViewModel by viewModel()

    private val allowMePresenter: AllowMePresenter by inject {
        parametersOf(this)
    }

    private val pixAlertNewLayoutBottomSheetHandler: PixAlertNewLayoutBottomSheetHandler by inject()

    private var _binding: FragmentPixHomeBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var _bindingHeader: IncludePixHomeSectionHeaderBinding? = null
    private val bindingHeader get() = requireNotNull(_bindingHeader)

    private var navigation: CieloNavigation? = null

    private val pixAccount by lazy {
        (navigation?.getData() as? PixHomeNavigationFlowActivity.NavArgs.Data)?.pixAccount
    }

    private lateinit var headerViewSection: PixHeaderViewSection
    private lateinit var accountBalanceViewSection: PixAccountBalanceViewSection
    private lateinit var transactionsViewSection: PixTransactionsViewSection
    private lateinit var myKeysViewSection: PixMyKeysViewSection
    private lateinit var menuViewSection: PixMenuViewSection

    private val toolbar get() = CieloCollapsingToolbarLayout.Configurator(
        layoutMode = CieloCollapsingToolbarLayout.LayoutMode.SCROLLABLE,
        toolbar = CieloCollapsingToolbarLayout.Toolbar(
            title = getString(R.string.pix_home_toolbar_title),
            menu = CieloCollapsingToolbarLayout.ToolbarMenu(
                menuRes = R.menu.menu_help,
                onOptionsItemSelected = ::onMenuOptionSelected,
            ),
        ),
        floatingTopSectionView = CieloCollapsingToolbarLayout.FloatingTopSectionView(
            fixedContentView = bindingHeader.root,
        ),
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): ConstraintLayout {
        _bindingHeader = IncludePixHomeSectionHeaderBinding.inflate(inflater, container, false)

        return FragmentPixHomeBinding
            .inflate(inflater, container, false)
            .also {_binding = it }
            .root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        initializeHeaderViewSection()
        initializeAccountBalanceViewSection()
        initializeTransactionViewSection()
        initializeMyKeysViewSection()
        initializeMenuViewSection()
        setupObservers()
        loadData()
        pixAlertNewLayoutBottomSheetHandler.verifyShowBottomSheet(requireActivity())
    }

    override fun onDestroyView() {
        _binding = null
        _bindingHeader = null
        super.onDestroyView()
    }

    private fun setupNavigation() {
        navigation = (requireActivity() as? CieloNavigation)?.also {
            it.setNavigationListener(this)
            it.configureCollapsingToolbar(toolbar)
        }
    }

    private fun onMenuOptionSelected(menuItem: MenuItem) {
        if (menuItem.itemId == R.id.menuActionHelp) {
            requireActivity().openFaq(
                tag = ConfigurationDef.TAG_HELP_CENTER_PIX,
                subCategoryName = getString(R.string.cielo_facilita_central_de_ajuda_pix),
            )
        }
    }

    private fun initializeHeaderViewSection() {
        headerViewSection =
            PixHeaderViewSection(
                fragment = this,
                viewModel = viewModel,
                binding = bindingHeader,
                onBankDomicileTap = if (pixAccount?.isCielo == true) ::onBankDomicileTap else null,
            )
    }

    private fun initializeAccountBalanceViewSection() {
        accountBalanceViewSection =
            PixAccountBalanceViewSection(
                fragment = this,
                viewModel = viewModel,
                binding = binding.includeSectionBalance,
            )
    }

    private fun initializeTransactionViewSection() {
        transactionsViewSection =
            PixTransactionsViewSection(
                fragment = this,
                viewModel = viewModel,
                binding = binding.includeSectionTransactions,
                buttons = PixTransactionsMenuFactory.create(requireContext()),
                onVerifyAllowMe = ::onVerifyAllowMe,
            )
    }

    private fun initializeMyKeysViewSection() {
        myKeysViewSection =
            PixMyKeysViewSection(
                fragment = this,
                viewModel = viewModel,
                binding = binding.includeSectionKeys,
            )
    }

    private fun initializeMenuViewSection() {
        menuViewSection =
            PixMenuViewSection(
                fragment = this,
                viewModel = viewModel,
                binding = binding.includeSectionOptions,
            )
    }

    private fun setupObservers() {
        viewModel.accountBalanceUiState.observe(viewLifecycleOwner) { state ->
            accountBalanceViewSection.handleObservableResult(state)
        }

        viewModel.userDataUiResult.observe(viewLifecycleOwner) { result ->
            headerViewSection.handleObservableResult(result)
        }

        viewModel.masterKeyUiState.observe(viewLifecycleOwner) { state ->
            myKeysViewSection.handleObservableResult(state)
        }
    }

    private fun onBankDomicileTap() {
        pixAccount?.let {
            PixBankDomicileBottomSheet(
                context = requireContext(),
                pixAccount = it
            ).show(requireActivity().supportFragmentManager)
        }
    }

    private fun loadData() {
        viewModel.run {
            loadAccountBalance()
            loadUserData()
            loadMasterKey()
        }
    }

    private fun onVerifyAllowMe() {
        allowMePresenter.collect(
            mAllowMeContextual = allowMePresenter.init(requireContext()),
            requireActivity(),
            mandatory = true,
        )
    }

    override fun successCollectToken(result: String) {
        transactionsViewSection.onSuccessCollectToken()
    }

    override fun errorCollectToken(
        result: String?,
        errorMessage: String,
        mandatory: Boolean,
    ) {
        transactionsViewSection.onErrorCollectToken(errorMessage)
    }

    override fun getSupportFragmentManagerInstance() = childFragmentManager

}
