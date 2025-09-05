package br.com.mobicare.cielo.commons.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import br.com.mobicare.cielo.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class FullscreenBottomSheet: BottomSheetDialogFragment() {

    /**
     *onCreateDialog
     * @param savedInstanceState
     * @return dialog
     * */
    @SuppressLint("StringFormatMatches")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState)
        changeDialog(dialog)

        dialog.setOnKeyListener { dialog, keyCode, _ ->
            if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                dialog.dismiss()
                this.onBackClicked()
                return@setOnKeyListener true
            } else {
                return@setOnKeyListener false
            }

        }
        return dialog
    }

    /**
     * método para vericar quando o dialog muda de estado
     * @param dialog
     * */
    private fun changeDialog(dialog: Dialog) {
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(
                R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
            behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    if (slideOffset <= 0.0f) {
                        this@FullscreenBottomSheet.dismiss()
                    }
                }
            })
        }
    }

    protected open fun onBackClicked() {}

}