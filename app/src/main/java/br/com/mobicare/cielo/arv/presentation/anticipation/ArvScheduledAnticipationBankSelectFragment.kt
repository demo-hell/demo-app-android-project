package br.com.mobicare.cielo.arv.presentation.anticipation

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.bottomsheet.CieloContentBottomSheet
import br.com.cielo.libflue.bottomsheet.CieloListBottomSheet
import br.com.cielo.libflue.bottomsheet.adapter.CieloListBottomSheetRecyclerViewAdapter
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.enum.CieloBankIcons
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV_AUTOMATIC_CONFIGURATION
import br.com.mobicare.cielo.arv.domain.model.ArvBank
import br.com.mobicare.cielo.arv.presentation.model.ScheduleContract
import br.com.mobicare.cielo.arv.utils.ArvConstants.BOTH_NEGOTIATION_TYPE
import br.com.mobicare.cielo.arv.utils.ArvConstants.CIELO_NEGOTIATION_TYPE
import br.com.mobicare.cielo.arv.utils.ArvConstants.MARKET_NEGOTIATION_TYPE
import br.com.mobicare.cielo.arv.utils.UiArvBranchesContractsState
import br.com.mobicare.cielo.arv.utils.UiArvScheduledMarketFeatureToggleState
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.constants.ONE_NEGATIVE
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.helpers.FormHelper
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.CNPJ_MASK_COMPLETE_FORMAT
import br.com.mobicare.cielo.commons.utils.formatDateToBrazilian
import br.com.mobicare.cielo.commons.utils.isRoot
import br.com.mobicare.cielo.commons.utils.spannable.addSpannable
import br.com.mobicare.cielo.databinding.BottomSheetBranchContractBinding
import br.com.mobicare.cielo.databinding.BottomSheetCieloBankAccountItemBinding
import br.com.mobicare.cielo.databinding.BranchContractDetailItemBinding
import br.com.mobicare.cielo.databinding.FragmentArvScheduledAnticipationBankSelectBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.extensions.setColouredSpan
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ArvScheduledAnticipationBankSelectFragment : BaseFragment(), CieloNavigationListener {

    private val args: ArvScheduledAnticipationBankSelectFragmentArgs by navArgs()

    private val viewModel: ArvScheduledAnticipationBankSelectViewModel by viewModel {
        parametersOf(args)
    }

    private val analytics: ArvAnalytics by inject()
    private val arvAnalytics: ArvAnalyticsGA4 by inject()

    private var navigation: CieloNavigation? = null
    private var binding: FragmentArvScheduledAnticipationBankSelectBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentArvScheduledAnticipationBankSelectBinding.inflate(inflater, container, false)
        .also {
            binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupObservers()
        setupListeners()
        setupBankList()
        analyticsScreenView()
        if (shouldShowBranchesContracts()) {
            viewModel.getBranchesContracts()
        }
    }

    override fun onResume() {
        super.onResume()
        arvAnalytics.logScreenView(screenName = SCREEN_VIEW_ARV_AUTOMATIC_CONFIGURATION)
    }

    private fun setupRadioButtons(typesAvailable: String?) {
        binding?.apply {
            when (typesAvailable) {
                BOTH_NEGOTIATION_TYPE -> {
                    enableCieloButton()
                    enableMarketButton()
                    enableBothButton()
                }

                CIELO_NEGOTIATION_TYPE -> {
                    enableCieloButton()
                    RBMarket.isEnabled = false
                    RBBoth.isEnabled = false
                }

                MARKET_NEGOTIATION_TYPE -> {
                    enableMarketButton()
                    RBCielo.isEnabled = false
                    RBBoth.isEnabled = false
                }

                else -> {
                    RBCielo.isEnabled = false
                    RBMarket.isEnabled = false
                    RBBoth.isEnabled = false
                }
            }
        }
    }

    private fun enableCieloButton() {
        binding?.RBCielo?.apply {
            setColouredSpan(
                getString(R.string.arv_cielo_receivables_colored_substring),
                ContextCompat.getColor(requireContext(), R.color.brand_400)
            )
            isEnabled = true
        }
    }

    private fun enableMarketButton() {
        binding?.RBMarket?.apply {
            setColouredSpan(
                getString(R.string.arv_market_receivables_colored_substring),
                ContextCompat.getColor(requireContext(), R.color.brand_400)
            )
            isEnabled = true
        }
    }

    private fun enableBothButton() {
        binding?.RBBoth?.apply {
            setColouredSpan(
                getString(R.string.arv_both_schedules_colored_substring),
                ContextCompat.getColor(requireContext(), R.color.brand_400)
            )
            isEnabled = true
        }
    }

    private fun onDisabledMarket() {
        binding?.apply {
            RBMarket.apply {
                isEnabled = false
                text = formatDisabledTitleAndSubtitle(
                    R.string.arv_market_receivables,
                    R.string.arv_negotiation_temporarily_unavailable
                )
            }
            RBBoth.isEnabled = false
        }
    }

    private fun formatDisabledTitleAndSubtitle(
        @StringRes title: Int,
        @StringRes subtitle: Int
    ) = SpannableStringBuilder().apply {
        append(
            getString(title)
                .addSpannable(
                    TextAppearanceSpan(
                        requireContext(),
                        R.style.medium_montserrat_16_cloud_400
                    )
                )
        )
        append(Text.NEW_LINE)
        append(
            getString(subtitle)
                .addSpannable(
                    TextAppearanceSpan(
                        requireContext(),
                        R.style.medium_montserrat_12_cloud_400
                    )
                )
        )
    }

    private fun shouldShowBranchesContracts() =
        isRoot() && args.scheduledanticipationargs.rateSchedules?.any {
            it?.cnpjBranch == true
        } == true

    private fun showBranchContracts(contracts: List<ScheduleContract>?) {
        contracts?.let { itContracts ->
            CieloContentBottomSheet
                .create(
                    headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                        title = getString(R.string.arv_branches_with_scheduled_hired),
                        onCloseTap = {
                            findNavController().navigateUp()
                        },
                        onSlideDismiss = { findNavController().navigateUp() }
                    ),
                    contentLayoutRes = R.layout.bottom_sheet_branch_contract,
                    onContentViewCreated = { view, bs ->
                        bs.dialog?.setOnCancelListener {
                            this@ArvScheduledAnticipationBankSelectFragment.findNavController()
                                .navigateUp()
                        }

                        val bsBinding = BottomSheetBranchContractBinding.bind(view)
                        bsBinding.rvBranchContracts.apply {
                            adapter = CieloListBottomSheetRecyclerViewAdapter(
                                layoutRes = R.layout.branch_contract_detail_item,
                                data = itContracts,
                                onViewBound = { branch, itemView, _ ->
                                    val itemBinding = BranchContractDetailItemBinding.bind(itemView)
                                    itemBinding.apply {
                                        tvBranchCnpj.text = FormHelper.maskFormatter(
                                            branch.cnpj,
                                            CNPJ_MASK_COMPLETE_FORMAT
                                        ).formattedText.string
                                        tvSchedule.text = when (branch.schedule) {
                                            CIELO_NEGOTIATION_TYPE -> getString(R.string.arv_cielo)
                                            MARKET_NEGOTIATION_TYPE -> getString(R.string.arv_market)
                                            else -> EMPTY
                                        }
                                        tvFee.text = branch.fee
                                        tvHireDate.text = branch.hireDate.formatDateToBrazilian()
                                    }

                                    itemBinding.root.isClickable = false
                                },
                                onItemClicked = { _, _ -> }
                            )
                        }
                    },
                    mainButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                        title = getString(R.string.arv_hire_anyway),
                        onTap = {
                            it.dismiss()
                        }
                    ),
                    disableExpandableMode = true
                )
                .show(childFragmentManager, tag).also {
                    viewModel.setAlreadyShowedContracts(true)
                }
        }

    }

    private fun analyticsScreenView() {
        analytics.logScreenView(
            name = ArvAnalytics.SCREEN_VIEW_ARV_SCHEDULED_ANTICIPATION,
            className = this.javaClass
        )
    }

    private fun setupBankList() {
        viewModel.arvBanksListLiveData.value ?: viewModel.setupBankList(
            args.scheduledanticipationargs.domicile?.filterNotNull() ?: listOf()
        )
    }

    private fun setupListeners() {
        binding?.apply {
            RBNegotiationType.setOnCheckedChangeListener { group, _ ->
                viewModel.setArvNegotiation(
                    when (group.checkedRadioButtonId) {
                        R.id.RBCielo -> CIELO_NEGOTIATION_TYPE
                        R.id.RBMarket -> MARKET_NEGOTIATION_TYPE
                        R.id.RBBoth -> BOTH_NEGOTIATION_TYPE
                        else -> null
                    }
                )
            }

            btContinue.setOnClickListener {
                arvAnalytics.logAddPaymentInfo(
                        screenName = SCREEN_VIEW_ARV_AUTOMATIC_CONFIGURATION,
                        bankName =  viewModel.arvBanksListLiveData.value?.first()?.name.orEmpty(),
                        itemCategory3 = viewModel.arvNegotiationTypeLiveData.value.orEmpty()
                    )

                analytics.logScreenActionsWithTwoLabel(
                    ArvAnalytics.ANTICIPATION,
                    ArvAnalytics.SCHEDULE_ARV,
                    analytics.negotiationType(viewModel.arvNegotiationTypeLiveData.value),
                    Label.BOTAO,
                    ArvAnalytics.CONTINUE
                )

                viewModel.arvSelectedBankLiveData.value?.let { arvBank ->
                    val selectedNegotiationTypes = if(viewModel.arvNegotiationTypeLiveData.value == BOTH_NEGOTIATION_TYPE)
                        args.scheduledanticipationargs.rateSchedules
                    else
                        args.scheduledanticipationargs.rateSchedules?.filter { it?.name == viewModel.arvNegotiationTypeLiveData.value }

                    findNavController().safeNavigate(
                        ArvScheduledAnticipationBankSelectFragmentDirections.actionArvScheduledAnticipationBankSelectFragmentToArvScheduledAnticipationConfirmationFragment(
                            args.scheduledanticipationargs.copy(rateSchedules = selectedNegotiationTypes),
                            arvBank
                        )
                    )
                }
            }
        }
    }

    override fun onHelpButtonClicked() {
        analytics.logScreenActionsWithTwoLabel(
            ArvAnalytics.ANTICIPATION,
            ArvAnalytics.SCHEDULE_ARV,
            analytics.negotiationType(viewModel.arvNegotiationTypeLiveData.value),
            Label.BOTAO,
            ArvAnalytics.HELP
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        analytics.logScreenActionsWithTwoLabel(
            ArvAnalytics.ANTICIPATION,
            ArvAnalytics.SCHEDULE_ARV,
            analytics.negotiationType(viewModel.arvNegotiationTypeLiveData.value),
            Label.BOTAO,
            Action.VOLTAR
        )
    }


    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.showHelpButton(isShow = true)
            navigation?.setupToolbar(
                title = getString(R.string.programmed_anticipation),
                isCollapsed = false
            )
        }
    }

    private fun setupObservers() {
        viewModel.apply {
            arvBanksListLiveData.observe(viewLifecycleOwner) { banks ->
                banks?.let { onShowArvBanks(it) }
            }

            arvSelectedBankLiveData.observe(viewLifecycleOwner) { bank ->
                bank?.let { setupAccount(it) } ?: run { binding?.btContinue?.isEnabled = false }
            }

            arvBranchesContractsStateLiveData.observe(viewLifecycleOwner) { uiState ->
                when (uiState) {
                    UiArvBranchesContractsState.AlreadyShowed -> Unit
                    is UiArvBranchesContractsState.ShowError -> onShowError()
                    UiArvBranchesContractsState.ShowLoading -> onShowLoading()
                    UiArvBranchesContractsState.HideLoading -> onHideLoading()
                    is UiArvBranchesContractsState.SuccessListContracts -> showBranchContracts(uiState.contracts)
                }

            }

            viewModel.arvMarketToggleLiveData.observe(viewLifecycleOwner) { uiState ->
                when (uiState) {
                    UiArvScheduledMarketFeatureToggleState.Disabled -> onDisabledMarket()
                }
            }

            viewModel.arvNegotiationAvailableTypeMutableLiveData.observe(viewLifecycleOwner) { typesAvailable ->
                setupRadioButtons(typesAvailable)
            }

            viewModel.arvNegotiationTypeLiveData.observe(viewLifecycleOwner) { typeSelected ->
                checkRadioButton(typeSelected)
            }
        }
    }

    private fun checkRadioButton(typeSelected: String?) {
        binding?.apply {
            when (typeSelected) {
                CIELO_NEGOTIATION_TYPE -> RBCielo.isChecked = true
                MARKET_NEGOTIATION_TYPE -> RBMarket.isChecked = true
                BOTH_NEGOTIATION_TYPE -> RBBoth.isChecked = true
                else -> ONE_NEGATIVE
            }
            btContinue.isEnabled = shouldEnableContinueButton()
        }
    }

    private fun onShowError() {
        doWhenResumed {
            navigation?.showCustomHandlerView(
                title = getString(
                    R.string.commons_generic_error_title
                ),
                message = getString(R.string.commons_generic_error_message),
                labelSecondButton = getString(R.string.text_try_again_label),
                isShowButtonClose = true,
                titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                messageAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                callbackClose = {
                    findNavController().navigateUp()
                },
                callbackSecondButton = {
                    viewModel.getBranchesContracts()
                }
            )
        }
    }

    private fun setupAccount(bank: ArvBank) {
        binding?.apply {
            arvAccount.apply {
                val bankMapped = bank.code?.let { CieloBankIcons.getBankFromCode(it) }
                bankMapped?.icon?.let { bankIcon.setImageResource(it) }
                tvBankName.text = bankMapped?.bankName ?: bank.name
                tvBankBranchNumber.text = bank.agency
                tvBankAccountNumber.text =
                    getString(R.string.arv_account_format, bank.account, bank.accountDigit)
            }
            arvAccount.accountContentGroup.visible()
            btContinue.isEnabled = shouldEnableContinueButton()
        }
    }

    private fun shouldEnableContinueButton() =
        viewModel.arvSelectedBankLiveData.value != null && viewModel.arvNegotiationTypeLiveData.value != null

    private fun onShowArvBanks(banks: List<ArvBank>) {
        binding?.apply {
            arvAccount.btEditBrands.setOnClickListener {

                analytics.logScreenActionsWithTwoLabel(
                    ArvAnalytics.ANTICIPATION,
                    ArvAnalytics.SCHEDULE_ARV,
                    analytics.negotiationType(viewModel.arvNegotiationTypeLiveData.value),
                    Label.BOTAO,
                    ArvAnalytics.CHANGE_ADDRESS
                )

                var tempSelectedAccount: ArvBank? = null

                CieloListBottomSheet
                    .create(
                        headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                            title = getString(R.string.arv_account_select_bs_title)
                        ),
                        layoutItemRes = R.layout.bottom_sheet_cielo_bank_account_item,
                        data = banks,
                        initialSelectedItem = viewModel.arvSelectedBankLiveData.value,
                        onViewBound = { bankAccount, isSelected, itemView ->
                            val bankAccountItemBinding =
                                BottomSheetCieloBankAccountItemBinding.bind(itemView)
                            bankAccountItemBinding.apply {
                                root.background = AppCompatResources.getDrawable(
                                    itemView.context,
                                    if (isSelected)
                                        R.drawable.background_stroke_1dp_round_brand_400
                                    else
                                        R.drawable.background_stroke_1dp_round_color_c5ced7
                                )
                                radioButton.isChecked = isSelected
                                tvBankName.text =
                                    bankAccount.code?.let { CieloBankIcons.getBankFromCode(it).bankName }
                                        ?: bankAccount.name
                                tvBankBranchNumber.text = bankAccount.agency
                                tvBankAccountNumber.text = getString(
                                    R.string.arv_account_format, bankAccount.account,
                                    bankAccount.accountDigit
                                )
                                bankIcon.setImageResource(
                                    CieloBankIcons.getBankFromCode(
                                        bankAccount.code ?: EMPTY
                                    ).icon
                                )

                            }
                        },
                        onItemClicked = { account, position, bottomSheet ->
                            analytics.logScreenActionsWithCheckButton(
                                Action.MODAL,
                                Action.CLIQUE,
                                ArvAnalytics.SCHEDULE_ARV,
                                analytics.negotiationType(viewModel.arvNegotiationTypeLiveData.value),
                                ArvAnalytics.SELECT_ACCOUNT_BANK_TO_RECEIPT,
                                account.name
                            )

                            tempSelectedAccount = account
                            bottomSheet.updateSelectedPosition(position)
                        },
                        mainButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                            title = getString(R.string.confirmar),
                            onTap = {
                                analytics.logScreenActionsWithCheckButton(
                                    Action.MODAL,
                                    Action.CLIQUE,
                                    ArvAnalytics.SCHEDULE_ARV,
                                    analytics.negotiationType(viewModel.arvNegotiationTypeLiveData.value),
                                    ArvAnalytics.SELECT_ACCOUNT_BANK_TO_RECEIPT,
                                    ArvAnalytics.CONFIRM
                                )

                                tempSelectedAccount?.let { itBank ->
                                    viewModel.handleBankSelect(
                                        itBank
                                    )
                                }
                                it.dismiss()
                            }
                        )
                    ).show(
                        childFragmentManager,
                        this@ArvScheduledAnticipationBankSelectFragment.javaClass.simpleName
                    ).also {
                        analytics.logScreenDialogShow(
                            Action.MODAL,
                            Action.EXIBICAO,
                            ArvAnalytics.SCHEDULE_ARV,
                            analytics.negotiationType(viewModel.arvNegotiationTypeLiveData.value),
                            ArvAnalytics.SELECT_ACCOUNT_BANK_TO_RECEIPT
                        )
                    }
            }
        }
    }

    private fun onShowLoading() {
        binding?.apply {
            RBNegotiationType.gone()

            shimmerRadioButton.apply {
                visible()
                startShimmer()
            }

            arvAccount.apply {
                accountContentGroup.gone()
                shimmerContainer.apply {
                    visible()
                    startShimmer()
                }
            }

            btContinue.isEnabled = false
        }
    }

    private fun onHideLoading() {
        binding?.apply {
            RBNegotiationType.visible()
            shimmerRadioButton.apply {
                stopShimmer()
                gone()
            }

            arvAccount.apply {
                accountContentGroup.visible()
                shimmerContainer.apply {
                    stopShimmer()
                    gone()
                }
            }

            btContinue.isEnabled = shouldEnableContinueButton()
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}