package br.com.mobicare.cielo.arv.presentation.anticipation

import android.content.Context
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.util.extensions.visible
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.ANTICIPATION
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.ANTICIPATION_WITH_PERIOD
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.EXHIBITION
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.FIELD_OF
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.FIELD_UNTIL
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.RECEIVABLES_CIELO
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.RECEIVABLES_MARKET
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.RELOAD
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SIMULATED_ANTICIPATION_WITH_VALUE
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SIMULATE_ARV_ANTICIPATION
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SIMULATE_FILL_PERIOD
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SINGLE_ARV
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.TAG_SUCCESS_EMPTY_VALUE
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.ANTICIPATION_PERIOD_VALUE
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.NO_BALANCE_AVAILABLE
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV_SINGLE_CONFIGURATION
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SIMULATE_VALUE
import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.utils.ArvConstants.CIELO_NEGOTIATION_TYPE
import br.com.mobicare.cielo.arv.utils.ArvConstants.MARKET_NEGOTIATION_TYPE
import br.com.mobicare.cielo.arv.utils.UiArvSingleFeatureToggleState
import br.com.mobicare.cielo.arv.utils.UiArvSingleWithDateState
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Action.CLIQUE
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.constants.ONE_NEGATIVE
import br.com.mobicare.cielo.commons.constants.Text.NEW_LINE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.CalendarCustom
import br.com.mobicare.cielo.commons.utils.CalendarDialogCustom
import br.com.mobicare.cielo.commons.utils.DataCustomNew
import br.com.mobicare.cielo.commons.utils.EMPTY_VALUE
import br.com.mobicare.cielo.commons.utils.dateFormatToBr
import br.com.mobicare.cielo.commons.utils.formatterErrorMessage
import br.com.mobicare.cielo.commons.utils.parseToLocalDatePT
import br.com.mobicare.cielo.commons.utils.spannable.addSpannable
import br.com.mobicare.cielo.commons.utils.toCalendar
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.FragmentArvSingleAnticipationBinding
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.extensions.setChildrenEnabled
import br.com.mobicare.cielo.extensions.setColouredSpan
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar

class ArvSingleAnticipationFragment : BaseFragment(), CieloNavigationListener {

    private val viewModel: ArvSingleAnticipationViewModel by viewModel()
    private var binding: FragmentArvSingleAnticipationBinding? = null
    private var navigation: CieloNavigation? = null
    private val args: ArvSingleAnticipationFragmentArgs by navArgs()
    private var arvAnticipation: ArvAnticipation? = null
    private var selectStartDate: DataCustomNew? = null
    private var selectEndDate: DataCustomNew? = null
    private val analytics: ArvAnalytics by inject()
    private val arvAnalytics: ArvAnalyticsGA4 by inject()
    private var negotiationTypeArv: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentArvSingleAnticipationBinding.inflate(
        inflater, container, false
    ).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitAnticipation()
        setupDate()
        analyticsScreenView()
        setupNavigation()
        setupRadioButtons()
        setupListeners()
        setupObservers()
        onShowInitSingleAnticipationInfo(arvAnticipation)
        checkTypeArvReceivableForAnalytics()
    }

    override fun onResume() {
        super.onResume()
        analyticsScreenViewGA4()
    }

    private fun setupRadioButtons() {
        binding?.apply {
            if (args.arvCieloAnticipation == null) {
                RBCielo.isEnabled = false
            } else {
                RBCielo.setColouredSpan(
                    getString(R.string.arv_cielo_receivables_colored_substring),
                    ContextCompat.getColor(requireContext(), R.color.brand_400)
                )
                RBCielo.isEnabled = true
            }
            if (args.arvMarketAnticipation == null) {
                RBMarket.isEnabled = false
            } else {
                RBMarket.setColouredSpan(
                    getString(R.string.arv_market_receivables_colored_substring),
                    ContextCompat.getColor(requireContext(), R.color.brand_400)
                )
                RBMarket.isEnabled = true
            }

            RBNegotiationType.check(
                when (arvAnticipation?.negotiationType) {
                    CIELO_NEGOTIATION_TYPE -> R.id.RBCielo
                    MARKET_NEGOTIATION_TYPE -> R.id.RBMarket
                    else -> ONE_NEGATIVE
                }
            )
        }
    }

    private fun setupInitAnticipation() {
        if (arvAnticipation == null) {
            arvAnticipation = args.arvCieloAnticipation ?: args.arvMarketAnticipation
            viewModel.receivableType = arvAnticipation?.negotiationType
        }
    }

    private fun setupDate() {
        viewModel.updateDateRange(
            arvAnticipation?.initialDate.dateFormatToBr(),
            arvAnticipation?.finalDate.dateFormatToBr()
        )
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.showHelpButton(isShow = true)
            navigation?.showContainerButton(isShow = false)
            navigation?.setupToolbar(
                title = getString(R.string.single_anticipation_title),
                isCollapsed = false
            )
        }
    }

    private fun setupObservers() {
        setupSingleAnticipationLiveDataObserver()
        setupMarketFTLiveDataObserver()
        setupDateRangeLiveDataObserver()
        setupFixedMonthRangeLiveDataObserver()
    }

    private fun setupFixedMonthRangeLiveDataObserver() {
        viewModel.monthsDifferenceLiveData.observe(viewLifecycleOwner) {
            binding?.monthSelectChipGroup?.apply {
                check(
                    when (it) {
                        TWENTY_FOUR_MONTHS -> R.id.chip24Months
                        TWELVE_MONTHS -> R.id.chip12Months
                        SIX_MONTHS -> R.id.chip6Months
                        THREE_MONTHS -> R.id.chip3Months
                        ONE_MONTH -> R.id.chip1Month
                        else -> {
                            clearCheck()
                            View.NO_ID
                        }
                    }
                )
            }
        }
    }

    private fun setupDateRangeLiveDataObserver() {
        viewModel.arvDateRangeLiveData.observe(viewLifecycleOwner) {
            binding?.apply {
                tvStartDateCalendar.apply {
                    text = context?.getString(
                        R.string.detached_anticipation_select_date_in, it.first
                    )
                }
                tvEndDateCalendar.apply {
                    text = context?.getString(
                        R.string.detached_anticipation_select_date_until, it.second
                    )
                }
            }
        }
    }

    private fun setupMarketFTLiveDataObserver() {
        viewModel.arvMarketToggleLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                UiArvSingleFeatureToggleState.Disabled -> onDisabledMarket()
            }
        }
    }

    private fun setupSingleAnticipationLiveDataObserver() {
        viewModel.arvSingleAnticipationWithDataLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiArvSingleWithDateState.ShowLoadingArvSingleWithDate -> onShowLoadingArvSingleWithData()
                is UiArvSingleWithDateState.HideLoadingArvSingleWithDate -> onHideLoadingArvSingleWithData()
                is UiArvSingleWithDateState.SuccessArvSingleWithDate -> onShowSingleAnticipationWithDateInfo(
                    uiState.anticipation
                )

                is UiArvSingleWithDateState.ErrorArvSingleWithDate -> onArvSingleError(
                    uiState.error, requireContext().formatterErrorMessage(uiState.message)
                )

                is UiArvSingleWithDateState.NoValuesToAnticipate -> showIsNotArvSingleAnticipateValue()
            }
        }
    }

    private fun onDisabledMarket() {
        binding?.RBMarket?.apply {
            isEnabled = false
            text = formatDisabledTitleAndSubtitle(
                R.string.arv_market_receivables,
                R.string.arv_negotiation_temporarily_unavailable
            )
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
        append(NEW_LINE)
        append(
            getString(subtitle)
                .addSpannable(
                    TextAppearanceSpan(
                        requireActivity(),
                        R.style.medium_montserrat_12_cloud_400
                    )
                )
        )
    }

    private fun getArvSingleAnticipationWithDate() {
        viewModel.getArvSingleAnticipationWithDate()
    }

    private fun showCalendar(isStartDate: Boolean) {
        val selectDateAux = viewModel.arvDateRangeLiveData.value?.let {
            if (isStartDate) it.first else it.second
        }

        val startDate = viewModel.minSelectableCalendar.toCalendar()
        val endDate = viewModel.maxSelectableCalendar.toCalendar()

        selectDateAux?.parseToLocalDatePT()?.toCalendar()
            ?.let { openCalendarDialog(startDate, endDate, it, isStartDate) }
    }

    private fun openCalendarDialog(
        startDate: Calendar,
        endDate: Calendar,
        cal: Calendar,
        isStartDate: Boolean
    ) {
        CalendarDialogCustom(
            null,
            null,
            ZERO,
            CalendarCustom.getDay(cal),
            CalendarCustom.getMonth(cal),
            CalendarCustom.getYear(cal),
            getString(if (isStartDate) R.string.calendar_init_date_label else R.string.calendar_end_date_label),
            context as Context,
            { _, year, monthOfYear, dayOfMonth ->
                val dateFilter = DataCustomNew()
                dateFilter.setDate(year, monthOfYear, dayOfMonth)
                if (isStartDate) {
                    selectStartDate = dateFilter
                    setDataInputDate(isStartDate = true)
                } else {
                    selectEndDate = dateFilter
                    setDataInputDate(isStartDate = false)
                }
            },
            R.style.DialogThemeArvCalendar,
            endDate,
            startDate,
        ).show()
    }

    private fun setDataInputDate(isStartDate: Boolean) {
        val date = if (isStartDate) selectStartDate else selectEndDate
        val value = date?.formatBRDate()

        value?.let {
            if (isStartDate) {
                analytics.logScreenActionsWithDates(
                    ANTICIPATION, SIMULATE_FILL_PERIOD, SINGLE_ARV, negotiationTypeArv, FIELD_OF, it
                )
                viewModel.updateDateRange(initDate = it)
            } else {
                selectStartDate?.formatBRDate()?.let { startDate ->
                    analytics.logScreenActionsWithDates(
                        ANTICIPATION,
                        SIMULATE_FILL_PERIOD,
                        SINGLE_ARV,
                        negotiationTypeArv,
                        FIELD_UNTIL,
                        startDate,
                        it
                    )
                }
                viewModel.updateDateRange(endDate = it)
            }
        }
        getArvSingleAnticipationWithDate()
    }

    private fun onShowLoadingArvSingleWithData() {
        arvAnticipation = null
        binding?.apply {
            setupViewOnError(true)
            clContainerValueInformation.gone()
            notAvailableValueInformation.root.gone()
            shimmerAnticipationType.apply {
                visible()
                contentDescription = getString(R.string.accessibility_arv_single_loading)
            }
            btSimulate.isButtonEnabled = false
            monthSelectChipGroup.setChildrenEnabled(false)
        }
    }

    private fun onHideLoadingArvSingleWithData() {
        binding?.apply {
            clContainerCalendar.visible()
            clContainerValueInformation.visible()
            notAvailableValueInformation.root.gone()
            shimmerAnticipationType.gone()
            monthSelectChipGroup.setChildrenEnabled(true)
        }
    }

    private fun onArvSingleError(error: NewErrorMessage, message: String) {
        trackAnticipationException(error)
        setupViewOnError(false, messageText = message, error)
    }

    private fun setupViewOnError(
        isVisible: Boolean,
        messageText: String? = null,
        error: NewErrorMessage? = null
    ) {
        binding?.apply {
            errorInclude.root.visible(isVisible.not())
            btSimulate.visible(isVisible)
            tvDetachedAnticipationGrossValueLabel.visible(isVisible)
            clContainerValueInformation.visible(isVisible)
            clContainerCalendar.visible(isVisible)
            tvSelectMonthLabel.visible(isVisible)
            tvTitle.visible(isVisible)
            errorInclude.apply {
                tvSorryMessage.text = messageText
                btReload.setOnClickListener {
                    analytics.logCallbackErrorButtonEvent(
                        SIMULATE_ARV_ANTICIPATION,
                        SINGLE_ARV,
                        negotiationTypeArv,
                        CLIQUE,
                        error,
                        RELOAD
                    )
                    viewModel.getArvSingleAnticipationWithDate()
                }
            }
        }
    }

    private fun onShowInitSingleAnticipationInfo(arvAnticipation: ArvAnticipation?) {
        if (arvAnticipation?.grossAmount != ZERO_DOUBLE)
            binding?.apply {
                shimmerCalendarSingleAnticipation.gone()
                shimmerAnticipationType.gone()
                clContainerValueInformation.visible()
                notAvailableValueInformation.root.gone()
                tvDetachedAnticipationGrossValue.text =
                    arvAnticipation?.grossAmount?.toPtBrRealString()
                btSimulate.apply {
                    visible()
                    isEnabled = true
                    setOnClickListener {
                        trackAnticipation(ANTICIPATION_PERIOD_VALUE)
                        arvAnticipation?.let {
                            findNavController().safeNavigate(
                                ArvSingleAnticipationFragmentDirections
                                    .actionArvSingleAnticipationFragmentToArvAnticipationSimulationFragment(
                                        it
                                    )
                            )
                        }
                    }
                }
                enableAnticipateByValueButton(arvAnticipation)

            }
        else
            showIsNotArvSingleAnticipateValue()
    }

    private fun enableAnticipateByValueButton(
        arvAnticipation: ArvAnticipation?
    ) {
        binding?.apply {
            btSimulateValueDisabled.gone()
            btSimulateValue.apply {
                visible()
                setOnClickListener {
                    trackAnticipation(SIMULATE_VALUE)
                    arvAnticipation?.let { arvAnticipation ->
                        findNavController().safeNavigate(
                            ArvSingleAnticipationFragmentDirections
                                .actionArvSingleAnticipationFragmentToArvSimulateSingleAnticipationValueFragment(
                                    arvAnticipation
                                )
                        )
                    }
                }
            }
        }
    }

    private fun onShowSingleAnticipationWithDateInfo(anticipation: ArvAnticipation) {
        if (anticipation.grossAmount != ZERO_DOUBLE) {
            binding?.apply {
                shimmerCalendarSingleAnticipation.gone()
                shimmerAnticipationType.gone()
                clContainerValueInformation.visible()
                tvDetachedAnticipationGrossValue.visible()
                notAvailableValueInformation.root.gone()
                tvDetachedAnticipationGrossValue.text = anticipation.grossAmount?.toPtBrRealString()
                btSimulate.apply {
                    isButtonEnabled = true
                    setOnClickListener {
                        trackAnticipation(ANTICIPATION_PERIOD_VALUE)
                        findNavController().safeNavigate(
                            ArvSingleAnticipationFragmentDirections
                                .actionArvSingleAnticipationFragmentToArvAnticipationSimulationFragment(
                                    anticipation
                                )
                        )
                    }
                }
                enableAnticipateByValueButton(anticipation)
            }
            arvAnticipation = anticipation
        } else {
            showIsNotArvSingleAnticipateValue()
        }
    }

    private fun showIsNotArvSingleAnticipateValue() {
        analytics.logScreenActionsWithCheckButton(
            SIMULATE_ARV_ANTICIPATION,
            SINGLE_ARV,
            negotiationTypeArv,
            EXHIBITION,
            TAG_SUCCESS_EMPTY_VALUE,
            EMPTY_VALUE,
            EMPTY_VALUE
        )
        binding?.apply {
            shimmerCalendarSingleAnticipation.gone()
            shimmerAnticipationType.gone()
            tvDetachedAnticipationGrossValue.gone()
            notAvailableValueInformation.apply {
                trackDisplayContent()
                root.visible()
                tvDetachedAnticipationGrossValueAvaliable.text =
                    getString(R.string.detached_anticipation_zero_liquid_value)
            }
            btSimulate.isButtonEnabled = false
            btSimulateValue.gone()
            btSimulateValueDisabled.visible()
        }
    }

    private fun setupListeners() {
        binding?.apply {
            tvStartDateCalendar.apply {
                setOnClickListener {
                    showCalendar(isStartDate = true)
                }
            }
            tvEndDateCalendar.apply {
                setOnClickListener {
                    showCalendar(isStartDate = false)
                }
            }

            RBNegotiationType.setOnCheckedChangeListener { group, _ ->
                val selectedType = when (group.checkedRadioButtonId) {
                    R.id.RBCielo -> CIELO_NEGOTIATION_TYPE
                    R.id.RBMarket -> MARKET_NEGOTIATION_TYPE
                    else -> null
                }
                if (viewModel.receivableType == null) {
                    viewModel.receivableType = selectedType
                } else if (viewModel.receivableType != selectedType) {
                    viewModel.receivableType = selectedType
                    getArvSingleAnticipationWithDate()
                }
                checkTypeArvReceivableForAnalytics()
            }

            monthSelectChipGroup.apply {
                setOnCheckedStateChangeListener { _, ints ->
                    updateMonthRange(ints.firstOrNull())
                }
            }
        }
    }

    private fun updateMonthRange(int: Int?) {
        viewModel.fetchAnticipationFixedPeriod(
            when (int) {
                R.id.chip24Months -> TWENTY_FOUR_MONTHS
                R.id.chip12Months -> TWELVE_MONTHS
                R.id.chip6Months -> SIX_MONTHS
                R.id.chip3Months -> THREE_MONTHS
                R.id.chip1Month -> ONE_MONTH
                else -> return
            }
        )

    }

    private fun analyticsScreenView() {
        analytics.logScreenView(
            name = ArvAnalytics.SCREEN_VIEW_ARV_SINGLE_ANTICIPATION,
            className = this.javaClass
        )
    }

    private fun analyticsScreenViewGA4() {
        arvAnalytics.logScreenView(
            SCREEN_VIEW_ARV_SINGLE_CONFIGURATION
        )
    }

    private fun trackAnticipation(anticipation: String) {
        when (anticipation) {
            SIMULATE_VALUE -> {
                analytics.logScreenActionsWithTwoLabel(
                    SIMULATE_ARV_ANTICIPATION,
                    SINGLE_ARV,
                    negotiationTypeArv,
                    Label.BOTAO,
                    SIMULATED_ANTICIPATION_WITH_VALUE
                )
                arvAnalytics.logClick(
                    screenName = SCREEN_VIEW_ARV_SINGLE_CONFIGURATION,
                    contentName = anticipation
                )
            }

            ANTICIPATION_PERIOD_VALUE -> {
                analytics.logScreenActionsWithTwoLabel(
                    SIMULATE_ARV_ANTICIPATION,
                    SINGLE_ARV,
                    negotiationTypeArv,
                    Label.BOTAO,
                    ANTICIPATION_WITH_PERIOD
                )
                arvAnalytics.logClick(
                    screenName = SCREEN_VIEW_ARV_SINGLE_CONFIGURATION,
                    contentName = anticipation
                )
            }
        }
    }

    private fun trackAnticipationException(error: NewErrorMessage? = null) {
        analytics.logCallbackErrorEvent(
            SIMULATE_ARV_ANTICIPATION,
            SINGLE_ARV,
            negotiationTypeArv,
            EXHIBITION,
            error
        )
        arvAnalytics.logException(
            SCREEN_VIEW_ARV_SINGLE_CONFIGURATION,
            error
        )
    }

    private fun trackDisplayContent() {
        arvAnalytics.logDisplayContent(
            SCREEN_VIEW_ARV_SINGLE_CONFIGURATION,
            NO_BALANCE_AVAILABLE
        )
    }

    override fun onHelpButtonClicked() {
        analytics.logScreenActionsWithTwoLabel(
            SIMULATE_ARV_ANTICIPATION,
            SINGLE_ARV,
            negotiationTypeArv,
            Label.BOTAO,
            ArvAnalytics.HELP
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        analytics.logScreenActionsWithTwoLabel(
            SIMULATE_ARV_ANTICIPATION,
            SINGLE_ARV,
            negotiationTypeArv,
            Label.BOTAO,
            Action.VOLTAR
        )
    }

    private fun checkTypeArvReceivableForAnalytics() {
        negotiationTypeArv = if (viewModel.receivableType == CIELO_NEGOTIATION_TYPE)
            RECEIVABLES_CIELO
        else
            RECEIVABLES_MARKET

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private companion object {
        const val ONE_MONTH = 1
        const val THREE_MONTHS = 3
        const val SIX_MONTHS = 6
        const val TWELVE_MONTHS = 12
        const val TWENTY_FOUR_MONTHS = 24
    }
}