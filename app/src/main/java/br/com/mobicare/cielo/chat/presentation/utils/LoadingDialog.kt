package br.com.mobicare.cielo.chat.presentation.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import br.com.mobicare.cielo.R

class LoadingDialog(context: Context) : AlertDialog(context) {

    private var dialog: LoadingDialog? = null
    private var currentActivity: Activity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(onSaveInstanceState())
        this.setContentView(R.layout.progress)

        this.setCancelable(false)
        this.setCanceledOnTouchOutside(false)
        this.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun showDialog(activity: Activity?) {
        if (dialog == null) {
            dialog = LoadingDialog(activity!!)
        }
        currentActivity = activity

        if (currentActivity != null && currentActivity!!.isFinishing) {
            return
        }

        if (!dialog!!.isShowing && !currentActivity!!.isFinishing) {
            dialog!!.show()
        }
    }

    fun dismissDialog() {
        if (currentActivity != null && currentActivity!!.isFinishing) {
            return
        }
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
        dialog = null
    }

}