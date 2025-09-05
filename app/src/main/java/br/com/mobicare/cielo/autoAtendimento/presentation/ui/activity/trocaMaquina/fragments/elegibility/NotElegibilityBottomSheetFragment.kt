package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.trocaMaquina.fragments.elegibility

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.troca_maquina_not_elegibility_fragment.*
import kotlinx.android.synthetic.main.version_machine_show_number_fragment.btn_rm_close

class NotElegibilityBottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.troca_maquina_not_elegibility_fragment, container, false)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            // For AndroidX use: com.google.android.material.R.id.design_bottom_sheet
            val bottomSheet = dialog.findViewById<View>(
                    com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
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

    private fun configureSpacer() {
        this.scrollView?.let { itScrollView ->
            itScrollView.post {
                this.content_constrant?.let {itLayout ->
                    val diff = itScrollView.measuredHeight - itLayout.height
                    if (diff > 0) {
                        val lp = this.spacerView.layoutParams
                        lp.height = diff + 16
                        this.spacerView.layoutParams = lp
                        this.spacerView.requestLayout()
                    }
                }
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_view_call_01.setOnClickListener {
            activity?.let {
                Utils.callPhone(it, text_view_call_01.text.toString())
            }
        }

        text_view_call_02.setOnClickListener {
            activity?.let {
                Utils.callPhone(it, text_view_call_02.text.toString())
            }
        }

        this.btn_rm_close?.setOnClickListener {
            dismiss()
        }

        this.nextButton?.setOnClickListener {
            dismiss()
        }

        configureSpacer()
    }

}