package br.com.mobicare.cielo.arv.presentation.anticipation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.field.TextFieldFlui
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.BUTTON_SIMULATED
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.EDIT_FLAGS_LOAD_WITH_VALUE
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.HAVE_A_PROBLEM
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.LOAD
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.NOT_POSSIBLE_SIMULATED_VALUE_ABOVE_LIQUID_VALUE
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.RECEIVABLES_CIELO
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.RECEIVABLES_MARKET
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SCREEN_VIEW_ARV_SIMULATED_WITH_VALUE
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SCREEN_VIEW_ARV_SIMULATED_WITH_VALUE_ERROR
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SCREEN_VIEW_ARV_SIMULATED_WITH_VALUE_LOAD
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.SINGLE_ARV
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.TRY_AGAIN
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.VALUE_WITH_DESIRE_WITHDRAW
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.VALUE_WITH_DESIRE_WITHDRAW_CALLBACK
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV_SINGLE_CONFIGURATION_SIMULATOR
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SIMULATE
import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.utils.ArvConstants
import br.com.mobicare.cielo.arv.utils.UiArvSingleWithValueState
import br.com.mobicare.cielo.commons.analytics.Action.BOTAO
import br.com.mobicare.cielo.commons.analytics.Action.FECHAR
import br.com.mobicare.cielo.commons.analytics.Action.VOLTAR
import br.com.mobicare.cielo.commons.analytics.ERRO
import br.com.mobicare.cielo.commons.analytics.EXIBICAO
import br.com.mobicare.cielo.commons.constants.TWO_THOUSAND
import br.com.mobicare.cielo.commons.constants.Text.EMPTY
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.formatterErrorMessage
import br.com.mobicare.cielo.commons.utils.getNewErrorMessage
import br.com.mobicare.cielo.commons.utils.moneyToDoubleValue
import br.com.mobicare.cielo.commons.utils.textToMoneyBigDecimalFormat
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.toPtBrRealStringWithoutSymbol
import br.com.mobicare.cielo.databinding.FragmentArvSimulateSingleAnticipationValueBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.safeNavigate
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.BigDecimal

class ArvSimulateSingleAnticipationValueFragment : BaseFragment(), CieloNavigationListener {

    private val viewModel: ArvSingleAnticipationSimulateWithValueViewModel by viewModel()
    private var binding: FragmentArvSimulateSingleAnticipationValueBinding? = null
    private var navigation: CieloNavigation? = null
    private val arvAnalytics: ArvAnalyticsGA4 by inject()
    private val analytics: ArvAnalytics by inject()
    private val args: ArvSimulateSingleAnticipationValueFragmentArgs by navArgs()
    private var arvAnticipation: ArvAnticipation? = null
    private lateinit var negotiationTypeArv: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentArvSimulateSingleAnticipationValueBinding.inflate(
        inflater, container, false
    ).also {
        binding = it
    }.root

    override fun onResume() {
        super.onResume()
        analyticsScreenViewG4A()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupObservers()
        setupInitAnticipation()
        setupInit(arvAnticipation)
        checkTypeArvReceivableForAnalytics(arvAnticipation)
        analyticsScreenView(SCREEN_VIEW_ARV_SIMULATED_WITH_VALUE)
    }

    private fun setupInitAnticipation() {
        binding.apply {
            args.simulationvalueargs.let { anticipation ->
                this@ArvSimulateSingleAnticipationValueFragment.arvAnticipation = anticipation
            }
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.showHelpButton(false)
            navigation?.showContainerButton(isShow = true)
            navigation?.showButton(true)
            navigation?.setTextButton(getString(R.string.simulate_label_button))
            navigation?.enableButton(false)
            navigation?.setupToolbar(
                title = getString(R.string.arv_simulate_what_value_do_you_want),
                isCollapsed = false
            )
        }
    }

    private fun setupInit(arvAnticipation: ArvAnticipation?) {
        viewModel.receivableType = arvAnticipation?.negotiationType
        binding?.apply {
            arvSimulateBoxInfo.tvValueArvSimulateValueBox.text =
                arvAnticipation?.netAmount?.toPtBrRealString()
            itAmount.apply {
                setInputTypeTextField(InputType.TYPE_CLASS_NUMBER)
                forceKeyboardOpening()
                setOnTextChangeListener(object :
                    TextFieldFlui.TextChangeListener {
                    override fun onTextChanged(
                        userInput: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        super.onTextChanged(userInput, start, before, count)
                        setInputError(userInput, arvAnticipation?.netAmount)
                    }
                })
            }
        }
    }

    private fun setInputError(value: CharSequence?, netAmount: Double?) {
        value.toString().moneyToDoubleValue().also { itValue ->
            if (itValue <= ZERO_DOUBLE) {
                navigation?.enableButton(false)
            } else {
                if (netAmount != null) {
                    val isAnError = itValue > netAmount
                    setupBoxValueError(isAnError)
                }
            }
        }
    }

    private fun setupBoxValueError(isAnError: Boolean) {
        binding?.itAmount?.apply {
            navigation?.enableButton(isAnError.not())
            isShowErrorIcon = isAnError
            isShowError = isAnError
            iconError = R.drawable.ic_close_round_danger_400_16_dp
            errorMessage = if (isAnError) {
                analytics.logScreenActionsWithCheckButton(
                    VALUE_WITH_DESIRE_WITHDRAW,
                    SINGLE_ARV,
                    negotiationTypeArv,
                    EXIBICAO,
                    ERRO,
                    NOT_POSSIBLE_SIMULATED_VALUE_ABOVE_LIQUID_VALUE
                )
                getString(R.string.arv_simulate_alert_max_value)
            } else EMPTY

            errorMessageContentDescription = if (isAnError)
                getString(R.string.arv_simulate_alert_max_value)
            else EMPTY
        }
    }

    private fun setupObservers() {
        viewModel.arvSingleAnticipationWithValueLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiArvSingleWithValueState.ShowLoadingArvSingleWithValue -> showLoading()
                is UiArvSingleWithValueState.HideLoadingArvSingleWithValue -> hideLoading()
                is UiArvSingleWithValueState.SuccessArvSingleWithValue -> onSuccess(
                    uiState.anticipation
                )

                is UiArvSingleWithValueState.ErrorArvSingleWithValue -> onArvSingleError(
                    uiState.error, requireContext().formatterErrorMessage(uiState.message)
                )
            }
        }
    }

    private fun getAmount(): BigDecimal {
        return binding?.itAmount?.getTextField()?.textToMoneyBigDecimalFormat()
            ?: BigDecimal.ZERO
    }

    override fun onButtonClicked(labelButton: String) {
        if (getAmount().toDouble() != ZERO_DOUBLE) {
            trackSimulateButtonClick()
            navigation?.hideKeyboard()
            sendValueSimulated()
        }
    }

    private fun sendValueSimulated() {
        val value = getAmount().toDouble()
        viewModel.getArvSingleAnticipationWithValue(
            value,
            arvAnticipation?.initialDate,
            arvAnticipation?.finalDate,
        )
    }

    private fun showLoading() {
        navigation?.showAnimatedLoading(
            message = R.string.arv_wait_a_moment
        )
        analyticsScreenView(SCREEN_VIEW_ARV_SIMULATED_WITH_VALUE_LOAD)
    }

    private fun hideLoading() {
        Handler(Looper.getMainLooper()).postDelayed({
            navigation?.hideAnimatedLoading()
        }, TWO_THOUSAND)
    }

    private fun onSuccess(arvAnticipation: ArvAnticipation?) {
        analytics.logEventCallbackNewArv(
            VALUE_WITH_DESIRE_WITHDRAW_CALLBACK,
            LOAD,
            SINGLE_ARV,
            negotiationTypeArv,
            EMPTY
        )
        goToSimulationFragment(arvAnticipation)
    }

    private fun onArvSingleError(error: NewErrorMessage, formatterErrorMessage: String) {
        trackSingleError(error)
        doWhenResumed {
            navigation?.showCustomHandlerView(
                contentImage = R.drawable.img_10_erro,
                title = getString(R.string.commons_generic_error_title),
                message = requireContext().getNewErrorMessage(
                    newMessage = R.string.commons_generic_error_message
                ),
                labelSecondButton = getString(R.string.btn_two_error),
                callbackSecondButton = {
                    analytics.logScreenActionsWithTwoLabel(
                        HAVE_A_PROBLEM,
                        EDIT_FLAGS_LOAD_WITH_VALUE,
                        SINGLE_ARV,
                        negotiationTypeArv,
                        BOTAO,
                        TRY_AGAIN
                    )
                    sendValueSimulated()
                },
                isShowButtonClose = true,
                titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                messageAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                titleStyle = R.style.Heading_600_bold_20_brand_600,
                callbackClose = {
                    analytics.logScreenActionsWithTwoLabel(
                        HAVE_A_PROBLEM,
                        EDIT_FLAGS_LOAD_WITH_VALUE,
                        SINGLE_ARV,
                        negotiationTypeArv,
                        BOTAO,
                        FECHAR
                    )
                },
                isShowFirstButton = true,
                callbackFirstButton = {
                    analytics.logScreenActionsWithTwoLabel(
                        HAVE_A_PROBLEM,
                        EDIT_FLAGS_LOAD_WITH_VALUE,
                        SINGLE_ARV,
                        negotiationTypeArv,
                        BOTAO,
                        VOLTAR
                    )
                },
                labelFirstButton = getString(R.string.back)
            )
        }
    }

    private fun goToSimulationFragment(arvAnticipation: ArvAnticipation?) {
        navigation?.showContainerButton(false)
        arvAnticipation?.let {
            findNavController().safeNavigate(
                ArvSimulateSingleAnticipationValueFragmentDirections
                    .actionArvSimulateSingleAnticipationValueFragmentToArvAnticipationSimulationFragment(
                        it
                    )
            )
            viewModel.resetState()
        }
    }

    private fun analyticsScreenView(screen: String) {
        analytics.logScreenView(
            name = screen,
            className = this.javaClass
        )
    }

    private fun analyticsScreenViewG4A() {
        arvAnalytics.logScreenView(
            SCREEN_VIEW_ARV_SINGLE_CONFIGURATION_SIMULATOR
        )
    }

    private fun trackSimulateButtonClick() {
        arvAnalytics.logClick(
            screenName = SCREEN_VIEW_ARV_SINGLE_CONFIGURATION_SIMULATOR,
            contentName = SIMULATE
        )
        analytics.logScreenActionsGetValue(
            VALUE_WITH_DESIRE_WITHDRAW,
            SINGLE_ARV,
            negotiationTypeArv,
            BUTTON_SIMULATED,
            getAmount().toPtBrRealStringWithoutSymbol()
        )
    }

    private fun trackSingleError(error: NewErrorMessage) {
        analyticsScreenView(SCREEN_VIEW_ARV_SIMULATED_WITH_VALUE_ERROR)
        analytics.logCallbackErrorEvent(
            VALUE_WITH_DESIRE_WITHDRAW_CALLBACK,
            LOAD,
            SINGLE_ARV,
            negotiationTypeArv,
            error
        )
        arvAnalytics.logException(
            SCREEN_VIEW_ARV_SINGLE_CONFIGURATION_SIMULATOR,
            error
        )
    }

    private fun checkTypeArvReceivableForAnalytics(arvAnticipation: ArvAnticipation?) {
        negotiationTypeArv =
            if (arvAnticipation?.negotiationType == ArvConstants.CIELO_NEGOTIATION_TYPE)
                RECEIVABLES_CIELO
            else
                RECEIVABLES_MARKET
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}