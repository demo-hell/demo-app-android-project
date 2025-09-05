package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.trocaMaquina.fragments.versionMachine

import android.app.Dialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_LAYOUT_AONDE_OBTER_INFORMACOES
import br.com.mobicare.cielo.commons.utils.spannable.addSpannable
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.version_machine_show_number_fragment.*

class OndeEncontroInformacoesMachineBottomSheetFragment : BottomSheetDialogFragment() {

    private var layoutId: Int = -1

    companion object {
        fun create(@LayoutRes layoutId: Int) =
                OndeEncontroInformacoesMachineBottomSheetFragment().apply {
                    var bundle = Bundle()
                    bundle.putInt(ARG_PARAM_LAYOUT_AONDE_OBTER_INFORMACOES, layoutId)
                    this.arguments = bundle
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.arguments?.let { itArguments ->
            this.layoutId = itArguments.getInt(ARG_PARAM_LAYOUT_AONDE_OBTER_INFORMACOES)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId, container, false)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_view_version_machine?.text = configSubtitle()

        this.btn_rm_close?.setOnClickListener {
            dismiss()
        }

        nextButton?.setOnClickListener {
            dismiss()
        }
    }

    private fun configSubtitle(): SpannableStringBuilder {
        val text = SpannableStringBuilder()

        text.append("Em sua maquininha, aperte a"
                .addSpannable(TextAppearanceSpan(requireContext(), R.style.TextBlack14spDescription)))

        text.append(" ")
        text.append("tecla 0"
                .addSpannable(TextAppearanceSpan(requireActivity(), R.style.TextBlack14spDescriptionBold)))

        text.append(" ")
        text.append("e verifique a versão."
                .addSpannable(TextAppearanceSpan(requireActivity(), R.style.TextBlack14spDescription)))

        return text
    }

}