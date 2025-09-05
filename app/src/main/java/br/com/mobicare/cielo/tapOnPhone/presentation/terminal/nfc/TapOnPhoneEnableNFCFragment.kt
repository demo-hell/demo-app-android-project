package br.com.mobicare.cielo.tapOnPhone.presentation.terminal.nfc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.processErrorMessage
import br.com.mobicare.cielo.databinding.FragmentTapOnPhoneEnableNfcBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.openNFCSettings
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import br.com.mobicare.cielo.tapOnPhone.analytics.TapOnPhoneAnalytics
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneTerminalResponse
import br.com.mobicare.cielo.tapOnPhone.presentation.terminal.setup.TapOnPhoneSetupTerminal
import br.com.mobicare.cielo.tapOnPhone.presentation.terminal.setup.TapOnPhoneSetupTerminalContract
import br.com.mobicare.cielo.tapOnPhone.utils.TapOnPhoneConstants
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class TapOnPhoneEnableNFCFragment : BaseFragment(),
    CieloNavigationListener, TapOnPhoneSetupTerminalContract.Result {

    private val tapOnPhoneSetupTerminal: TapOnPhoneSetupTerminal by inject {
        parametersOf(this@TapOnPhoneEnableNFCFragment)
    }

    private val analytics: TapOnPhoneAnalytics by inject()

    private var binding: FragmentTapOnPhoneEnableNfcBinding? = null
    private var navigation: CieloNavigation? = null

    private val args: TapOnPhoneEnableNFCFragmentArgs by navArgs()
    private var didCreate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        didCreate = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentTapOnPhoneEnableNfcBinding.inflate(inflater, container, false)
            .also {
                binding = it
            }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        tapOnPhoneSetupTerminal.onResume()
        logScreenView()
        checkTapOnPhone(args.devicetapargs)
    }

    override fun onPause() {
        super.onPause()
        tapOnPhoneSetupTerminal.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun logScreenView() {
        analytics.logEnableNfcOnSettings(TapOnPhoneAnalytics.TRANSACTIONAL, javaClass)
    }

    private fun logScreenAction(labelName: String) {
        analytics.logScreenActions(
            TapOnPhoneAnalytics.ENABLE_NFC_ON_SETTINGS,
            labelName = labelName
        )
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextButton(getString(R.string.tap_on_phone_enable_nfc))
            navigation?.showBackIcon()
            navigation?.showCloseButton()
            navigation?.showButton(isShow = true)
            navigation?.showHelpButton(isShow = true)
            navigation?.showContainerButton(isShow = true)
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupView() {
        binding?.startupStepsTapOnPhone?.apply {
            tvTitleTapOnPhone.text = getString(R.string.tap_on_phone_enable_nfc_title)
            tvSubtitleTapOnPhone.text = getString(R.string.tap_on_phone_enable_nfc_subtitle)

            ivTapOnPhone.setImageResource(R.drawable.ic_141_nfc)
        }
    }

    private fun checkTapOnPhone(device: TapOnPhoneTerminalResponse?) {
        doWhenResumed {
            if (didCreate)
                tapOnPhoneSetupTerminal.onInitializeAllowMe({
                    tapOnPhoneSetupTerminal.onInitializeTapOnPhone(
                        deviceResponse = device
                    )
                })
        }
    }

    override fun onButtonClicked(labelButton: String) {
        logScreenAction(TapOnPhoneAnalytics.ACCESS_SETTINGS)
        requireActivity().openNFCSettings()
    }

    override fun onTapShowLoading(message: Int?) {
        doWhenResumed {
            navigation?.showAnimatedLoading(message)
        }
    }

    override fun onTapChangeLoadingText(message: Int?) {
        doWhenResumed {
            navigation?.changeAnimatedLoadingText(message)
        }
    }

    override fun onTapHideLoading() {
        doWhenResumed {
            navigation?.hideAnimatedLoading()
        }
    }

    override fun isSaleScreen() = false

    override fun getActivityTapOnPhone() = requireActivity()

    override fun getFragmentManagerTapOnPhone() = childFragmentManager

    override fun hasCardReader(): Boolean {
        val intent = navigation?.getData() as? Bundle
        return intent?.getBoolean(TapOnPhoneConstants.TAP_ON_PHONE_HAS_CARD_READER_ARGS) ?: false
    }

    override fun onNFCIsNotSupported(gaFlowDetails: String) {
        doWhenResumed {
            navigation?.nfcIsNotSupported(gaFlowDetails)
        }
    }

    override fun onAndroidIsNotSupported(gaFlowDetails: String) {
        doWhenResumed {
            navigation?.androidIsNotSupported(gaFlowDetails)
        }
    }

    override fun onEnableNFC(device: TapOnPhoneTerminalResponse?) {
        doWhenResumed {
            setupView()
        }
    }

    override fun onRetryConnectCardReader(device: TapOnPhoneTerminalResponse?) {
        doWhenResumed {
            navigation?.retryConnectCardReader(onAction = {
                checkTapOnPhone(device)
            })
        }
    }

    override fun onDevelopModeEnable(device: TapOnPhoneTerminalResponse?) {
        doWhenResumed {
            findNavController().navigate(
                TapOnPhoneEnableNFCFragmentDirections
                    .actionTapOnPhoneEnableNFCFragmentToTapOnPhoneDeveloperModeIsEnableFragment(
                        device
                    )
            )
        }
    }

    override fun onSuccessInActivatingTerminal(device: TapOnPhoneTerminalResponse?) {
        doWhenResumed {
            analytics.logScreenView(
                name = TapOnPhoneAnalytics.ENABLEMENT_EXTENSION_ACTIVATED_PATH,
                className = javaClass
            )
            findNavController().popBackStack()
        }
    }

    override fun onErrorInActivatingTerminal(errorCode: Short?, errorMessage: ErrorMessage?) {
        doWhenResumed {
            analytics.logScreenView(
                name = TapOnPhoneAnalytics.ENABLEMENT_EXTENSION_ERROR_PATH,
                className = javaClass
            )
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
                headerCallback = {
                    navigation?.goToHome()
                },
                secondButtonCallback = {
                    navigation?.goToHome()
                },
                finishCallback = {
                    navigation?.goToHome()
                },
                isBack = true
            )
        }
    }

    override fun onExtensionError() {
        doWhenResumed {
            analytics.logScreenView(
                name = TapOnPhoneAnalytics.ENABLEMENT_EXTENSION_ERROR_PATH,
                className = javaClass
            )
            navigation?.showCustomHandler(
                contentImage = R.drawable.ic_19_maintenance,
                title = getString(R.string.tap_on_phone_initialize_terminal_generic_error_title),
                message = getString(R.string.id_onboarding_validate_p2_generic_error),
                titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                messageAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                labelSecondButton = getString(R.string.entendi),
                headerCallback = {
                    navigation?.goToHome()
                },
                secondButtonCallback = {
                    navigation?.goToHome()
                },
                finishCallback = {
                    navigation?.goToHome()
                }
            )
        }
    }

    override fun onHelpButtonClicked() {
        logScreenAction(Action.HELP)
        requireActivity().openFaq(
            tag = ConfigurationDef.TAG_HELP_CENTER_TAP_ON_PHONE,
            subCategoryName = getString(R.string.tap_on_phone)
        )
    }

    override fun onBackButtonClicked(): Boolean {
        logScreenAction(Action.VOLTAR)
        navigation?.goToHome()
        return super.onBackButtonClicked()
    }
}