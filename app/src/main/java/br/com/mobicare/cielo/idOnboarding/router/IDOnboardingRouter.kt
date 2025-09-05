package br.com.mobicare.cielo.idOnboarding.router

import android.app.Dialog
import android.content.Context
import androidx.fragment.app.FragmentActivity
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.IDOnboarding.ARG_PARAM_IS_LOGIN_ID
import br.com.mobicare.cielo.commons.constants.IDOnboarding.ARG_PARAM_SHOW_WARNING
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.dateFormatToBr
import br.com.mobicare.cielo.commons.utils.messageError
import br.com.mobicare.cielo.extensions.*
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.showCustomBottomSheet
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.userStatus
import br.com.mobicare.cielo.idOnboarding.IDOnboardingNavigationActivity
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.main.presentation.ui.activities.MainBottomNavigationActivity
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.jetbrains.anko.startActivity
import org.koin.core.parameter.parametersOf
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED

class IDOnboardingRouter(
    var activity: FragmentActivity?,
    val showLoadingCallback: (() -> Unit)? = null,
    val hideLoadingCallback: (() -> Unit)? = null,
    val isShowWarning: Boolean = true,
    val isLogin: Boolean = false
) : IDOnboardingRouterContract.View, KoinComponent {

    private val presenter: IDOnboardingRouterPresenter by inject {
        parametersOf(this)
    }

    private var bottomSheet: BottomSheetFluiGenericFragment? = null

    fun showOnboarding(): IDOnboardingRouter {
        if (presenter.canShowIdOnboarding())
            presenter.getIdOnboardingStatus()
        else
            goToHome()
        return this
    }

    override fun showUpdateUserDataDialog() {
        activity.doWhenResumed {
            activity?.run {
                if (isAvailable()) {
                    bottomSheet?.dismiss()
                    goToIDOnboarding(this)
                }
            }
        }
    }

    override fun showUserWithoutRole() {
        activity?.run {
            if (isAvailable()) {
                val canSkip = userStatus.p1Flow?.deadlineRemainingDays.orZero > 0

                val name = MenuPreference.instance.getLoginObj()?.establishment?.tradeName
                    ?: getString(R.string.Establishment)
                val toDate = userStatus.p1Flow?.deadlineOn.dateFormatToBr()

                showCustomBottomSheet(
                    this,
                    image = if (canSkip)
                        R.drawable.img_interrogacao
                    else
                        R.drawable.img_sempromos_promo,
                    title = if (canSkip)
                        getString(R.string.id_onboarding_p1_no_role_bs_title)
                    else
                        getString(R.string.id_onboarding_p1_no_role_mandatory_bs_title),
                    message = if (canSkip)
                        getString(R.string.id_onboarding_p1_no_role_bs_message, name, toDate)
                    else
                        getString(R.string.id_onboarding_p1_no_role_mandatory_bs_message, name),
                    bt2Title = getString(R.string.entendi),
                    bt2Callback = {
                        if (canSkip) {
                            backToHome()
                        } else {
                            logout(baseContext)
                        }
                        false
                    },
                    closeCallback = {
                        if (canSkip) {
                            backToHome()
                        } else {
                            logout(baseContext)
                        }
                    },
                    isCancelable = false
                )
            }
        }
    }

    override fun showP2PicturesStart() {
        try {
            activity?.startActivity<IDOnboardingNavigationActivity>(
                ARG_PARAM_IS_LOGIN_ID to isLogin
            )
        } catch (exception: Exception) {
            exception.message?.let { error ->
                FirebaseCrashlytics.getInstance().log(error)
            }
        }
    }

    override fun goToHome() {
        activity?.moveToHome()
    }

    override fun showAccessManager() {
        activity?.moveToHome()
    }

    override fun showLoading() {
        showLoadingCallback?.invoke()
    }

    override fun hideLoading() {
        hideLoadingCallback?.invoke()
    }

    override fun showError(error: ErrorMessage?) {
        try {
            activity?.run {
                if (isAvailable()) {
                    bottomSheetGenericFlui(
                        nameTopBar = EMPTY,
                        image = R.drawable.ic_generic_error_image,
                        title = getString(R.string.text_title_generic_error),
                        subtitle = messageError(error, this),
                        nameBtn1Bottom = EMPTY,
                        nameBtn2Bottom = if (error?.httpStatus == HTTP_UNAUTHORIZED)
                            getString(R.string.entendi)
                        else
                            getString(R.string.text_try_again_label),
                        btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
                        isFullScreen = true
                    ).apply {
                        onClick =
                            object :
                                BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                                override fun onBtnSecond(dialog: Dialog) {
                                    if (error?.httpStatus == HTTP_UNAUTHORIZED) {
                                        logout()
                                    } else {
                                        presenter.retry()
                                    }
                                    dismiss()
                                }
                            }
                    }.show(
                        supportFragmentManager,
                        getString(R.string.bottom_sheet_generic)
                    )
                }
            }
        } catch (exception: Exception) {
            exception.message?.let { error ->
                FirebaseCrashlytics.getInstance().log(error)
            }
        }
    }

    private fun logout(context: Context?) {
        context?.let { SessionExpiredHandler.userSessionExpires(it, true) }
    }

    private fun goToIDOnboarding(activity: FragmentActivity) {
        presenter.updateStatusAndThen {
            try {
                activity.startActivity<IDOnboardingNavigationActivity>(
                    ARG_PARAM_SHOW_WARNING to isShowWarning,
                    ARG_PARAM_IS_LOGIN_ID to isLogin
                )
            } catch (exception: Exception) {
                exception.message?.let { error ->
                    FirebaseCrashlytics.getInstance().log(error)
                }
            }
        }
    }

    fun onResume() {
        presenter.onResume()
    }

    fun onPause() {
        presenter.onPause()
    }
}