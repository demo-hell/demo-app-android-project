package br.com.mobicare.cielo.newRecebaRapido.presentation.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import br.com.cielo.libflue.bottomsheet.CieloContentBottomSheet
import br.com.cielo.libflue.bottomsheet.CieloListBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.util.extensions.visible
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.THIRTY
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.Text.EMPTY
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.enums.DiasDaSemana
import br.com.mobicare.cielo.commons.helpers.DateTimeHelper
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.commons.utils.formatterErrorMessage
import br.com.mobicare.cielo.commons.utils.spannable.htmlTextFormat
import br.com.mobicare.cielo.databinding.BottomSheetCieloSelectPeriodItemBinding
import br.com.mobicare.cielo.databinding.FragmentReceiveAutomaticDetailsBinding
import br.com.mobicare.cielo.extensions.addDoubleZeroPrefix
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.extensions.toStringAndReplaceDotWithComma
import br.com.mobicare.cielo.newRecebaRapido.analytics.RAGA4
import br.com.mobicare.cielo.newRecebaRapido.domain.model.CreditOfferItem
import br.com.mobicare.cielo.newRecebaRapido.domain.model.GeneralOfferSummary
import br.com.mobicare.cielo.newRecebaRapido.domain.model.InstallmentOfferItem
import br.com.mobicare.cielo.newRecebaRapido.domain.model.SelectedPlanSummary
import br.com.mobicare.cielo.newRecebaRapido.domain.model.mapToOfferSummary
import br.com.mobicare.cielo.newRecebaRapido.presentation.details.adapter.ReceiveAutomaticOfferFeesAdapter
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.BOTH
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.CREDIT
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.DAILY
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.INSTALLMENT
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.MONTHLY
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.MONTHLY_SELECT
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.ONE_DAY_SELECT
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.WEEKLY
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.WEEK_SELECT
import br.com.mobicare.cielo.newRecebaRapido.util.OfferState
import br.com.mobicare.cielo.newRecebaRapido.util.UiStateRAODetailsOffers
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef.TAG_HELP_CENTER_RECEBIMENTO_AUTOMATICO
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReceiveAutomaticDetailsFragment : BaseFragment(), CieloNavigationListener {

    private var binding: FragmentReceiveAutomaticDetailsBinding? = null
    private val viewModel: ReceiveAutomaticDetailsViewModel by viewModel()
    private var navigation: CieloNavigation? = null

    private val ga4: RAGA4 by inject()

    private val args: ReceiveAutomaticDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentReceiveAutomaticDetailsBinding.inflate(
            inflater, container, false
        ).also {
            binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitDetails()
        initSetupTitleToolBar()
        setupObservers()
        setupNavigation()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        ga4.run {
            logScreenView(
                getTransactionScreenName(viewModel.typeTransactionSelected)
            )
        }
    }

    private fun setupInitDetails() {
        viewModel.typeTransactionSelected = args.selectedtype
        viewModel.setInitOffer(args.argsoffers.toList())
        setupPeriodicityBtn()
    }

    private fun setupObservers() {
        setupRAOfferDetailsLiveDataObserver()

        setupCreditOfferLiveDataObserver()

        setupInstallmentOfferLiveDataObserver()
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

    private fun setupRAOfferDetailsLiveDataObserver() {
        viewModel.receiveAutomaticOffersDetailsLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiStateRAODetailsOffers.Loading -> loadingReceiveAutomaticDetailsOffer()
                is UiStateRAODetailsOffers.HideLoading -> hideLoadingReceiveAutomaticDetailsOffers()
                is UiStateRAODetailsOffers.Success -> successReceiveAutomaticOffers(uiState.data)
                is UiStateRAODetailsOffers.Error -> errorReceiveAutomaticOffers(
                    requireContext().formatterErrorMessage(uiState.message),
                    uiState.error
                )

                is UiStateRAODetailsOffers.Empty -> emptyReceiveAutomaticOffers()
            }
        }
    }

    private fun setupListeners() {
        binding?.apply {
            btnBothOptionPlan.setOnSelectListener { _, _ ->
                onShowBottomSelectPeriod(optionSelectPeriod())
            }
            btnCalendarChose.setOnSelectListener { _, _ ->
                onShowBottomSelectCalendar(optionSelectCalendar(viewModel.periodicitySelected))
            }
            btnSeeTaxWithBrand.setOnClickListener {
                ga4.run {
                    logClick(
                        screenName = getTransactionScreenName(viewModel.typeTransactionSelected),
                        contentComponent = getTransactionContentComponentName(viewModel.typeTransactionSelected),
                        contentName = RAGA4.SEE_FEE_BY_BRAND
                    )
                }
                showTaxesBottomSheet()
            }
            btnContractPlan.apply {
                setTextAppearance(R.style.semi_bold_montserrat_16)
                setOnClickListener(::onConfirmClick)
            }
        }
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

    private fun getData() {
        viewModel.getReceiveAutomaticOffers()
    }

    private fun initSetupTitleToolBar() {
        binding?.apply {
            when (viewModel.typeTransactionSelected) {
                BOTH -> {
                    viewModel.title =
                        getString(R.string.receive_auto_dialog_title_cash_and_installment_sales)
                    viewModel.titleDescription = getString(
                        R.string.receive_auto_details_subtitle_cash_and_installment_sales
                    )
                }

                CREDIT -> {
                    viewModel.title = getString(R.string.receive_auto_label_only_cash_sales)
                    viewModel.titleDescription = getString(
                        R.string.receive_auto_details_subtitle_cash_sales
                    )
                }

                INSTALLMENT -> {
                    viewModel.title = getString(R.string.receive_auto_label_only_installment_sales)
                    viewModel.titleDescription = getString(
                        R.string.receive_auto_details_subtitle_installment_sales
                    )
                }
            }
            containerInfoTaxBox.tvTaxInfoThree.text =
                getText(R.string.receive_auto_details_tax_brand)

            tvTitleDescription.text = viewModel.titleDescription
        }
    }

    private fun setupPeriodicityBtn() {
        binding?.btnBothOptionPlan?.text = viewModel.periodicityBottomSelected
    }

    private fun setupVisibilityCalendarBtn() {
        when (viewModel.periodicitySelected) {
            DAILY -> {
                binding?.btnCalendarChose?.gone()
            }

            WEEKLY, MONTHLY -> {
                binding?.btnCalendarChose?.visible()
            }
        }
    }

    private fun setupCalendarStyleView() {
        if (viewModel.periodicitySelected != DAILY) {
            binding?.btnCalendarChose?.apply {
                visible()
                text = if (viewModel.periodicitySelected == WEEKLY) {
                    getString(
                        R.string.receive_auto_details_choose_init_monday_receive,
                        viewModel.weekDayBottomSelected.toLowerCasePTBR()
                    )
                } else {
                    getString(
                        R.string.receive_auto_details_choose_init_day_one_receive,
                        viewModel.monthDaySelected.addDoubleZeroPrefix()
                    )
                }
            }
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.showButton(false)
            confugureToolbar()
        }
    }

    private fun confugureToolbar() {
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
                toolbarTitle = viewModel.title
            )
        )
    }

    private fun loadingReceiveAutomaticDetailsOffer() {
        isLoading(true)
    }

    private fun hideLoadingReceiveAutomaticDetailsOffers() {
        isLoading(false)
    }

    private fun isLoading(isActive: Boolean) {
        binding?.apply {
            containerInfoTaxBox.apply {
                root.visible(isActive)
                tvTaxInfoThree.visible(isActive.not())
                containerShimmerCardBrand.visible(isActive)
            }
            tvTaxDetailsCreditCash.visible(isActive.not())
            tvTaxDetailsInstallment.visible(isActive.not())
            shimmerLoad.visible(isActive)
            btnContractPlan.isEnabled = isActive.not()
            btnSeeTaxWithBrand.isEnabled = isActive.not()
            errorInclude.root.gone()
        }
    }

    private fun goToConfirmation() {
        findNavController().safeNavigate(
            ReceiveAutomaticDetailsFragmentDirections.actionReceiveAutomaticDetailsFragmentToReceiveAutomaticConfirmationFragment(
                viewModel.offers?.toTypedArray() ?: return,
                viewModel.offerSummary ?: return,
                viewModel.creditOffer,
                viewModel.installmentOffer,
                SelectedPlanSummary(
                    monthDaySelected = viewModel.monthDaySelected,
                    weekDaySelected = viewModel.weekDaySelected,
                    periodicitySelected = viewModel.periodicitySelected,
                    typeTransactionSelected = viewModel.typeTransactionSelected
                )
            )
        )
    }

    private fun successReceiveAutomaticOffers(offerSummary: GeneralOfferSummary?) {

        setupVisibilityHomeViews(true)
        setupVisibilityCalendarBtn()

        binding?.containerInfoTaxBox?.tvTaxInfoThree?.text =
            getString(
                R.string.receive_auto_details_tax_brand,
                offerSummary?.referenceBrand
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

    private fun emptyReceiveAutomaticOffers() {
        errorReceiveAutomaticOffers(getString(R.string.receive_auto_error_message))
    }

    private fun errorReceiveAutomaticOffers(message: String, error: NewErrorMessage? = null) {
        ga4.run {
            logException(
                screenName = getTransactionScreenName(viewModel.typeTransactionSelected),
                error = error
            )
        }

        setupVisibilityHomeViews(false)
        binding?.apply {
            btnCalendarChose.gone()
            errorInclude.apply {
                root.visible()
                tvSorryMessage.text = message
                btReload.apply {
                    setTextAppearance(R.style.semi_bold_montserrat_16)
                    btReload.setOnClickListener {
                        getData()
                        btnBothOptionPlan.visible()
                        containerTaxBox.visible()
                        root.gone()
                    }
                }
            }
        }
    }

    private fun setupVisibilityHomeViews(isActive: Boolean) {
        binding?.apply {
            errorInclude.root.visible(isActive.not())
            containerTaxBox.visible(isActive)
            containerInfoTaxBox.root.visible(isActive)
            btnSeeTaxWithBrand.visible(isActive)
            btnContractPlan.visible(isActive)
            if (viewModel.periodicitySelected == DAILY) {
                btnCalendarChose.visible(isActive.not())
            } else {
                setupCalendarStyleView()
                btnCalendarChose.visible(isActive)
            }
        }
    }

    private fun optionSelectPeriod(): List<String> {
        return periodOptionBottomList()
    }

    private fun optionSelectCalendar(periodSelected: String?): List<String> {
        return when (periodSelected) {
            WEEKLY -> {
                weekDaySelectorList()
            }

            else -> {
                dayMonthlyBottomList()
            }
        }
    }

    private fun onConfirmClick(v: View) {
        goToConfirmation()
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

    private fun periodOptionBottomList(): List<String> {
        return listOf(ONE_DAY_SELECT, WEEK_SELECT, MONTHLY_SELECT)
    }

    private fun dayMonthlyBottomList(): List<String> {
        val monthlyDays = ArrayList<String>()
        for (index: Int in ONE..THIRTY) {
            val day = index.addDoubleZeroPrefix()
            monthlyDays.add(day)
        }
        return monthlyDays
    }

    private fun weekDaySelectorList(): List<String> {
        return DiasDaSemana.values().dropLast(TWO).map { it.dia }
    }

    private fun onShowBottomSelectPeriod(dates: List<String>) {
        binding?.apply {
            var tempSelected = viewModel.periodicityBottomSelected
            CieloListBottomSheet
                .create(
                    headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                        title = getString(R.string.receive_auto_bottom_sheet_title_select_frequency_period)
                    ),
                    layoutItemRes = R.layout.bottom_sheet_cielo_select_period_item,
                    data = dates,
                    initialSelectedItem = viewModel.periodicityBottomSelected,
                    onViewBound = { optionPeriod, isSelected, itemView ->
                        val selectPeriodItemBinding =
                            BottomSheetCieloSelectPeriodItemBinding.bind(itemView)
                        selectPeriodItemBinding.apply {
                            radioButton.isChecked = isSelected
                            radioButton.setTextValue(optionPeriod)
                        }
                    },
                    onItemClicked = { period, position, bottomSheet ->
                        tempSelected = period
                        bottomSheet.updateSelectedPosition(position)
                    },
                    mainButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                        title = getString(R.string.confirmar),
                        onTap = {
                            viewModel.periodicityBottomSelected = tempSelected
                            updatePeriodPlan(viewModel.periodicityBottomSelected)
                            it.dismiss()
                        }
                    )
                ).show(
                    childFragmentManager,
                    this@ReceiveAutomaticDetailsFragment.javaClass.simpleName
                )
        }
    }

    private fun onShowBottomSelectCalendar(dates: List<String>) {
        binding?.apply {
            var tempSelect = EMPTY
            CieloListBottomSheet
                .create(
                    headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                        title = customTitleBottomSheet()
                    ),
                    layoutItemRes = R.layout.bottom_sheet_cielo_select_period_item,
                    data = dates,
                    initialSelectedItem = if (viewModel.periodicitySelected == WEEKLY) {
                        viewModel.weekDayBottomSelected
                    } else {
                        viewModel.monthDaySelected.addDoubleZeroPrefix()
                    },
                    onViewBound = { optionPeriod, isSelected, itemView ->
                        val selectPeriodItemBinding =
                            BottomSheetCieloSelectPeriodItemBinding.bind(itemView)
                        selectPeriodItemBinding.apply {
                            radioButton.isChecked = isSelected
                            radioButton.setTextValue(optionPeriod)
                        }
                    },
                    onItemClicked = { period, position, bottomSheet ->
                        tempSelect = period
                        bottomSheet.updateSelectedPosition(position)
                    },
                    mainButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                        title = getString(R.string.confirmar),
                        onTap = {
                            if (viewModel.periodicitySelected == WEEKLY) {
                                viewModel.weekDayBottomSelected = tempSelect
                            } else {
                                viewModel.monthDaySelected = tempSelect.toIntOrNull() ?: ZERO
                            }
                            updateCalendarPeriodBottom()
                            it.dismiss()
                        }
                    )
                ).show(
                    childFragmentManager,
                    this@ReceiveAutomaticDetailsFragment.javaClass.simpleName
                )
        }
    }

    private fun updatePeriodPlan(period: String) {
        binding?.apply {
            btnBothOptionPlan.text = period
            btnCalendarChose.gone()
        }
        when (period) {
            ONE_DAY_SELECT -> {
                viewModel.periodicitySelected = DAILY
            }

            WEEK_SELECT -> {
                viewModel.periodicitySelected = WEEKLY
            }

            MONTHLY_SELECT -> {
                viewModel.periodicitySelected = MONTHLY
            }
        }
        getData()
    }

    private fun updateCalendarPeriodBottom() {
        if (viewModel.periodicitySelected == WEEKLY) {
            binding?.btnCalendarChose?.text =
                getString(
                    R.string.receive_auto_details_choose_init_monday_receive,
                    viewModel.weekDayBottomSelected.toLowerCasePTBR()
                )
            mapWeekDay(viewModel.weekDayBottomSelected)
        }
        if (viewModel.periodicitySelected == MONTHLY) {
            binding?.btnCalendarChose?.text =
                getString(
                    R.string.receive_auto_details_choose_init_day_one_receive,
                    viewModel.monthDaySelected.addDoubleZeroPrefix()
                )
        }
    }

    private fun mapWeekDay(period: String) {
        viewModel.weekDaySelected = DateTimeHelper.convertWeekDayToEnglish(period)
    }

    private fun customTitleBottomSheet(): String {
        return when (viewModel.periodicitySelected) {
            WEEKLY -> {
                getString(R.string.receive_auto_bottom_sheet_title_select_week_period)
            }

            else -> {
                getString(R.string.receive_auto_bottom_sheet_title_select_month_period)
            }
        }
    }

    private fun showFaq() {
        navigation?.startHelpCenter(TAG_HELP_CENTER_RECEBIMENTO_AUTOMATICO)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}