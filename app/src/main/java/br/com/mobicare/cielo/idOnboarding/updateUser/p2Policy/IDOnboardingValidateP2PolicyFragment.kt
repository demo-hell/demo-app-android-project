package br.com.mobicare.cielo.idOnboarding.updateUser.p2Policy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import br.com.allowme.android.contextual.AllowMeContextual
import br.com.cielo.libflue.alert.CieloAlertDialogFragment
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.allowme.presentation.presenter.AllowMePresenter
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.commons.bottomsheet.callhelpcenter.CallHelpCenterBottomSheet
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentIdOnboardingValidateP2PolicyBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.extensions.genericError
import br.com.mobicare.cielo.extensions.moveToHome
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP2Analytics
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP2AnalyticsGA
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_MODAL_ERROR
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_MODAL_SUCCESS
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_DOCUMENTS_SENT_SUCCESS
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_VALIDATE_P2
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointP2.ALLOWME_SENT
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointP2.POLICY_2_REQUESTED
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointP2.POLICY_2_RESPONSE
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointP2.SELF_PHOTO_UPLOADED
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class IDOnboardingValidateP2PolicyFragment : BaseFragment(), CieloNavigationListener,
    IDOnboardingValidateP2PolicyContract.View, AllowMeContract.View {

    private val presenter: IDOnboardingValidateP2PolicyPresenter by inject { parametersOf(this) }
    private val analytics: IDOnboardingP2Analytics by inject()
    private val analyticsGA: IDOnboardingP2AnalyticsGA by inject()

    private var navigation: CieloNavigation? = null
    private var binding: FragmentIdOnboardingValidateP2PolicyBinding? = null
            private val allowMePresenter: AllowMePresenter by inject {
        parametersOf(this)
    }
    private val useSecurityHash: Boolean? by lazy {
        FeatureTogglePreference.instance.getFeatureTogle(
            FeatureTogglePreference.SECURITY_HASH
        )
    }
    private lateinit var mAllowMeContextual: AllowMeContextual

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIdOnboardingValidateP2PolicyBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAllowMeContextual = allowMePresenter.init(requireContext())
        setupText()
        setupNavigation()
        setupListeners()
        analytics.logIDScreenView(ANALYTICS_ID_SCREEN_VIEW_VALIDATE_P2, this.javaClass)
        analyticsGA.logIDScreenView(ANALYTICS_ID_SCREEN_VIEW_DOCUMENTS_SENT_SUCCESS)
    }

    private fun setupText() {
        binding?.tvTitle?.fromHtml(R.string.id_onboarding_validate_p2_loading_title)
        binding?.tvMessage?.fromHtml(R.string.id_onboarding_validate_p2_loading_subtitle)
    }

    private fun setupListeners() {
        binding?.btBackArrow?.setOnClickListener {
            activity?.onBackPressed()
                ?: baseLogout()
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    override fun onPause() {
        presenter.onPause()
        super.onPause()
    }

    override fun onResume() {
        presenter.onResume()
        super.onResume()

        when (IDOnboardingFlowHandler.checkpointP2) {
            SELF_PHOTO_UPLOADED -> {
                useSecurityHash?.let { useSecurityHash ->
                    if (useSecurityHash) {
                        activity?.let {
                            allowMePresenter.collect(
                                mAllowMeContextual = mAllowMeContextual,
                                it,
                                mandatory = false
                            )
                        } ?: baseLogout()
                    } else {
                        presenter.sendAllowme("")
                    }
                }
            }
            ALLOWME_SENT, POLICY_2_REQUESTED -> presenter.validateP2Policy()
            POLICY_2_RESPONSE -> {
                if (IDOnboardingFlowHandler.userStatus.onboardingStatus?.userStatus?.foreign == true) {
                    goForeignSuccess()
                } else if (IDOnboardingFlowHandler.userStatus.p2Flow?.p2Validation?.validated == true) {
                    goToHome()
                } else {
                    showUserBlocked()
                }
            }
            else -> baseLogout()
        }

    }

    override fun onAllowMeDone() {
        doWhenResumed(
            action = {
                presenter.validateP2Policy()
            },
            errorCallback = { baseLogout() }
        )
    }

    override fun showP2Success() {
        analytics.logIDOnSuccessValidateP2()
        analytics.logIDModal(ANALYTICS_ID_MODAL_SUCCESS)
        analytics.logAppsFlyerP2Success(requireContext())

        doWhenResumed(
            action = {
                if (IDOnboardingFlowHandler.userStatus.onboardingStatus?.userStatus?.foreign == true){
                    analyticsGA.logIDValidateP2PolicySignUp()
                    findNavController().navigate(
                        IDOnboardingValidateP2PolicyFragmentDirections
                            .actionIdOnboardingValidateP2PolicyFragmentToIdOnboardingP2ForeignSuccessFragment()
                    )
                } else {
                    analyticsGA.logIDValidateP2PolicyDisplay()

                        navigation?.showCustomBottomSheet(
                        image = R.drawable.img_aguarde,
                        title = getString(R.string.id_onboarding_validate_p2_data_sent_title),
                        message = getString(R.string.id_onboarding_validate_p2_data_sent_message),
                        bt2Title = getString(R.string.entendi),
                        bt2Callback = {
                            baseLogout()
                            false
                        },
                        closeCallback = {
                            baseLogout()
                        }
                    )
                }
            },
            errorCallback = { baseLogout() }
        )
    }

    override fun showError(error: ErrorMessage?, retryCallback: (() -> Unit)?) {
        analytics.logIDModal(ANALYTICS_ID_MODAL_ERROR)
        analytics.logIDOnErrorValidateP2(error?.code, error?.message)
        analyticsGA.logIDValidateP2PolicyExcepiton(error?.code ?: EMPTY)
        doWhenResumed(
            action = {
                navigation?.showCustomBottomSheet(
                    image = R.drawable.ic_generic_error_image,
                    title = getString(R.string.error_title_something_wrong),
                    message = HtmlCompat.fromHtml(
                        getString(R.string.id_onboarding_validate_p2_generic_error),
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    ).toString(),
                    bt2Title = getString(R.string.entendi),
                    bt2Callback = {
                        baseLogout()
                        false
                    },
                    closeCallback = {
                        baseLogout()
                    }
                ) ?: baseLogout()
            },
            errorCallback = { baseLogout() }
        )
    }

    override fun onErrorRefreshToken() {
        requireActivity().genericError(
            onFirstAction = {
                presenter.retry()
            },
            onSecondAction = {
                goToHome()
            },
            onSwipeAction = {
                goToHome()
            }
        )
    }

    private fun showUserBlocked() {
        if (isAttached()) {
            navigation?.showCustomBottomSheet(
                image = R.drawable.img_aguardando_doc,
                title = getString(R.string.id_onboarding_update_cpf_invalid_cpf_bs_title),
                message = getString(R.string.id_onboarding_update_cpf_invalid_cpf_bs_message),
                bt1Title = getString(R.string.text_call_center_action),
                bt1Callback = {
                    CallHelpCenterBottomSheet.newInstance().show(childFragmentManager, tag)
                    false
                },
                bt2Title = getString(R.string.entendi),
                bt2Callback = {
                    baseLogout()
                    false
                },
                closeCallback = {
                    baseLogout()
                },
                isPhone = false
            )
        }
    }

    private fun goToHome() {
        activity?.moveToHome()
            ?: baseLogout()
    }

    private fun goForeignSuccess() {
        findNavController().navigate(
            IDOnboardingValidateP2PolicyFragmentDirections
                .actionIdOnboardingValidateP2PolicyFragmentToIdOnboardingP2ForeignSuccessFragment()
        )
    }

    override fun showLoading() {
        doWhenResumed {
            binding?.progressBar?.visible()
        }
    }

    override fun hideLoading() {
        doWhenResumed {
            binding?.progressBar?.gone()
        }
    }

    override fun successCollectToken(result: String) {
        presenter.sendAllowme(result)
    }

    override fun errorCollectToken(result: String?, errorMessage: String, mandatory: Boolean) {
        if (mandatory) {
            showAlert(getString(R.string.text_title_error_fingerprint_allowme), errorMessage)
        } else {
            showError(
                ErrorMessage().apply {
                    this.errorMessage = errorMessage
                }
            )
        }
    }

    override fun getSupportFragmentManagerInstance(): FragmentManager {
        return childFragmentManager
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun showAlert(title: String, message: String) {
        CieloAlertDialogFragment
            .Builder()
            .title(title)
            .message(message)
            .closeTextButton(getString(R.string.ok))
            .build().showAllowingStateLoss(
                childFragmentManager,
                getString(R.string.text_cieloalertdialog)
            )
    }
}