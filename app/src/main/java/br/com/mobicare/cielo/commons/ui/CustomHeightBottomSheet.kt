package br.com.mobicare.cielo.commons.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.component.selectBottomSheet.SelectBottomSheetEnum
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber

private const val FULLSCREEN_SLIDEOFFSET = 0.0f
private const val CUSTOM_HEIGHT_SLIDEOFFSET = -1.0f

open class CustomHeightBottomSheet : BottomSheetDialogFragment() {

    lateinit var bottomSheetCustomHeight: SelectBottomSheetEnum

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

    private fun changeDialog(dialog: Dialog) {
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(
                R.id.design_bottom_sheet
            ) as? FrameLayout
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)

                behavior.apply {
                    peekHeight =
                        (Resources.getSystem().displayMetrics.heightPixels * bottomSheetCustomHeight.size).toInt()

                    state = when (bottomSheetCustomHeight) {
                        SelectBottomSheetEnum.FULLSCREEN -> BottomSheetBehavior.STATE_EXPANDED
                        SelectBottomSheetEnum.MEDIUM, SelectBottomSheetEnum.SMALL -> BottomSheetBehavior.STATE_COLLAPSED
                    }

                    setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                        override fun onStateChanged(bottomSheet: View, newState: Int) {}
                        override fun onSlide(bottomSheet: View, slideOffset: Float) {
                            Timber.i("Slide $slideOffset")

                            if (bottomSheetCustomHeight == SelectBottomSheetEnum.FULLSCREEN && slideOffset == FULLSCREEN_SLIDEOFFSET) {
                                this@CustomHeightBottomSheet.dismiss()
                            } else if (bottomSheetCustomHeight != SelectBottomSheetEnum.FULLSCREEN && slideOffset == CUSTOM_HEIGHT_SLIDEOFFSET) {
                                this@CustomHeightBottomSheet.dismiss()
                            }
                        }
                    })
                }
            }
        }
    }

    protected open fun onBackClicked() {}
}