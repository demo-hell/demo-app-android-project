package br.com.mobicare.cielo.arv.presentation.anticipation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.bottomsheet.CieloMessageBottomSheet
import br.com.cielo.libflue.bottomsheet.CieloNavLinksBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.enum.CieloBankIcons
import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.ONE
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV_AUTOMATIC_CONFIRMATION
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV_AUTOMATIC_SUCCESS
import br.com.mobicare.cielo.arv.domain.model.ArvBank
import br.com.mobicare.cielo.arv.domain.model.ArvScheduledAnticipation
import br.com.mobicare.cielo.arv.presentation.ArvEffectiveTimeViewModel
import br.com.mobicare.cielo.arv.utils.ArvConstants.BOTH_NEGOTIATION_TYPE
import br.com.mobicare.cielo.arv.utils.ArvConstants.CIELO_NEGOTIATION_TYPE
import br.com.mobicare.cielo.arv.utils.ArvConstants.MARKET_NEGOTIATION_TYPE
import br.com.mobicare.cielo.arv.utils.UiArvConfirmScheduledAnticipationState
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias.CentralAjudaSubCategoriasEngineActivity
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias.CentralAjudaSubCategoriasEngineActivity.Companion.NOT_CAME_FROM_HELP_CENTER
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_SUBCATEGORY_NAME
import br.com.mobicare.cielo.commons.constants.Intent.PDF
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.FileUtils
import br.com.mobicare.cielo.commons.utils.MerchantType
import br.com.mobicare.cielo.commons.utils.isRoot
import br.com.mobicare.cielo.commons.utils.merchantType
import br.com.mobicare.cielo.databinding.FragmentArvScheduledAnticipationConfirmationBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.formatRate
import br.com.mobicare.cielo.extensions.orZero
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef.TAG_HELP_CENTER_MFA
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef.TAG_KEY_HELP_CENTER
import kotlinx.coroutines.launch
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArvScheduledAnticipationConfirmationFragment : BaseFragment(), CieloNavigationListener {

    private val viewModel: ArvScheduledConfirmationViewModel by viewModel()
    private val arvEffectiveTImeViewModel: ArvEffectiveTimeViewModel by viewModel()

    private val analytics: ArvAnalytics by inject()
    private val arvAnalytics: ArvAnalyticsGA4 by inject()

    private var navigation: CieloNavigation? = null
    private var binding: FragmentArvScheduledAnticipationConfirmationBinding? = null

    private val args: ArvScheduledAnticipationConfirmationFragmentArgs by navArgs()

    private var negotiationTypeArv: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentArvScheduledAnticipationConfirmationBinding.inflate(inflater, container, false)
        .also {
            binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        analyticsScreenView(ArvAnalytics.SCREEN_VIEW_ARV_SCHEDULED_REVIEW_ANTICIPATION)
        setupNavigation()
        setupObservers()
        setupListeners()
        setupData()
    }

    override fun onResume() {
        super.onResume()
        arvAnalytics.logScreenView(SCREEN_VIEW_ARV_AUTOMATIC_CONFIRMATION)
    }

    private fun analyticsScreenView(screen: String) {
        analytics.logScreenView(
            name = screen,
            className = this@ArvScheduledAnticipationConfirmationFragment.javaClass
        )
    }

    private fun setupData() {
        viewModel.updateAnticipationData(
            args.scheduledanticipationargs
        )
        viewModel.updateAnticipationDataUpdateBankData(
            args.arvbankargs
        )
    }

    private fun setupListeners() {
        binding?.apply {
            tvTermsLink.setOnClickListener {
                CieloNavLinksBottomSheet.create(
                    title = getString(R.string.arv_contacts_title),
                    links = listOf(
                        CieloNavLinksBottomSheet.Link(
                            icon = R.drawable.ic_arrows_arrow_circle_right_brand_400_24_dp,
                            label = getString(R.string.arv_scheduled_anticipation_terms),
                            lazyBase64 = { link: CieloNavLinksBottomSheet.Link, lazyLoad: CieloNavLinksBottomSheet.LazyLoadListener ->
                                lifecycleScope.launch {
                                    viewModel.getArvScheduledContract(negotiationTypeArv.orEmpty())
                                        ?.let { base64Pdf ->
                                            lazyLoad.onLoad(link.apply {
                                                base64 = base64Pdf
                                                lazyBase64 = null
                                            })
                                        } ?: lazyLoad.onError()
                                }

                            },
                            button = CieloNavLinksBottomSheet.ButtonBSNavLinks(onTap = {
                                it.base64?.let { base64 ->
                                    val tempFile = FileUtils(requireContext()).convertBase64ToFile(
                                        base64String = base64,
                                        fileName = CONTRACT_FILENAME,
                                        fileType = PDF
                                    )
                                    FileUtils(requireContext()).startShare(tempFile)
                                }
                            })
                        )
                    )
                ).show(childFragmentManager, null)
            }

            btConfirm.setOnClickListener(this@ArvScheduledAnticipationConfirmationFragment::onConfirmClick)

            llcheckboxArea.setOnClickListener {
                checkBoxTerms.toggle()
            }

            checkBoxTerms.setOnCheckedChangeListener { _, isChecked ->
                btConfirm.isButtonEnabled = isChecked
                analytics.logScreenActionsWithTwoLabel(
                    ArvAnalytics.ARV_REVIEW_AND_REQUEST_ANTICIPATION,
                    ArvAnalytics.SCHEDULE_ARV,
                    analytics.negotiationType(negotiationTypeArv),
                    Label.BOTAO,
                    Label.CHECK_BOX
                )
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

    override fun onHelpButtonClicked() {
        analytics.logScreenActionsWithTwoLabel(
            ArvAnalytics.ARV_REVIEW_AND_REQUEST_ANTICIPATION,
            ArvAnalytics.SCHEDULE_ARV,
            analytics.negotiationType(negotiationTypeArv),
            Label.BOTAO,
            ArvAnalytics.HELP
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        analytics.logScreenActionsWithTwoLabel(
            ArvAnalytics.ARV_REVIEW_AND_REQUEST_ANTICIPATION,
            ArvAnalytics.SCHEDULE_ARV,
            analytics.negotiationType(negotiationTypeArv.orEmpty()),
            Label.BOTAO,
            Action.VOLTAR
        )
    }

    private fun setupObservers() {
        viewModel.apply {
            userInformationLiveData.observe(viewLifecycleOwner) { userData ->
                userData?.let { setupUserData(it) }
            }

            arvScheduledAnticipationLiveData.observe(viewLifecycleOwner) { anticipation ->
                anticipation?.let { setupAnticipationData(it) }
            }

            selectedBank.observe(viewLifecycleOwner) { arvBank ->
                arvBank?.let { setupAccount(it) }
            }

            arvConfirmScheduledAnticipationState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is UiArvConfirmScheduledAnticipationState.ShowLoading -> showLoading()
                    is UiArvConfirmScheduledAnticipationState.HideLoading -> hideLoading()
                    is UiArvConfirmScheduledAnticipationState.Success -> onConfirmAnticipationSuccess()
                    is UiArvConfirmScheduledAnticipationState.Error -> onConfirmAnticipationError(
                        state.error
                    )

                    is UiArvConfirmScheduledAnticipationState.ErrorToken -> onConfirmAnticipationErrorToken(
                        state.error
                    )
                    is UiArvConfirmScheduledAnticipationState.ErrorNotEligible -> onConfirmAnticipationNotEligibleError(state.error)
                }
            }
        }
    }

    private fun hideLoading() {
        navigation?.hideAnimatedLoading()
    }

    private fun showLoading() {
        navigation?.showAnimatedLoading()
    }

    private fun setupUserData(userData: Pair<String, String>) {
        binding?.apply {
            tvReceiverEstablishmentName.text = userData.first
            tvReceiverEstablishmentCnpj.text = userData.second
        }
    }

    private fun setupAnticipationData(anticipation: ArvScheduledAnticipation) {
        binding?.apply {
            tvSchedule.text = if (anticipation.rateSchedules?.size.orZero == ONE) {
                anticipation.rateSchedules?.first()?.name?.let {
                    negotiationTypeArv = it
                    when (it) {
                        CIELO_NEGOTIATION_TYPE -> getString(R.string.arv_cielo_receivables)
                        MARKET_NEGOTIATION_TYPE -> getString(R.string.arv_market_receivables)
                        else -> EMPTY
                    }
                }
            } else {
                negotiationTypeArv = BOTH_NEGOTIATION_TYPE
                getString(R.string.arv_cielo_and_market_receivables)
            }

            anticipation.rateSchedules?.forEach {
                when (it?.name) {
                    CIELO_NEGOTIATION_TYPE -> {
                        setupAboutCielo(it.rate)
                    }

                    MARKET_NEGOTIATION_TYPE -> {
                        setupAboutMarket(it.rate)
                    }
                }
            }
        }
    }

    private fun setupAboutCielo(
        rate: Double?
    ) {
        binding?.apply {
            aboutCielo.apply {
                tvAboutScheduleLabel.text = getString(
                    R.string.arv_about_schedule,
                    getString(R.string.arv_cielo)
                )
                tvAboutScheduleMessage.text = getString(
                    R.string.arv_about_schedule_message,
                    getString(R.string.arv_cielo)
                )
                tvFeeValue.text = rate.formatRate()
                root.visible()
            }
        }
    }

    private fun setupAboutMarket(
        rate: Double?
    ) {
        binding?.apply {
            aboutMarket.apply {
                tvAboutScheduleLabel.text = getString(
                    R.string.arv_about_schedule,
                    getString(R.string.arv_market)
                )
                tvAboutScheduleMessage.text = getString(
                    R.string.arv_about_schedule_message,
                    getString(R.string.arv_market)
                )
                tvFeeValue.text = rate.formatRate()
                root.visible()
            }
        }
    }

    private fun setupAccount(bank: ArvBank) {
        binding?.apply {
            val bankMapped = bank.code?.let { CieloBankIcons.getBankFromCode(it) }
            tvBankValue.text = getString(
                R.string.arv_bank_two_lines_format,
                bankMapped?.bankName ?: bank.name,
                bank.agency,
                bank.account,
                bank.accountDigit
            )
        }
    }

    private fun onConfirmClick(v: View) {
        analytics.logScreenActionsWithTwoLabel(
            ArvAnalytics.ARV_REVIEW_AND_REQUEST_ANTICIPATION,
            ArvAnalytics.SCHEDULE_ARV,
            analytics.negotiationType(negotiationTypeArv),
            Label.BOTAO,
            ArvAnalytics.COMPLETE_REQUEST
        )

        CieloMessageBottomSheet.create(
            headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                title = getString(R.string.arv_scheduled_anticipation_confirmation_title),
                showCloseButton = true
            ),
            message = CieloMessageBottomSheet.Message(
                text = getString(
                    when (merchantType()) {
                        MerchantType.PF -> R.string.arv_scheduled_anticipation_confirmation_message
                        else -> {
                            if (isRoot()) R.string.arv_scheduled_anticipation_confirmation_message_root
                            else R.string.arv_scheduled_anticipation_confirmation_message_branch
                        }
                    }, arvEffectiveTImeViewModel.arvEffectiveTimeLiveData.value
                )
            ),
            mainButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                title = getString(R.string.confirmar),
                onTap = {
                    analytics.logScreenActionsWithCheckButton(
                        Action.MODAL,
                        Action.CLIQUE,
                        ArvAnalytics.SCHEDULE_ARV,
                        analytics.negotiationType(negotiationTypeArv),
                        ArvAnalytics.ATENTION_INFO_BEFORE_TO_CONFIRM,
                        ArvAnalytics.CONFIRM
                    )

                    it.dismissAllowingStateLoss()
                    viewModel.confirmAnticipation()
                }
            ),
            disableExpandableMode = true
        ).show(childFragmentManager, tag).also {
            analytics.logScreenDialogShow(
                Action.MODAL,
                Action.EXIBICAO,
                ArvAnalytics.SCHEDULE_ARV,
                analytics.negotiationType(negotiationTypeArv),
                ArvAnalytics.ATENTION_INFO_BEFORE_TO_CONFIRM
            )
        }
    }

    private fun onConfirmAnticipationSuccess() {
        analytics.logEventCallback(
            Action.CALLBACK,
            ArvAnalytics.SCHEDULE_ARV,
            analytics.negotiationType(negotiationTypeArv),
            ArvAnalytics.RESIDENCE,
            viewModel.selectedBank.value?.name
        )

        arvAnalytics.logPurchase(
            screenName = SCREEN_VIEW_ARV_AUTOMATIC_SUCCESS,
            transactionId = ArvAnalyticsGA4.tArvAutomatic,
            bankName = viewModel.selectedBank.value?.name.orEmpty(),
            itemCategory3 = negotiationTypeArv.orEmpty()
        )

        doWhenResumed {
            navigation?.showCustomHandlerView(
                contentImage = R.drawable.ic_129_anticipation,
                title = getString(R.string.arv_scheduled_anticipation_success_request_title),
                message = getString(R.string.anticipation_success_request_message),
                labelSecondButton = getString(R.string.acompanhar_status),
                isShowButtonClose = true,
                titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                messageAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                callbackClose = {
                    analytics.logScreenActionsWithTwoLabel(
                        ArvAnalytics.REQUEST_MADE_SUCCESSFULLY,
                        ArvAnalytics.SCHEDULE_ARV,
                        analytics.negotiationType(negotiationTypeArv),
                        Action.BOTAO,
                        Action.FECHAR
                    )

                    findNavController().safeNavigate(
                        ArvScheduledAnticipationConfirmationFragmentDirections.actionArvScheduledAnticipationConfirmationFragmentToArvHomeFragment()
                    )
                },
                callbackSecondButton = {
                    findNavController().safeNavigate(
                        ArvScheduledAnticipationConfirmationFragmentDirections.actionArvScheduledAnticipationConfirmationFragmentToArvHistoricListFragment()
                    )
                    analytics.logScreenActionsWithTwoLabel(
                        ArvAnalytics.REQUEST_MADE_SUCCESSFULLY,
                        ArvAnalytics.SCHEDULE_ARV,
                        analytics.negotiationType(negotiationTypeArv),
                        Action.BOTAO,
                        ArvAnalytics.TRACK_STATUS
                    )
                }
            ).also {
                analyticsScreenView(ArvAnalytics.SCREEN_VIEW_ARV_SCHEDULED_SUCCESS_REQUEST)
                arvAnalytics.logScreenView(SCREEN_VIEW_ARV_AUTOMATIC_SUCCESS)
            }
        }
    }

    private fun onConfirmAnticipationNotEligibleError(error: NewErrorMessage? = null) {
        trackError(error)

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

    private fun onConfirmAnticipationErrorToken(error: NewErrorMessage? = null) {
        onConfirmAnticipationError(error)
    }

    private fun onConfirmAnticipationError(error: NewErrorMessage?) {
        trackError(error)

        doWhenResumed {

            navigation?.showCustomHandlerView(
                title = getString(
                    R.string.commons_generic_error_title
                ),
                message = error?.message.orEmpty(),
                labelSecondButton = getString(R.string.entendi),
                isShowButtonClose = true,
                titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                messageAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                callbackClose = {
                    analytics.logScreenActionsWithTwoLabel(
                        ArvAnalytics.HAVE_A_PROBLEM,
                        ArvAnalytics.MFA,
                        ArvAnalytics.SCHEDULE_ARV,
                        analytics.negotiationType(negotiationTypeArv),
                        Action.BOTAO,
                        Action.FECHAR
                    )
                },
                callbackSecondButton = {
                    analytics.logScreenActionsWithTwoLabel(
                        ArvAnalytics.HAVE_A_PROBLEM,
                        ArvAnalytics.MFA,
                        ArvAnalytics.SCHEDULE_ARV,
                        analytics.negotiationType(negotiationTypeArv),
                        Action.BOTAO,
                        Action.UNDERSTOOD
                    )
                }
            ).also {
                analyticsScreenView(ArvAnalytics.SCREEN_VIEW_ARV_SCHEDULED_MFA_PROBLEM)
                hideLoading()
            }
        }
    }

    private fun trackError(error: NewErrorMessage?) {
        analytics.logEventCallback(
            Action.CALLBACK,
            ArvAnalytics.SCHEDULE_ARV,
            analytics.negotiationType(negotiationTypeArv),
            ArvAnalytics.RESIDENCE,
            viewModel.selectedBank.value?.name,
            error
        )

        arvAnalytics.logException(
            SCREEN_VIEW_ARV_AUTOMATIC_CONFIRMATION,
            error
        )
    }

    private fun trackClick() {
        arvAnalytics.logClick(
            screenName = SCREEN_VIEW_ARV_AUTOMATIC_CONFIRMATION,
            contentName = ArvAnalyticsGA4Constants.KNOW_MORE_BTN,
        )
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private companion object {
        const val CONTRACT_FILENAME = "contrato_arv_programada"
    }
}