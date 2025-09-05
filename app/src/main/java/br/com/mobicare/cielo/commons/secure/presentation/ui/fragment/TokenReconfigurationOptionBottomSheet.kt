package br.com.mobicare.cielo.commons.secure.presentation.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.utils.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_token_reconfiguration_option_bottom_sheet.*

class TokenReconfigurationOptionBottomSheet: BottomSheetDialogFragment() {

    private var listener: Listener? = null
    var title: String? = null
    var passOrAt: String? = MFA_ATIVO

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_token_reconfiguration_option_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //ga verification
        UserPreferences.getInstance().keepStatusMfa?.let { stMfa->
                title = if (stMfa) MFA_NOVO_TOKEN else MFA_NOVO_TOKEN_TROCA
        }
        configureListeners()
    }

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

    private fun configureListeners() {
        this.dontHavePhoneButton?.setOnClickListener {
            this.listener?.onChoice(false)
            gaBottomSheet(getString(R.string.text_nao_possuo_o_celular))
            this.dismiss()
        }
        this.havePhoneButton?.setOnClickListener {
            this.listener?.onChoice(true)
            gaBottomSheet(getString(R.string.text_possuo_o_celular))
            this.dismiss()
        }
    }

    fun setListener(listener: Listener) : TokenReconfigurationOptionBottomSheet {
        this.listener = listener
        return this
    }

    interface Listener {
        fun onChoice(hasMobilePhone: Boolean)
    }

    //ga
    fun gaBottomSheet(nameBtn: String) {

        title?.let { t ->
            passOrAt?.let { st ->
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, t),
                    action = listOf(Action.ONBOARDING, st),
                    label = listOf(nameBtn)
                )
            }
        }
    }
    //end ga

}