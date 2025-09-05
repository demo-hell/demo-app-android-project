package br.com.mobicare.cielo.tapOnPhone.presentation.impersonate

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.allowme.presentation.presenter.AllowMePresenter
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.changeEc.ChangeEc
import br.com.mobicare.cielo.changeEc.domain.Impersonate
import br.com.mobicare.cielo.changeEc.domain.Merchant
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.processErrorMessage
import br.com.mobicare.cielo.databinding.FragmentTapOnPhoneImpersonateBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.errorAllowMe
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.tapOnPhone.analytics.TapOnPhoneGA4
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneEligibilityResponse
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

private const val IMPERSONATE_USER_ACTION = "IMPERSONATE_USER_ACTION"

class TapOnPhoneImpersonateFragment : BaseFragment(), CieloNavigationListener,
    TapOnPhoneImpersonateContract.View, AllowMeContract.View {

    private val presenter: TapOnPhoneImpersonatePresenter by inject {
        parametersOf(this)
    }

    private val allowMePresenter: AllowMePresenter by inject {
        parametersOf(this)
    }

    private val ga4: TapOnPhoneGA4 by inject()

    private var navigation: CieloNavigation? = null
    private var binding: FragmentTapOnPhoneImpersonateBinding? = null

    private val args: TapOnPhoneImpersonateFragmentArgs by navArgs()
    private val eligibility: TapOnPhoneEligibilityResponse by lazy {
        args.topeligibilityargs
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = FragmentTapOnPhoneImpersonateBinding.inflate(
        inflater, container, false
    ).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupView()
        logScreenView()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.showBackIcon()
            navigation?.showHelpButton()
            navigation?.showCloseButton()
            navigation?.showContainerButton()
            navigation?.showToolbar(isShow = false)
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupView() {
        binding?.handlerView?.apply {
            message = getString(R.string.tap_on_phone_impersonate_message, eligibility.merchant)
            setButtonContainedClickListener {
                ga4.logClick(
                    screenName = TapOnPhoneGA4.SCREEN_VIEW_TRANSACTIONAL_IMPERSONATE,
                    contentName = TapOnPhoneGA4.CHANGE_TO_CIELO_TAP
                )
                val allowMeContextual = allowMePresenter.init(requireContext())
                allowMePresenter.collect(
                    mAllowMeContextual = allowMeContextual,
                    context = requireActivity(),
                    mandatory = true
                )
            }
            setHeaderClickListener {
                navigation?.goToHome()
            }
        }
    }

    private fun logScreenView() {
        ga4.logScreenView(TapOnPhoneGA4.SCREEN_VIEW_TRANSACTIONAL_IMPERSONATE)
    }

    override fun successCollectToken(result: String) {
        presenter.findECTap(eligibility, result)
    }

    override fun errorCollectToken(result: String?, errorMessage: String, mandatory: Boolean) {
        requireActivity().errorAllowMe(
            isMandatory = mandatory,
            message = errorMessage,
            onNotMandatoryAction = {
                val fingerprint = result ?: EMPTY
                presenter.findECTap(eligibility, fingerprint)
            })
    }

    override fun getSupportFragmentManagerInstance(): FragmentManager {
        return childFragmentManager
    }

    override fun onShowLoading(message: Int?) {
        navigation?.showAnimatedLoading(message)
    }

    override fun onHideLoading() {
        navigation?.hideAnimatedLoading()
    }

    override fun onShowError(error: ErrorMessage?) {
        doWhenResumed(
            action = {
                navigation?.showCustomErrorHandler(
                    title = getString(R.string.tap_on_phone_initialize_terminal_generic_error_title),
                    error = processErrorMessage(
                        error,
                        getString(R.string.error_generic),
                        getString(R.string.tap_on_phone_initialize_terminal_generic_error_message)
                    ),
                    labelSecondButton = getString(R.string.entendi),
                    secondButtonCallback = {
                        requireActivity().finish()
                    },
                    finishCallback = {
                        requireActivity().finish()
                    },
                    isBack = true
                )
            },
            errorCallback = {
                navigation?.goToHome()
            }
        )
    }

    override fun onSuccessImpersonateECTap(impersonate: Impersonate, merchant: Merchant) {
        doWhenResumed(
            action = {
                ChangeEc().createNewLoginConvivencia(
                    impersonate,
                    merchant
                )
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(Intent(IMPERSONATE_USER_ACTION))

                requireActivity().finishAndRemoveTask()
            },
            errorCallback = {
                baseLogout()
            }
        )
    }

    override fun onBackButtonClicked(): Boolean {
        navigation?.goToHome()
        return super.onBackButtonClicked()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}