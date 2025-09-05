package br.com.mobicare.cielo.commons.ui.widget

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import br.com.mobicare.cielo.R
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.dialog_facilita.*

class CustomAlertDialogFacilita(activity: Activity?, private val cancelable: Boolean) : Dialog(activity!!) {

    private var title = ""
    private var text = ""
    private var TopbuttonName = ""
    private var BottombuttonName = ""


    private var listenerTop: View.OnClickListener? = null
    private var listenerBottom: View.OnClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_facilita)
        setCancelable(cancelable)
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        viewsClickListenerInit()
    }

    override fun onStart() {
        initDialog()
        super.onStart()
    }

    private fun fillFields(
        title: String,
        text: String?,
        topButtonText: String?,
        bottomButtonText: String?
    ) {
        clearDialog()
        this.title = title
        this.text = text ?: ""
        this.TopbuttonName = topButtonText!!
        this.BottombuttonName = bottomButtonText!!
    }

    private fun clearDialog() {
        title = ""
        text = ""
    }

    private fun initDialog() {
        if (title.isNotBlank()) {
            textDialogFacilitaTitle.text = title
        }
        if (text.isNotBlank()) {
            textDialogFacilitaDescription.text = text
        }
        if (TopbuttonName.isNotBlank()) {
            textDialogFacilitaNoOption.text = TopbuttonName
        }
        if (BottombuttonName.isNotBlank()) {
            buttonDialogFacilitaConfirm.setText(BottombuttonName)
        }

    }

    fun show(
        title: String, text: String?,
        topButtonText: String?,
        bottomButtonText: String?
    ) {
        fillFields(title, text, topButtonText, bottomButtonText)
        super.show()
    }

    fun setOnclickListenerTop(listener: View.OnClickListener): CustomAlertDialogFacilita {
        this.listenerTop = listener
        return this
    }

    fun setOnclickListenerBottom(listener: View.OnClickListener): CustomAlertDialogFacilita {
        this.listenerBottom = listener
        return this
    }

    private fun viewsClickListenerInit() {
        closeButtonDialog.setOnClickListener {
            dismiss()
        }
        textDialogFacilitaNoOption.setOnClickListener { view ->
            dismiss()
            listenerTop?.onClick(view)
        }
        buttonDialogFacilitaConfirm.setOnClickListener { view ->
            dismiss()
                try {
                    listenerBottom?.onClick(view)
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
        }
    }

    fun performClickTopButton() {
        this.listenerTop?.onClick(null)
    }

    fun performClickBottomButton() {
      this.listenerBottom?.onClick(null)
    }



}