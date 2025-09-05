package br.com.mobicare.cielo.arv.presentation.anticipation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.bottomsheet.CieloListBottomSheet
import br.com.cielo.libflue.bottomsheet.CieloMessageBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.enum.CieloBankIcons
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.ARV_REVIEW_AND_REQUEST_ANTICIPATION_WITH_PERIOD
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.ARV_REVIEW_AND_REQUEST_ANTICIPATION_WITH_VALUE
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.ATENTION_INFO_BEFORE_TO_CONFIRM
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.BACK_BEFORE_COMPLETE
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.CHANGE_ADDRESS
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.COMPLETE_REQUEST
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.CONFIRM
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.EDIT_FLAG_BRANDS
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.EXHIBITION
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.HAVE_A_PROBLEM
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.HAVE_A_PROBLEM_MFA_WITH_PERIOD
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.HAVE_A_PROBLEM_MFA_WITH_VALUE
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.HELP
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.MFA
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.NOT_ENOUGH_BALANCE
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.NOT_ENOUGH_BALANCE_WITH_PERIOD
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.NOT_ENOUGH_BALANCE_WITH_VALUE
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.PERIOD_FLOW
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.RECEIVABLES_CIELO
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.RECEIVABLES_MARKET
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.RELOAD
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.REQUEST_MADE_SUCCESSFULLY
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.RESIDENCE
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SCREEN_VIEW_ARV_MFA_PROBLEM_WITH_PERIOD
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SCREEN_VIEW_ARV_MFA_PROBLEM_WITH_VALUE
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SCREEN_VIEW_ARV_NOT_ENOUGH_BALANCE_WITH_PERIOD
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SCREEN_VIEW_ARV_NOT_ENOUGH_BALANCE_WITH_VALUE
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SCREEN_VIEW_ARV_REVIEW_ANTICIPATION_WITH_PERIOD
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SCREEN_VIEW_ARV_REVIEW_ANTICIPATION_WITH_VALUE
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SCREEN_VIEW_ARV_SUCCESS_REQUEST_WITH_PERIOD
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SCREEN_VIEW_ARV_SUCCESS_REQUEST_WITH_VALUE
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SELECT_ACCOUNT_BANK_TO_RECEIPT
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SINGLE_ARV
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.TRACK_STATUS
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.VALUE_FLOW
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.INSUFFICIENT_FUNDS
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV_SINGLE_CONFIRMATION
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV_SINGLE_SUCCESS
import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.domain.model.ArvBank
import br.com.mobicare.cielo.arv.presentation.ArvEffectiveTimeViewModel
import br.com.mobicare.cielo.arv.utils.ArvConstants
import br.com.mobicare.cielo.arv.utils.ArvConstants.CIELO_NEGOTIATION_TYPE
import br.com.mobicare.cielo.arv.utils.ArvConstants.MARKET_NEGOTIATION_TYPE
import br.com.mobicare.cielo.arv.utils.ArvConstants.SIMULATION_TYPE_VALUE
import br.com.mobicare.cielo.arv.utils.UiArvBanksState
import br.com.mobicare.cielo.arv.utils.UiArvConfirmAnticipationState
import br.com.mobicare.cielo.arv.utils.UiArvFeeLoadingState
import br.com.mobicare.cielo.arv.utils.UiArvLoadingState
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias.CentralAjudaSubCategoriasEngineActivity
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias.CentralAjudaSubCategoriasEngineActivity.Companion.NOT_CAME_FROM_HELP_CENTER
import br.com.mobicare.cielo.commons.analytics.Action.BOTAO
import br.com.mobicare.cielo.commons.analytics.Action.CALLBACK
import br.com.mobicare.cielo.commons.analytics.Action.CLIQUE
import br.com.mobicare.cielo.commons.analytics.Action.FECHAR
import br.com.mobicare.cielo.commons.analytics.Action.MODAL
import br.com.mobicare.cielo.commons.analytics.Action.UNDERSTOOD
import br.com.mobicare.cielo.commons.analytics.Action.VOLTAR
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_SUBCATEGORY_NAME
import br.com.mobicare.cielo.commons.constants.HTTP_ENHANCE
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.DataCustomNew
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.toPtBrRealStringWithoutSymbol
import br.com.mobicare.cielo.commons.utils.toStringCommaSeparatedAndLast
import br.com.mobicare.cielo.databinding.BottomSheetCieloBankAccountItemBinding
import br.com.mobicare.cielo.databinding.FragmentArvSimulationBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef.TAG_HELP_CENTER_MFA
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef.TAG_KEY_HELP_CENTER
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArvAnticipationSimulationFragment : BaseFragment(), CieloNavigationListener {

    private val arvSimulationViewModel: ArvSimulationViewModel by viewModel()
    private val arvEffectiveTImeViewModel: ArvEffectiveTimeViewModel by viewModel()

    private var navigation: CieloNavigation? = null
    private var binding: FragmentArvSimulationBinding? = null
    private val analytics: ArvAnalytics by inject()
    private val arvAnalytics: ArvAnalyticsGA4 by inject()
    private val args: ArvAnticipationSimulationFragmentArgs by navArgs()
    private var negotiationTypeArv: String? = null
    private var arvAnticipation: ArvAnticipation? = null
    private var tempSelectedAccount: ArvBank? = null
    private lateinit var whatTypeFlowCurrent: String
    private lateinit var whatTypeFlowReviewTag: String
    private lateinit var dialogFlowCurrent: String
    private lateinit var screenViewSuccessCurrentFlow: String
    private lateinit var screenViewErrorMfaCurrentFlow: String
    private lateinit var screenViewNotEnoughBalanceCurrentFlow: String
    private lateinit var errorMfaCurrentFlow: String
    private lateinit var errorNotEnoughBalanceCurrentFlow: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentArvSimulationBinding.inflate(inflater, container, false)
        .also {
            binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateSimulationData()
        setupInitAnticipation()
        setupNavigation()
        setupObservers()
        setupListeners()
        checkTypeArvReceivableForAnalytics(arvAnticipation)
        checkFlowArvSingleAnticipationForAnalytics(arvAnticipation)
        analyticsScreenView(whatTypeFlowCurrent)
    }

    override fun onResume() {
        super.onResume()
        analyticsScreenViewGA4()
    }

    private fun setupInitAnticipation() {
        binding.apply {
            args.simulationargs.let { anticipation ->
                this@ArvAnticipationSimulationFragment.arvAnticipation = anticipation
            }
        }
    }

    private fun setupListeners() {
        binding?.apply {
            arvAccount.apply {
                btReload.setOnClickListener {
                    analytics.logScreenActionsWithTwoLabel(
                        whatTypeFlowReviewTag,
                        SINGLE_ARV,
                        negotiationTypeArv,
                        Label.BOTAO,
                        RELOAD
                    )
                    getBanksInformation()
                }
            }

            this.arvAccountFees.tvEditBrands.setOnClickListener {
                analytics.logScreenActionsWithTwoLabel(
                    ArvAnalytics.ARV_REVIEW_AND_REQUEST_ANTICIPATION,
                    SINGLE_ARV,
                    negotiationTypeArv,
                    Label.BOTAO,
                    EDIT_FLAG_BRANDS
                )
                arvSimulationViewModel.arvSimulationDataLiveData.value?.let { arvAnticipation ->
                    findNavController().safeNavigate(
                        ArvAnticipationSimulationFragmentDirections
                            .actionArvAnticipationSimulationFragmentToArvQueryByFlagFragment(
                                arvAnticipation,
                                arvSimulationViewModel.arvSelectedBankLiveData.value?.receiveToday
                                    ?: false
                            )
                    )
                }
            }
            btConfirm.setOnClickListener(::onConfirmClick)

            btRedoSimulation.setOnClickListener {
                analytics.logScreenActionsWithTwoLabel(
                    whatTypeFlowReviewTag,
                    SINGLE_ARV,
                    negotiationTypeArv,
                    Label.BOTAO,
                    BACK_BEFORE_COMPLETE
                )
                findNavController().navigateUp()
            }
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.showHelpButton(isShow = true)
            navigation?.setupToolbar(
                title = getString(R.string.arv_review_anticipation),
                isCollapsed = false
            )
        }
    }

    private fun setupObservers() {
        arvSimulationViewModel.apply {
            banksLiveData.observe(viewLifecycleOwner) { uiState ->
                when (uiState) {
                    is UiArvBanksState.ShowLoadingArvBanks -> onShowLoadingArvBanks()
                    is UiArvBanksState.SuccessArvBanks -> onShowArvBanks(uiState.banks)
                    is UiArvBanksState.ShowTryAgain -> onShowTryAgain(uiState.error)
                }
            }
            arvSimulationDataLiveData.observe(viewLifecycleOwner) { simulation ->
                simulation?.let { arvAnticipation ->
                    setupSimulationData(arvAnticipation)
                }
            }

            arvSelectedBankLiveData.observe(viewLifecycleOwner) { bank ->
                bank?.let { setupAccount(it) } ?: run { binding?.btConfirm?.isButtonEnabled = false }
            }

            userInformationLiveData.observe(viewLifecycleOwner) { userData ->
                userData?.let { setupUserData(it) }
            }

            arvConfirmAnticipationState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is UiArvConfirmAnticipationState.Success -> onConfirmAnticipationSuccess()
                    is UiArvConfirmAnticipationState.Error -> onConfirmAnticipationError(state.error)
                    is UiArvConfirmAnticipationState.ErrorToken -> onConfirmAnticipationErrorToken(
                        state.error
                    )
                    is UiArvConfirmAnticipationState.ErrorNotEligible -> onConfirmAnticipationNotEligibleError(
                        state.error
                    )
                }
            }

            arvLoadingState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is UiArvLoadingState.ShowLoading -> onShowLoading()
                    is UiArvLoadingState.HideLoading -> onHideLoading()
                }
            }

            arvFeeLoadingState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is UiArvFeeLoadingState.ShowLoading -> onFeeShowLoading()
                    is UiArvFeeLoadingState.HideLoading -> onFeeHideLoading()
                    is UiArvFeeLoadingState.Error -> onFeeLoadingError(state.onErrorAction)
                }
            }

            arvPurpleAlertState.observe(viewLifecycleOwner) { hasReceiveToday ->
                if (hasReceiveToday) {
                    hidePurpleAlert()
                } else {
                    showPurpleAlert()
                }
            }
        }
    }

    private fun setupSimulationData(simulationData: ArvAnticipation) {
        binding?.apply {
            val isSimulationByPeriod = simulationData.simulationType != SIMULATION_TYPE_VALUE
            if (isSimulationByPeriod) {
                setupSimulationByPeriod(simulationData)
            } else {
                setupSimulationByValue()
            }

            tvAnticipationType.text = getText(R.string.single_anticipation)
            tvSchedule.text = when (simulationData.negotiationType) {
                CIELO_NEGOTIATION_TYPE -> getString(R.string.arv_cielo_receivables)
                MARKET_NEGOTIATION_TYPE -> getString(R.string.arv_market_receivables)
                else -> EMPTY
            }

            with(arvAccountFees) {
                tvSelectionLabel.text = getString(
                    R.string.arv_selection_label,
                    getString(
                        when (simulationData.negotiationType) {
                            MARKET_NEGOTIATION_TYPE -> R.string.arv_acquirers
                            else -> R.string.arv_brands
                        }
                    )
                )
                tvSelection.text = getSelectionNames(simulationData)
                tvGrossValue.text = simulationData.grossAmount?.toPtBrRealString()
                tvFees.text =
                    getString(
                        R.string.arv_fees_format,
                        simulationData.effectiveFee?.toPtBrRealStringWithoutSymbol(),
                        simulationData.standardFee?.toPtBrRealStringWithoutSymbol()
                    )
                tvDiscountTotal.text =
                    getString(
                        R.string.arv_total_discount_format,
                        simulationData.discountAmount?.toPtBrRealString()
                    )
                tvNetValue.text = simulationData.netAmount?.toPtBrRealString()
            }
        }
    }

    private fun getSelectionNames(simulationData: ArvAnticipation): String {
        val data = when (simulationData.negotiationType) {
            CIELO_NEGOTIATION_TYPE -> simulationData.acquirers?.first()?.cardBrands
            else -> simulationData.acquirers
        }
        val selection = data?.filter { it?.isSelected == true }.orEmpty()
        val showMoreQuantity = selection.size - THREE

        val selectionNamesList = selection.mapNotNull { it?.name }.take(THREE).toMutableList()
        if(showMoreQuantity > ZERO) {
            selectionNamesList.add(
                when (simulationData.negotiationType) {
                    CIELO_NEGOTIATION_TYPE -> resources.getQuantityString(R.plurals.brand_plurals, showMoreQuantity, showMoreQuantity)
                    else -> resources.getQuantityString(R.plurals.acquirer_plurals, showMoreQuantity, showMoreQuantity)
                })
        }
        return selectionNamesList.toStringCommaSeparatedAndLast()
    }

    private fun setupSimulationByValue() {
        binding?.apply {
            arvAccountFees.tvHighlightTitle.text = getString(R.string.arv_highlights_value_flow)
            tvSimulatedPeriodLabel.gone()
            tvPeriod.gone()
            ivCalendar.gone()
        }
    }

    private fun setupSimulationByPeriod(simulationData: ArvAnticipation) {
        binding?.apply {
            tvPeriod.text = getString(R.string.arv_period_format,
                simulationData.initialDate?.let { formatDate(it) } ?: EMPTY,
                simulationData.finalDate?.let { formatDate(it) } ?: EMPTY
            )

            arvAccountFees.tvHighlightTitle.text = getString(R.string.arv_highlights_period_flow)
            arvAccountFees.tvEditBrands.text = getString(R.string.arv_edit, when(simulationData.negotiationType) {
                CIELO_NEGOTIATION_TYPE -> getString(R.string.arv_edit_brands)
                MARKET_NEGOTIATION_TYPE -> getString(R.string.arv_edit_acquirer)
                else -> EMPTY
            })
            arvAccountFees.barView2.visible()
            arvAccountFees.tvEditBrands.visible()
        }
    }

    private fun formatDate(apiDate: String) =
        DataCustomNew().setDateFromAPI(apiDate).formatBRDate()

    private fun setupUserData(userData: Pair<String, String>) {
        binding?.apply {
            tvReceiverEstablishmentName.text = userData.first
            tvReceiverEstablishmentCnpj.text = userData.second
        }
    }

    private fun onShowTryAgain(error: NewErrorMessage?) {
        trackException(error)
        binding?.arvAccount?.apply {
            accountContentGroup.gone()
            accountErrorGroup.visible()
            shimmerContainer.gone()
        }
    }

    private fun onShowLoadingArvBanks() {
        binding?.arvAccount?.apply {
            accountContentGroup.gone()
            accountErrorGroup.gone()
            shimmerContainer.visible()
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
                accountErrorGroup.gone()
                shimmerContainer.gone()
                accountContentGroup.visible()
            }
            btConfirm.isButtonEnabled = true
        }
        tempSelectedAccount = bank
    }

    private fun onShowArvBanks(banks: List<ArvBank>) {
        binding?.apply {
            arvAccount.btEditBrands.setOnClickListener {
                analytics.logScreenActionsWithTwoLabel(
                    whatTypeFlowReviewTag,
                    SINGLE_ARV,
                    negotiationTypeArv,
                    Label.BOTAO,
                    CHANGE_ADDRESS
                )

                CieloListBottomSheet
                    .create(
                        headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                            title = getString(R.string.arv_account_select_bs_title)
                        ),
                        layoutItemRes = R.layout.bottom_sheet_cielo_bank_account_item,
                        data = banks,
                        initialSelectedItem = arvSimulationViewModel.arvSelectedBankLiveData.value,
                        onViewBound = { bankAccount, isSelected, itemView ->
                            val bankAccountItemBinding =
                                BottomSheetCieloBankAccountItemBinding.bind(itemView)
                            bankAccountItemBinding.apply {
                                this.mainContainer.background = AppCompatResources.getDrawable(
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
                                val shouldShowReceiveToday = bankAccount.receiveToday && arvAnticipation?.eligibleTimeToReceiveToday ?: false
                                tvReceiveToday.visible(shouldShowReceiveToday)
                            }
                        },
                        onItemClicked = { account, position, bottomSheet ->
                            analytics.logScreenActionsCheckButton(
                                MODAL,
                                CLIQUE,
                                dialogFlowCurrent,
                                SINGLE_ARV,
                                negotiationTypeArv,
                                SELECT_ACCOUNT_BANK_TO_RECEIPT,
                                account.name
                            )
                            tempSelectedAccount = account
                            bottomSheet.updateSelectedPosition(position)
                        },
                        mainButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                            title = getString(R.string.confirmar),
                            onTap = {
                                tempSelectedAccount?.let { itBank ->
                                    analytics.logScreenActionsCheckButton(
                                        MODAL,
                                        CLIQUE,
                                        dialogFlowCurrent,
                                        SINGLE_ARV,
                                        negotiationTypeArv,
                                        SELECT_ACCOUNT_BANK_TO_RECEIPT,
                                        CONFIRM,
                                        itBank.name
                                    )
                                    with(arvSimulationViewModel) {
                                        handleBankSelect(
                                            itBank,
                                            arvSelectedBankLiveData.value?.receiveToday
                                        )
                                    }
                                }
                                it.dismiss()
                            }
                        )
                    ).show(
                        childFragmentManager,
                        this@ArvAnticipationSimulationFragment.javaClass.simpleName
                    ).also {
                        analytics.logScreenActionsOnWithFlowDialog(
                            MODAL,
                            EXHIBITION,
                            dialogFlowCurrent,
                            SINGLE_ARV,
                            negotiationTypeArv,
                            SELECT_ACCOUNT_BANK_TO_RECEIPT
                        )
                    }
            }
        }
    }

    private fun getBanksInformation() {
        arvSimulationViewModel.updateBanks()
    }

    private fun updateSimulationData() {
        args.simulationargs.let { arvSimulationViewModel.updateSimulationData(it) }
    }

    private fun onConfirmClick(v: View) {
        analytics.logScreenActionsWithTwoLabel(
            whatTypeFlowReviewTag,
            SINGLE_ARV,
            negotiationTypeArv,
            Label.BOTAO,
            COMPLETE_REQUEST
        )
        trackConfirmClick()
        CieloMessageBottomSheet.create(
            headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                title = getString(R.string.anticipation_confirmation_title)
            ),
            message = CieloMessageBottomSheet.Message(
                text = getString(
                    R.string.anticipation_confirmation_message,
                    arvEffectiveTImeViewModel.arvEffectiveTimeLiveData.value
                )
            ),
            mainButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                title = getString(R.string.confirmar),
                onTap = {
                    analytics.logScreenActionsCheckButton(
                        MODAL,
                        CLIQUE,
                        dialogFlowCurrent,
                        SINGLE_ARV,
                        negotiationTypeArv,
                        ATENTION_INFO_BEFORE_TO_CONFIRM,
                        CONFIRM
                    )
                    it.dismissAllowingStateLoss()
                    confirmAnticipation()
                }
            )
        ).show(childFragmentManager, tag)
        analytics.logScreenActionsOnWithFlowDialog(
            MODAL,
            EXHIBITION,
            dialogFlowCurrent,
            SINGLE_ARV,
            negotiationTypeArv,
            ATENTION_INFO_BEFORE_TO_CONFIRM
        )
    }

    private fun confirmAnticipation() {
        val arvToken = arvSimulationViewModel.arvSimulationDataLiveData.value?.token ?: return
        val arvBank = arvSimulationViewModel.arvSelectedBankLiveData.value ?: return
        arvSimulationViewModel.confirmAnticipation(
            arvToken = arvToken,
            arvBank = arvBank
        )
    }

    private fun onShowLoading() {
        navigation?.showAnimatedLoading()
    }

    private fun onHideLoading() {
        navigation?.hideAnimatedLoading()
    }

    private fun onFeeShowLoading() {
        binding?.arvAccountFees?.apply {
            accountFeeContentGroup.gone()
            shimmerContainer.visible()
        }
    }

    private fun onFeeHideLoading() {
        binding?.arvAccountFees?.apply {
            accountFeeContentGroup.visible()
            shimmerContainer.gone()
        }
    }

    private fun onFeeLoadingError(action: (() -> Unit)?) {
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
                    action?.invoke()
                }
            )
        }
    }

    private fun showPurpleAlert() {
        binding?.purpleAlert?.tvPurpleAlert?.text = getString(
            R.string.arv_purple_alert,
            arvEffectiveTImeViewModel.arvEffectiveTimeLiveData.value
        )
        binding?.purpleAlert?.purpleAlertContainer.visible()
    }

    private fun hidePurpleAlert() {
        binding?.purpleAlert?.purpleAlertContainer.gone()
    }

    private fun onConfirmAnticipationSuccess() {
        navigation?.showAnimatedLoadingSuccess {
            trackSingleConfirmationAndPurchase()
            doWhenResumed {
                navigation?.showCustomHandlerView(
                    contentImage = R.drawable.ic_129_anticipation,
                    title = getString(R.string.anticipation_success_request_title),
                    message = getString(R.string.anticipation_success_request_message),
                    labelSecondButton = getString(R.string.acompanhar_status),
                    isShowButtonClose = true,
                    titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                    messageAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                    callbackClose = {
                        analytics.logScreenActionsWithTwoLabel(
                            REQUEST_MADE_SUCCESSFULLY,
                            dialogFlowCurrent,
                            SINGLE_ARV,
                            negotiationTypeArv,
                            BOTAO,
                            FECHAR
                        )
                        findNavController().safeNavigate(
                            ArvAnticipationSimulationFragmentDirections
                                .actionArvAnticipationSimulationFragmentToArvHomeFragment()
                        )
                    },
                    callbackSecondButton = {
                        analytics.logScreenActionsWithTwoLabel(
                            REQUEST_MADE_SUCCESSFULLY,
                            dialogFlowCurrent,
                            SINGLE_ARV,
                            negotiationTypeArv,
                            BOTAO,
                            TRACK_STATUS
                        )
                        findNavController().safeNavigate(
                            ArvAnticipationSimulationFragmentDirections
                                .actionArvAnticipationSimulationFragmentToArvHistoricListFragment()
                        )
                    }
                )
            }
        }
    }

    private fun onConfirmAnticipationErrorToken(error: NewErrorMessage? = null) {
        onConfirmAnticipationError(error)
    }

    private fun onConfirmAnticipationNotEligibleError(error: NewErrorMessage? = null) {
        trackException(error)
        doWhenResumed {
            navigation?.showCustomHandlerView(
                title =
                getString(
                    R.string.text_funcionality_dont_free_title,
                ),
                message = getString(R.string.text_funcionality_dont_free_subtitle),
                labelSecondButton = getString(R.string.text_lgpd_saiba_mais),
                isShowButtonClose = true,
                callbackClose = { requireActivity().finish() },
                titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                messageAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                callbackSecondButton = ::goToHelpCenter,
            )
        }.also {
            onHideLoading()
        }
    }

    private fun goToHelpCenter() {
        trackClick()
        requireActivity().startActivity<CentralAjudaSubCategoriasEngineActivity>(
            TAG_KEY_HELP_CENTER to TAG_HELP_CENTER_MFA,
            ARG_PARAM_SUBCATEGORY_NAME to getString(R.string.text_token),
            NOT_CAME_FROM_HELP_CENTER to true,
        )
        requireActivity().finish()
    }

    private fun trackClick() {
        arvAnalytics.logClick(
            screenName = SCREEN_VIEW_ARV_SINGLE_CONFIRMATION,
            contentName = ArvAnalyticsGA4Constants.KNOW_MORE_BTN,
        )
    }

    private fun onConfirmAnticipationError(error: NewErrorMessage?) {
        analytics.logEventCallback(
            CALLBACK,
            SINGLE_ARV,
            negotiationTypeArv,
            RESIDENCE,
            tempSelectedAccount?.name,
            error
        )
        val isInsufficientBalanceError =
            error?.flagErrorCode == ArvConstants.INVALID_RECEIVABLE_AMOUNT
                    || error?.httpCode == HTTP_ENHANCE

        if (isInsufficientBalanceError) {
            analyticsScreenView(screenViewNotEnoughBalanceCurrentFlow)
            arvAnalytics.logDisplayContent(
                SCREEN_VIEW_ARV_SINGLE_CONFIRMATION,
                INSUFFICIENT_FUNDS
            )
        } else {
            analyticsScreenView(screenViewErrorMfaCurrentFlow)
            arvAnalytics.logException(
                SCREEN_VIEW_ARV_SINGLE_CONFIRMATION,
                error
            )
        }

        doWhenResumed {
            navigation?.showCustomHandlerView(
                title = getString(
                    if (isInsufficientBalanceError)
                        R.string.anticipation_error_insufficient_balance_title
                    else
                        R.string.commons_generic_error_title
                ),
                message = getString(
                    if (isInsufficientBalanceError)
                        R.string.anticipation_error_insufficient_balance_message
                    else
                        R.string.commons_generic_error_message
                ),
                labelSecondButton = getString(R.string.entendi),
                isShowButtonClose = true,
                titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                messageAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                callbackSecondButton = {
                    if (isInsufficientBalanceError)
                        analyticsConfirmAnticipationError(NOT_ENOUGH_BALANCE, VOLTAR)
                    else
                        analyticsConfirmAnticipationError(HAVE_A_PROBLEM, UNDERSTOOD)
                },
                callbackClose = {
                    if (isInsufficientBalanceError)
                        analyticsConfirmAnticipationError(NOT_ENOUGH_BALANCE, FECHAR)
                    else
                        analyticsConfirmAnticipationError(HAVE_A_PROBLEM, FECHAR)
                }
            ).also {
                onHideLoading()
            }
        }
    }

    private fun analyticsConfirmAnticipationError(label: String, action: String) {
        analytics.logScreenActionsWithThreeLabel(
            label,
            MFA,
            dialogFlowCurrent,
            SINGLE_ARV,
            negotiationTypeArv,
            BOTAO,
            action
        )
    }

    private fun analyticsScreenView(screen: String) {
        analytics.logScreenView(
            name = screen,
            className = this.javaClass
        )
    }

    override fun onHelpButtonClicked() {
        analytics.logScreenActionsWithTwoLabel(
            whatTypeFlowReviewTag,
            SINGLE_ARV,
            negotiationTypeArv,
            Label.BOTAO,
            HELP
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        analytics.logScreenActionsWithTwoLabel(
            whatTypeFlowReviewTag,
            SINGLE_ARV,
            negotiationTypeArv,
            Label.BOTAO,
            VOLTAR
        )
    }

    private fun checkTypeArvReceivableForAnalytics(arvAnticipation: ArvAnticipation?) {
        negotiationTypeArv = if (arvAnticipation?.negotiationType == CIELO_NEGOTIATION_TYPE)
            RECEIVABLES_CIELO
        else
            RECEIVABLES_MARKET
    }

    private fun checkFlowArvSingleAnticipationForAnalytics(arvAnticipation: ArvAnticipation?) {
        when (arvAnticipation?.simulationType) {
            SIMULATION_TYPE_VALUE -> {
                whatTypeFlowCurrent = SCREEN_VIEW_ARV_REVIEW_ANTICIPATION_WITH_VALUE
                whatTypeFlowReviewTag = ARV_REVIEW_AND_REQUEST_ANTICIPATION_WITH_VALUE
                dialogFlowCurrent = VALUE_FLOW
                screenViewSuccessCurrentFlow = SCREEN_VIEW_ARV_SUCCESS_REQUEST_WITH_VALUE
                screenViewErrorMfaCurrentFlow = SCREEN_VIEW_ARV_MFA_PROBLEM_WITH_VALUE
                screenViewNotEnoughBalanceCurrentFlow =
                    SCREEN_VIEW_ARV_NOT_ENOUGH_BALANCE_WITH_VALUE
                errorMfaCurrentFlow = HAVE_A_PROBLEM_MFA_WITH_VALUE
                errorNotEnoughBalanceCurrentFlow = NOT_ENOUGH_BALANCE_WITH_VALUE

            }

            else -> {
                whatTypeFlowCurrent = SCREEN_VIEW_ARV_REVIEW_ANTICIPATION_WITH_PERIOD
                whatTypeFlowReviewTag = ARV_REVIEW_AND_REQUEST_ANTICIPATION_WITH_PERIOD
                dialogFlowCurrent = PERIOD_FLOW
                screenViewSuccessCurrentFlow = SCREEN_VIEW_ARV_SUCCESS_REQUEST_WITH_PERIOD
                screenViewErrorMfaCurrentFlow = SCREEN_VIEW_ARV_MFA_PROBLEM_WITH_PERIOD
                screenViewNotEnoughBalanceCurrentFlow =
                    SCREEN_VIEW_ARV_NOT_ENOUGH_BALANCE_WITH_PERIOD
                errorMfaCurrentFlow = HAVE_A_PROBLEM_MFA_WITH_PERIOD
                errorNotEnoughBalanceCurrentFlow = NOT_ENOUGH_BALANCE_WITH_PERIOD

            }
        }
    }

    private fun analyticsScreenViewGA4() {
        if (arvAnticipation?.simulationType != SIMULATION_TYPE_VALUE) {
            arvAnalytics.logScreenView(
                SCREEN_VIEW_ARV_SINGLE_CONFIRMATION
            )
        }
    }

    private fun trackConfirmClick() {
        arvAnalytics.logAnticipationSingleAddPaymentInfo(
            value = args.simulationargs.netAmount ?: ZERO_DOUBLE,
            periodStart = args.simulationargs.initialDate,
            periodEnd = args.simulationargs.finalDate,
            bankName = tempSelectedAccount?.name ?: EMPTY,
            itemCategory3 = args.simulationargs.negotiationType ?: EMPTY
        )
    }

    private fun trackException(error: NewErrorMessage?) {
        analytics.logCallbackErrorEvent(
            whatTypeFlowReviewTag,
            SINGLE_ARV,
            negotiationTypeArv,
            EXHIBITION,
            error
        )
        arvAnalytics.logException(
            SCREEN_VIEW_ARV_SINGLE_CONFIRMATION,
            error
        )
    }

    private fun trackSingleConfirmationAndPurchase() {
        analyticsScreenView(screenViewSuccessCurrentFlow)
        analytics.logEventCallback(
            CALLBACK,
            SINGLE_ARV,
            negotiationTypeArv,
            RESIDENCE,
            tempSelectedAccount?.name,
            null
        )
        arvAnalytics.logScreenView(
            screenName = SCREEN_VIEW_ARV_SINGLE_SUCCESS
        )
        arvAnalytics.logSingleAnticipationSuccessPurchase(
            value = args.simulationargs.netAmount ?: ZERO_DOUBLE,
            transactionId = ArvAnalyticsGA4.tArvSingle,
            periodStart = args.simulationargs.initialDate,
            periodEnd = args.simulationargs.finalDate,
            bankName = tempSelectedAccount?.name ?: EMPTY,
            itemCategory3 = args.simulationargs.negotiationType ?: EMPTY
        )
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}