package br.com.mobicare.cielo.newRecebaRapido.presentation.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import br.com.cielo.libflue.bottomsheet.CieloContentBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.helpers.DateTimeHelper
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.EMPTY_VALUE
import br.com.mobicare.cielo.commons.utils.dateFormatToBr
import br.com.mobicare.cielo.commons.utils.formatterErrorMessage
import br.com.mobicare.cielo.commons.utils.spannable.htmlTextFormat
import br.com.mobicare.cielo.databinding.FragmentReceiveAutomaticConfirmationBinding
import br.com.mobicare.cielo.databinding.LayoutBsAutomaticReceiveFeesBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.extensions.toStringAndReplaceDotWithComma
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.newRecebaRapido.analytics.RAGA4
import br.com.mobicare.cielo.newRecebaRapido.domain.model.CreditOfferItem
import br.com.mobicare.cielo.newRecebaRapido.domain.model.InstallmentOfferItem
import br.com.mobicare.cielo.newRecebaRapido.domain.model.OfferSummary
import br.com.mobicare.cielo.newRecebaRapido.domain.model.mapToOfferSummary
import br.com.mobicare.cielo.newRecebaRapido.presentation.details.adapter.ReceiveAutomaticOfferFeesAdapter
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.BOTH
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.CREDIT
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.DAILY
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.INSTALLMENT
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.MONTHLY
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.WEEKLY
import br.com.mobicare.cielo.newRecebaRapido.util.OfferState
import br.com.mobicare.cielo.newRecebaRapido.util.OfferValidityState
import br.com.mobicare.cielo.newRecebaRapido.util.PlanState
import br.com.mobicare.cielo.newRecebaRapido.util.UiStateRAOContract
import br.com.mobicare.cielo.newRecebaRapido.util.UiStateRAODetailsOffers
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReceiveAutomaticConfirmationFragment : BaseFragment(), CieloNavigationListener {

    private var binding: FragmentReceiveAutomaticConfirmationBinding? = null
    private val viewModel: ReceiveAutomaticConfirmationViewModel by viewModel()
    private var navigation: CieloNavigation? = null

    private val ga4: RAGA4 by inject()

    private val args: ReceiveAutomaticConfirmationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentReceiveAutomaticConfirmationBinding.inflate(
            inflater, container, false
        ).also {
            binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitDetails()
        setupObservers()
        setupNavigation()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        logScreenViewAndCheckout()
    }

    private fun logScreenViewAndCheckout() {
        ga4.apply {
            logScreenView(
                getTransactionScreenName(viewModel.typeTransactionSelected)
            )
            logRABeginCheckout(
                viewModel.typeTransactionSelected,
                viewModel.periodicitySelected,
                binding?.tvValidityContent?.text?.toString()
            )
        }
    }

    private fun setupInitDetails() {
        viewModel.setReceiveAutomaticOffers(
            args.argsoffers.toList(),
            args.argsoffersummary,
            args.argscreditoffer,
            args.argsinstallmentoffer,
            args.argsplansummary
        )
    }

    private fun setupObservers() {
        setupOfferContractLiveDataObserver()
        setupCreditOfferLiveDataObserver()
        setupInstallmentOfferLiveDataObserver()
        setupOfferValidityLiveDataObserver()
        setupPlanSummaryLiveDataObserver()
        setupOfferBrandLiveDataObserver()
    }

    private fun setupPlanSummaryLiveDataObserver() {
        viewModel.planSummaryLiveData.observe(viewLifecycleOwner) {
            it?.let {
                setupSelectedPlan(it)
            }
        }
    }

    private fun setupOfferBrandLiveDataObserver() {
        viewModel.receiveAutomaticOffersBrandLiveData.observe(viewLifecycleOwner) { brand ->
            brand?.let {
                setupOfferReferenceBrand(brand)
            }
        }
    }

    private fun setupOfferValidityLiveDataObserver() {
        viewModel.receiveAutomaticOffersValidityLiveData.observe(viewLifecycleOwner) { offerValidityState ->
            offerValidityState?.let {
                setupOfferValidity(offerValidityState)
            }
        }
    }

    private fun setupInstallmentOfferLiveDataObserver() {
        viewModel.installmentOfferLiveData.observe(viewLifecycleOwner) { installmentOfferState ->
            when (installmentOfferState) {
                is OfferState.Show -> setupInstallmentOffer(installmentOfferState.offer)
                is OfferState.Hide -> hideInstallmentOffer()
            }
        }
    }

    private fun setupCreditOfferLiveDataObserver() {
        viewModel.creditOfferLiveData.observe(viewLifecycleOwner) { creditOfferState ->
            when (creditOfferState) {
                is OfferState.Show -> setupCreditOffer(creditOfferState.offer)
                is OfferState.Hide -> hideCreditOffer()
            }
        }
    }

    private fun setupOfferContractLiveDataObserver() {
        viewModel.receiveAutomaticOffersDetailsContractLiveData.observe(viewLifecycleOwner) { uiStateContracting ->
            when (uiStateContracting) {
                is UiStateRAOContract.Loading -> loadingContractingReceiveAutomaticOffer()
                is UiStateRAOContract.HideLoading -> hideLoadingContractingReceiveAutomaticOffer()
                is UiStateRAOContract.Success, UiStateRAOContract.Empty -> showLoadingSuccessContracting()
                is UiStateRAOContract.Error -> onErrorContracting(
                    requireContext().formatterErrorMessage(uiStateContracting.message),
                    uiStateContracting.error
                )
            }
        }
    }

    private fun setupSelectedPlan(selectedPlanState: PlanState) {
        binding?.tvPlanContent?.text = when (selectedPlanState) {
            is PlanState.Daily -> getString(R.string.receive_auto_confirmation_plan_two_days)
            is PlanState.Weekly -> getString(R.string.receive_auto_confirmation_plan_weekly,
                DateTimeHelper.convertWeekDayToPortuguese(
                        selectedPlanState.weekday
                    )
            )
            is PlanState.Monthly -> getString(
                R.string.receive_auto_confirmation_plan_monthly,
                selectedPlanState.monthDay
            )

            PlanState.Empty -> EMPTY_VALUE
        }
    }

    private fun setupOfferValidity(
        offerValidityState: OfferValidityState
    ) {

        binding?.tvValidityContent?.apply {
            text = when (offerValidityState) {
                is OfferValidityState.Months -> {
                    val validityQuantity = offerValidityState.months
                    getString(
                        R.string.receive_auto_offer_confirmation_validity_period,
                        validityQuantity,
                        resources.getQuantityString(
                            R.plurals.month_plurals,
                            validityQuantity
                        )
                    ).htmlTextFormat()
                }

                is OfferValidityState.FixedDate -> {
                    getString(
                        R.string.receive_auto_offer_confirmation_validity_fixed_date,
                        offerValidityState.date.dateFormatToBr()
                    ).htmlTextFormat()
                }

                OfferValidityState.Empty -> EMPTY_VALUE
            }
            binding?.validityGroup.visible(text.isNullOrEmpty().not())
        }
    }

    private fun setupListeners() {
        binding?.apply {
            btnSeeTaxWithBrand.setOnClickListener {
                logSeeTaxesWithBrandClick()
                showTaxesBottomSheet()
            }
            btnContractPlan.apply {
                setTextAppearance(R.style.semi_bold_montserrat_16)
                setOnClickListener(::onConfirmClick)
            }


            btnPostValidityFees.setOnClickListener {
                showPostValidityTaxesBottomSheet()
            }
        }
    }

    private fun logSeeTaxesWithBrandClick() {
        ga4.apply {
            logClick(
                screenName = getTransactionScreenName(viewModel.typeTransactionSelected),
                contentComponent = getTransactionContentComponentName(viewModel.typeTransactionSelected),
                contentName = RAGA4.SEE_FEE_BY_BRAND
            )
        }
    }

    private fun showPostValidityTaxesBottomSheet() {
        CieloContentBottomSheet
            .create(
                headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                    title = getString(R.string.receive_auto_post_validity_bs_title)
                ),
                contentLayoutRes = R.layout.layout_bs_automatic_receive_fees,
                onContentViewCreated = { view, bs ->
                    setBSToExpanded(bs)
                    val binding = LayoutBsAutomaticReceiveFeesBinding.bind(view)
                    setupPostValidityLiveDataObserver(bs, binding)

                    viewModel.getPostValidityOffers()
                },
                disableExpandableMode = true
            ).show(childFragmentManager, tag)

        ga4.logScreenView(
            ga4.getPostValidityBSScreenName(
                viewModel.typeTransactionSelected
            )
        )
    }

    private fun setBSToExpanded(bs: CieloBottomSheet) {
        (bs.dialog as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun setupPostValidityLiveDataObserver(
        bs: CieloBottomSheet,
        binding: LayoutBsAutomaticReceiveFeesBinding
    ) {
        viewModel.receiveAutomaticOffersPostValidityLiveData.observe(bs.viewLifecycleOwner) {
            when (it) {
                is UiStateRAODetailsOffers.Success -> {
                    setupPostValidityFeesBSSuccess(binding, it)
                }

                UiStateRAODetailsOffers.HideLoading -> {
                    hideLoadingPostValidityFeesBS(binding)
                }

                UiStateRAODetailsOffers.Loading -> {
                    showLoadingPostValidityFeesBS(binding)
                }

                is UiStateRAODetailsOffers.Error -> setupPostValidityFeesBSError(binding, it)
                UiStateRAODetailsOffers.Empty -> setupPostValidityFeesBSError(binding)
            }
        }
    }

    private fun setupPostValidityFeesBSError(
        bsFeesBinding: LayoutBsAutomaticReceiveFeesBinding,
        error: UiStateRAODetailsOffers.Error? = null
    ) {
        bsFeesBinding.apply {
            feesContainer.gone()
            shimmer.gone()
            errorInclude.apply {
                error?.message?.let {
                    tvSorryMessage.text = context?.formatterErrorMessage(it)
                }
                btReload.setOnClickListener { viewModel.getPostValidityOffers() }
                root.visible()
            }
        }
        ga4.logException(
            ga4.getPostValidityBSScreenName(
                viewModel.typeTransactionSelected,
            ),
            error?.error
        )
    }

    private fun showLoadingPostValidityFeesBS(bsFeesBinding: LayoutBsAutomaticReceiveFeesBinding) {
        bsFeesBinding.apply {
            shimmer.startShimmer()
            shimmer.visible()
            feesContainer.gone()
            errorInclude.root.gone()
        }
    }

    private fun hideLoadingPostValidityFeesBS(bsFeesBinding: LayoutBsAutomaticReceiveFeesBinding) {
        bsFeesBinding.shimmer.gone()
    }

    private fun setupPostValidityFeesBSSuccess(
        bsFeesBinding: LayoutBsAutomaticReceiveFeesBinding,
        it: UiStateRAODetailsOffers.Success<List<OfferSummary>>
    ) {
        bsFeesBinding.apply {
            rvRAFees.adapter = ReceiveAutomaticOfferFeesAdapter(
                it.data.orEmpty(),
                viewModel.typeTransactionSelected
            )
            tvSubtitle.gone()
            rvRAFees.updatePadding(top = ZERO)
            feesContainer.visible()
            errorInclude.root.gone()
        }
        ga4.logScreenView(
            ga4.getPostValidityBSScreenName(
                viewModel.typeTransactionSelected
            )
        )
    }

    private fun showTaxesBottomSheet() {
        CieloContentBottomSheet
            .create(
                headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                    title = taxesTitle(
                        viewModel.typeTransactionSelected,
                        viewModel.periodicitySelected
                    )
                ),
                contentLayoutRes = R.layout.layout_bs_automatic_receive_fees,
                onContentViewCreated = { view, _ ->
                    view.findViewById<RecyclerView>(R.id.rvRAFees)?.apply {
                        adapter = ReceiveAutomaticOfferFeesAdapter(
                            viewModel.offers?.mapToOfferSummary().orEmpty(),
                            viewModel.typeTransactionSelected
                        )
                    }
                },
                disableExpandableMode = true
            ).show(childFragmentManager, tag)
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun loadingContractingReceiveAutomaticOffer() {
        showLoadingContracting()
    }

    private fun hideLoadingContractingReceiveAutomaticOffer() {
        hideLoadingContracting()
    }

    private fun showLoadingContracting() {
        navigation?.showLoading(
            message = R.string.p2m_load_wait
        )
        binding?.btnContractPlan?.isEnabled = false
        binding?.btnSeeTaxWithBrand?.isEnabled = false
        binding?.btnPostValidityFees?.isEnabled = false
    }

    private fun hideLoadingContracting() {
        navigation?.hideLoading()
        binding?.btnContractPlan?.isEnabled = true
        binding?.btnSeeTaxWithBrand?.isEnabled = true
        binding?.btnPostValidityFees?.isEnabled = true
    }

    private fun showLoadingSuccessContracting() {
        ga4.logPurchase(
            viewModel.typeTransactionSelected,
            viewModel.periodicitySelected,
            binding?.tvValidityContent?.text?.toString()
        )
        doWhenResumed {
            goToEnd()
        }
    }

    private fun goToEnd() {
        findNavController().safeNavigate(
            ReceiveAutomaticConfirmationFragmentDirections.actionReceiveAutomaticConfirmationFragmentToReceiveAutomaticEndFragment(
                viewModel.typeTransactionSelected,
                viewModel.periodicitySelected,
                viewModel.weekDaySelected?.let {
                    DateTimeHelper.convertWeekDayToPortuguese(
                        it
                    ).toLowerCasePTBR()
                } ?: EMPTY_VALUE,
                viewModel.monthDaySelected.toString()
            )
        )
    }

    private fun setupOfferReferenceBrand(offerBrand: String) {
        binding?.containerInfoTaxBox?.tvTaxInfoThree?.text =
            getString(
                R.string.receive_auto_details_tax_brand,
                offerBrand
            )
    }

    private fun setupInstallmentOffer(
        installmentOffer: InstallmentOfferItem
    ) {
        binding?.tvTaxDetailsInstallment?.text = getString(
            R.string.receive_auto_details_tax_installment_sales,
            installmentOffer.summarizedMdr.toStringAndReplaceDotWithComma(),
            installmentOffer.mdr.toStringAndReplaceDotWithComma(),
            installmentOffer.recebaRapidoMdr.toStringAndReplaceDotWithComma()
        ).htmlTextFormat()
    }

    private fun hideInstallmentOffer() {
        binding?.tvTaxDetailsInstallment?.gone()
    }

    private fun setupCreditOffer(
        creditOffer: CreditOfferItem
    ) {
        binding?.tvTaxDetailsCreditCash?.text = getString(
            R.string.receive_auto_details_tax_cash_sales,
            creditOffer.summarizedMdr.toStringAndReplaceDotWithComma(),
            creditOffer.mdr.toStringAndReplaceDotWithComma(),
            creditOffer.recebaRapidoMdr.toStringAndReplaceDotWithComma()
        ).htmlTextFormat()
    }

    private fun hideCreditOffer() {
        binding?.tvTaxDetailsCreditCash?.gone()
    }

    private fun onErrorContracting(message: String, error: NewErrorMessage?) {
        navigation?.showCustomHandlerView(
            contentImage = R.drawable.img_10_erro,
            title = getString(R.string.receive_auto_contract_error_title),
            message = message,
            labelSecondButton = getString(R.string.btn_two_error),
            isShowButtonClose = true
        )
        ga4.logException(
            screenName = ga4.getConfirmScreenName(viewModel.typeTransactionSelected),
            error = error
        )
    }

    private fun onConfirmClick(v: View) {
        confirmContractReceiveAutomaticOffer()
        ga4.logRAConfirmAddPaymentInfo(
            viewModel.typeTransactionSelected,
            viewModel.periodicitySelected,
            binding?.tvValidityContent?.text?.toString()
        )
    }

    private fun confirmContractReceiveAutomaticOffer() {
        viewModel.contractingReceiveAutomaticOffers()
    }

    private fun taxesTitle(
        typeTransactionSelected: String,
        periodSelected: String
    ): String {
        return getString(
            R.string.receive_auto_fees_bs_title,
            when (periodSelected) {
                DAILY -> getString(R.string.receive_auto_fees_bs_title_two_days)
                WEEKLY -> getString(R.string.receive_auto_fees_bs_title_weekly)
                MONTHLY -> getString(R.string.receive_auto_fees_bs_title_monthly)
                else -> {}
            },
            when (typeTransactionSelected) {
                BOTH -> getString(R.string.receive_auto_fees_bs_title_cash_and_installment)
                CREDIT -> getString(R.string.receive_auto_fees_bs_title_cash)
                INSTALLMENT -> getString(R.string.receive_auto_fees_bs_title_installment)
                else -> {}
            }
        )
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}