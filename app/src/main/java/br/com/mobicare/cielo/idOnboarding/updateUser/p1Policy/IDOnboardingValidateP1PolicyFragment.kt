package br.com.mobicare.cielo.idOnboarding.updateUser.p1Policy

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.bottomsheet.callhelpcenter.CallHelpCenterBottomSheet
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.ONE_MINUTE_MILLIS
import br.com.mobicare.cielo.commons.utils.ONE_SECOND_MILLIS
import br.com.mobicare.cielo.commons.utils.ValidationUtils.isEmail
import br.com.mobicare.cielo.commons.utils.messageError
import br.com.mobicare.cielo.databinding.FragmentIdOnboardingValidateP1PolicyBinding
import br.com.mobicare.cielo.extensions.backToHome
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.extensions.moveToHome
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.canPostponeOnboarding
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.checkpointP1
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.userStatus
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointP1.CELLPHONE_VALIDATION_CONFIRM
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointUserCnpj.P1_VALIDATED
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointUserCnpj.USER_CNPJ_CHECKED
import br.com.mobicare.cielo.main.presentation.ui.activities.MainBottomNavigationActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class IDOnboardingValidateP1PolicyFragment : BaseFragment(),
    CieloNavigationListener, IDOnboardingValidateP1PolicyContract.View {

    private val presenter: IDOnboardingValidateP1PolicyPresenter by inject { parametersOf(this) }
    private var navigation: CieloNavigation? = null
    private lateinit var refreshTimer: CountDownTimer
    private var refreshAgain: Boolean = false
    private var refreshResulted: Boolean = true
    private var binding: FragmentIdOnboardingValidateP1PolicyBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentIdOnboardingValidateP1PolicyBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupText()
        setupNavigation()
        setupListeners()
        startJobs()
    }

    private fun setupText() {
        binding?.tvTitle?.fromHtml(R.string.id_onboarding_validate_p1_loading_title)
        binding?.tvMessage?.fromHtml(R.string.id_onboarding_validate_p1_loading_subtitle)
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupListeners() {
        binding?.btBackArrow?.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun startJobs() {
        doWhenResumed(
            action = {
                when (checkpointP1) {
                    CELLPHONE_VALIDATION_CONFIRM -> presenter.requestP1PolicyValidation()
                    else -> startStatusRefreshTimer()
                }
            },
            errorCallback = { baseLogout() }
        )
    }

    override fun onPause() {
        presenter.onPause()
        super.onPause()
    }

    override fun onResume() {
        presenter.onResume()
        super.onResume()

        refreshResulted = true
    }

    override fun onPolicyP1Requested() {
        startStatusRefreshTimer()
    }

    override fun onPolicyP1StatusArrived(validated: Boolean?, userRole: String?) {
        if (validated == null) {
            if (refreshAgain) {
                refreshStatus()
            } else {
                refreshResulted = true
            }
        }
        else if (validated) {
            binding?.progressBar?.gone()
            if (::refreshTimer.isInitialized) refreshTimer.cancel()

            val username = UserPreferences.getInstance().userName
            if (isEmail(username) && userStatus.p1Flow?.emailValidation?.let { it.email != username } == true) {
                userStatus.p1Flow?.emailValidation?.email?.let { newEmail ->
                    UserPreferences.getInstance().keepUserName(newEmail)
                }
                goToDataUpdatedSuccessfully(userRole)
            } else {
                showP1Success(userRole)
            }
        }
        else {
            binding?.progressBar?.gone()

            if (::refreshTimer.isInitialized) refreshTimer.cancel()

            if (userStatus.onboardingStatus?.userStatus?.foreign == true) {
                goToDataUpdatedSuccessfully(userRole)
            } else {
                showUserBlocked()
            }
        }
    }

    fun refreshStatus() {
        binding?.progressBar?.visible()
        presenter.checkP1PolicyResult()
        refreshAgain = false
        refreshResulted = false
    }

    private fun startStatusRefreshTimer() {
        binding?.progressBar?.visible()
        if (::refreshTimer.isInitialized) refreshTimer.cancel()

        refreshTimer = object : CountDownTimer(TIMEOUT, RETRY_INTERVAL) {
            override fun onTick(p0: Long) {
                if (isAttached()) {
                    if (refreshResulted) {
                        refreshStatus()
                    } else {
                        refreshAgain = true
                    }
                } else {
                    this.cancel()
                }
            }

            override fun onFinish() {
                startStatusRefreshTimer()
            }
        }.start()
    }

    private fun showUserBlocked() {
        doWhenResumed(
            action = {
                navigation?.showCustomBottomSheet(
                    image = R.drawable.img_sempromos_promo_azul,
                    title = getString(R.string.id_onboarding_validate_p1_denied_bs_title),
                    message = getString(R.string.id_onboarding_validate_p1_denied_bs_message),
                    bt1Title = getString(R.string.text_call_center_action),
                    bt1Callback = {
                        CallHelpCenterBottomSheet.newInstance().show(childFragmentManager,tag)
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
                ) ?: baseLogout()
            },
            errorCallback = { baseLogout() }
        )
    }

    private fun showP1Success(userRole: String?) {
        presenter.updateStatusAndCallIfSucceeded(userRole) {
            doWhenResumed(
                action = {
                    when (userStatus.onboardingStatus?.onboardingCheckpointCode) {
                        USER_CNPJ_CHECKED.code, P1_VALIDATED.code -> goToDataUpdatedSuccessfully(
                            userRole
                        )
                        else -> goToHome()
                    }
                },
                errorCallback = { baseLogout() }
            )
        }
    }

    private fun goToDataUpdatedSuccessfully(userRole: String?) {
        findNavController().navigate(
            IDOnboardingValidateP1PolicyFragmentDirections.actionIdOnboardingValidateP1PolicyFragmentToIdOnboardingDataUpdatedSuccessfullyP1PolicyFragment(
                userRole,
                cpfOrEmailWasUpdated()
            )
        )
    }

    private fun cpfOrEmailWasUpdated(): Boolean {
        val p1Flow = userStatus.p1Flow

        val isCpfChanged =  (p1Flow?.cpfValidation?.cpfOld.isNullOrEmpty().not()) &&  (p1Flow?.cpfValidation?.cpf != p1Flow?.cpfValidation?.cpfOld)
        val isEmailChanged =  (p1Flow?.emailValidation?.emailOld.isNullOrEmpty().not()) &&  (p1Flow?.emailValidation?.email != p1Flow?.emailValidation?.emailOld)

        return isCpfChanged || isEmailChanged
    }

    override fun showError(error: ErrorMessage?) {
        presenter.onPause()
        if (::refreshTimer.isInitialized) {
            refreshTimer.cancel()
        }

        doWhenResumed(
            action = {
                val canPostpone = canPostponeOnboarding
                navigation?.showCustomBottomSheet(
                    image = R.drawable.ic_generic_error_image,
                    title = getString(R.string.generic_error_title),
                    message = messageError(error, requireActivity()),
                    bt1Title = if (canPostpone)
                        getString(R.string.do_it_later)
                    else
                        null,
                    bt1Callback = if (canPostpone)
                        { ->
                            goToHome()
                            false
                        }
                    else
                        null,
                    bt2Title = getString(R.string.text_try_again_label),
                    bt2Callback = {
                        onRetry()
                        false
                    },
                    closeCallback = {
                        startJobs()
                    }
                ) ?: baseLogout()
            },
            errorCallback = { baseLogout() }
        )
    }

    override fun showErrorUserNotFound(userRole: String?) = goToDataUpdatedSuccessfully(userRole)

    override fun onRetry() {
        binding?.progressBar?.visible()
        presenter.onResume()
        presenter.retry()
    }

    override fun onPauseActivity() {
        presenter.onPause()
    }

    override fun onRetryBottomSheet() {
        presenter.onResume()
        onRetry()
    }

    private fun goToHome() {
        activity?.moveToHome()
            ?: baseLogout()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    companion object {
        const val RETRY_INTERVAL = ONE_SECOND_MILLIS * 2
        const val TIMEOUT = ONE_MINUTE_MILLIS * 10
    }
}