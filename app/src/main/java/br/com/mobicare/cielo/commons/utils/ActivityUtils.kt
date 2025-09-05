package br.com.mobicare.cielo.commons.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy.dialog.BottomSheetGenericFragment
import com.google.android.material.textfield.TextInputLayout

const val ldpi = 0.75f
const val mdpi = 1.0f
const val hdpi = 1.5f

//const val xhdpi = 2.0f
//const val xxhdpi = 3.0f
//const val xxxhdpi = 3.0f
fun Activity.hideSoftKeyboard(delayAfterClosing: Long? = null) {
    this.currentFocus?.let {
        val inputManager =
            this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(it.windowToken, ZERO)
    }
    delayAfterClosing?.let { Thread.sleep(it) }
}

fun Activity.showSoftKeyboard(view: View?) {
    view?.let {
        val inputManager =
            this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
        view.requestFocusFromTouch()
    }
}

fun Activity.showKeyboard(view: View?) {
    view?.requestFocus()
    val imm =
        this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(
        InputMethodManager.SHOW_IMPLICIT,
        InputMethodManager.HIDE_IMPLICIT_ONLY
    )
}

fun Context.isNetworkAvailable(): Boolean {
    val connectionManager = this.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager
    return connectionManager.activeNetworkInfo != null &&
            connectionManager.activeNetworkInfo!!.isAvailable &&
            connectionManager.activeNetworkInfo!!.isConnected
}

fun Context.density(): Float {
    return this.resources.displayMetrics.density
}

fun Context.typeDensity(): String {
    val density = density()
    return when {
        density <= ldpi -> "PNG_SMALL"
        density <= hdpi -> "PNG_MEDIUM"
        else -> "PNG_LARGE"
    }
}

fun FragmentActivity.createBiometricPrompt(
    title: String,
    subtitle: String? = null,
    description: String,
    biometricPromptAuthCallback: BiometricPrompt.AuthenticationCallback
): BiometricPrompt {
    val biometricPrompt = BiometricPrompt(
        this, MainThreadExecutor(),
        biometricPromptAuthCallback
    )
    val biometricPromptBuilder = BiometricPrompt.PromptInfo.Builder()
    subtitle?.run {
        biometricPromptBuilder.setSubtitle(this)
    }
    val biometricPromptInfo = biometricPromptBuilder.setDescription(description)
        .setTitle(title)
        .setNegativeButtonText(getString(R.string.text_cancel_label))
        .build()
    biometricPrompt.authenticate(biometricPromptInfo)
    return biometricPrompt
}

fun FragmentActivity.anyErrorFields(vararg fields: TextInputLayout): Boolean {
    return fields.any { !TextUtils.isEmpty(it.error) }
}

fun Activity.getScreenHeight() = this.resources.displayMetrics.heightPixels

fun AppCompatActivity.addWithAppearFromBottomAnimation(fragment: Fragment) {
    supportFragmentManager?.run {
        val addTransaction = this.beginTransaction()
        addTransaction.setCustomAnimations(
            R.anim.slide_from_bottom_to_up,
            R.anim.slide_from_up_to_bottom
        )
        addTransaction.add(
            fragment,
            fragment::class.java.simpleName
        )
        addTransaction.commitAllowingStateLoss()
        executePendingTransactions()
    }
}



fun AppCompatActivity.bottomSheetGeneric(
    nameTopBar: String,
    image: Int,
    title: String,
    subtitle: String,
    nameBtnBottom: String,
    statusBtnClose: Boolean = true,
    statusBtnOk: Boolean = true,
    statusViewLine: Boolean = true,
    txtToolbarNameStyle: TextToolbaNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
    txtTitleStyle: TxtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
    txtSubtitleStyle: TxtSubTitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
    btnBottomStyle: ButtonBottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
    isResizeToolbar: Boolean = false
) = BottomSheetGenericFragment.newInstance(
    nameTopBar,
    image,
    title,
    subtitle,
    nameBtnBottom,
    statusBtnClose,
    statusBtnOk,
    statusViewLine,
    txtToolbarNameStyle,
    txtTitleStyle,
    txtSubtitleStyle,
    btnBottomStyle,
    isResizeToolbar
)

fun AppCompatActivity.bottomSheetGenericFlui(
    nameTopBar: String = "",
    image: Int,
    title: String,
    subtitle: String,
    nameBtn1Bottom: String = "",
    nameBtn2Bottom: String,
    statusNameTopBar: Boolean = true,
    statusTitle: Boolean = true,
    statusSubTitle: Boolean = true,
    statusImage: Boolean = true,
    statusBtnClose: Boolean = true,
    statusBtnFirst: Boolean = true,
    statusBtnSecond: Boolean = true,
    statusView1Line: Boolean = true,
    statusView2Line: Boolean = true,
    txtToolbarNameStyle: TextToolbaNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
    txtTitleStyle: TxtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
    txtSubtitleStyle: TxtSubTitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
    btn1BottomStyle: ButtonBottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
    btn2BottomStyle: ButtonBottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE_TXT_BLUE,
    isFullScreen: Boolean = false,
    isCancelable: Boolean = true,
    isPhone: Boolean = true
) = BottomSheetFluiGenericFragment.newInstance(
    nameTopBar,
    image,
    title,
    subtitle,
    nameBtn1Bottom,
    nameBtn2Bottom,
    statusNameTopBar,
    statusTitle,
    statusSubTitle,
    statusImage,
    statusBtnClose,
    statusBtnFirst,
    statusBtnSecond,
    statusView1Line,
    statusView2Line,
    txtToolbarNameStyle,
    txtTitleStyle,
    txtSubtitleStyle,
    btn1BottomStyle,
    btn2BottomStyle,
    isFullScreen,
    isCancelable,
    isPhone
)

fun Activity.bottomSheetGenericFlui(
    nameTopBar: String = "",
    image: Int,
    title: String,
    subtitle: String,
    nameBtn1Bottom: String = "",
    nameBtn2Bottom: String,
    statusNameTopBar: Boolean = true,
    statusTitle: Boolean = true,
    statusSubTitle: Boolean = true,
    statusImage: Boolean = true,
    statusBtnClose: Boolean = true,
    statusBtnFirst: Boolean = true,
    statusBtnSecond: Boolean = true,
    statusView1Line: Boolean = true,
    statusView2Line: Boolean = true,
    txtToolbarNameStyle: TextToolbaNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
    txtTitleStyle: TxtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
    txtSubtitleStyle: TxtSubTitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
    btn1BottomStyle: ButtonBottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
    btn2BottomStyle: ButtonBottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE_TXT_BLUE,
    isFullScreen: Boolean = false,
    isCancelable: Boolean = true,
    isPhone: Boolean = true
) = BottomSheetFluiGenericFragment.newInstance(
    nameTopBar,
    image,
    title,
    subtitle,
    nameBtn1Bottom,
    nameBtn2Bottom,
    statusNameTopBar,
    statusTitle,
    statusSubTitle,
    statusImage,
    statusBtnClose,
    statusBtnFirst,
    statusBtnSecond,
    statusView1Line,
    statusView2Line,
    txtToolbarNameStyle,
    txtTitleStyle,
    txtSubtitleStyle,
    btn1BottomStyle,
    btn2BottomStyle,
    isFullScreen,
    isCancelable,
    isPhone
)