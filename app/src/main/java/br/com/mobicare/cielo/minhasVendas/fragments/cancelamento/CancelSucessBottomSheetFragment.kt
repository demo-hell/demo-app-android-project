package br.com.mobicare.cielo.minhasVendas.fragments.cancelamento

import android.app.Dialog
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action.FECHAR
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.minhasVendas.activities.CANCELAR_VENDAS_CATEGORY
import br.com.mobicare.cielo.minhasVendas.activities.ENVIANDO_CANCELAMENTO_VENDA_EVENT
import br.com.mobicare.cielo.mySales.analytics.MySalesGA4
import br.com.mobicare.cielo.mySales.analytics.MySalesGA4.Companion.SCREEN_NAME_CANCEL_SUCESS
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_detail_cancel_sucess.*
import org.koin.android.ext.android.inject

class CancelSucessBottomSheetFragment : BottomSheetDialogFragment(){

    private val ga4: MySalesGA4 by inject()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_detail_cancel_sucess, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            // For AndroidX use: com.google.android.material.R.id.design_bottom_sheet
            val bottomSheet = dialog.findViewById<View>(
                    com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
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

        view.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
        }

        btn_introduce_close.setOnClickListener {
            activity?.finish()
            dismiss()
            gaSendButtonCancelOk(FECHAR)
        }

        btn_cancel_sucess.setOnClickListener {
            activity?.finish()
            dismiss()
            gaSendButtonCancelOk("ok")
        }

        tv_cancel_content.text = getTextAlert()
    }

    private fun getTextAlert(): CharSequence?{
        val textFinal = SpannableStringBuilder()

        textFinal.append(requireActivity().getString(R.string.tv_cancel_alert_01))
        textFinal.append(" ")
        val start = textFinal.length

        textFinal.append(requireActivity().getString(R.string.tv_cancel_alert_02))
        textFinal.setSpan(StyleSpan(Typeface.BOLD), start,textFinal.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return  textFinal
    }

    private fun gaSendButtonCancelOk(name: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, CANCELAR_VENDAS_CATEGORY),
            action = listOf(ENVIANDO_CANCELAMENTO_VENDA_EVENT),
            label = listOf(Label.BOTAO, name)
        )
    }

    override fun onResume() {
        super.onResume()
        ga4.logScreenView(SCREEN_NAME_CANCEL_SUCESS)
        ga4.cancel()
    }
}