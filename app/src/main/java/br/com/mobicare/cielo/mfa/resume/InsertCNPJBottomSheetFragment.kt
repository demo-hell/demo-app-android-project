package br.com.mobicare.cielo.mfa.resume

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chat.presentation.utils.ChatApollo
import br.com.mobicare.cielo.commons.helpers.FormHelper
import br.com.mobicare.cielo.commons.utils.CNPJ_MASK_INITIAL
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.mfa.MfaAccount
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_insert_cnpj_bottom_sheet.*

const val ARG_PARAM_MFA_ACCOUNT = "ARG_PARAM_MFA_ACCOUNT_CNPJ"
const val MASK_ROOT = "####-##"

class InsertCNPJBottomSheetFragment : BottomSheetDialogFragment() {

    private var listener: InsertCNPJBottomSheetFragmentContract? = null
    private var mfaAccount: MfaAccount? = null
    private var isValidate: Boolean = true

    companion object {
        fun create(mfaAccount: MfaAccount, listener: InsertCNPJBottomSheetFragmentContract) = InsertCNPJBottomSheetFragment().apply {
            this.listener = listener
            this.arguments = Bundle().apply {
                this.putParcelable(ARG_PARAM_MFA_ACCOUNT, mfaAccount)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setupDialog(dialog)
        return inflater.inflate(R.layout.fragment_insert_cnpj_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFocus()
        verifyCNPJ()
        setupBtnListener()
        getMfaAccount()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.dismiss()
    }

    private fun getMfaAccount() {
        arguments?.getParcelable<MfaAccount>(ARG_PARAM_MFA_ACCOUNT)?.let { mfaAccount ->
            this.mfaAccount = mfaAccount
            setupView(mfaAccount)
        }
    }

    private fun verifyCNPJ() {
        text_root_cnpj?.addTextChangedListener(
                onTextChanged = { s, start, before, count ->
                    s?.let { root ->
                        if (count > 6) {
                            val cnpj = returnCNPJ(root.toString())

                            if (cnpj.isNullOrEmpty().not() && ValidationUtils.isCNPJ(cnpj)) {
                                isValidate = true
                                setupError()
                            } else {
                                isValidate = false
                                setupError()
                            }
                        }else{
                            btn_send_cnpj?.isEnabled = false
                            setupResourcesActive()
                        }
                    } ?: run {
                        setupResourcesActive()
                    }
                }
        )
    }

    private fun setFocus() {
        text_root_cnpj?.requestFocus()
        setupResourcesActive()

        text_root_cnpj?.setOnFocusChangeListener { _, focus ->
            if (focus.not())
                text_root_cnpj?.setBackgroundResource(R.drawable.ic_background_cnpj)
        }
    }

    private fun setupError() {
        if (isValidate) {
            btn_send_cnpj?.isEnabled = true
            setupResourcesActive()
        } else {
            btn_send_cnpj?.isEnabled = false
            text_view_error_mfa_cnpj?.visible()
            text_root_cnpj?.setBackgroundResource(R.drawable.background_mfa_error)
        }
    }

    private fun setupResourcesActive() {
        text_view_error_mfa_cnpj?.gone()
        text_root_cnpj?.setBackgroundResource(R.drawable.background_mfa_active)
    }

    private fun setupView(mfaAccount: MfaAccount) {
        text_cnpj?.isEnabled = false
        btn_send_cnpj?.isEnabled = false
        text_subtitle_insert_cnpj?.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_5A646E))
        view_title_cnpj?.setBackgroundResource(R.drawable.ic_drag_bottom_sheet)

        text_root_cnpj?.addTextChangedListener(text_root_cnpj.getMask(MASK_ROOT, text_root_cnpj))
        val cnpj = mfaAccount.identificationNumber?.substring(0, 8)?.let { FormHelper.maskFormatter(it, CNPJ_MASK_INITIAL).formattedText.string }
        cnpj?.let { text_cnpj?.setText("$it/") }
    }

    private fun setupBtnListener() {
        btn_chat_cnpj?.setOnClickListener {
            ChatApollo.callChat(requireActivity())
        }

        btn_send_cnpj?.setOnClickListener {
            mfaAccount?.let { account ->
                val cnpj = returnCNPJ(text_root_cnpj.text.toString())
                if (ValidationUtils.isCNPJ(cnpj)) {
                    mfaAccount?.identificationNumber = cnpj
                    dismiss()
                    listener?.verifyData(account)
                } else {
                    isValidate = false
                    setupError()
                }
            }
        }
    }

    private fun returnCNPJ(root: String): String? {
        mfaAccount?.identificationNumber?.let {
            return it.substring(0, 8) + root.replace("-", "")
        }

        return null
    }

    private fun setupDialog(dialog: Dialog?) {
        dialog?.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet) as? FrameLayout
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
    }

}
