package br.com.mobicare.cielo.arv.presentation.anticipation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.CANCEL
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.HELP
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV_CANCEL_REASONS
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV_CANCEL_SUCCESS
import br.com.mobicare.cielo.arv.utils.UiArvCancelScheduledAnticipationState
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Action.VOLTAR
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.constants.ONE_NEGATIVE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.databinding.FragmentArvCancelReasonBinding
import br.com.mobicare.cielo.extensions.backToHome
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.safeNavigate
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArvCancelScheduledAnticipationFragment : BaseFragment(), CieloNavigationListener {

    private val arvCancelScheduledViewModel: ArvCancelScheduledViewModel by viewModel()

    private val args: ArvCancelScheduledAnticipationFragmentArgs by navArgs()

    private var navigation: CieloNavigation? = null
    private var binding: FragmentArvCancelReasonBinding? = null
    private val analytics: ArvAnalytics by inject()
    private val arvAnalytics: ArvAnalyticsGA4 by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = FragmentArvCancelReasonBinding.inflate(inflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupListeners()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        arvAnalytics.logScreenView(screenName = SCREEN_VIEW_ARV_CANCEL_REASONS)
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.showHelpButton(isShow = true)
            navigation?.setupToolbar(
                title = getString(R.string.arv_cancel_reason), isCollapsed = false
            )
        }
    }

    override fun onHelpButtonClicked() {
        analytics.logScreenActionsWithOneLabel(
            CANCEL, EMPTY_VALUE, Label.BOTAO, HELP
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        analytics.logScreenActionsWithOneLabel(
            CANCEL, EMPTY_VALUE, Label.BOTAO, VOLTAR
        )
    }

    private fun setupListeners() {
        binding?.apply {
            reasonsGroup.setOnCheckedChangeListener { _, id ->
                when (id) {
                    R.id.RBOther -> setupMandatoryComment()
                    else -> setupOptionalComment()
                }
                validateInput()
            }
            etCancelReason.addTextChangedListener {
                validateInput()
            }
            btConfirm.setOnClickListener {
                analytics.logScreenActionsWithLabelAndDetail(
                    CANCEL,
                    ArvAnalytics.SCHEDULE_ARV,
                    args.scheduledanticipationcancelargs,
                    Action.CONFIRMAR,
                    getSelectedReason(),
                    getComment()
                )
                arvCancelScheduledViewModel.cancelAnticipation(args.scheduledanticipationcancelargs)
            }
        }
    }

    private fun getComment() = binding?.etCancelReason?.text?.toString().orEmpty()

    private fun getSelectedReason(): String {
        binding?.reasonsGroup?.apply {
            return findViewById<RadioButton>(checkedRadioButtonId).text.toString()
        }
        return EMPTY_VALUE
    }

    private fun validateInput() {
        binding?.apply {
            btConfirm.isEnabled = when (reasonsGroup.checkedRadioButtonId) {
                R.id.RBOther -> etCancelReason.text?.isNotEmpty() == true
                ONE_NEGATIVE -> false
                else -> true
            }

        }
    }

    private fun setupOptionalComment() {
        binding?.apply {
            tvCancelReason.visible()
            itCancelReason.visible()
            etCancelReason.hint =
                getString(R.string.arv_write_reason_here, getString(R.string.arv_optional))
        }
    }

    private fun setupMandatoryComment() {
        binding?.apply {
            tvCancelReason.visible()
            itCancelReason.visible()
            etCancelReason.hint =
                getString(R.string.arv_write_reason_here, getString(R.string.arv_mandatory))
        }
    }

    private fun setupObservers() {
        arvCancelScheduledViewModel.arvCancelScheduledAnticipationState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiArvCancelScheduledAnticipationState.ShowLoading -> showLoading()
                is UiArvCancelScheduledAnticipationState.HideLoading -> hideLoading()
                is UiArvCancelScheduledAnticipationState.Success -> onCancelAnticipationSuccess()
                is UiArvCancelScheduledAnticipationState.Error -> onCancelAnticipationError(
                    state.error
                )

                is UiArvCancelScheduledAnticipationState.ErrorToken -> onCancelAnticipationErrorToken(
                    state.error
                )
            }
        }

    }

    private fun onCancelAnticipationSuccess() {
        arvAnalytics.logCancel(reason = getSelectedReason())

        doWhenResumed {
            navigation?.showCustomHandlerView(contentImage = R.drawable.img_08_celular_dinheiro_cartao,
                title = getString(R.string.arv_cancel_success_title),
                titleStyle = R.style.bold_montserrat_20_brand_600_spacing_8,
                message = getString(R.string.arv_cancel_success_message),
                labelSecondButton = getString(R.string.go_to_beginning),
                isShowButtonClose = true,
                titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                messageAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                callbackClose = {
                    activity?.backToHome()
                },
                callbackSecondButton = {
                    findNavController().safeNavigate(
                        ArvCancelScheduledAnticipationFragmentDirections.actionArvScheduledAnticipationConfirmationFragmentToArvHomeFragment()
                    )
                }).also {
                    arvAnalytics.logScreenView(screenName = SCREEN_VIEW_ARV_CANCEL_SUCCESS)
            }
        }
    }

    private fun onCancelAnticipationErrorToken(error: NewErrorMessage? = null) {
        onCancelAnticipationError(error)
    }

    private fun onCancelAnticipationError(error: NewErrorMessage?) {
        arvAnalytics.logException(
            screenName = SCREEN_VIEW_ARV_CANCEL_REASONS,
            error = error
        )
        doWhenResumed {
            navigation?.showCustomHandlerView(
                title = getString(
                    R.string.commons_generic_error_title
                ),
                message = error?.message.orEmpty(),
                labelSecondButton = getString(R.string.entendi),
                isShowButtonClose = true,
                titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                messageAlignment = View.TEXT_ALIGNMENT_TEXT_START
            ).also {
                hideLoading()
            }
        }
    }

    private fun hideLoading() {
        navigation?.hideAnimatedLoading()
    }

    private fun showLoading() {
        navigation?.showAnimatedLoading()
    }


    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}