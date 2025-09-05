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
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.meuCadastroNovo.analytics.MeuCadastroGA4.ScreenView.SCREEN_VIEW_MY_PROFILE_ACCOUNT_FLAG_TRANSFER_SUCCESS
import br.com.mobicare.cielo.meuCadastroNovo.domain.Bank
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.github.mikephil.charting.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.ft_fragment_screen_sucess.*
import kotlinx.android.synthetic.main.ft_fragment_screen_sucess.view.*
import br.com.mobicare.cielo.meuCadastroNovo.analytics.MeuCadastroGA4 as ga4


/**
 * @author Enzo teles
 * */
class FtScreenSucessBottomSheet(val actionListner: ActivityStepCoordinatorListener?) : BottomSheetDialogFragment(), View.OnClickListener {

    lateinit var type: String

    companion object {
        const val TYPETRANFER = "tranfer_brands"
        const val TYPEINSTALLMACHINE = "install_machine"
        const val TYPEREPLACEMACHINE = "replace_machine"
        val BANK = "bank"
        const val PROTOCOL_ARGS = "PROTOCOL_ARGS"
        const val HOURS_ARGS = "HOURS_ARGS"
        var protocol: String = EMPTY
        var hours: Int = ZERO
        fun newInstanceTransferBrands(bank: Bank, type: String, actionListner: ActivityStepCoordinatorListener?): FtScreenSucessBottomSheet {
            return FtScreenSucessBottomSheet(actionListner).apply {
                arguments = Bundle().apply {
                    putParcelable(BANK, bank)
                    putString(TYPETRANFER, type)
                }
            }
        }

        fun newInstanceInstallOrReplaceMachine(type: String, actionListner: ActivityStepCoordinatorListener?, protocol: String?, hours: Int?): FtScreenSucessBottomSheet {
            return FtScreenSucessBottomSheet(actionListner).apply {
                arguments = Bundle().apply {
                    if (type.equals(TYPEINSTALLMACHINE)) putString(TYPEINSTALLMACHINE, type)
                    else putString(TYPEREPLACEMACHINE, type)
                    putString(PROTOCOL_ARGS, protocol?: EMPTY)
                    putInt(HOURS_ARGS, hours ?: ZERO)
                }
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            when {
                (!it.getString(TYPETRANFER).isNullOrEmpty()) -> type = it.getString(TYPETRANFER)!!
                (!it.getString(TYPEINSTALLMACHINE).isNullOrEmpty()) -> type = it.getString(TYPEINSTALLMACHINE)!!
                (!it.getString(TYPEREPLACEMACHINE).isNullOrEmpty()) -> type = it.getString(TYPEREPLACEMACHINE)!!
            }

            protocol = it.getString(PROTOCOL_ARGS, EMPTY)
            hours = it.getInt(HOURS_ARGS, ZERO)
        }

        verificationOfScreenShow()
        btn_close.setOnClickListener(this)
        ft_btn_close.setOnClickListener(this)


    }

    /**
     * método que mostra qual tela vai ser mostrada conforme o retorno da api
     * */
    private fun verificationOfScreenShow() {

        when (type) {

            TYPETRANFER -> {
                ft_num_protocolor.visibility = View.GONE
                ft_num_protocolor_value.visibility = View.INVISIBLE
                ft_day.visibility = View.GONE
                ft_day_value.visibility = View.GONE
                ft_subtitle_sucess.visibility = View.VISIBLE
                ft_subtitle_sucess.text = requireActivity().getString(R.string.ft_subtitle_sucess_tranfer)
                ft_text_imp_value.apply {
                    text = getTextTransferBrands()
                }
                toolbar_migration.tv_toolbar.text = requireActivity().getString(R.string.tv_toolbar_tranfer)
                logScreenView()
            }
            TYPEINSTALLMACHINE -> {
                ft_num_protocolor.visibility = View.VISIBLE
                ft_num_protocolor_value.visibility = View.VISIBLE
                ft_day.visibility = View.GONE
                ft_day_value.visibility = View.GONE
                ft_num_protocolor_value?.text = protocol
                ft_subtitle_sucess.visibility = View.VISIBLE
                ft_subtitle_sucess.text = requireActivity().getString(R.string.ft_subtitle_sucess_install_machine)
                ft_text_imp_value.apply {
                    text = getTextInstallMachineBrands()
                }
                toolbar_migration.tv_toolbar.text = requireActivity().getString(R.string.tv_toolbar_install_machine)
            }
            TYPEREPLACEMACHINE -> {
                ft_num_protocolor.visibility = View.VISIBLE
                ft_num_protocolor_value.visibility = View.VISIBLE
                ft_day.visibility = View.VISIBLE
                ft_day_value.visibility = View.VISIBLE
                ft_num_protocolor_value?.text = protocol
                ft_day_value?.text = resources.getQuantityString(R.plurals.text_analysis_period, hours, hours)
                ft_subtitle_sucess.visibility = View.GONE
                ft_text_imp_value.apply {
                    text = getTextInstallMachineBrands()
                }
                ft_title_sucess.text = "Solicitação efetuada com sucesso!"
                dc_iv_brand.setImageResource(R.drawable.ic_shape_success)
                val lp = dc_iv_brand.layoutParams
                lp.height = Utils.convertDpToPixel(70f).toInt()
                lp.width = Utils.convertDpToPixel(70f).toInt()
                dc_iv_brand.layoutParams = lp
                toolbar_migration.tv_toolbar.text = requireActivity().getString(R.string.tv_toolbar_replace_machine)
            }
        }
    }

    private fun getTextAddAccount(): CharSequence? {

        val textFinal = SpannableStringBuilder()

        textFinal.append(requireActivity().getString(R.string.add_account_01))
        textFinal.append(" ")
        val start = textFinal.length

        textFinal.append(requireActivity().getString(R.string.add_account_02))
        textFinal.setSpan(StyleSpan(Typeface.BOLD), start, textFinal.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textFinal.append(" ")

        textFinal.append(requireActivity().getString(R.string.add_account_03))
        textFinal.append(" ")
        val end = textFinal.length

        textFinal.append(requireActivity().getString(R.string.add_account_04))
        textFinal.setSpan(StyleSpan(Typeface.BOLD), end, textFinal.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textFinal.append(" ")

        textFinal.append(requireActivity().getString(R.string.add_account_05))
        textFinal.append(" ")

        return textFinal
    }

    private fun getTextTransferBrands(): CharSequence? {
        val textFinal = SpannableStringBuilder()

        textFinal.append(requireActivity().getString(R.string.transfer_brands_01))
        textFinal.append(" ")
        val start = textFinal.length

        textFinal.append(requireActivity().getString(R.string.transfer_brands_02))
        textFinal.setSpan(StyleSpan(Typeface.BOLD), start, textFinal.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textFinal.append(" ")

        textFinal.append(requireActivity().getString(R.string.transfer_brands_04))
        textFinal.append(" ")
        val end = textFinal.length

        textFinal.append(requireActivity().getString(R.string.transfer_brands_04))
        textFinal.setSpan(StyleSpan(Typeface.BOLD), end, textFinal.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textFinal.append(" ")

        return textFinal
    }

    private fun getTextInstallMachineBrands(): CharSequence? {
        val textFinal = SpannableStringBuilder()

        textFinal.append(requireActivity().getString(R.string.transfer_brands_install_machine_01))
        textFinal.append(" ")
        val start = textFinal.length

        textFinal.append(requireActivity().getString(R.string.transfer_brands_install_machine_02))
        textFinal.setSpan(StyleSpan(Typeface.BOLD), start, textFinal.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textFinal.append(" ")

        return textFinal
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.ft_fragment_screen_sucess, container, false)
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
                actionListner?.onNextStep(true, null)
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
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        behavior.state = BottomSheetBehavior.STATE_EXPANDED;
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            })
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_close -> {
                dismiss()
                actionListner?.onNextStep(true, null)

            }
            R.id.ft_btn_close -> {
                dismiss()
                actionListner?.onNextStep(true, null)
            }
        }
    }

    private fun logScreenView(){
        ga4.logScreenView(SCREEN_VIEW_MY_PROFILE_ACCOUNT_FLAG_TRANSFER_SUCCESS)
    }
}