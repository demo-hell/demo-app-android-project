package br.com.mobicare.cielo.mfa.router

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.enums.EnrollmentType
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGeneric
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.finishP2
import br.com.mobicare.cielo.extensions.genericError
import br.com.mobicare.cielo.extensions.moveToHome
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.extensions.successConfiguringMfa
import br.com.mobicare.cielo.idOnboarding.router.IDOnboardingRouter
import br.com.mobicare.cielo.mfa.activation.PutValueMFAFragment
import br.com.mobicare.cielo.mfa.merchantstatus.FluxoNavegacaoMerchantStatusMfaActivity
import br.com.mobicare.cielo.mfa.router.userWithP2.MfaTokenConfigurationBottomSheet
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy.dialog.BottomSheetGenericFragment
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class MfaRouterFragment : BaseFragment(),
    CieloNavigationListener,
    MfaRouterContract.View {

    val presenter: MfaRouterPresenter by inject {
        parametersOf(this)
    }

    private var cieloNavigation: CieloNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.blank_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureCieloNavigation()
        presenter.load()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    private fun configureCieloNavigation() {
        doWhenResumed {
            if (requireActivity() is CieloNavigation) {
                this.cieloNavigation = requireActivity() as CieloNavigation
                this.cieloNavigation?.setTextToolbar("Token")
                this.cieloNavigation?.showButton(false)
                this.cieloNavigation?.setNavigationListener(this)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    private fun genericErrorMfa(error: ErrorMessage?) {
        requireActivity().genericError(
            error = error,
            onFirstAction = {
                presenter.load()
            },
            onSecondAction = {
                requireActivity().moveToHome()
            },
            onSwipeAction = {
                requireActivity().moveToHome()
            },
            isErrorMFA = true
        )
    }

    private fun showOTPCode() {
        val fromRouteHandler =
            activity?.intent?.extras?.get(MfaRouteHandler.MFA_FROM_ROUTE_HANDLER) == true
        if (fromRouteHandler) {
            MfaRouteHandler.canMfaProceedForAction = true
            requireActivity().setResult(Activity.RESULT_OK)
            requireActivity().finish()
        } else {
            findNavController().safeNavigate(
                MfaRouterFragmentDirections
                    .actionMfaRouterFragmentToOtpRegisterFragment(
                        UserPreferences.getInstance()
                            .userName
                    )
            )
        }
    }

    override fun showLoading(isShow: Boolean) {
        cieloNavigation?.showLoading(isShow)
    }

    override fun showError(error: ErrorMessage) {
        cieloNavigation?.showError(error)
    }

    override fun showTokenGenerator() {
        showOTPCode()
    }

    override fun showOnboarding() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.mfaRouterFragment, true)
            .build()
        findNavController().navigate(
            MfaRouterFragmentDirections.actionMfaRouterFragmentToPrimeiroAcessoMfaFragment(),
            navOptions
        )
        this.cieloNavigation?.showLoading(false)
        this.cieloNavigation?.showContent(true)
    }

    override fun showNotEligible() {
        this.cieloNavigation?.showIneligibleUser()
    }

    override fun showMFAStatusPending() {
        cieloNavigation?.showMFAStatusPending()
    }

    override fun showMFAStatusErrorPennyDrop() {
        this.cieloNavigation?.showMFAStatusErrorPennyDrop()
    }

    override fun showMerchantOnboard(status: String?) {
        this.cieloNavigation?.showLoading(false)
        this.cieloNavigation?.showContent(true)
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.mfaRouterFragment, true)
            .build()
        findNavController().navigate(
            R.id.action_mfaRouterFragment_to_nav_graph_merchant_status_mfa,
            Bundle().apply {
                this.putString(FluxoNavegacaoMerchantStatusMfaActivity.MERCHANT_STATUS_MFA, status)
            },
            navOptions
        )
    }

    override fun callPutValuesValidate() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.mfaRouterFragment, true)
            .build()

        //INFO passando os parâmetros para construir a tela
        val bundleArgs = Bundle()
        bundleArgs.putBoolean(PutValueMFAFragment.IS_ACTIVE_BANK_ENABLED_PARAM, true)

        findNavController().navigate(
            R.id.action_mfaRouterFragment_to_putValueMFAFragment,
            bundleArgs,
            navOptions
        )
        this.cieloNavigation?.showLoading(false)
        this.cieloNavigation?.showContent(true)
    }

    override fun callBlockedForAttempt() {
        bottomSheetGeneric(
            getString(R.string.bottom_sheet_error_third_unavailable_put_value_label),
            R.drawable.ic_07,
            getString(R.string.bottom_sheet_error_third_unavailable_put_value_title),
            getString(R.string.bottom_sheet_error_third_unavailable_put_value_subtitle),
            getString(R.string.ok),
            statusBtnClose = false,
            statusBtnOk = true,
            statusViewLine = true
        ).apply {
            this.onClick = object : BottomSheetGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnClose(dialog: Dialog) {
                    dialog.dismiss()
                }

                override fun onBtnOk(dialog: Dialog) {
                    requireActivity().finish()
                }

                override fun onSwipeClosed() {
                    requireActivity().finish()
                }
            }
        }.show(requireActivity().supportFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    override fun callTokenReconfiguration() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.mfaRouterFragment, true)
            .build()

        findNavController().navigate(
            MfaRouterFragmentDirections
                .actionMfaRouterFragmentToTokenReconfigurationFragment(),
            navOptions
        )
        this.cieloNavigation?.showContent(true)
    }

    override fun onRetry() {
        presenter.load()
    }

    override fun bottomSheetConfiguringMfaDismiss() {
        requireActivity().onBackPressed()
    }

    override fun showDifferentDevice() {
        MfaTokenConfigurationBottomSheet.onCreate(
            listener = this, isResend = true
        ).show(childFragmentManager, tag)
    }

    override fun showUserWithP2(type: EnrollmentType) {
        MfaTokenConfigurationBottomSheet.onCreate(
            listener = this, type = type.name, isResend = false
        ).show(childFragmentManager, tag)
    }

    override fun onErrorResendPennyDrop(error: ErrorMessage?) {
        requireActivity().genericError(
            error = error,
            onFirstAction = {
                presenter.resendPennyDrop()
            },
            onSecondAction = {
                requireActivity().moveToHome()
            },
            onSwipeAction = {
                requireActivity().moveToHome()
            },
            isErrorMFA = true
        )
    }

    override fun onShowSuccessConfiguringMfa(isShowMessage: Boolean) {
        if (isShowMessage)
            activity.successConfiguringMfa {
                showOTPCode()
            }
        else
            showOTPCode()
    }

    override fun onErrorConfiguringMfa(error: ErrorMessage?) {
        genericErrorMfa(error)
    }

    override fun showUserNeedToFinishP2(error: ErrorMessage?) {
        requireActivity().finishP2(
            onFirstAction = {
                requireActivity().moveToHome()
            },
            onSecondAction = {
                showOnboardingID()
            },
            onSwipeAction = {
                requireActivity().moveToHome()
            },
            error
        )
    }

    private fun showOnboardingID() {
        IDOnboardingRouter(
            activity = requireActivity(),
            showLoadingCallback = {},
            hideLoadingCallback = {}
        ).showOnboarding()
    }
}