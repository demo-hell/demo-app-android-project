package br.com.mobicare.cielo.pixMVVM.presentation.router.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.token.presentation.HandlerValidationToken
import br.com.mobicare.cielo.databinding.FragmentPixRouterBinding
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.constants.PIX_PARTNER_USAGE_TERMS_URL
import br.com.mobicare.cielo.pixMVVM.domain.model.OnBoardingFulfillment
import br.com.mobicare.cielo.pixMVVM.presentation.extract.PixNewExtractNavigationFlowActivity
import br.com.mobicare.cielo.pixMVVM.presentation.home.PixHomeNavigationFlowActivity
import br.com.mobicare.cielo.pixMVVM.presentation.router.utils.PixRouterUiState
import br.com.mobicare.cielo.pixMVVM.presentation.router.viewmodel.PixRouterViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.status.PixAuthorizationStatusActivity
import org.jetbrains.anko.browse
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PixRouterFragment : BaseFragment(), CieloNavigationListener {

    private val viewModel: PixRouterViewModel by viewModel()

    private val handlerValidationToken: HandlerValidationToken by inject()

    private var binding: FragmentPixRouterBinding? = null
    private var navigation: CieloNavigation? = null

    private val showDataQuery get() = navigation?.getData() as? Boolean ?: false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentPixRouterBinding.inflate(inflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupObserver()
        setShowDataQuery()
        getOnBoardingFulfillment()
    }

    override fun onClickSecondButtonError() = navigateToHome()

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = (requireActivity() as CieloNavigation).apply {
                setNavigationListener(this@PixRouterFragment)
                showContainerButton()
                showButton()
                showHelpButton()
                setTextToolbar(
                    getString(R.string.text_toolbar_home_pix)
                )
            }
        }
    }

    private fun setupObserver() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PixRouterUiState.Loading -> handleLoading()
                is PixRouterUiState.Error -> handleError(state)
                is PixRouterUiState.Success -> handleSuccess(state)
            }
        }
    }

    private fun setShowDataQuery() {
        viewModel.setShowDataQuery(showDataQuery)
    }

    private fun getOnBoardingFulfillment() {
        viewModel.getOnBoardingFulfillment()
    }

    private fun handleLoading() {
        navigation?.showAnimatedLoading()
    }

    private fun handleError(state: PixRouterUiState.Error) {
        showContent()
        when (state) {
            is PixRouterUiState.Unavailable -> showUnavailabilityMessage()
            is PixRouterUiState.MfaEligibilityError -> showError(state.error)
            else -> showErrorMessage()
        }
    }

    private fun showUnavailabilityMessage() {
        navigation?.showCustomHandler(
            contentImage = R.drawable.ic_19_maintenance,
            title = getString(R.string.text_unavailability_message_title),
            message = getString(R.string.pix_unavailability_message),
            titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
            labelSecondButton = getString(R.string.go_to_initial_screen),
            isShowHeaderImage = false,
            secondButtonCallback = ::navigateToHome,
            finishCallback = ::navigateToHome
        )
    }

    private fun showErrorMessage() {
        navigation?.showCustomHandler(
            title = getString(R.string.commons_generic_error_title),
            message = getString(R.string.commons_generic_error_message),
            titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
            labelSecondButton = getString(R.string.text_try_again_label),
            isShowHeaderImage = true,
            secondButtonCallback = ::getOnBoardingFulfillment,
            headerCallback = ::navigateToHome,
            finishCallback = ::navigateToHome
        )
    }

    private fun showError(error: ErrorMessage?) {
        navigation?.showError(error)
    }

    private fun showContent(show: Boolean = true) {
        navigation?.showContent(show)
    }

    private fun handleSuccess(state: PixRouterUiState.Success) {
        when (state) {
            is PixRouterUiState.NotEligible -> showNotEligibleMessage()
            is PixRouterUiState.AccreditationRequired -> navigateToPixAccreditation()
            is PixRouterUiState.ShowAuthorizationStatus -> navigateToPixAuthorizationStatus()
            is PixRouterUiState.BlockPennyDrop -> navigateToPixBlockPennyDrop()
            is PixRouterUiState.EnablePixPartner -> showEnabledPixPartnerMessage()
            is PixRouterUiState.ShowPixExtract -> navigateToPixExtract(state)
            is PixRouterUiState.TokenConfigurationRequired -> configureToken(state)
            is PixRouterUiState.OnBoardingRequired -> navigateToPixHomeOnBoarding(state.pixAccount)
            is PixRouterUiState.ShowPixHome -> navigateToPixHome(state.pixAccount)
        }
    }

    private fun navigateToPixAccreditation() {
        showContent()
        findNavController().navigate(
            PixRouterFragmentDirections.actionPixRouterFragmentToPixTermOnboardingFragment(false)
        )
    }

    private fun navigateToPixBlockPennyDrop() {
        showContent()
        findNavController().navigate(
            PixRouterFragmentDirections.actionPixRouterFragmentToPixBlockPennyDropFragment()
        )
    }

    private fun navigateToPixAuthorizationStatus() {
        requireActivity().run {
            finish()
            startActivity<PixAuthorizationStatusActivity>()
        }
    }

    private fun navigateToPixExtract(state: PixRouterUiState.ShowPixExtract) {
        requireActivity().run {
            finish()
            startActivity<PixNewExtractNavigationFlowActivity>(
                PixNewExtractNavigationFlowActivity.NavArgs.PROFILE_TYPE to state.profileType,
                PixNewExtractNavigationFlowActivity.NavArgs.PIX_ACCOUNT to state.pixAccount,
                PixNewExtractNavigationFlowActivity.NavArgs.SETTLEMENT_SCHEDULED to state.settlementScheduled,
            )
        }
    }

    private fun navigateToPixHome(pixAccount: OnBoardingFulfillment.PixAccount?) {
        requireActivity().startActivity<PixHomeNavigationFlowActivity>(
            PixHomeNavigationFlowActivity.NavArgs.IS_PIX_HOME_ONBOARDING_VIEWED to true,
            PixHomeNavigationFlowActivity.NavArgs.PIX_ACCOUNT to pixAccount
        )
    }

    private fun navigateToPixHomeOnBoarding(pixAccount: OnBoardingFulfillment.PixAccount?) {
        requireActivity().startActivity<PixHomeNavigationFlowActivity>(
            PixHomeNavigationFlowActivity.NavArgs.IS_PIX_HOME_ONBOARDING_VIEWED to false,
            PixHomeNavigationFlowActivity.NavArgs.PIX_ACCOUNT to pixAccount
        )
    }

    private fun configureToken(state: PixRouterUiState.TokenConfigurationRequired) {
        showContent()
        handlerValidationToken.getToken(
            childFragmentManager,
            object : HandlerValidationToken.CallbackToken {
                override fun onSuccess(token: String) = onTokenSuccess(state)
                override fun onError() = onTokenError(state)
            }
        )
    }

    private fun onTokenSuccess(state: PixRouterUiState.TokenConfigurationRequired) {
        handlerValidationToken.hideAnimation(
            isDelay = false,
            callbackStopAnimation = object : HandlerValidationToken.CallbackStopAnimation {
                override fun onStop() {
                    if (state.isOnBoardingViewed) {
                        navigateToPixHome(state.pixAccount)
                    } else {
                        navigateToPixHomeOnBoarding(state.pixAccount)
                    }
                }
            }
        )
    }

    private fun onTokenError(state: PixRouterUiState.TokenConfigurationRequired) {
        handlerValidationToken.playAnimationError(
            callbackAnimationError = object : HandlerValidationToken.CallbackAnimationError {
                override fun onTryAgain() = configureToken(state)
                override fun onBack() = navigateToHome()
                override fun onClose() = navigateToHome()
            }
        )
    }

    private fun showNotEligibleMessage() {
        navigation?.run {
            showContent(true)
            showCustomHandler(
                contentImage = R.drawable.ic_29,
                title = getString(R.string.pix_not_eligible_title),
                message = getString(R.string.pix_not_eligible_subtitle),
                titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                labelSecondButton = getString(R.string.go_to_initial_screen),
                isShowHeaderImage = false,
                secondButtonCallback = ::navigateToHome,
                finishCallback = ::navigateToHome
            )
        }
    }

    private fun showEnabledPixPartnerMessage() {
        showContent()
        bottomSheetGenericFlui(
            nameTopBar = EMPTY,
            R.drawable.ic_11,
            getString(R.string.text_info_pix_partner_title),
            getString(R.string.text_info_pix_partner_sub_title),
            getString(R.string.text_info_pix_partner_contract_authorization),
            getString(R.string.text_close),
            statusNameTopBar = false,
            statusBtnClose = false,
            statusView2Line = false,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE_TXT_BLUE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isFullScreen = true
        ).apply {
            onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnFirst(dialog: Dialog) {
                    requireActivity().browse(PIX_PARTNER_USAGE_TERMS_URL)
                }

                override fun onBtnSecond(dialog: Dialog) {
                    dialog.dismiss()
                    navigateToHome()
                }

                override fun onSwipeClosed() {
                    requireActivity().finish()
                }
            }
        }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    private fun navigateToHome() {
        navigation?.goToHome()
    }

}