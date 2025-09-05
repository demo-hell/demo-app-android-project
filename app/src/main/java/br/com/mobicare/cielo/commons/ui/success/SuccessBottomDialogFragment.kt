package br.com.mobicare.cielo.commons.ui.success

import android.app.Dialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_success_common.*

const val SUCCESS_DEFAULT = "SUCCESS_DEFAULT"
const val SUCCESS_BUNDLE_TITLE = "SUCCESS_BUNDLE_TITLE"
const val SUCCESS_BUNDLE_TITLE_SCREEN = "SUCCESS_BUNDLE_TITLE_SCREEN"
const val SUCCESS_BUNDLE_BUTTON = "SUCCESS_BUNDLE_BUTTON"

class SuccessBottomDialogFragment : BottomSheetDialogFragment() {


    private var click: () -> Unit = {}
    private var type: String = EMPTY

    companion object {
        fun create(type: String, bundle: Bundle, click: () -> Unit): SuccessBottomDialogFragment {
            val fragment = SuccessBottomDialogFragment()
            fragment.type = type
            fragment.click = click
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_success_common, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnKeyListener { dialog, keyCode, _ ->
            if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                click()
                dialog.dismiss()
                return@setOnKeyListener true
            } else {
                return@setOnKeyListener false
            }

        }

        dialog.setOnShowListener {
            // For AndroidX use: com.google.android.material.R.id.design_bottom_sheet
            val bottomSheet = dialog.findViewById<View>(
                    com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
            behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    // React to state change
                    if (newState >= 4) {
                        dismiss()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            })
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
        }

        arguments?.let {
            textTitle.text = arguments?.getString(SUCCESS_BUNDLE_TITLE_SCREEN)
            textSuccessTitle.text = arguments?.getString(SUCCESS_BUNDLE_TITLE)
            buttonEndAction.setText(SpannableStringBuilder.valueOf(arguments?.getString(SUCCESS_BUNDLE_BUTTON)).toString())
        }

        btn_welcome_close.setOnClickListener {
            click()
//            dialog.dismiss()
        }
        buttonEndAction.setOnClickListener {
            click()
//            dialog.dismiss()
        }
    }

}