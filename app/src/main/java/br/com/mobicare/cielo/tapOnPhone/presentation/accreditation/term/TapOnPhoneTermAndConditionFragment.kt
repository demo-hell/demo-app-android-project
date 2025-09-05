package br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.term

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.bottomsheet.callhelpcenter.CallHelpCenterBottomSheet
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.processErrorMessage
import br.com.mobicare.cielo.commons.utils.recycler.MarginItemDecoration
import br.com.mobicare.cielo.commons.utils.registerForActivityResultCustom
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.OfferResponse
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.Term
import br.com.mobicare.cielo.component.requiredDataField.presentation.RequiredDataFieldFlowActivity
import br.com.mobicare.cielo.component.requiredDataField.presentation.RequiredDataFieldFlowActivityContract
import br.com.mobicare.cielo.component.requiredDataField.presentation.model.UiRequiredDataField
import br.com.mobicare.cielo.databinding.FragmentTapOnPhoneTermAndConditionBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.tapOnPhone.analytics.TapOnPhoneAnalytics
import br.com.mobicare.cielo.tapOnPhone.analytics.TapOnPhoneGA4
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneAccount
import br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.term.adapter.TermLinkAdapter
import br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.term.adapter.TermLinkContract
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class TapOnPhoneTermAndConditionFragment : BaseFragment(), CieloNavigationListener,
    TapOnPhoneTermAndConditionContract.View {

    private var navigation: CieloNavigation? = null
    private var binding: FragmentTapOnPhoneTermAndConditionBinding? = null

    private val presenter: TapOnPhoneTermAndConditionPresenter by inject {
        parametersOf(this)
    }

    private val analytics: TapOnPhoneAnalytics by inject()
    private val ga4: TapOnPhoneGA4 by inject()

    private val args: TapOnPhoneTermAndConditionFragmentArgs by navArgs()

    private var requiredDataFieldFlowActivity: ActivityResultLauncher<Intent>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentTapOnPhoneTermAndConditionBinding.inflate(inflater, container, false)
            .also {
                binding = it
            }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerRequiredDataFieldFlowActivity()
        setupNavigation()
        setupTermsRv()
        setupListeners()
    }

    override fun onResume() {
        analytics.logScreenView(
            TapOnPhoneAnalytics.ACCREDITATION_TERM_AND_CONDITIONS_SCREEN_PATH,
            javaClass
        )
        ga4.logScreenView(TapOnPhoneGA4.SCREEN_VIEW_TERM_AND_CONDITIONS)
        presenter.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun onShowRequiredDataField(data: UiRequiredDataField) {
        val intent = RequiredDataFieldFlowActivity.launch(requireContext(), data)
        requiredDataFieldFlowActivity?.launch(intent)
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.showBackIcon()
            navigation?.showHelpButton()
            navigation?.showCloseButton()
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupTermsRv() {
        binding?.apply {
            rvTerms.layoutManager = LinearLayoutManager(
                requireActivity(),
                LinearLayoutManager.VERTICAL, false
            )
            rvTerms.adapter = TermLinkAdapter(
                args.offerargs.offer?.agreements.orEmpty().flatMap { it.terms.orEmpty() },
                object : TermLinkContract.View() {
                    override fun onTermClick(item: Term) {
                        item.url?.let { showTerms(it) }
                    }
                }
            )
            rvTerms.addItemDecoration(
                MarginItemDecoration(
                    resources.getDimensionPixelSize(R.dimen.dimen_26dp),
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    private fun setupListeners() {
        binding?.apply {
            checkboxArea.setOnClickListener {
                checkBoxTerms.toggle()
            }

            checkBoxTerms.setOnCheckedChangeListener { _, isChecked ->
                btnConclude.isEnabled = isChecked
            }

            btnConclude.setOnClickListener {
                analytics.logScreenActions(
                    flowName = TapOnPhoneAnalytics.TERM_AND_CONDITIONS,
                    labelName = btnConclude.text.toString()
                )
                ga4.logClick(
                    screenName = TapOnPhoneGA4.SCREEN_VIEW_TERM_AND_CONDITIONS,
                    contentName = btnConclude.text.toString(),
                    contentComponent = TapOnPhoneGA4.TERM_AND_CONDITIONS
                )
                requestAccreditation(args.accountargs, args.offerargs, args.sessionidargs)
            }
        }
    }

    private fun showTerms(url: String) {
        findNavController().navigate(
            TapOnPhoneTermAndConditionFragmentDirections
                .actionTapOnPhoneTermAndConditionFragmentToTermViewFragment(url)
        )
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun showLoading() {
        navigation?.showAnimatedLoading(R.string.tap_on_phone_sending_order_request)
    }

    override fun hideLoading() {
        navigation?.hideAnimatedLoading()
    }

    private fun requestAccreditation(
        account: TapOnPhoneAccount,
        offer: OfferResponse,
        sessionId: String
    ) {
        presenter.requestAccreditation(account, offer, sessionId)
    }

    override fun onRequestTapOnPhoneOrderSuccess(order: String) {
        doWhenResumed {
            analytics.logOrderRequestCallback()
            ga4.logPurchase(
                screenName = TapOnPhoneGA4.SCREEN_VIEW_ACCREDITATION_ORDER_SUCCESS,
                transactionId = order
            )
            navigation?.showAnimatedLoadingSuccess(
                message = R.string.tap_on_phone_accreditation_loading_success_message,
                onAction = {
                    analytics.logScreenView(
                        TapOnPhoneAnalytics.ACCREDITATION_ORDER_SUCCESS_SCREEN_PATH,
                        javaClass
                    )
                    ga4.logScreenView(TapOnPhoneGA4.SCREEN_VIEW_ACCREDITATION_ORDER_SUCCESS)
                    showSuccessHandler(order)
                })
        }

    }

    override fun showError(error: ErrorMessage?) {
        doWhenResumed {
            analytics.logOrderRequestCallback(
                isError = true,
                errorMessage = error?.errorMessage.orEmpty(),
                errorCode = error?.code.orEmpty()
            )
            ga4.logException(
                screenName = TapOnPhoneGA4.SCREEN_VIEW_ACCREDITATION_TERM_ACCEPT_ERROR,
                errorMessage = error?.errorMessage.orEmpty(),
                errorCode = error?.code.orEmpty()
            )
            navigation?.showAnimatedLoadingError(
                onAction = {
                    analytics.logScreenView(
                        TapOnPhoneAnalytics.ACCREDITATION_TERM_ACCEPT_ERROR_SCREEN_PATH,
                        javaClass
                    )
                    ga4.logScreenView(TapOnPhoneGA4.SCREEN_VIEW_ACCREDITATION_TERM_ACCEPT_ERROR)
                    showErrorHandler(error)
                })
        }
    }

    override fun onShowCallCenter(error: ErrorMessage) {
        doWhenResumed {
            analytics.logOrderRequestCallback(
                isError = true,
                errorMessage = error.errorMessage,
                errorCode = error.code
            )
            ga4.logException(
                screenName = TapOnPhoneGA4.SCREEN_VIEW_ACCREDITATION_TERM_ACCEPT_ERROR,
                errorMessage = error.errorMessage,
                errorCode = error.code
            )
            navigation?.showAnimatedLoadingError(
                onAction = {
                    analytics.logScreenView(
                        TapOnPhoneAnalytics.ACCREDITATION_CANNOT_PROCEED_SCREEN_PATH,
                        javaClass
                    )
                    ga4.logScreenView(TapOnPhoneGA4.SCREEN_VIEW_ACCREDITATION_TERM_ACCEPT_ERROR)
                    navigation?.showCustomHandler(
                        contentImage = R.drawable.ic_90_celular_atencao,
                        titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                        title = getString(R.string.tap_on_phone_bs_required_data_field_title),
                        message = getString(R.string.tap_on_phone_bs_required_data_field_description),
                        isShowFirstButton = true,
                        isShowHeaderImage = false,
                        labelFirstButton = getString(R.string.tap_on_phone_bs_required_data_field_label_button_secondary),
                        labelSecondButton = getString(R.string.text_call_center_action),
                        firstButtonCallback = {
                            navigation?.goToHome()
                        },
                        secondButtonCallback = {
                            CallHelpCenterBottomSheet.newInstance().show(childFragmentManager, tag)
                            requireActivity().finish()
                        },
                        headerCallback = {
                            navigation?.goToHome()
                        },
                        finishCallback = {
                            navigation?.goToHome()
                        }
                    )
                })
        }
    }

    private fun showErrorHandler(error: ErrorMessage?) {
        doWhenResumed {
            navigation?.showCustomErrorHandler(
                title = getString(R.string.tap_on_phone_initialize_terminal_generic_error_title),
                error = processErrorMessage(
                    error,
                    getString(R.string.error_generic),
                    getString(R.string.tap_on_phone_initialize_terminal_generic_error_message)
                ),
                labelSecondButton = getString(R.string.entendi),
                secondButtonCallback = {
                    finishScreen()
                },
                finishCallback = {
                    finishScreen()
                },
                isBack = true
            )
        }
    }

    private fun showSuccessHandler(order: String) {
        doWhenResumed {
            navigation?.showCustomHandler(
                contentImage = R.drawable.ic_08,
                title = getString(R.string.required_data_field_title_bs_success_update_data),
                message = getString(R.string.required_data_field_message_bs_success_update_data, order),
                titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                messageAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                labelSecondButton = getString(R.string.go_to_initial_screen),
                secondButtonCallback = ::finishScreen,
                finishCallback = ::finishScreen,
                headerCallback = ::finishScreen,
                isBack = true,
                isShowHeaderImage = true
            )
        }
    }

    private fun finishScreen() {
        navigation?.goToHome()
    }

    override fun onBackButtonClicked(): Boolean {
        analytics.logScreenActions(
            flowName = TapOnPhoneAnalytics.TERM_AND_CONDITIONS,
            labelName = Action.VOLTAR
        )
        return super.onBackButtonClicked()
    }

    private fun registerRequiredDataFieldFlowActivity() {
        requiredDataFieldFlowActivity =
            registerForActivityResultCustom(::callbackResultRequiredDataFieldFlowActivity)
    }

    private fun callbackResultRequiredDataFieldFlowActivity(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            requireActivity().finish()
        }
    }

}