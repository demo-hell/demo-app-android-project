package br.com.mobicare.cielo.migration.presentation.ui.fragment

import android.os.Bundle
import android.text.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.migration.domains.entities.MigrationDomain
import br.com.mobicare.cielo.migration.presentation.ui.activity.MigrationActionListener
import com.google.android.material.textfield.TextInputLayout
import com.redmadrobot.inputmask.helper.Mask
import com.redmadrobot.inputmask.model.CaretString
import kotlinx.android.synthetic.main.fragment_migration_step_02.*


class MigrationStep02 : BaseFragment() {

    private var mIsEmailValidate = false
    private var mIsEmailConfirmValidate = false
    private var mIsCpfValidate = false
    private var mCpfValue = ""

    private var isToValidateCpf: Boolean = true


    private lateinit var recebaMaisActionListener: MigrationActionListener
    private var migrationDomain: MigrationDomain? = null

    companion object {
        fun create(actionListener: MigrationActionListener, migrationDomain: MigrationDomain): MigrationStep02 {
            val fragment = MigrationStep02().apply {
                recebaMaisActionListener = actionListener
                this.migrationDomain = migrationDomain
            }

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_migration_step_02, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        migrationDomain?.let {
            it.email?.let {
                it
                edit_text_email.text = SpannableStringBuilder.valueOf(it)
            }
            it.emailConfirmation?.let {
                it
                edit_text_email_confirm.text = SpannableStringBuilder.valueOf(it)
            }

            it.cpf?.let {
                it
                edit_text_cpf.text = SpannableStringBuilder.valueOf(it)
            }

        }

        configEmailField()
        configEmailConfirmField()
        configCpfField()
        configNextStep()

        configForeignCheckbox()
    }

    private fun configForeignCheckbox() {
        checkboxUserForeign.setOnCheckedChangeListener { _, isChecked ->

            isToValidateCpf = !isChecked

            if (!isToValidateCpf) {
                text_input_cpf.hint = getString(R.string.text_optional_cpf_label)
            } else {
                text_input_cpf.hint = getString(R.string.sr_cpf_hint)
            }

        }
    }

    private fun configNextStep() {
        button_migration_next.setOnClickListener {

            gaSendButton(button_migration_next.text.toString())
            if (Utils.isNetworkAvailable(requireActivity())) {
                if (isAttached()) {
                    if (isValidateFields()) {
                        migrationDomain?.email = edit_text_email.text.toString()
                        migrationDomain?.emailConfirmation = edit_text_email_confirm.text.toString()

                        if (isToValidateCpf) {
                            migrationDomain?.cpf = edit_text_cpf.text.toString()
                        }

                        recebaMaisActionListener.onNextStep(false)
                    }
                }
            } else {
                requireContext().showMessage(getString(R.string.title_error_wifi_subtitle),
                        title = getString(R.string.title_error_wifi_title))
            }

        }
    }

    private fun configEmailField() {
        edit_text_email.afterTextChangesNotEmptySubscribe {
            callEmailValidate(it, true)
            edit_text_email_confirm.text?.let { it1 -> callEmailConfirmValidate(it1, false) }
        }

        edit_text_email.afterTextChangesEmptySubscribe {
            if (isAttached()) {
                mIsEmailValidate = false
                errorFieldCancel(text_input_email)
            }
        }
    }


    private fun configEmailConfirmField() {
        edit_text_email_confirm.afterTextChangesNotEmptySubscribe {
            edit_text_email.text?.let { it1 -> callEmailValidate(it1, false) }
            callEmailConfirmValidate(it, true)

        }

        edit_text_email_confirm.afterTextChangesEmptySubscribe {
            if (isAttached()) {
                mIsEmailConfirmValidate = false
                //updateButtonNextState(false)
                errorFieldCancel(text_input_email_confirm)
            }
        }

        edit_text_email_confirm.isLongClickable = false
    }


    private fun configCpfField() {
        edit_text_cpf.inputType = InputType.TYPE_CLASS_NUMBER
        edit_text_cpf.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(CPF_LENGTH))
        edit_text_cpf.addTextChangedListener(object : TextWatcher {
            var isUpdate = false

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isAttached()) {
                    s?.let {

                        if (!isUpdate) {
                            isUpdate = true
                            mCpfValue = it.toString()

                            val cpfMask = cpfMaskFormatter(mCpfValue)
                            val cpf = cpfMask.formattedText.string
                            edit_text_cpf.setText(cpf)
                            edit_text_cpf.setSelection(cpf.length)

                            callCPFValidade(cpf)

                            isUpdate = false
                        }
                    }
                }
            }

        })


        edit_text_cpf.afterTextChangesEmptySubscribe {
            if (isAttached()) {
                errorFieldCancel(text_input_cpf)
                mCpfValue = ""
                mIsCpfValidate = false
            }
        }
    }

    private fun callCPFValidade(cpf: String) {
        when {
            cpf.isNullOrEmpty() -> {
                mIsCpfValidate = false
                errorFieldCancel(text_input_cpf)
            }
            ValidationUtils.isCPF(cpf) -> {
                mIsCpfValidate = true
                errorFieldCancel(text_input_cpf)
            }
            else -> {
                mIsCpfValidate = false
                errorFieldShow(text_input_cpf, getString(R.string.migration02_cpf))
            }
        }
    }

    fun cpfMaskFormatter(inputCpf: String): Mask.Result {
        val cpfMask = Mask(CPF_MASK_FORMAT)
        return cpfMask.apply(CustomCaretString.forward(inputCpf))
    }


    private fun callEmailValidate(it: Editable, isShowError: Boolean) {
        if (isAttached()) {
            mIsEmailValidate = false
            //updateButtonNextState(false)

            val email = it.toString()
            if (email.isNullOrBlank()) {
                errorFieldCancel(text_input_email)
                return
            }
            if (ValidationUtils.isEmail(email)) {
                errorFieldCancel(text_input_email)
            } else {
                if (isShowError) errorFieldShow(text_input_email, getString(R.string.migration02_email_01))
                return
            }

            val emailConfirm = edit_text_email_confirm.text.toString()
            if (!TextUtils.isEmpty(emailConfirm)) {
                if (emailConfirm != email) {
                    if (isShowError) errorFieldShow(text_input_email, getString(R.string.migration02_email_01))
                    return
                } else {
                    errorFieldCancel(text_input_email)
                }
            }

            mIsEmailValidate = true
        }
    }

    private fun callEmailConfirmValidate(it: Editable, isShowError: Boolean) {
        if (isAttached()) {
            mIsEmailConfirmValidate = false
            //updateButtonNextState(false)

            val emailConfirm = it.toString()
            if (emailConfirm.isNullOrBlank()) {
                errorFieldCancel(text_input_email_confirm)
                return
            }
            if (!ValidationUtils.isEmail(emailConfirm)) {
                if (isShowError) errorFieldShow(text_input_email_confirm, getString(R.string.migration02_email_02))
                return
            }

            val email = edit_text_email.text.toString()
            if (email != emailConfirm) {
                if (isShowError) errorFieldShow(text_input_email_confirm, getString(R.string.migration02_email_02))
                return
            } else {
                errorFieldCancel(text_input_email_confirm)
            }

            mIsEmailConfirmValidate = true

        }
    }


    private fun errorFieldCancel(view: TextInputLayout) {
        if (isAttached()) {
            view.error = null
            view.isErrorEnabled = false
        }
    }

    private fun errorFieldShow(view: TextInputLayout, errorString: String) {
        if (isAttached()) {
            view.error = errorString
            view.isErrorEnabled = true
        }
    }


    fun isValidateFields(): Boolean {
        var result = true
        if (isAttached()) {

            if (edit_text_email.text.toString().trim().isBlank()) {
                errorFieldShow(text_input_email, getString(R.string.migration02_email_01))
                mIsEmailValidate = false
                result = false
            }

            if (edit_text_email_confirm.text.toString().trim().isBlank()) {
                errorFieldShow(text_input_email_confirm, getString(R.string.migration02_email_02))
                mIsEmailConfirmValidate = false
                result = false
            }

            if (edit_text_email_confirm.text.toString().trim() != edit_text_email.text.toString().trim()) {
                errorFieldShow(text_input_email_confirm, getString(R.string.migration02_email_02))
                mIsEmailConfirmValidate = false
                result = false
            }

            val cpf = edit_text_cpf.text.toString()

            if (isToValidateCpf ||
                    !TextUtils.isEmpty(edit_text_cpf.text)) {
                callCPFValidade(cpf)
                if (!mIsCpfValidate) {
                    errorFieldShow(text_input_cpf, getString(R.string.migration02_cpf))
                    result = false
                }
            }

        }
        return result
    }

    private fun gaSendButton(labelButton: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO,  HOME_LOGADA),
                action = listOf(Action.ATUALIZAR_ACESSO, Action.FORMULARIO),
                label = listOf(Label.BOTAO, labelButton.replace("\n", ""))
            )
        }
    }

}
