package br.com.mobicare.cielo.mfa.onbordingSuccess

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.text.HtmlCompat
import br.com.mobicare.cielo.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_onboarding_success.*

class OnboardingSuccessBottomSheetFragment : BottomSheetDialogFragment() {

    var buttonText: String? = null
    var subTitleFirst: String? = null

    interface OnboardingSuccessListener {
        fun onButtonClicked() {}
        fun onSwipeClosed() {}
    }

    var listener: OnboardingSuccessListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(R.layout.layout_onboarding_success, container, false)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        verificationStatusDialog(dialog)
        return dialog
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureText()
        configureTextButton()
        configureListeners()
    }

    private fun configureText() {
        var text = ""
        text = getString(R.string.text_onboarding_success_text)

        subTitleFirst?.let {
            text = it
        }

        val spannableString = SpannableString(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY))
        this.txt_subtitle.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    private fun configureTextButton() {
        buttonText?.let {
            buttonView.setText(it)
        }
    }

    private fun configureListeners() {
        this.buttonView?.setOnClickListener {
            this.listener?.onButtonClicked()
        }
    }

    private fun verificationStatusDialog(dialog: Dialog) {
        dialog.setOnShowListener {
            // For AndroidX use: com.google.android.material.R.id.design_bottom_sheet
            val bottomSheet = dialog.findViewById<View>(
                    com.google.android.material.R.id.design_bottom_sheet
            ) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
            behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState >= 4) {
                        this@OnboardingSuccessBottomSheetFragment.listener?.onSwipeClosed()
                        dismiss()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            })
        }
    }

}