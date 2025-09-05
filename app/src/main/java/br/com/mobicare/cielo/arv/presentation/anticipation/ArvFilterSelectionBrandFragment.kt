package br.com.mobicare.cielo.arv.presentation.anticipation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.bottomsheet.CieloContentBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.button.v2.CieloButton
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.ALL_ARV
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.ATTENTION
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.EDITING_FLAGS
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.EXHIBITION
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.HAVE_A_PROBLEM
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.HELP
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SAVE_CHANGES
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SCREEN_VIEW_ARV_EDIT_FLAGS_LOAD_WITH_PERIOD
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SCREEN_VIEW_ARV_SIMULATED_WITH_PERIOD_ERROR
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SEE_MORE_BTN
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SELECT_AT_LEAST_ONE_FLAG
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV_SINGLE_CONFIRMATION
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV_SINGLE_CONFIRMATION_FLAG_SELECTION
import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.presentation.anticipation.adapter.ArvFilterSelectionAdapter
import br.com.mobicare.cielo.arv.presentation.anticipation.adapter.ArvSelectableItem
import br.com.mobicare.cielo.arv.utils.ArvConstants
import br.com.mobicare.cielo.arv.utils.UiArvBrandsSelectionState
import br.com.mobicare.cielo.commons.analytics.Action.CALLBACK
import br.com.mobicare.cielo.commons.analytics.Action.FECHAR
import br.com.mobicare.cielo.commons.analytics.Action.MODAL
import br.com.mobicare.cielo.commons.analytics.Action.UNDERSTOOD
import br.com.mobicare.cielo.commons.analytics.Action.VOLTAR
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.AccessibilityUtils
import br.com.mobicare.cielo.commons.utils.getNewErrorMessage
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.toPtBrRealStringWithoutSymbol
import br.com.mobicare.cielo.databinding.FragmentArvFilterBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.safeNavigate
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.BigDecimal


class ArvFilterSelectionBrandFragment : BaseFragment(), CieloNavigationListener {

    private val arvFilterSelectionViewModel: ArvFilterSelectionViewModel by viewModel()

    private var binding: FragmentArvFilterBinding? = null
    private var navigation: CieloNavigation? = null
    private val analytics: ArvAnalytics by inject()
    private val arvAnalytics: ArvAnalyticsGA4 by inject()
    private val args: ArvFilterSelectionBrandFragmentArgs by navArgs()
    private lateinit var adapter: ArvFilterSelectionAdapter

    private val initialList by lazy { getListFromAnticipation(args.brandsselectionargs) }
    private var currentList: List<ArvSelectableItem>? = null
    private var negotiationTypeArv: String? = null
    private var arvAnticipation: ArvAnticipation = ArvAnticipation()
    private lateinit var whatTypeFlowCurrent: String
    private lateinit var whatTypeFlowReviewTag: String
    private lateinit var loadFlowCurrent: String
    private lateinit var screenViewFlowCurrent: String
    private lateinit var dialogFlowCurrent: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentArvFilterBinding.inflate(inflater, container, false)
        .also {
            binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupObservers()
        setupListeners()
        getArvAnticipation()
        checkFlowArvSingleAnticipationForAnalytics(arvAnticipation)
        setupHeader()
        setupRecyclerView()
        setupFooterView(initialList)
        analyticsScreenView(whatTypeFlowCurrent)
    }

    private fun setupListeners() {
        binding?.apply {
            footer.infoButton.setOnClickListener {
                analytics.logScreenActionsWithTwoLabel(
                    whatTypeFlowReviewTag,
                    ArvAnalytics.SINGLE_ARV,
                    negotiationTypeArv,
                    Label.BOTAO,
                    SEE_MORE_BTN
                )
                showFeesInfoBS()
            }
        }

        binding?.btnSaveChanges?.setOnClickListener {
            analytics.logScreenActionsWithTwoLabel(
                whatTypeFlowReviewTag,
                ArvAnalytics.SINGLE_ARV,
                negotiationTypeArv,
                Label.BOTAO,
                SAVE_CHANGES
            )
            currentList?.let { cardBrandList ->
                if (cardBrandList.none { it.isSelected }) {
                    analytics.logScreenActionsOnWithFlowDialog(
                        MODAL,
                        EXHIBITION,
                        dialogFlowCurrent,
                        ArvAnalytics.SINGLE_ARV,
                        negotiationTypeArv,
                        ATTENTION,
                        SELECT_AT_LEAST_ONE_FLAG
                    )
                    CieloDialog.create(
                        title = getString(R.string.arv_empty_brands_dialog_title),
                        message = getString(
                            if (arvAnticipation.negotiationType == ArvConstants.CIELO_NEGOTIATION_TYPE)
                                R.string.arv_empty_brands_dialog_message
                            else
                                R.string.arv_empty_acquirers_dialog_message
                        )
                    ).setTitleTextAlignment(View.TEXT_ALIGNMENT_CENTER)
                        .setPrimaryButton(getString(R.string.text_close))
                        .show(childFragmentManager, tag)
                } else {
                    arvFilterSelectionViewModel.updateAnticipation(
                        previousAnticipation = args.brandsselectionargs,
                        selectedList = cardBrandList,
                        receiveToday = args.receivetodayargs
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        arvAnalytics.logScreenView(
            SCREEN_VIEW_ARV_SINGLE_CONFIRMATION_FLAG_SELECTION
        )
    }

    private fun showFeesInfoBS() {
        CieloContentBottomSheet
            .create(
                headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                    title = getString(R.string.arv_about_discounts_dialog_title)
                ),
                contentLayoutRes = R.layout.bottom_sheet_fees_info,
                onContentViewCreated = { view, bs ->
                    view.findViewById<TextView>(R.id.tvInfo).apply {
                        text = getString(
                            R.string.arv_fees_format,
                            args.brandsselectionargs.effectiveFee?.toPtBrRealStringWithoutSymbol(),
                            args.brandsselectionargs.standardFee?.toPtBrRealStringWithoutSymbol()
                        )
                    }
                    view.findViewById<CieloButton>(R.id.btUnderstand).apply {
                        setOnClickListener { bs.dismiss() }
                    }
                },
                disableExpandableMode = true
            )
            .show(childFragmentManager, EMPTY)
    }

    private fun setupObservers() {
        arvFilterSelectionViewModel.arvBrandsSelectionLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiArvBrandsSelectionState.ShowLoadingAnticipation -> showLoading()
                is UiArvBrandsSelectionState.HideLoadingAnticipation -> hideLoading()
                is UiArvBrandsSelectionState.ShowError -> showError(uiState.error)
                is UiArvBrandsSelectionState.SuccessLoadArvAnticipation -> navigateToSimulation(
                    uiState.anticipation
                )
            }
        }
    }

    private fun hideLoading() {
        navigation?.hideAnimatedLoading()
    }

    private fun navigateToSimulation(anticipation: ArvAnticipation) {
        analytics.logEventLoadCallback(
            EDITING_FLAGS,
            loadFlowCurrent,
            ArvAnalytics.SINGLE_ARV,
            negotiationTypeArv,
            CALLBACK,
            null
        )
        findNavController().safeNavigate(
            ArvFilterSelectionBrandFragmentDirections.actionArvQueryByBrandFragmentToArvAnticipationSimulationFragment(
                anticipation
            )
        )
    }

    private fun showError(error: NewErrorMessage?) {
        trackException(error)
        doWhenResumed {
            navigation?.showCustomHandlerView(
                title = getString(R.string.commons_generic_error_title),
                message = requireContext().getNewErrorMessage(
                    error,
                    R.string.commons_generic_error_message
                ),
                labelSecondButton = getString(R.string.entendi),
                isShowButtonClose = true,
                callbackSecondButton = {
                    analyticsButtonEvent(UNDERSTOOD)
                },
                callbackClose = {
                    analyticsButtonEvent(FECHAR)
                }
            )
        }
    }

    private fun showLoading() {
        navigation?.showAnimatedLoading(R.string.wait_a_moment_message)
        analyticsScreenView(SCREEN_VIEW_ARV_EDIT_FLAGS_LOAD_WITH_PERIOD)
    }

    private fun getArvAnticipation() {
        arvAnticipation = args.brandsselectionargs
        checkTypeArvReceivableForAnalytics(arvAnticipation)
    }


    private fun getListFromAnticipation(anticipation: ArvAnticipation): List<ArvSelectableItem> {
        checkTypeArvReceivableForAnalytics(arvAnticipation)
        return when(anticipation.negotiationType) {
                ArvConstants.CIELO_NEGOTIATION_TYPE -> anticipation.acquirers?.first()?.cardBrands.orEmpty()
            else -> anticipation.acquirers?.filterNotNull().orEmpty()
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.showHelpButton(isShow = true)
            navigation?.setupToolbar(
                title = getString(
                    R.string.arv_query_by_flags_title,
                    if (args.brandsselectionargs.negotiationType == ArvConstants.CIELO_NEGOTIATION_TYPE)
                        getString(R.string.arv_edit_brands)
                    else getString(R.string.arv_edit_acquirer)
                ),
                isCollapsed = false
            )
        }
    }

    private fun setupHeader() {
        binding?.selectAllCheckbox?.apply {
            isChecked = initialList.all { it.isSelected }
            setOnClickListener {
                onHeaderClick(isChecked)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = ArvFilterSelectionAdapter(
            initialList.map { it.copy() },
            ::onListUpdate,
            negotiationTypeArv,
            analytics,
            arvAnalytics,
            whatTypeFlowReviewTag
        )
        binding?.recyclerView?.adapter = adapter
    }

    private fun onHeaderClick(allChecked: Boolean) {
        adapter.selectAll(allChecked)
        if (allChecked) {
            analytics.logScreenActionsWithCheckButton(
                whatTypeFlowReviewTag,
                ArvAnalytics.SINGLE_ARV,
                negotiationTypeArv,
                ArvAnalytics.CHECKED,
                Label.CHECK_BOX,
                ALL_ARV
            )
        } else {
            analytics.logScreenActionsWithCheckButton(
                whatTypeFlowReviewTag,
                ArvAnalytics.SINGLE_ARV,
                negotiationTypeArv,
                ArvAnalytics.UNCHECKED,
                Label.CHECK_BOX,
                ALL_ARV
            )
        }
    }

    private fun onListUpdate(list: List<ArvSelectableItem>) {
        setupFooterView(list)
        currentList = list
        binding?.selectAllCheckbox?.isChecked = list.all { it.isSelected }

        binding?.apply {
            if (initialList == list) {
                footer.tvValuesWarn.gone()
                btnSaveChanges.gone()
            } else {
                footer.tvValuesWarn.visible()
                btnSaveChanges.visible()
            }
        }
    }

    private fun setupFooterView(list: List<ArvSelectableItem>?) {
        binding?.footer?.apply {
            list.orEmpty().filter { it.isSelected }.let { arvSelectableItems ->
                val grossAmount = arvSelectableItems.sumOf { BigDecimal.valueOf(it.grossAmount ?: ZERO_DOUBLE) }
                grossValue.text = grossAmount.toPtBrRealString()
                grossValue.contentDescription = AccessibilityUtils.convertAmount(
                    grossAmount.toDouble(),
                    requireContext()
                )
                val discountAmount =
                    arvSelectableItems.sumOf { BigDecimal.valueOf(it.discountAmount ?: ZERO_DOUBLE) }
                discountValue.text = discountAmount.toPtBrRealString()
                discountValue.contentDescription = AccessibilityUtils.convertAmount(
                    discountAmount.toDouble(),
                    requireContext()
                )
                val netAmount = arvSelectableItems.sumOf { BigDecimal.valueOf(it.netAmount ?: ZERO_DOUBLE) }
                netAmountReceivableValue.text = netAmount.toPtBrRealString()
                netAmountReceivableValue.contentDescription = AccessibilityUtils.convertAmount(
                    netAmount.toDouble(),
                    requireContext()
                )
            }
        }
    }

    private fun analyticsScreenView(screen: String) {
        analytics.logScreenView(
            name = screen,
            className = this.javaClass
        )
    }

    private fun analyticsButtonEvent(buttonType: String) {
        analytics.logScreenActionsWithTwoLabel(
            HAVE_A_PROBLEM,
            whatTypeFlowReviewTag,
            ArvAnalytics.SINGLE_ARV,
            negotiationTypeArv,
            Label.BOTAO,
            buttonType
        )
    }

    override fun onHelpButtonClicked() {
        analytics.logScreenActionsWithTwoLabel(
            whatTypeFlowReviewTag,
            ArvAnalytics.SINGLE_ARV,
            negotiationTypeArv,
            Label.BOTAO,
            HELP
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        analytics.logScreenActionsWithTwoLabel(
            whatTypeFlowReviewTag,
            ArvAnalytics.SINGLE_ARV,
            negotiationTypeArv,
            Label.BOTAO,
            VOLTAR
        )
    }

    private fun checkTypeArvReceivableForAnalytics(arvAnticipation: ArvAnticipation) {
        negotiationTypeArv =
            if (arvAnticipation.negotiationType == ArvConstants.CIELO_NEGOTIATION_TYPE)
                ArvAnalytics.RECEIVABLES_CIELO
            else
                ArvAnalytics.RECEIVABLES_MARKET

    }

    private fun checkFlowArvSingleAnticipationForAnalytics(arvAnticipation: ArvAnticipation?) {
        when (arvAnticipation?.simulationType) {
            ArvConstants.SIMULATION_TYPE_VALUE -> {
                whatTypeFlowCurrent = ArvAnalytics.SCREEN_VIEW_ARV_EDIT_FLAG_BRAND_WITH_VALUE
                whatTypeFlowReviewTag = ArvAnalytics.EDITING_FLAGS_WITH_VALUE
                loadFlowCurrent = ArvAnalytics.EDIT_FLAGS_LOAD_WITH_VALUE
                screenViewFlowCurrent = ArvAnalytics.SCREEN_VIEW_ARV_EDIT_FLAGS_LOAD_WITH_VALUE
                dialogFlowCurrent = ArvAnalytics.VALUE_FLOW
            }

            else -> {
                whatTypeFlowCurrent = ArvAnalytics.SCREEN_VIEW_ARV_EDIT_FLAG_BRAND_WITH_PERIOD
                whatTypeFlowReviewTag = ArvAnalytics.EDITING_FLAGS_WITH_PERIOD
                loadFlowCurrent = ArvAnalytics.EDIT_FLAGS_LOAD_WITH_PERIOD
                screenViewFlowCurrent = ArvAnalytics.SCREEN_VIEW_ARV_EDIT_FLAGS_LOAD_WITH_PERIOD
                dialogFlowCurrent = ArvAnalytics.PERIOD_FLOW
            }
        }
    }

    private fun trackException(error: NewErrorMessage?) {
        analytics.logScreenView(
            SCREEN_VIEW_ARV_SIMULATED_WITH_PERIOD_ERROR,
            className = this.javaClass
        )
        analytics.logEventLoadCallback(
            EDITING_FLAGS,
            loadFlowCurrent,
            ArvAnalytics.SINGLE_ARV,
            negotiationTypeArv,
            CALLBACK,
            error
        )
        arvAnalytics.logException(
            SCREEN_VIEW_ARV_SINGLE_CONFIRMATION,
            error
        )
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}