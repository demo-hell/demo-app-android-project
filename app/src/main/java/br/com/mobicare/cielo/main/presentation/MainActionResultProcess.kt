package br.com.mobicare.cielo.main.presentation

import android.app.Dialog
import android.content.Context
import android.util.DisplayMetrics
import androidx.fragment.app.FragmentActivity
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.AlertDialogCustom

const val REQUEST_CODE_COIL = 3
const val REQUEST_CODE_STICKER = 4
const val REQUEST_CODE_TRANSFER_ACCOUNT_ADD = 7
const val REQUEST_CODE_MEU_CADASTRO_NOVO = 7


var isSessionTimeoutAlertCreated = false

interface OnLogoutProceedCallback {
    fun logout()
}

var dialogBuilded: Dialog? = null

fun Context.createSessionTimeoutDialog(onLogoutProceedCallback:
                                       OnLogoutProceedCallback) {

    if (!isSessionTimeoutAlertCreated) {
        isSessionTimeoutAlertCreated = true

        dialogBuilded = AlertDialogCustom.Builder(this, "")
                .setMessage("Sua sessão expirou.")
                .setBtnRight(getString(R.string.ok))
                .setCancelable(false)
                .setOnclickListenerRight {
                    isSessionTimeoutAlertCreated = false
                    onLogoutProceedCallback.logout()
                    dialogBuilded?.run {
                        dismiss()
                    }
                }
                .show()
    }
}


fun FragmentActivity.getScreenDimension(): Array<String?> {
    val dm = DisplayMetrics()
    this.windowManager.defaultDisplay.getMetrics(dm)
    val width = dm.widthPixels
    val height = dm.heightPixels
    val dens = dm.densityDpi
    val wi = width.toDouble() / dens.toDouble()
    val hi = height.toDouble() / dens.toDouble()
    val x = Math.pow(wi, 2.0)
    val y = Math.pow(hi, 2.0)
    val screenInches = Math.sqrt(x + y)

    val screenInformation = arrayOfNulls<String>(3)
    screenInformation[0] = "${width}"
    screenInformation[1] = "${height}"
    screenInformation[2] = String.format("%.2f", screenInches) + " inches"

    return screenInformation
}