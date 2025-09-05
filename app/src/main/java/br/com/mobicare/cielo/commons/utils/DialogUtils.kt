    package br.com.mobicare.cielo.commons.utils

import android.app.Activity
import android.app.Dialog
import android.view.Window
import android.view.WindowManager
import android.widget.RadioButton
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import kotlinx.android.synthetic.main.alert_preciso_ajuda.*

fun Activity.createForgetUserDialog(screenPath: String, action: (radioSelectedId: Int) -> Unit) {
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.alert_preciso_ajuda)
    dialog.setTitle("")

    val params = WindowManager.LayoutParams()
    params.copyFrom(dialog.window!!.attributes)
    params.width = WindowManager.LayoutParams.MATCH_PARENT
    params.height = WindowManager.LayoutParams.WRAP_CONTENT

    val pj = dialog.findViewById(R.id.radio_button_ajuda_alert_pessoa_juridica) as RadioButton
    pj.setOnClickListener {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.TAPRADIO),
            action = listOf(screenPath),
            label = listOf("Radio ${pj.text}")
        )
    }

    val pf = dialog.findViewById(R.id.radio_button_ajuda_alert_pessoa_fisica) as RadioButton
    pf.setOnClickListener {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.TAPRADIO),
            action = listOf(screenPath),
            label = listOf("Radio ${pf.text}")
        )
    }

    dialog.custom_dialog_button_prosseguir.setOnClickListener {
        action(dialog.radio_group_ajuda_alert.checkedRadioButtonId)
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Action.FORMULARIO),
            action = listOf(screenPath),
            label = listOf(Label.BOTAO, dialog.custom_dialog_button_prosseguir.text.toString().toLowerCasePTBR()),
        )
        dialog.dismiss()
    }

    dialog.custom_dialog_button_cancelar.setOnClickListener {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Action.FORMULARIO),
            action = listOf(screenPath),
            label = listOf(Label.BOTAO, dialog.custom_dialog_button_cancelar.text.toString().toLowerCasePTBR()),
        )
        dialog.dismiss()
    }

    dialog.show()
    dialog.window!!.attributes = params


}