package br.com.mobicare.cielo.commons.bottomsheet

import android.util.DisplayMetrics
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheet : BottomSheetDialogFragment() {

    private var bottomSheet: View? = null
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var type = EnumDefaultBottomSheet.FULL_SCREEN

    override fun onResume() {
        super.onResume()
        when (type) {
            EnumDefaultBottomSheet.FULL_SCREEN -> configureFullBottomSheet()
            EnumDefaultBottomSheet.WRAP_CONTENT -> configureWrapContentBottomSheet()
        }
    }

    private fun configureWrapContentBottomSheet() {
        bottomSheet = view
        bottomSheetBehavior = BottomSheetBehavior
                .from(view?.parent as View)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun configureFullBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior
                .from(view?.parent as View)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED

        val childLayoutParams = bottomSheet?.layoutParams
        val displayMetrics = DisplayMetrics()

        requireActivity()
                .windowManager
                .defaultDisplay
                .getMetrics(displayMetrics)

        val statusBar = resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusBarSize = resources.getDimensionPixelSize(statusBar)

        childLayoutParams?.height = displayMetrics.heightPixels - statusBarSize;
        bottomSheet?.layoutParams = childLayoutParams
    }

    open fun setTypeBottomSheet(eigth: Int?, type: EnumDefaultBottomSheet, layoutContent : View) {
        this.type = type
        this.bottomSheet = layoutContent
    }
}

enum class EnumDefaultBottomSheet {
    FULL_SCREEN,
    BY_PERCENT_SCREEN,
    WRAP_CONTENT,
    FIX_HEIGHT
}

