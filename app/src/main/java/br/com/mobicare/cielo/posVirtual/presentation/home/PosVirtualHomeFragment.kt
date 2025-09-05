package br.com.mobicare.cielo.posVirtual.presentation.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.cardbutton.CieloCardButton
import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.extensions.isGone
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.router.Router
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.databinding.FragmentPosVirtualHomeBinding
import br.com.mobicare.cielo.extensions.moveToHome
import br.com.mobicare.cielo.extensions.showToast
import br.com.mobicare.cielo.main.domain.Menu
import br.com.mobicare.cielo.main.domain.MenuTarget
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.FluxoNavegacaoSuperlinkActivity
import br.com.mobicare.cielo.posVirtual.analytics.PosVirtualAF
import br.com.mobicare.cielo.posVirtual.analytics.PosVirtualAnalytics
import br.com.mobicare.cielo.posVirtual.domain.model.PosVirtualProduct
import br.com.mobicare.cielo.posVirtual.presentation.home.enum.PosVirtualProductUiContent
import br.com.mobicare.cielo.posVirtual.presentation.home.utils.PosVirtualProductClickAction
import br.com.mobicare.cielo.posVirtual.presentation.home.views.PosMenuItemViewFactory
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import br.com.mobicare.cielo.superlink.utils.SuperLinkNavStartRouter
import br.com.mobicare.cielo.tapOnPhone.presentation.TapOnPhoneNavigationFlowActivity
import br.com.mobicare.cielo.tapOnPhone.utils.TapOnPhoneConstants.TAP_ON_PHONE_ACTIVITY_WAS_OPENED_BY_POS_VIRTUAL_ARGS
import br.com.mobicare.cielo.tapOnPhone.utils.TapOnPhoneConstants.TAP_ON_PHONE_HAS_CARD_READER_ARGS
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PosVirtualHomeFragment :
    BaseFragment(),
    CieloNavigationListener {
    private var binding: FragmentPosVirtualHomeBinding? = null
    private var navigation: CieloNavigation? = null

    private val viewModel: PosVirtualHomeViewModel by viewModel()
    private val args: PosVirtualHomeFragmentArgs by navArgs()

    private val products: Array<PosVirtualProduct> by lazy { args.posvirtualproducts }
    private val merchantId: String by lazy { args.merchantid }

    private val ga4: PosVirtualAnalytics by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setProductList(products.toList())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentPosVirtualHomeBinding
        .inflate(
            inflater,
            container,
            false,
        ).also { binding = it }
        .root

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupEnabledProductsObserver()
        setupNotEnabledProductsObserver()
        setupListeners()

        viewModel.buildMenuItems()
    }

    override fun onResume() {
        super.onResume()

        setupNavigation()
        logScreenView()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onBackButtonClicked(): Boolean {
        requireActivity().moveToHome()
        return super.onBackButtonClicked()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.apply {
                setNavigationListener(this@PosVirtualHomeFragment)
                showButton(false)
                configureCollapsingToolbar(
                    CollapsingToolbarBaseActivity.Configurator(
                        toolbarTitle = getString(R.string.txt_title_pos_virtual_home),
                        toolbarMenu =
                            CollapsingToolbarBaseActivity.ToolbarMenu(
                                menuRes = R.menu.menu_help,
                                onOptionsItemSelected = {
                                    requireActivity().openFaq(
                                        tag = ConfigurationDef.TAG_HELP_CENTER_POS_VIRTUAL,
                                        subCategoryName = getString(R.string.pos_virtual),
                                    )
                                },
                            ),
                    ),
                )
            }
        }
    }

    private fun setupEnabledProductsObserver() {
        viewModel.enabledProductsLiveData.observe(viewLifecycleOwner) { enabledProducts ->
            with(PosMenuItemViewFactory(requireContext())) {
                addMenuItemsEnabledToView(
                    enabledProducts.mapNotNull { create(it, ::onItemClick) },
                )
            }
        }
    }

    private fun setupNotEnabledProductsObserver() {
        viewModel.notEnabledProductsLiveData.observe(viewLifecycleOwner) { notEnabledProducts ->
            with(PosMenuItemViewFactory(requireContext())) {
                addMenuItemsPendingToView(
                    notEnabledProducts.mapNotNull { create(it, ::onItemClick) },
                )
            }
        }
    }

    private fun setupListeners() {
        binding?.btnSeeRatesAndPlans?.setOnClickListener(::navigateToRatesAndPlans)
    }

    private fun onItemClick(product: PosVirtualProduct) {
        val name =
            PosVirtualProductUiContent.find(product.id)?.title?.let { getString(it) } ?: EMPTY
        val status = product.status?.label?.let { getString(it) } ?: EMPTY
        logClickItem(name, status)

        viewModel.routeAction(product) { action ->
            when (action) {
                is PosVirtualProductClickAction.Enabled -> handleEnabledAction(action)
                is PosVirtualProductClickAction.RequestDetails -> navigateToRequestDetails(action.product)
                else -> context?.showToast(getString(R.string.generic_error_title))
            }
        }
    }

    private fun handleEnabledAction(action: PosVirtualProductClickAction) {
        when (action) {
            is PosVirtualProductClickAction.Pix -> onQrCodePixItemClick(action.logicalNumber)
            is PosVirtualProductClickAction.SuperLink -> onSuperLinkItemClick()
            is PosVirtualProductClickAction.TapOnPhone -> onTapOnPhoneItemClick(action.hasCardReader)
            else -> context?.showToast(getString(R.string.pos_virtual_home_unavailable_option))
        }
    }

    private fun navigateToRequestDetails(product: PosVirtualProduct) {
        findNavController().navigate(
            PosVirtualHomeFragmentDirections
                .actionPosVirtualHomeFragmentToPosVirtualRequestDetailsFragment(product, merchantId),
        )
    }

    private fun onTapOnPhoneItemClick(hasCardReader: Boolean) {
        val intent = Intent(requireContext(), TapOnPhoneNavigationFlowActivity::class.java)
        intent.putExtra(TAP_ON_PHONE_HAS_CARD_READER_ARGS, hasCardReader)
        intent.putExtra(TAP_ON_PHONE_ACTIVITY_WAS_OPENED_BY_POS_VIRTUAL_ARGS, true)
        startActivity(intent)
    }

    private fun onQrCodePixItemClick(logicalNumber: String?) {
        logicalNumber?.let {
            findNavController().navigate(
                PosVirtualHomeFragmentDirections
                    .actionPosVirtualHomeFragmentToPosVirtualInsertAmountQRCodePix(it),
            )
        } ?: context?.showToast(getString(R.string.pos_virtual_home_logic_number_not_found))
    }

    private fun onSuperLinkItemClick() {
        Intent(requireContext(), FluxoNavegacaoSuperlinkActivity::class.java).let {
            it.putExtra(
                SuperLinkNavStartRouter.FlowStartArg.KEY,
                SuperLinkNavStartRouter.FlowStartArg.POS_VIRTUAL.name,
            )
            startActivity(it)
        }
    }

    private fun addMenuItemsEnabledToView(items: List<CieloCardButton>) {
        binding?.sectionEnabled?.apply {
            if (items.isNotEmpty()) {
                items.forEach { addView(it) }
                visible()
            }
        }
    }

    private fun addMenuItemsPendingToView(items: List<CieloCardButton>) {
        binding?.apply {
            if (items.isNotEmpty()) {
                items.forEach { containerItemsPending.addView(it) }
                sectionPending.visible()
                if (sectionEnabled.isGone()) {
                    dividerPending.gone()
                    tvEnablementPending.gone()
                }
            }
        }
    }

    private fun navigateToRatesAndPlans(view: View) {
        Router.navigateTo(
            requireContext(),
            Menu(
                code = Router.APP_ANDROID_RATES,
                icon = EMPTY,
                items = listOf(),
                name = getString(R.string.txp_header),
                showIcons = false,
                shortIcon = EMPTY,
                privileges = listOf(),
                show = false,
                showItems = false,
                menuTarget = MenuTarget(),
            ),
        )
    }

    private fun logScreenView() {
        ga4.logScreenView(PosVirtualAnalytics.SCREEN_VIEW_HOME)
        PosVirtualAF.logHomeScreenView()
    }

    private fun logClickItem(
        productName: String,
        productStatus: String,
    ) = ga4.logSelectContentHome(productName, productStatus)
}
