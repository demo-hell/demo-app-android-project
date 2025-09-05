package br.com.mobicare.cielo.arv.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.TEXT_ALIGNMENT_TEXT_START
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.bottomsheet.CieloMessageBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.ENGAGE
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.HELP
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.MODEL_ANTICIPATION
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SCHEDULE_ARV
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SCREEN_VIEW_ARV_HOME
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SEE_ALL_HYSTORC
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SINGLE_ARV
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.AUTOMATIC
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.CANCEL_ANTICIPATION
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.REALIZE_AUTHORIZATION
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.REALIZE_AUTHORIZATION_OPTIN
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV_CANCEL
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV_OPTIN
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SINGLE
import br.com.mobicare.cielo.arv.data.model.response.Item
import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.domain.model.ArvScheduledAnticipation
import br.com.mobicare.cielo.arv.presentation.ArvEffectiveTimeViewModel
import br.com.mobicare.cielo.arv.presentation.home.utils.ArvNavigation
import br.com.mobicare.cielo.arv.presentation.home.utils.ArvWhatsAppContactData
import br.com.mobicare.cielo.arv.presentation.home.whatsAppNews.ArvWhatsAppNewsHandler
import br.com.mobicare.cielo.arv.presentation.home.whatsAppNews.ArvWhatsAppNewsViewModel
import br.com.mobicare.cielo.arv.presentation.model.enum.ReceivableStatusEnum
import br.com.mobicare.cielo.arv.utils.ArvConstants
import br.com.mobicare.cielo.arv.utils.OptInState
import br.com.mobicare.cielo.arv.utils.UiArvHistoricState
import br.com.mobicare.cielo.arv.utils.UiArvHomeState
import br.com.mobicare.cielo.arv.utils.UiArvScheduledAnticipationState
import br.com.mobicare.cielo.arv.utils.UiArvScheduledMarketFeatureToggleState
import br.com.mobicare.cielo.arv.utils.UiArvSingleState
import br.com.mobicare.cielo.arv.utils.UiArvTypeState
import br.com.mobicare.cielo.arv.utils.UiArvUserState
import br.com.mobicare.cielo.balcaoRecebiveis.AuthorizationActivity
import br.com.mobicare.cielo.commons.analytics.Action.VOLTAR
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.MODAL
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.helpers.AppHelper
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.EMPTY_VALUE
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.dateFormatToBr
import br.com.mobicare.cielo.commons.utils.formatterErrorMessage
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.FragmentArvHomeBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.pix.constants.EMPTY
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArvHomeFragment : BaseFragment(), CieloNavigationListener, ArvNavigation.Listener {
    private val arvHomeViewModel: ArvHomeViewModel by viewModel()
    private val arvEffectiveTImeViewModel: ArvEffectiveTimeViewModel by viewModel()
    private val arvWhatsAppNewsViewModel: ArvWhatsAppNewsViewModel by viewModel()

    private var navigation: CieloNavigation? = null
    private var arvNavigation: ArvNavigation? = null
    private var binding: FragmentArvHomeBinding? = null
    private var arvCieloAnticipation: ArvAnticipation? = null
    private var arvMarketAnticipation: ArvAnticipation? = null
    private var arvScheduledAnticipation: ArvScheduledAnticipation? = null
    private val analytics: ArvAnalytics by inject()
    private val arvAnalytics: ArvAnalyticsGA4 by inject()

    private val arvWhatsAppNewsHandler by lazy {
        ArvWhatsAppNewsHandler(
            fragment = this,
            viewModel = arvWhatsAppNewsViewModel,
            onConfirmTap = {
                arvNavigation?.setHighlightOnArvWhatsAppButton()
            },
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentArvHomeBinding.inflate(inflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        analyticsScreenView()
        setupNavigation()
        setupView()
        setupListeners()
        setupObservers()
        setupCieloScheduledAnticipation()
        setupMarketScheduledAnticipation()
    }

    override fun onResume() {
        super.onResume()
        trackScreenView()
        getInformation()
    }

    private fun setupNavigation() {
        navigation =
            (requireActivity() as? CieloNavigation)?.also {
                it.setNavigationListener(this)
                it.setupToolbar(
                    title = getString(R.string.anticipate_receivables),
                    isCollapsed = false,
                )
                it.showHelpButton(isShow = true)
            }
        arvNavigation =
            (requireActivity() as? ArvNavigation)?.also {
                it.setArvNavigationListener(this)
                it.showArvWhatsAppButton(true)
            }
    }

    private fun analyticsScreenView() {
        analytics.logScreenView(
            name = SCREEN_VIEW_ARV_HOME,
            className = this.javaClass,
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        analytics.logScreenActionsWithOneLabel(
            MODEL_ANTICIPATION,
            EMPTY_VALUE,
            Label.BOTAO,
            VOLTAR,
        )
    }

    override fun onHelpButtonClicked() {
        analytics.logScreenActionsWithOneLabel(
            MODEL_ANTICIPATION,
            EMPTY_VALUE,
            Label.BOTAO,
            HELP,
        )
    }

    override fun onArvWhatsAppButtonClicked() {
        arvHomeViewModel.arvWhatsAppContactData.value.let { data ->
            if (data != null) {
                navigateToWhatsApp(data)
            } else {
                arvHomeViewModel.fetchWhatsAppContactData()
            }
        }
    }

    private fun navigateToWhatsApp(data: ArvWhatsAppContactData) {
        AppHelper.showWhatsAppMessage(
            requireActivity(),
            data.phoneNumber,
            data.message,
        )
    }

    private fun setupListeners() {
        binding?.apply {
            clSingleAnticipationContainer.setOnClickListener {
                trackSingleAnticipations()
                findNavController().safeNavigate(
                    ArvHomeFragmentDirections.actionArvHomeFragmentToArvSingleAnticipationFragment(
                        arvCieloAnticipation,
                        arvMarketAnticipation,
                    ),
                )
            }
            clScheduledAnticipationContainer.notHiredScheduledAnticipation.root.setOnClickListener {
                trackAutomaticAnticipations()
                arvScheduledAnticipation?.let {
                    findNavController().safeNavigate(
                        ArvHomeFragmentDirections.actionArvHomeFragmentToArvScheduledAnticipationBankSelectFragment(
                            it,
                        ),
                    )
                }
            }
            clScheduledAnticipationContainer.btHireAnotherScheduledAnticipation.apply {
                setOnClickListener {
                    arvScheduledAnticipation?.let {
                        findNavController().safeNavigate(
                            ArvHomeFragmentDirections.actionArvHomeFragmentToArvScheduledAnticipationBankSelectFragment(
                                it,
                            ),
                        )
                    }
                }
            }
            clScheduledAnticipationContainer.hiredScheduledAnticipationCielo.btCancelAnticipation.setOnClickListener {
                showCancelBS(ArvConstants.CIELO_NEGOTIATION_TYPE)
            }
            clScheduledAnticipationContainer.hiredScheduledAnticipationMarket.btCancelAnticipation.setOnClickListener {
                showCancelBS(ArvConstants.MARKET_NEGOTIATION_TYPE)
            }
        }
    }

    private fun showCancelBS(negotiationType: String) {
        CieloMessageBottomSheet.create(
            headerConfigurator =
                CieloBottomSheet.HeaderConfigurator(
                    title = getString(R.string.arv_cancel_confirmation_title),
                ),
            message =
                CieloMessageBottomSheet.Message(
                    text = getString(R.string.arv_cancel_confirmation_message),
                ),
            mainButtonConfigurator =
                CieloBottomSheet.ButtonConfigurator(
                    title = getString(R.string.arv_keep_anticipation),
                    onTap = {
                        it.dismissAllowingStateLoss()
                    },
                ),
            secondaryButtonConfigurator =
                CieloBottomSheet.ButtonConfigurator(
                    title = getString(R.string.arv_yes_keep),
                    onTap = {
                        it.dismissAllowingStateLoss()
                        findNavController().safeNavigate(
                            ArvHomeFragmentDirections.actionArvHomeFragmentToArvCancelScheduledAnticipationFragment(
                                negotiationType,
                            ),
                        )
                    },
                ),
        ).show(childFragmentManager, tag).also {
            arvAnalytics.logDisplayContent(
                screenName = SCREEN_VIEW_ARV_CANCEL,
                description = CANCEL_ANTICIPATION,
                contentType = MODAL,
            )
        }
    }

    private fun setupObservers() {
        arvHomeViewModel.arvAnticipationTypeLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiArvTypeState.SetupAnticipationSingle ->
                    onAnticipationSingle(
                        uiState.anticipationSingleEnable,
                        uiState.anticipationSingleBackground,
                    )
            }
        }

        arvHomeViewModel.arvHomeSingleAnticipationLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiArvHomeState.ShowLoadingArvNegotiation -> onShowLoadingArvNegotiation()
                is UiArvHomeState.HideLoadingArvNegotiation -> onHideLoadingArvNegotiation()
                is UiArvHomeState.ErrorArvNegotiation ->
                    onAnticipationError(
                        requireContext().formatterErrorMessage(
                            uiState.message,
                        ),
                        uiState.error,
                    )

                is UiArvHomeState.NotEligible -> onNotEligible(uiState.error)
                is UiArvHomeState.ClosedMarket -> onClosedMarket()
                is UiArvHomeState.CorporateDesk -> onCorporateDesk()
                is UiArvHomeState.NoValuesToAnticipate -> onNoValuesToAnticipate()
            }
        }
        arvHomeViewModel.arvCieloAnticipationLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiArvSingleState.SuccessArvSingle -> onShowCieloAnticipationInfo(uiState.anticipation)
                is UiArvSingleState.NoValuesToAnticipate -> onCieloNoValuesToAnticipate()
                is UiArvSingleState.Disabled -> onCieloSingleDisabled()
            }
        }

        arvHomeViewModel.arvMarketAnticipationLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiArvSingleState.SuccessArvSingle -> onShowMarketAnticipationInfo(uiState.anticipation)
                is UiArvSingleState.NoValuesToAnticipate -> onMarketNoValuesToAnticipate()
                is UiArvSingleState.Disabled -> onMarketSingleDisabled()
            }
        }

        arvHomeViewModel.arvScheduledAnticipationLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiArvHomeState.ShowLoadingArvScheduledAnticipation -> onShowScheduledAnticipationTypeLoading()
                is UiArvHomeState.HideLoadingArvScheduledAnticipation -> onHideLoadingScheduledAnticipationType()
                is UiArvHomeState.SuccessArvScheduledNegotiation ->
                    onSetupScheduleAnticipation(
                        uiState.anticipation,
                    )

                is UiArvHomeState.ErrorArvNegotiation ->
                    onAnticipationError(
                        requireContext().formatterErrorMessage(
                            uiState.message,
                        ),
                        uiState.error,
                    )

                is UiArvHomeState.NotEligible -> onNotEligible(uiState.error)
                is UiArvHomeState.CorporateDesk -> onCorporateDesk()
            }
        }

        arvHomeViewModel.arvScheduledAnticipationStateLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                UiArvScheduledAnticipationState.CieloOnlyHired -> cieloOnlyHiredScheduled()
                UiArvScheduledAnticipationState.CieloOnlyHiredByRoot -> cieloOnlyHiredByRootScheduled()
                UiArvScheduledAnticipationState.FullHired -> fullHiredScheduled()
                UiArvScheduledAnticipationState.FullHiredByRoot -> fullHiredByRootScheduled()
                UiArvScheduledAnticipationState.MarketOnlyHired -> marketOnlyHiredScheduled()
                UiArvScheduledAnticipationState.MarketOnlyHiredByRoot -> marketOnlyHiredByRootScheduled()
                UiArvScheduledAnticipationState.NotHired -> notHiredScheduled()
                UiArvScheduledAnticipationState.CieloByRootMarketByBranch -> cieloByRootMarketByBranchScheduled()
                UiArvScheduledAnticipationState.MarketByRootCieloByBranch -> marketByRootCieloByBranch()
                UiArvScheduledAnticipationState.DisabledScheduled -> disableScheduled()
                UiArvScheduledAnticipationState.ShowLoading -> onShowScheduledAnticipationTypeLoading()
            }
        }

        arvHomeViewModel.arvUserState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiArvUserState.ShowLoadingMeInformation -> onShowLoadingMeInformation()
                is UiArvUserState.HideLoadingMeInformation -> onHideLoadingMeInformation()
                is UiArvUserState.SuccessMeInformation ->
                    onSuccessUserInformation(
                        uiState.name,
                        uiState.numberEstablishment,
                        uiState.cpnjEstablishment,
                    )

                is UiArvUserState.ErrorMeInformation ->
                    onErrorUserInformation(
                        requireContext().formatterErrorMessage(
                            uiState.message,
                        ),
                    )
            }
        }

        arvHomeViewModel.arvAnticipationHistoryLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiArvHistoricState.ShowLoadingHistoric -> onShowAnticipationHistoryLoading()
                is UiArvHistoricState.HideLoadingHistoric -> onHideAnticipationHistoryLoading()
                is UiArvHistoricState.SuccessHistoric -> onShowAnticipationHistory(uiState.historic)
                is UiArvHistoricState.ErrorHistoric ->
                    onShowAnticipationHistoryError(
                        requireContext().formatterErrorMessage(
                            uiState.message ?: EMPTY,
                        ),
                    )

                is UiArvHistoricState.EmptyHistoric -> onShowAnticipationEmptyHistory()
            }
        }

        arvHomeViewModel.arvOptInStateLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                OptInState.MissingOptIn -> showOptInRequest()
            }
        }

        arvEffectiveTImeViewModel.arvEffectiveTimeLiveData.observe(viewLifecycleOwner) {
            setEffectiveTime(it)
        }

        arvHomeViewModel.arvWhatsAppContactData.observe(viewLifecycleOwner) { contactData ->
            navigateToWhatsApp(contactData)
        }
    }

    private fun showOptInRequest() {
        doWhenResumed {
            trackOptInScreenView()
            navigation?.showCustomHandlerView(
                contentImage = R.drawable.ic_generic_error_image,
                title = getString(R.string.arv_opt_in_request_title),
                titleAlignment = TEXT_ALIGNMENT_TEXT_START,
                message = getString(R.string.arv_opt_in_request_message),
                isShowButtonClose = true,
                callbackClose = {
                    activity?.finishAndRemoveTask()
                },
                labelSecondButton = getString(R.string.arv_opt_in_request_button_label),
                callbackSecondButton = {
                    trackOptInRequestButtonClick()
                    requireActivity().apply {
                        startActivity<AuthorizationActivity>()
                        finish()
                    }
                },
            )
        }
    }

    private fun marketByRootCieloByBranch() {
        disableSingleAnticipation(getString(R.string.arv_ec_has_scheduled_anticipations_single_disabled_message))

        binding?.clScheduledAnticipationContainer?.apply {
            notHiredScheduledAnticipation.root.gone()
            clRootAnticipations.apply {
                visible()
                singleScheduledAnticipation.tvProgrammedType.apply {
                    visible()
                    text = getString(R.string.arv_type_scheduled_market_label)
                }
            }
            clAnticipations.visible()

            hiredScheduledAnticipationCielo.apply {
                root.visible()
                btCancelAnticipation.visible()
            }

            hiredScheduledAnticipationMarket.apply {
                root.gone()
            }
            dividerScheduled.gone()
            btHireAnotherScheduledAnticipation.gone()
        }
    }

    private fun cieloByRootMarketByBranchScheduled() {
        disableSingleAnticipation(getString(R.string.arv_ec_has_scheduled_anticipations_single_disabled_message))

        binding?.clScheduledAnticipationContainer?.apply {
            notHiredScheduledAnticipation.root.gone()
            clRootAnticipations.apply {
                visible()
                singleScheduledAnticipation.tvProgrammedType.apply {
                    visible()
                    text = getString(R.string.arv_type_scheduled_cielo_label)
                }
            }
            clAnticipations.visible()

            hiredScheduledAnticipationCielo.apply {
                root.gone()
            }

            hiredScheduledAnticipationMarket.apply {
                root.visible()
                btCancelAnticipation.visible()
            }
            dividerScheduled.gone()
            btHireAnotherScheduledAnticipation.gone()
        }
    }

    private fun setupCieloScheduledAnticipation() {
        binding?.clScheduledAnticipationContainer?.hiredScheduledAnticipationCielo?.tvProgrammedType?.text =
            getString(R.string.arv_type_scheduled_cielo_label)
    }

    private fun setupMarketScheduledAnticipation() {
        binding?.clScheduledAnticipationContainer?.hiredScheduledAnticipationMarket?.tvProgrammedType?.text =
            getString(R.string.arv_type_scheduled_market_label)
    }

    private fun cieloOnlyHiredScheduled() {
        disableCieloSingleAnticipation()

        binding?.clScheduledAnticipationContainer?.apply {
            notHiredScheduledAnticipation.root.gone()
            clRootAnticipations.gone()
            clAnticipations.apply {
                visible()
                tvTitle.text = getString(R.string.arv_ec_has_scheduled_anticipations)
            }

            hiredScheduledAnticipationCielo.apply {
                root.visible()
                btCancelAnticipation.visible()
            }

            hiredScheduledAnticipationMarket.apply {
                root.gone()
            }
            dividerScheduled.gone()
            if (arvHomeViewModel.arvMarketScheduledAnticipationFeatureToggleLiveData.value
                    is UiArvScheduledMarketFeatureToggleState.Disabled
            ) {
                disableScheduledMarketHiring()
            } else {
                btHireAnotherScheduledAnticipation.apply {
                    text = getText(R.string.arv_hire_another_schedule_market)
                    visible()
                }
            }
        }
    }

    private fun cieloOnlyHiredByRootScheduled() {
        disableCieloSingleAnticipation()

        binding?.clScheduledAnticipationContainer?.apply {
            notHiredScheduledAnticipation.root.gone()
            clRootAnticipations.gone()
            clAnticipations.apply {
                visible()
                tvTitle.text = getString(R.string.arv_root_has_scheduled_anticipations)
            }

            hiredScheduledAnticipationMarket.apply {
                root.gone()
            }

            hiredScheduledAnticipationCielo.apply {
                root.visible()
                btCancelAnticipation.gone()
            }
            dividerScheduled.gone()
            if (arvHomeViewModel.arvMarketScheduledAnticipationFeatureToggleLiveData.value
                    is UiArvScheduledMarketFeatureToggleState.Disabled
            ) {
                disableScheduledMarketHiring()
            } else {
                btHireAnotherScheduledAnticipation.apply {
                    text = getString(R.string.arv_hire_another_schedule_market)
                    visible()
                }
            }
        }
    }

    private fun disableScheduledMarketHiring() {
        binding?.clScheduledAnticipationContainer?.apply {
            multipleScheduledAnticipation.apply {
                background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.background_stroke_cloud_50_8dp_radius,
                    )
                isEnabled = false
                isClickable = false
            }

            btHireAnotherScheduledAnticipation.gone()
            tvScheduledMarketUnavailable.visible()
        }
    }

    private fun disableCieloSingleAnticipation() {
        arvCieloAnticipation = null
        if (arvHomeViewModel.arvMarketAnticipationLiveData.value in
            listOf(
                UiArvSingleState.NoValuesToAnticipate,
                UiArvSingleState.Disabled,
            )
        ) {
            disableSingleAnticipation(
                getString(
                    R.string.arv_one_schedule_with_no_balance_and_other_with_scheduled_activated,
                    getString(R.string.arv_market),
                    getString(R.string.arv_cielo),
                ),
            )
        }
    }

    private fun marketOnlyHiredScheduled() {
        disableMarketSingleAnticipation()

        binding?.clScheduledAnticipationContainer?.apply {
            notHiredScheduledAnticipation.root.gone()
            clRootAnticipations.gone()
            clAnticipations.apply {
                visible()
                tvTitle.text = getString(R.string.arv_ec_has_scheduled_anticipations)
            }

            hiredScheduledAnticipationMarket.apply {
                root.visible()
                btCancelAnticipation.visible()
            }

            hiredScheduledAnticipationCielo.apply {
                root.gone()
            }
            dividerScheduled.gone()
            btHireAnotherScheduledAnticipation.apply {
                text = getString(R.string.arv_hire_another_schedule_cielo)
                visible()
            }
            tvScheduledMarketUnavailable.gone()
        }
    }

    private fun disableMarketSingleAnticipation() {
        arvMarketAnticipation = null
        if (arvHomeViewModel.arvCieloAnticipationLiveData.value is UiArvSingleState.NoValuesToAnticipate) {
            disableSingleAnticipation(
                getString(
                    R.string.arv_one_schedule_with_no_balance_and_other_with_scheduled_activated,
                    getString(R.string.arv_cielo),
                    getString(R.string.arv_market),
                ),
            )
        }
    }

    private fun marketOnlyHiredByRootScheduled() {
        disableMarketSingleAnticipation()

        binding?.clScheduledAnticipationContainer?.apply {
            notHiredScheduledAnticipation.root.gone()
            clRootAnticipations.gone()
            clAnticipations.apply {
                visible()
                tvTitle.text = getString(R.string.arv_root_has_scheduled_anticipations)
            }

            hiredScheduledAnticipationCielo.apply {
                root.gone()
                btCancelAnticipation.gone()
            }

            hiredScheduledAnticipationMarket.apply {
                root.visible()
                btCancelAnticipation.gone()
            }
            dividerScheduled.gone()
            btHireAnotherScheduledAnticipation.apply {
                text = getString(R.string.arv_hire_another_schedule_cielo)
                visible()
            }
        }
    }

    private fun fullHiredScheduled() {
        disableSingleAnticipation(getString(R.string.arv_ec_has_scheduled_anticipations_single_disabled_message))

        binding?.clScheduledAnticipationContainer?.apply {
            notHiredScheduledAnticipation.root.gone()
            clRootAnticipations.gone()
            clAnticipations.visible()

            hiredScheduledAnticipationCielo.apply {
                root.visible()
                btCancelAnticipation.visible()
            }

            hiredScheduledAnticipationMarket.apply {
                root.visible()
                btCancelAnticipation.visible()
            }
            dividerScheduled.visible()
            btHireAnotherScheduledAnticipation.gone()
        }
    }

    private fun disableSingleAnticipation(message: String) {
        binding?.apply {
            clSingleAnticipationContainer.apply {
                background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.background_stroke_cloud_50_8dp_radius,
                    )
                isEnabled = false
                isClickable = false
            }
            tvSingleSubtitle.text = message
            ivAction.gone()
        }
    }

    private fun fullHiredByRootScheduled() {
        disableSingleAnticipation(getString(R.string.arv_root_has_scheduled_anticipations_single_disabled_message))

        binding?.clScheduledAnticipationContainer?.apply {
            notHiredScheduledAnticipation.root.gone()
            clRootAnticipations.apply {
                visible()
                singleScheduledAnticipation.tvProgrammedType.gone()
            }
        }
    }

    private fun notHiredScheduled() {
        binding?.clScheduledAnticipationContainer?.apply {
            notHiredScheduledAnticipation.apply {
                root.visible()
                root.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.background_stroke_cloud_200_8dp_radius,
                    )
            }
            clRootAnticipations.gone()
            clAnticipations.gone()
        }
    }

    private fun onSetupScheduleAnticipation(anticipation: ArvScheduledAnticipation) {
        arvScheduledAnticipation = anticipation
        arvWhatsAppNewsHandler.checkEnablement()
    }

    private fun onNoValuesToAnticipate() {
        binding?.apply {
            clSingleAnticipationContainer.isClickable = false
            clSingleAnticipationContainer.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.background_stroke_cloud_50_8dp_radius,
                )
            ivAction.gone()
            tvSingleSubtitle.text = getString(R.string.single_anticipation_date_zero)
            includeAnticipationValue.apply {
                tvGrossValueTitle.text = getString(R.string.arv_no_value_to_anticipate_info_title)
                root.visible()
            }
        }
        arvWhatsAppNewsHandler.checkEnablement()
    }

    private fun onCieloNoValuesToAnticipate() {
        binding?.apply {
            includeAnticipationValue.apply {
                tvCieloGrossValue.text = ZERO_DOUBLE.toPtBrRealString()
                root.visible()
            }
        }
        arvWhatsAppNewsHandler.checkEnablement()
    }

    private fun onMarketNoValuesToAnticipate() {
        binding?.apply {
            includeAnticipationValue.apply {
                tvMarketGrossValue.text = ZERO_DOUBLE.toPtBrRealString()
                root.visible()
            }
        }
        arvWhatsAppNewsHandler.checkEnablement()
    }

    private fun onMarketSingleDisabled() {
        binding?.includeAnticipationValue?.tvMarketGrossValue?.text =
            getString(R.string.negotiation_type_unavailable)

        arvWhatsAppNewsHandler.checkEnablement()
    }

    private fun onCieloSingleDisabled() {
        binding?.includeAnticipationValue?.tvCieloGrossValue?.text =
            getString(R.string.negotiation_type_unavailable)

        arvWhatsAppNewsHandler.checkEnablement()
    }

    private fun getInformation() {
        arvHomeViewModel.getUserInformation()
        arvHomeViewModel.getOptIn()
    }

    private fun setupView() {
        onShowLoadingArvNegotiation()
        onShowLoadingMeInformation()
        onShowAnticipationTypeLoading()
        onShowAnticipationHistoryLoading()
    }

    private fun onAnticipationSingle(
        anticipationSingleEnable: Boolean,
        @DrawableRes anticipationSingleBackground: Int,
    ) {
        binding?.apply {
            if (anticipationSingleEnable) ivAction.visible() else ivAction.gone()
            clSingleAnticipationContainer.apply {
                background =
                    ContextCompat.getDrawable(requireContext(), anticipationSingleBackground)
                isEnabled = anticipationSingleEnable
                isClickable = anticipationSingleEnable
            }
        }
    }

    private fun setEffectiveTime(timeEffective: String) {
        binding?.tvSingleSubtitle?.text =
            getString(R.string.single_anticipation_date, timeEffective)
    }

    private fun disableScheduled() {
        binding?.clScheduledAnticipationContainer?.notHiredScheduledAnticipation?.apply {
            ivProgrammedAction.gone()
            root.apply {
                background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.background_stroke_cloud_50_8dp_radius,
                    )
                isEnabled = false
                isClickable = false
            }
        }
    }

    private fun onHideLoadingAnticipationType() {
        binding?.apply {
            shimmerAnticipationType.stopShimmer()
            anticipationContainersGroup.visible()
            shimmerAnticipationType.invisible()
        }
    }

    private fun onHideLoadingScheduledAnticipationType() {
        binding?.apply {
            shimmerScheduledAnticipationType.stopShimmer()
            clScheduledAnticipationContainer.root.visible()
            shimmerScheduledAnticipationType.invisible()
        }
    }

    private fun onShowLoadingArvNegotiation() {
        arvCieloAnticipation = null
        arvMarketAnticipation = null

        binding?.includeAnticipationValue?.apply {
            clHistory.gone()
            shimmerLayout.visible()
            shimmerLayout.startShimmer()
            root.visible()
        }

        binding?.apply {
            anticipationContainersGroup.invisible()
            errorInclude.root.gone()
            shimmerAnticipationType.apply {
                visible()
                startShimmer()
                contentDescription = getString(R.string.content_description_loading)
            }
        }
    }

    private fun onHideLoadingArvNegotiation() {
        binding?.includeAnticipationValue?.apply {
            shimmerLayout.stopShimmer()
            clHistory.visible()
            shimmerLayout.gone()
        }

        binding?.apply {
            shimmerAnticipationType.stopShimmer()
            anticipationContainersGroup.visible()
            shimmerAnticipationType.invisible()
        }
    }

    private fun onShowLoadingMeInformation() {
        binding?.includeUserInformation?.apply {
            clError.gone()
            clUserInformation.gone()
        }
    }

    private fun onHideLoadingMeInformation() {
        binding?.includeUserInformation?.apply {
            clUserInformation.visible()
            clInformation.visible()
            clError.gone()
        }
    }

    private fun onShowAnticipationTypeLoading() {
        binding?.apply {
            anticipationContainersGroup.invisible()

            shimmerAnticipationType.apply {
                visible()
                startShimmer()
                contentDescription = getString(R.string.content_description_loading)
            }
        }
    }

    private fun onShowScheduledAnticipationTypeLoading() {
        binding?.apply {
            clScheduledAnticipationContainer.root.invisible()
            shimmerScheduledAnticipationType.apply {
                visible()
                startShimmer()
                contentDescription = getString(R.string.content_description_loading)
            }
        }
    }

    private fun onShowAnticipationHistoryLoading() {
        binding?.anticipationHistoryInclude?.apply {
            includeFullHistory.root.gone()
            includeHistoryError.root.gone()
            emptyAnticipationHistoryTitle.gone()
            anticipationHistoryLoadingShimmer.apply {
                startShimmer()
                visible()
                contentDescription = getString(R.string.content_description_loading)
            }
        }
    }

    private fun onShowCieloAnticipationInfo(anticipation: ArvAnticipation) {
        binding?.includeAnticipationValue?.apply {
            tvCieloGrossValue.text = anticipation.grossAmount?.toPtBrRealString()
            root.visible()
        }
        arvCieloAnticipation = anticipation
        arvWhatsAppNewsHandler.checkEnablement()
    }

    private fun onShowMarketAnticipationInfo(anticipation: ArvAnticipation) {
        binding?.includeAnticipationValue?.apply {
            tvMarketGrossValue.text = anticipation.grossAmount?.toPtBrRealString()
            root.visible()
        }
        arvMarketAnticipation = anticipation
        arvWhatsAppNewsHandler.checkEnablement()
    }

    private fun onSuccessUserInformation(
        name: String,
        numberEstablishment: String,
        cpnjEstablishment: String,
    ) {
        onHideLoadingMeInformation()
        binding?.includeUserInformation?.apply {
            tvNameUserInformation.text = name
            tvNumberEstablishment.text =
                getString(R.string.anticipation_card_number_establishment, numberEstablishment)
            tvCpnjEstablishment.text =
                getString(R.string.anticipation_card_cnpj_establishment, cpnjEstablishment)
        }
    }

    private fun onErrorUserInformation(message: String) {
        onHideLoadingMeInformation()
        binding?.includeUserInformation?.apply {
            clInformation.gone()
            clError.visible()
            tvErrorMessage.text = message
            tvReload.setOnClickListener {
                arvHomeViewModel.getUserInformation(isLocal = false)
            }
        }
    }

    private fun onAnticipationError(
        message: String,
        error: NewErrorMessage?,
    ) {
        trackException(error)
        binding?.apply {
            clFullContent.gone()
            errorInclude.apply {
                root.visible()
                tvSorryMessage.text = message
                btnReload.setOnClickListener {
                    root.gone()
                    arvHomeViewModel.getOptIn()
                    clFullContent.visible()
                }
            }
        }
    }

    private fun onNotEligible(error: NewErrorMessage?) {
        trackException(error)
        doWhenResumed {
            navigation?.showCustomHandlerViewWithHelp(
                contentImage = R.drawable.ic_50_not_eligible,
                title = getString(R.string.anticipation_not_eligible_title),
                message = getString(R.string.anticipation_not_eligible_message),
                labelSecondButton = getString(R.string.go_to_initial_screen),
                callbackSecondButton = {
                    navigation?.goToHome()
                },
            )
        }
    }

    private fun trackScreenView() {
        arvAnalytics.logScreenView(
            SCREEN_VIEW_ARV,
        )
    }

    private fun trackException(error: NewErrorMessage?) {
        arvAnalytics.logException(
            SCREEN_VIEW_ARV,
            error,
        )
    }

    private fun trackSingleAnticipations() {
        analytics.logScreenActionsWithTwoLabel(
            MODEL_ANTICIPATION,
            SINGLE_ARV,
            EMPTY_VALUE,
            Label.BOTAO,
            ENGAGE,
        )
        arvAnalytics.logBeginCheckout(
            SCREEN_VIEW_ARV,
            SINGLE,
        )
    }

    private fun trackAutomaticAnticipations() {
        analytics.logScreenActionsWithTwoLabel(
            MODEL_ANTICIPATION,
            SCHEDULE_ARV,
            EMPTY_VALUE,
            Label.BOTAO,
            ENGAGE,
        )
        arvAnalytics.logBeginCheckout(
            SCREEN_VIEW_ARV,
            AUTOMATIC,
        )
    }

    private fun trackOptInScreenView() {
        arvAnalytics.logScreenView(
            SCREEN_VIEW_ARV_OPTIN,
        )
    }

    private fun trackOptInRequestButtonClick() {
        arvAnalytics.logClick(
            screenName = SCREEN_VIEW_ARV_OPTIN,
            contentName = REALIZE_AUTHORIZATION,
            contentComponent = REALIZE_AUTHORIZATION_OPTIN,
        )
    }

    private fun onClosedMarket() {
        binding?.apply {
            ivAction.gone()
            tvSingleSubtitle.text = getString(R.string.anticipation_closed_market_message)
            clSingleAnticipationContainer.apply {
                background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.background_stroke_cloud_50_8dp_radius,
                    )
                isEnabled = false
                isClickable = false
            }
            includeAnticipationValue.root.gone()
        }
        arvWhatsAppNewsHandler.checkEnablement()
    }

    private fun onCorporateDesk() {
        doWhenResumed {
            navigation?.showCustomHandlerViewWithHelp(
                contentImage = R.drawable.ic_37_initial_onboarding,
                title = getString(R.string.anticipation_corporate_desk_title),
                message = getString(R.string.anticipation_corporate_desk_message),
                isShowFirstButton = true,
                labelFirstButton = getString(R.string.anticipation_corporate_desk_phone),
                labelSecondButton = getString(R.string.go_to_initial_screen),
                callbackFirstButton = {
                    Utils.callPhone(
                        requireActivity(),
                        getString(R.string.anticipation_corporate_desk_phone),
                    )
                },
                callbackSecondButton = {
                    navigation?.goToHome()
                },
            )
        }
    }

    private fun onShowAnticipationHistory(history: Item) {
        binding?.anticipationHistoryInclude?.includeFullHistory?.apply {
            tvAnticipationHistoryType.text =
                getString(
                    R.string.anticipation_history_name_title,
                    when (history.negotiationType) {
                        ArvConstants.CIELO_NEGOTIATION_TYPE -> getString(R.string.arv_cielo)
                        ArvConstants.MARKET_NEGOTIATION_TYPE -> getString(R.string.arv_market)
                        else -> history.negotiationType
                    },
                )
            tvAnticipationHistoryDate.text = history.negotiationDate.dateFormatToBr()
            tvAnticipationHistoryValue.text = history.grossAmount?.toPtBrRealString()
            tvAnticipationHistoryStatus.text = history.status

            ReceivableStatusEnum.values().forEach { status ->
                if (history.status == status.status) {
                    tvAnticipationHistoryStatus.setTextColor(
                        ContextCompat.getColor(requireContext(), status.getColor()),
                    )
                    icStatus.setImageResource(status.getIcon())
                }
            }

            tvAnticipationSeeFullHistory.setOnClickListener {
                analytics.logScreenActionsWithOneLabel(
                    MODEL_ANTICIPATION,
                    EMPTY_VALUE,
                    Label.BOTAO,
                    SEE_ALL_HYSTORC,
                )
                navigateToHistoric()
            }
        }
    }

    private fun onShowAnticipationEmptyHistory() {
        binding?.anticipationHistoryInclude?.apply {
            includeFullHistory.root.gone()
            emptyAnticipationHistoryTitle.visible()
        }
    }

    private fun onHideAnticipationHistoryLoading() {
        binding?.anticipationHistoryInclude?.apply {
            anticipationHistoryLoadingShimmer.stopShimmer()
            includeFullHistory.root.visible()
            anticipationHistoryLoadingShimmer.gone()
        }
    }

    private fun onShowAnticipationHistoryError(message: String) {
        binding?.anticipationHistoryInclude?.apply {
            includeFullHistory.root.gone()
            includeHistoryError.root.visible()
            includeHistoryError.tvErrorInfo.text = message

            includeHistoryError.btTryAgain.setOnClickListener {
                arvHomeViewModel.getArvAnticipationHistory()
            }
        }
    }

    private fun navigateToHistoric() {
        findNavController().safeNavigate(
            ArvHomeFragmentDirections.actionArvHomeFragmentToArvHistoricFragment(),
        )
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
