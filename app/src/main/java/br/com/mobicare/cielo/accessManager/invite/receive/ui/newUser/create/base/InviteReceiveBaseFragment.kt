package br.com.mobicare.cielo.accessManager.invite.receive.ui.newUser.create.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.bottomsheet.callhelpcenter.CallHelpCenterBottomSheet
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.messageError
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.idOnboarding.router.IDOnboardingRouter
import br.com.mobicare.cielo.newLogin.NewLoginActivity

abstract class InviteReceiveBaseFragment : BaseFragment(), CieloNavigationListener,
    InviteReceiveContract.View {

    private var mIDORouter: IDOnboardingRouter? = null

    protected var navigation: CieloNavigation? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
    }

    fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    override fun onUserCreatedSuccess() {
        showUserCreatedSuccessBottomSheet()
    }

    private fun showUserCreatedSuccessBottomSheet() {
        doWhenResumed (
            action = {
                navigation?.showCustomBottomSheet(
                    image = R.drawable.img_conta_criada_sucesso,
                    title = getString(R.string.access_manager_account_created_sucess_title),
                    message = getString(R.string.account_created_sucess_message),
                    bt2Title = getString(R.string.btn_continuar),
                    closeCallback = { goToLogin() },
                    bt2Callback = {
                        goToLogin()
                        false
                    }
                )
            },
            errorCallback = { goToLogin() }
        )
    }

    override fun onPasswordError(error: ErrorMessage) {
        navigation?.showCustomBottomSheet(
            message = messageError(error, requireActivity())
        )
    }

    override fun onInviteExpiredError(error: ErrorMessage) {
        showInviteExpiredErrorBottomSheet()
    }

    override fun onGenericError(error: ErrorMessage) {
        showGenericErrorBottomSheet(error)
    }

    override fun showConnectionError(retryCallback: () -> Unit) {
        navigation?.showCustomBottomSheet(
            image = R.drawable.img_sem_conexao,
            title = getString(R.string.access_manager_no_connection_title),
            message = getString(R.string.access_manager_verify_connection),
            bt2Title = getString(R.string.access_manager_update),
            bt2Callback = {
                retryCallback.invoke()
                false
            })
    }

    override fun onInvalidCpf(error: ErrorMessage) {
        navigation?.showCustomBottomSheet(
            image = R.drawable.img_aguardando_doc,
            title = getString(R.string.id_onboarding_update_cpf_invalid_cpf_bs_new_title),
            message = getString(R.string.id_onboarding_validate_p1_denied_bs_message),
            bt1Title = getString(R.string.text_call_center_action),
            bt1Callback = {
                CallHelpCenterBottomSheet.newInstance().show(childFragmentManager, tag)
                false
            },
            bt2Title = getString(R.string.entendi),
            bt2Callback = {
                goToLogin()
                false
            },
            closeCallback = {
                goToLogin()
            },
            isPhone = false
        )
    }

    override fun onCpfValidateMaxTriesExceeded(error: ErrorMessage) {
        navigation?.showCustomBottomSheet(
            title = getString(R.string.error_title_service_unavailable),
            message = getString(R.string.error_message_cant_proceed_now_try_again),
            bt2Title = getString(R.string.entendi),
            bt2Callback = {
                goToLogin()
                false
            },
            closeCallback = {
                goToLogin()
            }
        )
    }

    private fun showInviteExpiredErrorBottomSheet() {
        showUnrecoverableErrorBottomSheet(
            title = getString(R.string.access_manager_invite_expired_title),
            message = getString(R.string.access_manager_invite_expired_message),
            bt2Title = getString(R.string.access_manager_receive_a_new_invite)
        )
    }

    private fun showGenericErrorBottomSheet(error: ErrorMessage) {
        showUnrecoverableErrorBottomSheet(
            message = messageError(error, requireActivity())
        )
    }

    private fun showUnrecoverableErrorBottomSheet(
        title: String? = null,
        message: String? = null,
        bt2Title: String? = getString(R.string.entendi)
    ) {
        navigation?.showCustomBottomSheet(
            title = title,
            message = message,
            bt2Title = bt2Title,
            bt2Callback = {
                goToLogin()
                false
            },
            closeCallback = { goToLogin() }
        )
    }

    protected fun goToLogin() {
        requireActivity().let {
            it.startActivity(Intent(it, NewLoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            })
            it.finish()
        }
    }

    override fun showLoading() {
        doWhenResumed {
            navigation?.showLoading()
        }

    }

    override fun hideLoading() {
        doWhenResumed {
            navigation?.showContent()
        }
    }

    override fun hideLoading(
        @StringRes successMessage: Int?,
        loadingSuccessCallback: (() -> Unit)?,
        vararg messageArgs: String
    ) {
        doWhenResumed(
            action = {
                navigation?.showContent(true, successMessage, loadingSuccessCallback, *messageArgs)
                    ?: loadingSuccessCallback?.invoke()
            },
            errorCallback = { loadingSuccessCallback?.invoke() }
        )
    }

    protected fun startDigitalID() {
        mIDORouter = IDOnboardingRouter(
            activity = requireActivity(),
            showLoadingCallback = {
                showLoading()
            },
            hideLoadingCallback = {
                hideLoading()
            }
        ).showOnboarding()
    }
}