package br.com.mobicare.cielo.commons.ui.widget

import android.content.Context
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import kotlinx.android.synthetic.main.dialog_common_cielo.*

class UpdaterVersionDialog : androidx.fragment.app.DialogFragment() {

    private var activityContext: Context? = null

    var optionChooseListener: OnOptionChooseListener? = null

    var isForceUpdate: Boolean = false

    var versionDescription: String = ""

    private val STORE_NAME = "Google Play"

    interface OnOptionChooseListener {
        fun onConfirmationClick()
        fun onCancelClick()
    }


    companion object {

        const val TAG: String = "UpdaterVersionDialog"

        fun newInstance(context: Context?): UpdaterVersionDialog = UpdaterVersionDialog()
                .apply {
            this.activityContext = context
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CommonCieloDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_common_cielo, container, false)
    }

    override fun onStart() {
        super.onStart()

        if (isForceUpdate) {
            textCielolNoOption.visibility = View.GONE
        } else {
            textCielolNoOption.visibility = View.VISIBLE
        }

        if (versionDescription.isNotEmpty()) {
            textCieloCommonDescription.text = SpannableStringBuilder
                    .valueOf(versionDescription.replace("#lojadevice#",
                            STORE_NAME))
        }

    }

    override fun onResume() {
        super.onResume()

        dialog.apply {
            this?.setCancelable(false)
        }

        buttonCieloConfirm.setOnClickListener {
            optionChooseListener?.onConfirmationClick()
        }

        textCielolNoOption.setOnClickListener {
            optionChooseListener?.onCancelClick()
        }

        frameDialogCommonContent.bringToFront()
    }
}