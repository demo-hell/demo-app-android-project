package br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment

import android.annotation.SuppressLint
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
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.ft_fragment_screen_sucess.*


/**
 * @author Enzo teles
 * */
class FtScreenProgressingBottomSheet(val actionListner: ActivityStepCoordinatorListener?) : BottomSheetDialogFragment(), View.OnClickListener{

    lateinit var type:String
    companion object {
        fun newInstance(actionListner: ActivityStepCoordinatorListener?) = FtScreenProgressingBottomSheet(actionListner)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_close.setOnClickListener(this)
        ft_btn_close.setOnClickListener(this)
        ft_subtitle_sucess.apply {
            text = getTextProsTransfer()
        }
    }

    private fun getTextProsTransfer(): CharSequence? {

        val textFinal = SpannableStringBuilder()

        textFinal.append(requireActivity().getString(R.string.transfer_brands_pross_01))
        textFinal.append(" ")
        val start = textFinal.length

        textFinal.append(requireActivity().getString(R.string.transfer_brands_pross_02))
        textFinal.setSpan(StyleSpan(Typeface.BOLD), start,textFinal.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textFinal.append(" ")
        return  textFinal
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.ft_fragment_screen_progressing, container, false)
    }

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
                    if (newState >= 4) {
                        dismiss()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            })
        }

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_close->{
                dismiss()
                actionListner?.onNextStep(true, null)

            }
            R.id.ft_btn_close->{
                dismiss()
                actionListner?.onNextStep(true, null)
            }
        }
    }


}