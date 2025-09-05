package br.com.mobicare.cielo.newRecebaRapido.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.databinding.LayoutCieloFlexTagBinding
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.cielo.libflue.util.extensions.visible
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values
import br.com.mobicare.cielo.commons.constants.Text.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.router.Router
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.commons.utils.dateFormatToBr
import br.com.mobicare.cielo.commons.utils.formatterErrorMessage
import br.com.mobicare.cielo.commons.utils.getNewErrorMessage
import br.com.mobicare.cielo.commons.utils.spannable.htmlTextFormat
import br.com.mobicare.cielo.databinding.FragmentReceiveAutomaticHomeBinding
import br.com.mobicare.cielo.databinding.SelectPeriodComponentBinding
import br.com.mobicare.cielo.extensions.backToHome
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.extensions.toStringAndReplaceDotWithComma
import br.com.mobicare.cielo.main.domain.Menu
import br.com.mobicare.cielo.main.domain.MenuTarget
import br.com.mobicare.cielo.newRecebaRapido.analytics.RAGA4
import br.com.mobicare.cielo.newRecebaRapido.domain.model.CreditOfferItem
import br.com.mobicare.cielo.newRecebaRapido.domain.model.GeneralOfferSummary
import br.com.mobicare.cielo.newRecebaRapido.domain.model.InstallmentOfferItem
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.BOTH
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.CREDIT
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.INSTALLMENT
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.VALIDITY_TYPE_FIXED_DATE
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.VALIDITY_TYPE_MONTHS
import br.com.mobicare.cielo.newRecebaRapido.util.OfferState
import br.com.mobicare.cielo.newRecebaRapido.util.UiStateRAOffers
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef.TAG_HELP_CENTER_RECEBIMENTO_AUTOMATICO
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReceiveAutomaticHomeFragment : BaseFragment(), CieloNavigationListener {

    private var binding: FragmentReceiveAutomaticHomeBinding? = null
    private val viewModel: ReceiveAutomaticHomeViewModel by viewModel()
    private var navigation: CieloNavigation? = null

    private val ga4: RAGA4 by inject()

    private var typeTransactionSelected = BOTH

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentReceiveAutomaticHomeBinding.inflate(
            inflater, container, false
        ).also {
            binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSetupOptions()
        setupObservers()
        setupNavigation()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        ga4.logScreenView(
            ga4.getHomeScreenName(viewModel.isOffer)
        )
    }

    private fun setupObservers() {
        setupGeneralOfferLiveDataObserver()
        setupBothOfferLiveDataObserver()
        setupCreditOfferLiveDataObserver()
        setupInstallmentOfferLiveDataObserver()
    }

    private fun setupGeneralOfferLiveDataObserver() {
        viewModel.receiveAutomaticOffersMutableLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiStateRAOffers.Loading -> loadingReceiveAutomaticOffers()
                is UiStateRAOffers.HideLoading -> hideLoadingReceiveAutomaticOffers()
                is UiStateRAOffers.Success -> successReceiveAutomaticOffers(uiState.data)
                is UiStateRAOffers.Error -> errorReceiveAutomaticOffers(
                    requireContext().formatterErrorMessage(uiState.message),
                    uiState.error
                )

                is UiStateRAOffers.Empty -> emptyReceiveAutomaticOffers()
                is UiStateRAOffers.HiredOfferExists -> hasReceiveAutomaticContracted()
                is UiStateRAOffers.OffersNotFound -> onOffersNotFound()
            }
        }
    }

    private fun setupBothOfferLiveDataObserver() {
        viewModel.bothOfferLiveData.observe(viewLifecycleOwner) { bothOfferState ->
            binding?.bothOptionPlan?.apply {
                when (bothOfferState) {
                    is OfferState.Hide -> this.root.gone()
                    is OfferState.Show -> {
                        setupCredit(this, bothOfferState.offer.first)
                        setupInstallment(this, bothOfferState.offer.second)
                    }
                }
            }
        }
    }

    private fun setupInstallmentOfferLiveDataObserver() {
        viewModel.installmentOfferLiveData.observe(viewLifecycleOwner) { installmentOfferState ->
            binding?.installmentOptionPlan?.apply {
                when (installmentOfferState) {
                    is OfferState.Hide -> this.root.gone()
                    is OfferState.Show -> {
                        setupInstallment(this, installmentOfferState.offer)
                    }
                }
            }
        }
    }

    private fun setupCreditOfferLiveDataObserver() {
        viewModel.creditOfferLiveData.observe(viewLifecycleOwner) { creditOfferState ->
            binding?.cashOptionPlan?.apply {
                when (creditOfferState) {
                    is OfferState.Hide -> this.root.gone()
                    is OfferState.Show -> {
                        setupCredit(this, creditOfferState.offer)
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding?.apply {
            bothOptionPlan.apply {
                containerButtonTax.setOnClickListener {
                    typeTransactionSelected = BOTH
                    navigateToDetails()
                    ga4.logClick(
                        screenName = ga4.getHomeScreenName(viewModel.isOffer),
                        contentComponent = RAGA4.BOTH,
                        contentName = RAGA4.SEE_PLAN_DETAILS
                    )
                }
                ivRadioButtonInfo.setOnClickListener {
                    dialogInfo(
                        getString(R.string.receive_auto_dialog_title_cash_and_installment_sales),
                        getString(R.string.receive_auto_dialog_subTitle_cash_and_installment_sales)
                    )
                }
            }
            cashOptionPlan.apply {
                containerButtonTax.setOnClickListener {
                    ga4.logClick(
                        screenName = ga4.getHomeScreenName(viewModel.isOffer),
                        contentComponent = RAGA4.CASH,
                        contentName = RAGA4.SEE_PLAN_DETAILS
                    )
                    typeTransactionSelected = CREDIT
                    navigateToDetails()
                }
                ivRadioButtonInfo.setOnClickListener {
                    dialogInfo(
                        getString(R.string.receive_auto_dialog_title_cash_sales),
                        getString(R.string.receive_auto_dialog_subTitle_cash_sales)
                    )
                }
            }
            installmentOptionPlan.apply {
                containerButtonTax.setOnClickListener {
                    ga4.logClick(
                        screenName = ga4.getHomeScreenName(viewModel.isOffer),
                        contentComponent = RAGA4.INSTALLMENTS,
                        contentName = RAGA4.SEE_PLAN_DETAILS
                    )
                    typeTransactionSelected = INSTALLMENT
                    navigateToDetails()
                }
                ivRadioButtonInfo.setOnClickListener {
                    dialogInfo(
                        getString(R.string.receive_auto_dialog_title_installment_sales),
                        getString(R.string.receive_auto_dialog_subTitle_installment_sales)
                    )
                }
            }
        }
    }

    private fun navigateToDetails() {
        findNavController().safeNavigate(
            ReceiveAutomaticHomeFragmentDirections.actionReceiveAutomaticHomeFragmentToReceiveAutomaticDetailsFragment(
                typeTransactionSelected,
                viewModel.offers?.toTypedArray() ?: return

            )
        )
    }

    private fun getData() {
        viewModel.getReceiveAutomaticOffers()
    }

    private fun initSetupOptions() {
        binding?.apply {
            cashOptionPlan.apply {
                tvLabelTypeSales.text = getText(R.string.receive_auto_label_only_cash_sales)
                tagPlanAdvantage.gone()
                tvTaxInstallment.gone()
            }
            installmentOptionPlan.apply {
                tvLabelTypeSales.text = getText(R.string.receive_auto_label_only_installment_sales)
                tagPlanAdvantage.gone()
                tvTaxCreditCash.gone()
            }

            LayoutCieloFlexTagBinding.bind(bothOptionPlan.tagPlanAdvantage).tvDescription.setTextAppearance(
                R.style.regular_montserrat_12
            )
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.showButton(false)
            navigation?.configureCollapsingToolbar(
                CollapsingToolbarBaseActivity.Configurator(
                    show = true,
                    isExpanded = false,
                    disableExpandableMode = false,
                    toolbarMenu = CollapsingToolbarBaseActivity.ToolbarMenu(
                        menuRes = R.menu.menu_common_only_faq_blue,
                        onOptionsItemSelected = {
                            if ((it.itemId == R.id.action_help)) {
                                showFaq()
                            }
                        }
                    ),
                    showBackButton = true,
                    toolbarTitle = getString(R.string.receive_auto_title)
                )
            )
        }
    }

    private fun loadingReceiveAutomaticOffers() {
        binding?.apply {
            errorInclude.root.gone()
            setupVisibilityHomeViews(true)
            bothOptionPlan.llContainerShimmer.visible()
            cashOptionPlan.llContainerShimmerTwo.visible()
            installmentOptionPlan.llContainerShimmerTwo.visible()
            containerInfoTaxBox.root.gone()
        }
    }

    private fun hideLoadingReceiveAutomaticOffers() {
        binding?.apply {
            bothOptionPlan.llContainerShimmer.gone()
            cashOptionPlan.llContainerShimmerTwo.gone()
            installmentOptionPlan.llContainerShimmerTwo.gone()
        }
    }

    private fun successReceiveAutomaticOffers(offerSummary: GeneralOfferSummary?) {
        offerSummary?.let {
            binding?.containerInfoTaxBox?.apply {
                tvTaxInfoThree.text =
                    getString(R.string.receive_auto_label_tax_three, it.referenceBrand)
                root.visible()
            }
            setupOffersValidity(it)
            ga4.logScreenView(
                ga4.getHomeScreenName(viewModel.isOffer)
            )
        }
    }

    private fun setupInstallment(
        binding: SelectPeriodComponentBinding,
        installmentOffer: InstallmentOfferItem
    ) {
        binding.tvTaxInstallment.text = getString(
            R.string.receive_auto_tax_installment_sales,
            installmentOffer.summarizedMdr.toStringAndReplaceDotWithComma(),
            installmentOffer.mdr.toStringAndReplaceDotWithComma(),
            installmentOffer.recebaRapidoMdr.toStringAndReplaceDotWithComma()
        ).htmlTextFormat()
    }

    private fun setupCredit(binding: SelectPeriodComponentBinding, creditOffer: CreditOfferItem) {
        binding.tvTaxCreditCash.text = getString(
            R.string.receive_auto_tax_cash_sales,
            creditOffer.summarizedMdr.toStringAndReplaceDotWithComma(),
            creditOffer.mdr.toStringAndReplaceDotWithComma(),
            creditOffer.recebaRapidoMdr.toStringAndReplaceDotWithComma()
        ).htmlTextFormat()
    }

    private fun setupOffersValidity(
        offerSummary: GeneralOfferSummary
    ) {
        val validityPeriod = offerSummary.validityPeriod ?: return
        val validityPeriodType = offerSummary.validityPeriodType ?: return

        binding?.apply {
            setupPlanValidity(bothOptionPlan, validityPeriod, validityPeriodType)
            setupPlanValidity(cashOptionPlan, validityPeriod, validityPeriodType)
            setupPlanValidity(installmentOptionPlan, validityPeriod, validityPeriodType)
        }
    }

    private fun setupPlanValidity(
        componentBinding: SelectPeriodComponentBinding,
        validity: String,
        validityPeriodType: String
    ) {
        componentBinding.apply {
            tvOfferValidity.text =
                when (validityPeriodType) {
                     VALIDITY_TYPE_MONTHS -> {
                        val validityQuantity = validity.toIntOrNull() ?: return
                        getString(
                            R.string.receive_auto_offer_validity_period,
                            validity,
                            resources.getQuantityString(
                                R.plurals.month_plurals,
                                validityQuantity
                            )
                        ).htmlTextFormat()
                    }
                    VALIDITY_TYPE_FIXED_DATE -> {
                        getString(
                            R.string.receive_auto_offer_validity_fixed_date,
                            validity.dateFormatToBr()
                        ).htmlTextFormat()
                    }
                    else -> EMPTY
                }
            tvOfferValidity.visible(tvOfferValidity.text.isNullOrEmpty().not())
        }
    }

    private fun emptyReceiveAutomaticOffers() {
        errorReceiveAutomaticOffers(getString(R.string.receive_auto_error_message))
    }

    private fun errorReceiveAutomaticOffers(message: String, error: NewErrorMessage? = null) {
        setupVisibilityHomeViews(false)
        binding?.apply {
            ga4.logException(
                screenName = RAGA4.SCREEN_VIEW_HOME,
                error = error
            )
            errorInclude.apply {
                root.visible()
                tvSorryMessage.text = message
                btReload.apply {
                    setTextAppearance(R.style.semi_bold_montserrat_16)
                    setOnClickListener {
                        getData()
                    }
                }
            }
        }
    }

    private fun setupVisibilityHomeViews(isActive: Boolean) {
        binding?.apply {
            bothOptionPlan.root.visible(isActive)
            cashOptionPlan.root.visible(isActive)
            installmentOptionPlan.root.visible(isActive)
        }
    }

    private fun hasReceiveAutomaticContracted() {
        setupVisibilityHomeViews(false)
        navigation?.showCustomHandlerView(
            contentImage = R.drawable.img_10_erro,
            title = getString(R.string.receive_auto_home_have_contract_title),
            message = requireContext().getNewErrorMessage(
                newMessage = R.string.receive_auto_home_have_contract_description
            ),
            labelSecondButton = getString(R.string.receive_auto_bt_tax_plans),
            callbackSecondButton = {
                ga4.logClick(
                    screenName = RAGA4.SCREEN_VIEW_HAS_RA,
                    contentComponent = RAGA4.ALREADY_HAS_RA_HIRED,
                    contentName = RAGA4.GO_TO_FEES_AND_PLAN_LABEL
                )
                Router.navigateTo(
                    requireContext(),
                    Menu(
                        Router.APP_ANDROID_RATES, EMPTY,
                        listOf(),
                        getString(R.string.txp_header),
                        false,
                        EMPTY,
                        listOf(),
                        show = false,
                        showItems = false,
                        menuTarget = MenuTarget()
                    )
                ).also {
                    activity?.finishAndRemoveTask()
                }
            },
            isShowButtonClose = true,
            titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
            messageAlignment = View.TEXT_ALIGNMENT_TEXT_START,
            titleStyle = R.style.bold_montserrat_20_brand_600_spacing_8,
            callbackClose = {
                returnHome()
            },
            isShowSecondButton = true,
        ).also {
            ga4.logScreenView(
                RAGA4.SCREEN_VIEW_HAS_RA
            )
            ga4.logDisplayContent(
                RAGA4.SCREEN_VIEW_HAS_RA,
                RAGA4.YOU_ALREADY_HAS_A_PLAN_HIRED,
                GoogleAnalytics4Values.MESSAGE
            )
        }
    }

    private fun onOffersNotFound() {
        setupVisibilityHomeViews(false)
        navigation?.showCustomHandlerView(
            contentImage = R.drawable.img_10_erro,
            title = getString(R.string.auto_receive_offers_not_found_title),
            message = getString(R.string.auto_receive_offers_not_found_message),
            labelSecondButton = getString(R.string.back),
            callbackSecondButton = {
                returnHome()
            },
            isShowButtonClose = true,
            titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
            messageAlignment = View.TEXT_ALIGNMENT_TEXT_START,
            titleStyle = R.style.bold_montserrat_20_brand_600_spacing_8,
            callbackClose = {
                returnHome()
            },
            isShowSecondButton = true,
        )
    }

    private fun dialogInfo(title: String, message: String) {
        val cieloDialog = CieloDialog.create(
            title = title,
            message = message
        )
        cieloDialog
            .closeButtonVisible(true)
            .setPrimaryButton(getString(R.string.bt_close))
            .setTitleTextAppearance(R.style.bold_montserrat_20_cloud_500_spacing_8)
            .setTitleTextAlignment(View.TEXT_ALIGNMENT_CENTER)
            .setMessageTextAlignment(View.TEXT_ALIGNMENT_CENTER)
            .show(
                childFragmentManager,
                ReceiveAutomaticHomeFragment::class.java.simpleName
            )
    }

    private fun showFaq() {
        navigation?.startHelpCenter(TAG_HELP_CENTER_RECEBIMENTO_AUTOMATICO)
    }

    private fun returnHome() {
        requireActivity().backToHome()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}