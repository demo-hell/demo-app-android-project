package br.com.mobicare.cielo.pix.ui.banner

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.FOUR
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.utils.setFullHeight
import br.com.mobicare.cielo.pix.constants.PIX_USAGE_TERMS_URL
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_banner_success_pix_bottom_sheet.*
import org.jetbrains.anko.browse

class BannerSuccessPixBottomSheet : BottomSheetDialogFragment() {

    var onClick: OnClickButtonsOptionsListener? = null

    interface OnClickButtonsOptionsListener {
        fun onBtnClose(dialog: Dialog) {}
        fun onCloseBanner(dialog: Dialog) {}
    }

    companion object {
        fun newInstance(): BannerSuccessPixBottomSheet {
            return BannerSuccessPixBottomSheet()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        setupDialog(dialog)
        return inflater.inflate(R.layout.layout_banner_success_pix_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDialog(dialog)
        setupListeners()
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogResize
    }

    private fun setupDialog(dialog: Dialog?) {
        dialog?.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet) as? FrameLayout
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)

            setFullHeight(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = ZERO

            behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState >= FOUR) {
                        dismiss()
                        dialog.let { dialog -> onClick?.onCloseBanner(dialog) }
                    }
                }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    }
                })
            }
        }
    }

    private fun setupListeners() {
        followOfTerm.setOnClickListener {
            dialog?.let { it1 -> onClick?.onBtnClose(it1) }
        }

        link_access_contact.setOnClickListener {
            requireActivity().browse(PIX_USAGE_TERMS_URL)
        }
    }
}