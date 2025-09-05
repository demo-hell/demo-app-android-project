package br.com.mobicare.cielo.chat.presentation.utils

import android.app.Activity
import android.graphics.Typeface
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chat.domains.EnumFeatures
import br.com.mobicare.cielo.chat.presentation.ui.ChatDialog
import br.com.mobicare.cielo.chat.presentation.ui.YesNoDialogInterface
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences

object ChatApollo {

    fun configFont(textView: TextView, activity: Activity) {
        val type = Typeface.createFromAsset(activity.assets, "fonts/MuseoSans-700.ttf")
        textView.typeface = type
    }

    fun showAlertConfirm(activity: Activity, message: String, title: String,
                         yesNoDialogInterface: YesNoDialogInterface,
                         lblYes: String, lblNo: String, path: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Action.FORMULARIO),
            action = listOf(path),
            label = listOf(Label.MENSAGEM, Label.INFO, "Deseja Encerrar o Chat Cielo?"),
        )

        val alertDialog = AlertDialog.Builder(activity, R.style.DialogStyle)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(lblYes) { dialog, id ->
                    Analytics.trackEvent(
                        category = listOf(Category.APP_CIELO, Category.TAPICON),
                        action = listOf(path),
                        label = listOf("Confirmar o Encerramento do Chat Cielo")
                    )
                    yesNoDialogInterface.onDialogPositiveClick()
                    dialog.dismiss()
                    ChatDialog.webV!!.visibility = View.GONE
                }
                .setNegativeButton(lblNo) { dialog, id ->
                    Analytics.trackEvent(
                        category = listOf(Category.APP_CIELO, Category.TAPICON),
                        action = listOf(path),
                        label = listOf("Cancelar o Encerramento do Chat Cielo")
                    )
                    yesNoDialogInterface.onDialogNegativeClick()
                    dialog.dismiss()
                }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            alertDialog.setTitle(Html.fromHtml("<b>$title</b>", Html.FROM_HTML_MODE_LEGACY))
        } else {
            alertDialog.setTitle(Html.fromHtml("<b>$title</b>"))
        }
        alertDialog.show()
    }

    fun logout() {
        ChatDialog.dialog = null
    }

    fun callChat(activity: Activity) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.TAPICON),
            action = listOf("Inicio"),
            label = listOf("Chat")
        )
        ChatDialog.showDialog(activity, EnumFeatures.CHAT, UserPreferences.getInstance().numeroEC,
                UserPreferences.getInstance().token, "Inicio")
    }

    fun callChatNotLogin(activity: Activity) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.TAPICON),
            action = listOf("Inicio"),
            label = listOf("Chat")
        )

        ChatDialog.showDialog(activity, EnumFeatures.CHAT, "Inicio")
    }
}
