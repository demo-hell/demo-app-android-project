package br.com.mobicare.cielo.tapOnPhone.presentation.router

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.bottomsheet.callhelpcenter.CallHelpCenterBottomSheet
import br.com.mobicare.cielo.commons.constants.HelpCenter.PHONE_CALL_CENTER
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.processErrorMessage
import br.com.mobicare.cielo.databinding.FragmentTapOnPhoneBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.posVirtual.analytics.PosVirtualAF
import br.com.mobicare.cielo.tapOnPhone.analytics.TapOnPhoneAnalytics
import br.com.mobicare.cielo.tapOnPhone.analytics.TapOnPhoneGA4
import br.com.mobicare.cielo.tapOnPhone.constants.TOP_ELIGIBILITY_ARGS
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneEligibilityResponse
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneTerminalResponse
import br.com.mobicare.cielo.tapOnPhone.enums.TapOnPhoneStatusEnum
import br.com.mobicare.cielo.tapOnPhone.presentation.terminal.setup.TapOnPhoneSetupTerminal
import br.com.mobicare.cielo.tapOnPhone.presentation.terminal.setup.TapOnPhoneSetupTerminalContract
import br.com.mobicare.cielo.tapOnPhone.utils.TapOnPhoneConstants
import br.com.mobicare.cielo.tapOnPhone.utils.TapOnPhoneConstants.TAP_ON_PHONE_ACTIVITY_WAS_OPENED_BY_POS_VIRTUAL_ARGS
import br.com.mobicare.cielo.tapOnPhone.utils.deviceBundle
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class TapOnPhoneFragment : BaseFragment(), CieloNavigationListener,
    TapOnPhoneSetupTerminalContract.Result, TapOnPhoneContract.View {

    private val tapOnPhoneSetupTerminal: TapOnPhoneSetupTerminal by inject {
        parametersOf(this@TapOnPhoneFragment)
    }

    private val presenter: TapOnPhonePresenter by inject {
        parametersOf(this@TapOnPhoneFragment)
    }

    private val analytics: TapOnPhoneAnalytics by inject()
    private val ga4: TapOnPhoneGA4 by inject()

    private var navigation: CieloNavigation? = null
    private var binding: FragmentTapOnPhoneBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = FragmentTapOnPhoneBinding.inflate(
        inflater, container, false
    ).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        setupOnResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
        tapOnPhoneSetupTerminal.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setupOnResume() {
        this.doWhenResumed {
            presenter.onResume()
            tapOnPhoneSetupTerminal.onResume()
            checkStatus()
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.showBackIcon()
            navigation?.showHelpButton()
            navigation?.showCloseButton()
            navigation?.showContainerButton()
            navigation?.setNavigationListener(this)
        }
    }

    private fun checkStatus() {
        val intent = navigation?.getData() as? Bundle
        val isPOS = intent?.getBoolean(TAP_ON_PHONE_ACTIVITY_WAS_OPENED_BY_POS_VIRTUAL_ARGS)
            ?: false
        presenter.onGetTapStatus(isPOS)
    }

    override fun onShowLoading(message: Int?) {
        this.doWhenResumed {
            navigation?.showAnimatedLoading(message)
        }
    }

    override fun onTapShowLoading(message: Int?) {
        this.doWhenResumed {
            navigation?.showAnimatedLoading(message)
        }
    }

    override fun onChangeLoadingText(message: Int?) {
        this.doWhenResumed {
            navigation?.changeAnimatedLoadingText(message)
        }
    }

    override fun onTapChangeLoadingText(message: Int?) {
        this.doWhenResumed {
            navigation?.changeAnimatedLoadingText(message)
        }
    }

    override fun onHideLoading() {
        this.doWhenResumed {
            navigation?.hideAnimatedLoading()
        }
    }

    override fun onTapHideLoading() {
        this.doWhenResumed {
            navigation?.hideAnimatedLoading()
        }
    }

    override fun onToDoAccreditation() {
        this.doWhenResumed {
            logTapOnPhoneApiStatusCallback(apiStatus = TapOnPhoneStatusEnum.ELIGIBLE.name)
            this.findNavController().navigate(
                R.id.action_tapOnPhoneFragment_to_tapOnPhoneOnboardingFragment
            )
        }
    }

    private fun logTapOnPhoneApiStatusCallback(apiStatus: String) {
        this.doWhenResumed {
            analytics.logStatusCallback(
                status = apiStatus
            )
        }
    }

    override fun onAccreditationInProgress() {
        this.doWhenResumed {
            logTapOnPhoneApiStatusCallback(apiStatus = TapOnPhoneStatusEnum.ORDER_IN_PROGRESS.name)

            showStatusAccreditation(
                title = R.string.tap_on_phone_accreditation_in_progress_title,
                message = R.string.tap_on_phone_accreditation_in_progress_message
            )
            analytics.logScreenView(
                TapOnPhoneAnalytics.ACCREDITATION_ORDER_IN_PROGRESS_SCREEN_PATH,
                javaClass
            )
            PosVirtualAF.logAccreditationInProgressScreenView()
        }
    }

    override fun onEstablishmentCreationInProgress() {
        this.doWhenResumed {
            logTapOnPhoneApiStatusCallback(apiStatus = TapOnPhoneStatusEnum.NOT_ACTIVE.name)

            showStatusAccreditation(
                title = R.string.tap_on_phone_establishment_creation_in_progress_title,
                message = R.string.tap_on_phone_establishment_creation_in_progress_message
            )
            analytics.logScreenView(
                TapOnPhoneAnalytics.ACCREDITATION_NOT_ACTIVE_SCREEN_PATH,
                javaClass
            )
        }
    }

    private fun showStatusAccreditation(title: Int, message: Int) {
        this.doWhenResumed {
            navigation?.showCustomHandler(
                contentImage = R.drawable.ic_08,
                title = getString(title),
                message = getString(message),
                titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                messageAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                labelSecondButton = getString(R.string.go_to_initial_screen),
                secondButtonCallback = {
                    this.doWhenResumed {
                        navigation?.goToHome()
                    }
                },
                finishCallback = {
                    this.doWhenResumed {
                        navigation?.goToHome()
                    }
                },
                headerCallback = {
                    this.doWhenResumed {
                        navigation?.goToHome()
                    }
                },
                isBack = true,
                isShowHeaderImage = true
            )
        }
    }

    override fun onTapIsActive(wasOpenedByPOSVirtual: Boolean) {
        this.doWhenResumed {
            logTapOnPhoneApiStatusCallback(apiStatus = TapOnPhoneStatusEnum.ENABLED.name)
            tapOnPhoneSetupTerminal.onInitializeAllowMe(
                onAction = {
                    this.doWhenResumed {
                        tapOnPhoneSetupTerminal.onCheckDeviceCompatibility(
                            gaFlowDetails = TapOnPhoneAnalytics.TRANSACTIONAL
                        )
                    }
                },
                isShowLoading = wasOpenedByPOSVirtual
            )
        }
    }

    override fun onExchangeEstablishment(eligibilityResponse: TapOnPhoneEligibilityResponse) {
        this.doWhenResumed {
            this.findNavController().navigate(
                R.id.action_tapOnPhoneFragment_to_tapOnPhoneImpersonateFragment,
                Bundle().apply {
                    putParcelable(
                        TOP_ELIGIBILITY_ARGS, eligibilityResponse
                    )
                })
        }
    }

    override fun onShowError(error: ErrorMessage?) {
        this.doWhenResumed {
            analytics.run {
                logScreenView(TapOnPhoneAnalytics.API_STATUS_ERROR_SCREEN_PATH, javaClass)
                logStatusCallback(
                    isError = true,
                    errorMessage = error?.errorMessage.orEmpty(),
                    errorCode = error?.code.orEmpty()
                )
            }
            ga4.run {
                logScreenView(TapOnPhoneGA4.SCREEN_VIEW_API_STATUS_ERROR)
                logException(
                    screenName = TapOnPhoneGA4.SCREEN_VIEW_API_STATUS_ERROR,
                    errorMessage = error?.errorMessage.orEmpty(),
                    errorCode = error?.code.orEmpty()
                )
            }

            genericError(errorMessage = error, onErrorAction = {
                requireActivity().finish()
            })
        }
    }

    override fun onShowCallCenter(error: ErrorMessage) {
        this.doWhenResumed {
            analytics.logOrderRequestCallback(
                isError = true,
                errorMessage = error.errorMessage,
                errorCode = error.code
            )
            ga4.logException(
                screenName = TapOnPhoneGA4.SCREEN_VIEW_API_STATUS_ERROR,
                errorMessage = error.errorMessage,
                errorCode = error.code
            )

            navigation?.showAnimatedLoadingError(
                onAction = {
                    this.doWhenResumed {
                        analytics.logScreenView(
                            TapOnPhoneAnalytics.ACCREDITATION_CANNOT_PROCEED_SCREEN_PATH,
                            javaClass
                        )
                        ga4.logScreenView(TapOnPhoneGA4.SCREEN_VIEW_ACCREDITATION_CANNOT_PROCEED)
                        navigation?.showCustomHandler(
                            title = getString(R.string.tap_on_phone_initialize_terminal_generic_error_title),
                            message = getString(
                                R.string.tap_on_phone_order_error_personal_info_message,
                                error.message
                            ),
                            labelSecondButton = getString(R.string.text_call_center_action),
                            secondButtonCallback = {
                                this.doWhenResumed {
                                    analytics.logScreenActions(
                                        TapOnPhoneAnalytics.CANNOT_PROCEED_WITH_ACCREDITATION,
                                        labelName = TapOnPhoneAnalytics.CALL_TO_CALL_CENTER
                                    )
                                    ga4.logClick(
                                        screenName = TapOnPhoneGA4.SCREEN_VIEW_ACCREDITATION_CANNOT_PROCEED,
                                        contentName = TapOnPhoneGA4.CALL_TO_CALL_CENTER
                                    )
                                    CallHelpCenterBottomSheet.newInstance()
                                        .show(childFragmentManager, tag)
                                }
                            },
                            headerCallback = {
                                this.doWhenResumed {
                                    analytics.logScreenActions(
                                        TapOnPhoneAnalytics.CANNOT_PROCEED_WITH_ACCREDITATION,
                                        labelName = Action.FECHAR
                                    )
                                    navigation?.goToHome()
                                }
                            },
                            finishCallback = {
                                this.doWhenResumed {
                                    navigation?.goToHome()
                                }
                            },
                            isBack = true
                        )
                    }
                })
        }
    }

    override fun getActivityTapOnPhone() = requireActivity()

    override fun getFragmentManagerTapOnPhone() = childFragmentManager

    override fun hasCardReader(): Boolean {
        val intent = navigation?.getData() as? Bundle
        return intent?.getBoolean(TapOnPhoneConstants.TAP_ON_PHONE_HAS_CARD_READER_ARGS) ?: false
    }

    override fun onDeviceCompatibility() {
        this.doWhenResumed {
            tapOnPhoneSetupTerminal.onInitializeTapOnPhone(
                isCheckDevice = false,
                isTransaction = false
            )
        }
    }

    override fun onNFCIsNotSupported(gaFlowDetails: String) {
        this.doWhenResumed {
            navigation?.nfcIsNotSupported(gaFlowDetails)
        }
    }

    override fun onAndroidIsNotSupported(gaFlowDetails: String) {
        this.doWhenResumed {
            navigation?.androidIsNotSupported(gaFlowDetails)
        }
    }

    override fun onNonEligible() {
        this.doWhenResumed {
            analytics.logStatusCallback(status = TapOnPhoneStatusEnum.NOT_ELIGIBLE.name)
            navigation?.notEligibleForTapOnPhone()
        }
    }

    override fun onEnableNFC(device: TapOnPhoneTerminalResponse?) {
        this.doWhenResumed {
            this.findNavController().navigate(
                R.id.action_tapOnPhoneFragment_to_tapOnPhoneEnableNFCFragment,
                deviceBundle(device)
            )
        }
    }

    override fun onDevelopModeEnable(device: TapOnPhoneTerminalResponse?) {
        this.doWhenResumed {
            this.findNavController().navigate(
                R.id.action_tapOnPhoneFragment_to_tapOnPhoneDeveloperModeIsEnableFragment,
                deviceBundle(device)
            )
        }
    }

    override fun onRetryConnectCardReader(device: TapOnPhoneTerminalResponse?) {
        doWhenResumed {
            navigation?.retryConnectCardReader(onAction = {
                this.doWhenResumed {
                    tapOnPhoneSetupTerminal.onInitializeAllowMe({
                        tapOnPhoneSetupTerminal.onInitializeTapOnPhone(deviceResponse = device)
                    })
                }
            })
        }
    }

    override fun onSuccessInActivatingTerminal(device: TapOnPhoneTerminalResponse?) {
        try {
            this.doWhenResumed {
                analytics.logScreenView(
                    name = TapOnPhoneAnalytics.ENABLEMENT_EXTENSION_ACTIVATED_PATH,
                    className = javaClass
                )

                navigation?.showAnimatedLoadingSuccess(message = R.string.tap_on_phone_extension_activated,
                    onAction = {
                        this.doWhenResumed {
                            this.findNavController().navigate(
                                R.id.action_tapOnPhoneFragment_to_tapOnPhoneTerminalReadyFragment,
                                deviceBundle(device)
                            )
                        }
                    })
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance()
                .log(
                    getString(
                        R.string.tap_on_phone_format_exception,
                        this::class.java.name,
                        ::onShowInsertSaleValueScreen.name,
                        e.message
                    )
                )
            showErrorDeleteCache()
        }
    }

    override fun onShowInsertSaleValueScreen(device: TapOnPhoneTerminalResponse?) {
        try {
            this.doWhenResumed {
                navigation?.showAnimatedLoadingSuccess(message = R.string.tap_on_phone_extension_activated,
                    onAction = {
                        this.doWhenResumed {
                            this.findNavController().navigate(
                                R.id.action_tapOnPhoneFragment_to_tapOnPhoneSaleValueFragment,
                                deviceBundle(device)
                            )
                        }
                    })
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance()
                .log(
                    getString(
                        R.string.tap_on_phone_format_exception,
                        this::class.java.name,
                        ::onShowInsertSaleValueScreen.name,
                        e.message
                    )
                )
            showErrorDeleteCache()
        }
    }


    override fun onErrorInActivatingTerminal(errorCode: Short?, errorMessage: ErrorMessage?) {
        this.doWhenResumed {
            analytics.logScreenView(
                name = TapOnPhoneAnalytics.TRANSACTIONAL_SDK_INITIALIZE_ERROR_PATH,
                className = javaClass
            )
            ga4.run {
                logScreenView(TapOnPhoneGA4.SCREEN_VIEW_TRANSACTIONAL_SDK_INITIALIZE_ERROR)
                logException(
                    screenName = TapOnPhoneGA4.SCREEN_VIEW_TRANSACTIONAL_SDK_INITIALIZE_ERROR,
                    errorMessage = errorMessage?.errorMessage.orEmpty(),
                    errorCode = errorMessage?.errorCode.orEmpty()
                )
            }
            genericError(errorMessage = errorMessage, onErrorAction = {
                this.doWhenResumed {
                    navigation?.goToHome()
                }
            })
        }
    }

    override fun onExtensionError() {
        this.doWhenResumed {
            analytics.logScreenView(
                name = TapOnPhoneAnalytics.TRANSACTIONAL_SDK_INITIALIZE_ERROR_PATH,
                className = javaClass
            )
            ga4.run {
                logScreenView(TapOnPhoneGA4.SCREEN_VIEW_TRANSACTIONAL_SDK_INITIALIZE_ERROR)
                logException(
                    screenName = TapOnPhoneGA4.SCREEN_VIEW_TRANSACTIONAL_SDK_INITIALIZE_ERROR,
                    errorMessage = EMPTY,
                    errorCode = EMPTY
                )
            }
            navigation?.showCustomHandler(
                contentImage = R.drawable.ic_19_maintenance,
                title = getString(R.string.tap_on_phone_initialize_terminal_generic_error_title),
                message = getString(R.string.id_onboarding_validate_p2_generic_error),
                titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                messageAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                labelSecondButton = getString(R.string.entendi),
                secondButtonCallback = {
                    this.doWhenResumed {
                        navigation?.goToHome()
                    }
                },
                headerCallback = {
                    this.doWhenResumed {
                        navigation?.goToHome()
                    }
                },
                finishCallback = {
                    this.doWhenResumed {
                        navigation?.goToHome()
                    }
                })
        }
    }

    override fun onTapIsDisabled() {
        this.doWhenResumed {
            analytics.run {
                logStatusCallback(status = TapOnPhoneStatusEnum.CANCELED.name)
                logScreenView(
                    name = TapOnPhoneAnalytics.DEACTIVATED_SCREEN_PATH,
                    className = javaClass
                )
            }
            ga4.logScreenView(TapOnPhoneGA4.SCREEN_VIEW_DEACTIVATED)

            navigation?.showCustomHandler(
                title = getString(R.string.tap_on_phone_is_disabled_title),
                titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                message = getString(R.string.tap_on_phone_is_disabled_message),
                messageAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                labelSecondButton = getString(R.string.text_call_center_action),
                contentImage = R.drawable.img_50_nao_elegivel,
                isBack = true,
                isShowHeaderImage = true,
                secondButtonCallback = {
                    openCallCenter()
                },
                finishCallback = {
                    closeDeactivatedMessage()
                },
                headerCallback = {
                    closeDeactivatedMessage()
                }
            )
        }
    }

    private fun closeDeactivatedMessage() {
        this.doWhenResumed {
            analytics.logScreenActions(
                TapOnPhoneAnalytics.DEACTIVATED_ON_YOUR_ACCOUNT,
                labelName = Action.FECHAR
            )
            navigation?.goToHome()
        }
    }

    private fun openCallCenter() {
        this.doWhenResumed {
            analytics.logScreenActions(
                TapOnPhoneAnalytics.DEACTIVATED_ON_YOUR_ACCOUNT,
                labelName = TapOnPhoneAnalytics.CALL_TO_CALL_CENTER
            )
            ga4.logClick(
                screenName = TapOnPhoneGA4.SCREEN_VIEW_DEACTIVATED,
                contentName = TapOnPhoneGA4.CALL_TO_CALL_CENTER
            )
            Utils.openCall(requireActivity(), PHONE_CALL_CENTER)
            navigation?.goToHome()
        }
    }

    private fun genericError(errorMessage: ErrorMessage?, onErrorAction: (() -> Unit)) {
        this.doWhenResumed {
            navigation?.showCustomErrorHandler(
                title = getString(R.string.tap_on_phone_initialize_terminal_generic_error_title),
                error = processErrorMessage(
                    errorMessage,
                    getString(R.string.error_generic),
                    getString(R.string.tap_on_phone_initialize_terminal_generic_error_message)
                ),
                titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                messageAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                labelSecondButton = getString(R.string.entendi),
                secondButtonCallback = {
                    this.doWhenResumed {
                        onErrorAction.invoke()
                    }
                },
                headerCallback = {
                    this.doWhenResumed {
                        navigation?.goToHome()
                    }
                },
                finishCallback = {
                    this.doWhenResumed {
                        onErrorAction.invoke()
                    }
                },
                isBack = true
            )
        }
    }

    private fun showErrorDeleteCache() {
        this.doWhenResumed {
            navigation?.showCustomHandler(
                title = getString(R.string.tap_on_phone_bs_error_delete_cache_title),
                message = getString(R.string.tap_on_phone_bs_error_delete_cache_message),
                titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                messageAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                labelSecondButton = getString(R.string.tap_on_phone_bs_error_delete_cache_label_button),
                secondButtonCallback = {
                    this.doWhenResumed {
                        deleteCache()
                    }
                },
                headerCallback = {
                    this.doWhenResumed {
                        navigation?.goToHome()
                    }
                },
                finishCallback = {
                    this.doWhenResumed {
                        navigation?.goToHome()
                    }
                })
        }
    }

    private fun deleteCache() {
        presenter.onDeleteCache(requireContext())
        baseLogout(isLoginScreen = false)
    }

}