package br.com.mobicare.cielo.commons.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.Store.STORE_MARKET_URI_PREFIX
import br.com.mobicare.cielo.commons.constants.WhatsApp.PLEASE_INSTALL_APP_AGAIN
import br.com.mobicare.cielo.commons.constants.WhatsApp.WHATSAPP_NOT_INSTALLED
import br.com.mobicare.cielo.commons.constants.WhatsApp.WHATSAPP_PACKAGE_NAME
import br.com.mobicare.cielo.commons.helpers.AppHelper.StoreType.STORE_APP
import br.com.mobicare.cielo.commons.helpers.AppHelper.StoreType.STORE_WEB
import br.com.mobicare.cielo.commons.utils.showMessage

const val CIELO_PAY = "br.com.cielo.hyaku"

class AppHelper {
    companion object {
        var isCieloPay: Boolean = false

        var storeAppIntent: Intent? = null
            get() {
                if (field == null) {
                    field = getIntentForStore(STORE_APP)
                }
                return field
            }

        var storeWebIntent: Intent? = null
            get() {
                if (field == null) {
                    field = getIntentForStore(STORE_WEB)
                }
                return field
            }

        private fun getIntentForStore(type: StoreType): Intent? {
            CieloApplication.context?.let { context ->
                val appPackageName =
                    if (isCieloPay) CIELO_PAY else context.packageName.replace(".homolog", "")
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        when (type) {
                            STORE_APP -> "$STORE_MARKET_URI_PREFIX$appPackageName"
                            STORE_WEB -> "https://play.google.com/store/apps/details?id=$appPackageName"
                        }
                    )
                )

                return if (canStartIntent(context, intent)) {
                    intent
                } else {
                    Intent("")
                }
            } ?: return null
        }

        private fun canStartIntent(context: Context, intent: Intent) =
            context.packageManager.resolveActivity(
                intent,
                PackageManager.GET_RESOLVED_FILTER
            ) != null

        private fun isValidIntent(intent: Intent?): Boolean = intent?.action?.isNotEmpty() ?: false

        fun canRedirectToGooglePlay(): Boolean {
            val canRedirect = isValidIntent(storeAppIntent) || isValidIntent(storeWebIntent)

            if (canRedirect.not()) {
                storeAppIntent = null
                storeWebIntent = null
            }

            return canRedirect
        }

        fun redirectToGooglePlay(context: Context, isCieloPay: Boolean = false) {
            this.isCieloPay = isCieloPay

            handleActivityException(
                action = {
                    if (isValidIntent(storeAppIntent)) {
                        context.startActivity(storeAppIntent)
                    } else {
                        if (isValidIntent(storeWebIntent)) {
                            context.startActivity(storeWebIntent)
                        } else {
                            Toast.makeText(
                                context,
                                R.string.error_open_playstore,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    storeAppIntent = null
                    storeWebIntent = null
                },
                fallback = {
                    Toast.makeText(context, R.string.error_open_playstore, Toast.LENGTH_SHORT)
                        .show()
                }
            )
        }

        private fun isAppInstalled(context: Context, packageName: String): Boolean {
            return context.packageManager.getLaunchIntentForPackage(packageName) != null
        }

        private fun verificationZapInDevice(activity: Activity): Boolean {
            if (isAppInstalled(activity, WHATSAPP_PACKAGE_NAME).not()) {
                activity.showMessage(
                    message = PLEASE_INSTALL_APP_AGAIN,
                    title = WHATSAPP_NOT_INSTALLED
                )
                return false
            }
            return true
        }

        fun showWhatsAppMessage(activity: Activity, phoneNumber: String, message: String) {
            if (verificationZapInDevice(activity)) {
                activity.showMessage("Cielo deseja abrir \"WhatsApp\"") {
                    this.setBtnLeft("Cancelar")
                    this.setBtnRight("OK")
                    this.setOnclickListenerRight {
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=$message")
                        ).let {
                            activity.startActivity(it)
                        }
                    }
                }
            }
        }

        private fun handleActivityException(action: () -> Unit, fallback: () -> Unit) {
            try {
                action()
            } catch (e: Exception) {
                e.printStackTrace()
                fallback()
            }
        }

        fun redirectToStore(context: Context, address: String) {
            handleActivityException(
                action = {
                    val intent = getIntentForStore(address)
                    if (isValidIntent(intent)) {
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(
                            context,
                            R.string.error_open_playstore,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                fallback = {
                    Toast.makeText(context, R.string.error_open_playstore, Toast.LENGTH_SHORT)
                        .show()
                }
            )
        }


        private fun getIntentForStore(address: String): Intent? {
            return CieloApplication.context?.let { context ->
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(address)
                )

                if (canStartIntent(context, intent))
                    intent
                else
                    null
            }
        }
    }

    private enum class StoreType {
        STORE_WEB, STORE_APP
    }
}