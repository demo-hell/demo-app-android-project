package br.com.mobicare.cielo.balcaoRecebiveis.fragment

import android.app.Dialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.balcaoRecebiveis.AuthorizationActivity
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.utils.spannable.addSpannable
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_balcao_recebiveis_bottom_sheet.*
import org.jetbrains.anko.startActivity

class BalcaoRecebiveisBottomSheet : BottomSheetDialogFragment() {


    companion object {
        fun newInstance(): BalcaoRecebiveisBottomSheet {
            return BalcaoRecebiveisBottomSheet()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =
            inflater.inflate(R.layout.layout_balcao_recebiveis_bottom_sheet, container, false)
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {

            val bottomSheet = dialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            ) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
            behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
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
        UserPreferences.getInstance().saveBannerStatusBalcaoRebevies(true)
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
        }

        validationLatter.setOnClickListener {
            dismiss()
        }

        followOfTerm.setOnClickListener {
            requireActivity().startActivity<AuthorizationActivity>()
            dismiss()

        }
        tv_subtitle_bc.text = configureSubtitle()
    }

    private fun configureSubtitle(): SpannableStringBuilder {
        val text = SpannableStringBuilder()

        text.append(getString(R.string.text_body_br_bottom_sheet)
            .addSpannable(TextAppearanceSpan(requireActivity(), R.style.Paragraph_300_display_400)))
        text.append(" ")

        text.append(getString(R.string.text_body_br_bottom_sheet_01)
            .addSpannable(TextAppearanceSpan(requireActivity(), R.style.Paragraph_300_display_400_bs)))

        return text
    }
}