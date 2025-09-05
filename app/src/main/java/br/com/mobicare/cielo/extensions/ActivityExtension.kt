package br.com.mobicare.cielo.extensions

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import br.com.cielo.libflue.alert.CieloAlertDialogFragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.PACKAGE
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.router.Router
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.getErrorMessage
import br.com.mobicare.cielo.main.domain.Menu
import br.com.mobicare.cielo.main.domain.MenuTarget
import br.com.mobicare.cielo.mfa.analytics.MfaAnalytics
import br.com.mobicare.cielo.mfa.analytics.MfaAnalytics.Companion.ACCESS_OTHER_CELLPHONE
import br.com.mobicare.cielo.mfa.analytics.MfaAnalytics.Companion.CONFIRM_ID
import br.com.mobicare.cielo.mfa.analytics.MfaAnalyticsGA4
import br.com.mobicare.cielo.pix.constants.EMPTY

fun Activity?.isAvailable(): Boolean {
    return this?.let {
        it.isDestroyed.not() && it.isFinishing.not()
    } ?: false
}

inline fun FragmentActivity?.doWhenResumed(
    crossinline action: () -> Unit,
    crossinline errorCallback: () -> Unit = {}
) {
    this?.lifecycleScope?.launchWhenResumed {
        this@doWhenResumed.run {
            if (isAvailable()) {
                action.invoke()
            } else errorCallback.invoke()
        }
    } ?: errorCallback.invoke()
}

inline fun FragmentActivity?.doWhenResumed(crossinline action: () -> Unit) {
    this.doWhenResumed(
        action = action,
        errorCallback = {}
    )
}

fun FragmentActivity?.showBottomSheet(
    image: Int? = null,
    title: String? = null,
    message: String? = null,
    bt1Title: String? = null,
    bt2Title: String? = null,
    bt1Callback: (() -> Boolean)? = null,
    bt2Callback: (() -> Boolean)? = null,
    closeCallback: (() -> Unit)? = null,
    isCancelable: Boolean = true,
    isPhone: Boolean = true,
    titleBlack: Boolean = false
) {
    doWhenResumed {
        if (this != null && this.isAvailable()) {
            bottomSheetGenericFlui(
                image = image ?: R.drawable.ic_generic_error_image,
                title = title ?: getString(R.string.generic_error_title),
                subtitle = message ?: getString(R.string.error_generic),
                nameBtn1Bottom = bt1Title ?: "",
                nameBtn2Bottom = bt2Title ?: getString(R.string.ok),
                txtTitleStyle = if (titleBlack) TxtTitleStyle.TXT_TITLE_DARK_BLACK else TxtTitleStyle.TXT_TITLE_DARK_BLUE,
                txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_FLUI_BOTTOM_SHEET,
                statusBtnFirst = bt1Title != null,
                btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE_TXT_BLUE,
                isCancelable = isCancelable,
                isPhone = isPhone
            ).apply {
                onClick =
                    object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {

                        override fun onBtnFirst(dialog: Dialog) {
                            if (bt1Callback?.invoke() != true) dismiss()
                        }

                        override fun onBtnSecond(dialog: Dialog) {
                            if (bt2Callback?.invoke() != true) dismiss()
                        }

                        override fun onSwipeClosed() {
                            closeCallback?.invoke()
                        }

                        override fun onCancel() {
                            closeCallback?.invoke()
                        }
                    }
            }.show(
                supportFragmentManager,
                getString(R.string.bottom_sheet_generic)
            )
        }
    }
}

fun Activity?.openNFCSettings() {
    this?.startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
}

fun FragmentActivity?.showAlertAllowMe(message: String) {
    this?.let {
        CieloAlertDialogFragment
            .Builder()
            .title(it.getString(R.string.text_title_error_fingerprint_allowme))
            .message(message)
            .closeTextButton(it.getString(R.string.dialog_button))
            .build().showAllowingStateLoss(
                it.supportFragmentManager,
                it.getString(R.string.text_cieloalertdialog)
            )
    }
}

fun FragmentActivity?.errorAllowMe(
    isMandatory: Boolean,
    message: String,
    onMandatoryAction: () -> Unit = {},
    onNotMandatoryAction: () -> Unit
) {
    this?.let {
        if (isMandatory) {
            showAlertAllowMe(message)
            onMandatoryAction.invoke()
        } else
            onNotMandatoryAction.invoke()
    }
}

fun FragmentActivity?.finishP2(
    onFirstAction: () -> Unit,
    onSecondAction: () -> Unit,
    onSwipeAction: () -> Unit,
    error: ErrorMessage? = null,
) {
    this?.let { fragmentActivity ->
        val analytics = MfaAnalytics()
        val labelButtonOne = getString(R.string.mfa_token_finish_p2_first_btn)
        val labelButtonTwo = getString(R.string.mfa_token_finish_p2_second_btn)
        analytics.logMFACallbackError(error?.code, error?.message ?: CONFIRM_ID)
        analytics.logMFAShowBottomSheet(CONFIRM_ID)

        fragmentActivity.bottomSheetGenericFlui(
            image = R.drawable.ic_07,
            title = getString(R.string.mfa_token_finish_p2_title),
            subtitle = getString(R.string.mfa_token_finish_p2_message),
            nameBtn1Bottom = labelButtonOne,
            nameBtn2Bottom = labelButtonTwo,
            statusNameTopBar = false,
            statusBtnClose = false,
            statusView2Line = false,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isPhone = false,
            isFullScreen = true
        ).apply {
            this.onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnFirst(dialog: Dialog) {
                    analytics.logMFAClickBottomSheet(
                        CONFIRM_ID,
                        labelButtonOne
                    )
                    dialog.dismiss()
                    onFirstAction.invoke()
                }

                override fun onBtnSecond(dialog: Dialog) {
                    analytics.logMFAClickBottomSheet(
                        CONFIRM_ID,
                        labelButtonTwo
                    )
                    dialog.dismiss()
                    onSecondAction.invoke()
                }

                override fun onSwipeClosed() {
                    onSwipeAction.invoke()
                }
            }
        }.show(fragmentActivity.supportFragmentManager, getString(R.string.bottom_sheet_generic))
    }
}

fun FragmentActivity?.differentDevice(
    onFirstAction: () -> Unit,
    onSecondAction: () -> Unit,
    onSwipeAction: () -> Unit,
    error: ErrorMessage? = null,
) {
    this?.let { fragmentActivity ->
        val analytics = MfaAnalytics()
        val labelButtonOne = getString(R.string.mfa_token_finish_p2_first_btn)
        val labelButtonTwo = getString(R.string.mfa_token_finish_p2_second_btn)
        analytics.logMFACallbackError(error?.code, error?.message ?: ACCESS_OTHER_CELLPHONE)
        analytics.logMFAShowBottomSheet(ACCESS_OTHER_CELLPHONE)

        fragmentActivity.bottomSheetGenericFlui(
            image = R.drawable.ic_07,
            title = getString(R.string.mfa_token_different_device_title),
            subtitle = getString(R.string.mfa_token_different_device_message),
            nameBtn1Bottom = labelButtonOne,
            nameBtn2Bottom = labelButtonTwo,
            statusNameTopBar = false,
            statusBtnClose = false,
            statusView2Line = false,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isPhone = false,
            isFullScreen = true
        ).apply {
            this.onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnFirst(dialog: Dialog) {
                    analytics.logMFAClickBottomSheet(
                        ACCESS_OTHER_CELLPHONE,
                        labelButtonOne
                    )
                    dialog.dismiss()
                    onFirstAction.invoke()
                }

                override fun onBtnSecond(dialog: Dialog) {
                    analytics.logMFAClickBottomSheet(
                        ACCESS_OTHER_CELLPHONE,
                        labelButtonTwo
                    )
                    dialog.dismiss()
                    onSecondAction.invoke()
                }

                override fun onSwipeClosed() {
                    onSwipeAction.invoke()
                }

                override fun onCancel() {
                    onSwipeAction.invoke()
                }
            }
        }.show(fragmentActivity.supportFragmentManager, getString(R.string.bottom_sheet_generic))
    }
}

fun FragmentActivity?.genericError(
    error: ErrorMessage? = null,
    onFirstAction: () -> Unit,
    onSecondAction: () -> Unit,
    onSwipeAction: () -> Unit,
    isErrorMFA: Boolean = false,
    isShowFirstButton: Boolean = true
) {
    this?.let { fragmentActivity ->
        val message = getErrorMessage(error, getString(R.string.mfa_token_generic_error_message))
        val analytics = MfaAnalytics()
        val title = getString(R.string.commons_generic_error_title)
        val labelButtonOne = getString(R.string.text_try_again_label)
        val labelButtonTwo = getString(R.string.go_to_initial_screen)
        if (isErrorMFA) {
            analytics.logMFACallbackError(error?.code, error?.message)
            analytics.logMFAShowBottomSheet(title)
        }

        fragmentActivity.bottomSheetGenericFlui(
            image = R.drawable.ic_07,
            title = title,
            subtitle = message,
            nameBtn1Bottom = labelButtonOne,
            statusBtnFirst = isShowFirstButton,
            nameBtn2Bottom = labelButtonTwo,
            statusNameTopBar = false,
            statusBtnClose = false,
            statusView2Line = false,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isPhone = false,
            isFullScreen = true
        ).apply {
            this.onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnFirst(dialog: Dialog) {
                    if (isErrorMFA) {
                        analytics.logMFAClickBottomSheet(
                            title,
                            labelButtonOne
                        )
                    }
                    dialog.dismiss()
                    onFirstAction.invoke()
                }

                override fun onBtnSecond(dialog: Dialog) {
                    if (isErrorMFA) {
                        analytics.logMFAClickBottomSheet(
                            title,
                            labelButtonTwo
                        )
                    }
                    dialog.dismiss()
                    onSecondAction.invoke()
                }

                override fun onSwipeClosed() {
                    onSwipeAction.invoke()
                }

                override fun onCancel() {
                    onSwipeAction.invoke()
                }
            }
        }.show(fragmentActivity.supportFragmentManager, getString(R.string.bottom_sheet_generic))
    }
}

fun FragmentActivity?.successConfiguringMfa(
    onAction: () -> Unit
) {
    this?.let { fragmentActivity ->
        val analytics = MfaAnalytics()
        val analyticsGA4 = MfaAnalyticsGA4()
        val title = getString(R.string.mfa_token_success_title)
        val labelButton = getString(R.string.entendi)
        analytics.logMFACallbackSuccess()
        analytics.logMFAShowBottomSheet(title)

        analyticsGA4.logScreenView()
        analyticsGA4.logSignUp()

        fragmentActivity.bottomSheetGenericFlui(
            image = R.drawable.ic_08,
            title = title,
            subtitle = getString(R.string.mfa_token_success_message),
            nameBtn2Bottom = labelButton,
            statusNameTopBar = false,
            statusBtnClose = false,
            statusView2Line = false,
            statusBtnSecond = true,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isPhone = false,
            isFullScreen = true
        ).apply {
            this.onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnSecond(dialog: Dialog) {
                    analytics.logMFAClickBottomSheet(
                        title,
                        labelButton
                    )
                    dialog.dismiss()
                    onAction.invoke()
                }

                override fun onSwipeClosed() {
                    onAction.invoke()
                }

                override fun onCancel() {
                    onAction.invoke()
                }
            }
        }.show(fragmentActivity.supportFragmentManager, getString(R.string.bottom_sheet_generic))
    }
}

fun FragmentActivity?.errorNotBooting(
    onAction: () -> Unit,
    message: String
) {
    this?.let { fragmentActivity ->
        fragmentActivity.bottomSheetGenericFlui(
            image = R.drawable.ic_07,
            title = getString(R.string.commons_generic_error_title),
            subtitle = message,
            nameBtn2Bottom = getString(R.string.text_try_again_label),
            statusNameTopBar = false,
            statusBtnClose = false,
            statusView2Line = false,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_DARK_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_FLUI_BOTTOM_SHEET,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isPhone = false
        ).apply {
            this.onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnSecond(dialog: Dialog) {
                    dialog.dismiss()
                    onAction.invoke()
                }

                override fun onSwipeClosed() {
                    onAction.invoke()
                }

                override fun onCancel() {
                    onAction.invoke()
                }
            }
        }.show(fragmentActivity.supportFragmentManager, getString(R.string.bottom_sheet_generic))
    }
}

fun Activity.goToFlowUsingRouter(
    flowName: String,
    toolbarTitle: String,
    bundleParams: Bundle? = null
) {
    Router.navigateTo(
        this, Menu(
            flowName, EMPTY, listOf(),
            toolbarTitle, false, EMPTY,
            listOf(), show = false, showItems = false, menuTarget = MenuTarget(
                false,
                type = EMPTY, mail = EMPTY, url = EMPTY
            )
        ),
        params = bundleParams
    )
}

fun AppCompatActivity.showApplicationConfiguration() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts(PACKAGE, this.packageName, null)
    intent.data = uri
    startActivity(intent)
}

fun AppCompatActivity.setNavGraphStartDestination(
    @IdRes navHostFragmentId: Int,
    @NavigationRes navGraphId: Int,
    @IdRes startDestinationId: Int,
    args: Bundle? = null
) {
    val navHostFragment = supportFragmentManager.findFragmentById(navHostFragmentId) as NavHostFragment
    val inflater = navHostFragment.navController.navInflater
    inflater.inflate(navGraphId).also {
        it.setStartDestination(startDestinationId)
        navHostFragment.navController.setGraph(it, args ?: intent.extras)
    }
}

inline fun <reified T : AppCompatActivity> AppCompatActivity.goToAndFinishCurrentActivity() {
    startActivity(Intent(this, T::class.java))
    finish()
}