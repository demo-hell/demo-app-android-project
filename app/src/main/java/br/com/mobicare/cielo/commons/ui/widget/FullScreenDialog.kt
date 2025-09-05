package br.com.mobicare.cielo.commons.ui.widget

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import br.com.mobicare.cielo.R
import kotlinx.android.synthetic.main.toolbar_common_dialogs.view.*


open class FullScreenDialog : androidx.fragment.app.DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogFloatinStyle)
    }


    override fun onStart() {
        super.onStart()

        dialog?.apply {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT

            this.window?.run {
                this.setLayout(width, height)
                this.attributes.windowAnimations = R.style.dialog_animation
            }

        }

    }


    fun setupToolbar(toolbar: Toolbar, toolbarTitle: String? = null) {
        toolbarTitle?.run {

            toolbar.txtTitle.text = SpannableStringBuilder.valueOf(this)


        }
    }

}