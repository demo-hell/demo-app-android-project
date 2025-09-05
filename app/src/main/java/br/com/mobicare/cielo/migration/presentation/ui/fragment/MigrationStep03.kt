package br.com.mobicare.cielo.migration.presentation.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.data.domain.MultichannelUserTokenResponse
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceEditTextView
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.afterTextChangesEmptySubscribe
import br.com.mobicare.cielo.commons.utils.afterTextChangesNotEmptySubscribe
import br.com.mobicare.cielo.commons.utils.showMessage
import br.com.mobicare.cielo.migration.MigrationRepository
import br.com.mobicare.cielo.migration.domains.entities.MigrationDomain
import br.com.mobicare.cielo.migration.presentation.presenter.MigrationContract
import br.com.mobicare.cielo.migration.presentation.presenter.MigrationPresenter
import br.com.mobicare.cielo.migration.presentation.ui.activity.MigrationActionListener
import br.com.mobicare.cielo.selfRegistration.passwordpolitic.PasswordPoliticActivity
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.card_error.*
import kotlinx.android.synthetic.main.fragment_migration_step_03.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class MigrationStep03 : BaseFragment(), MigrationContract.View {

    private lateinit var presenter: MigrationPresenter

    private var mIsPasswordValidate = false
    private var mIsPasswordConfirmValidate = false

    private var migrationActionListener: MigrationActionListener? = null
    private var migrationDomain: MigrationDomain? = null

    val repository: MigrationRepository by inject {
        parametersOf(this)
    }

    companion object {
        fun create(actionListener: MigrationActionListener, migrationDomain: MigrationDomain): MigrationStep03 {
            val fragment = MigrationStep03().apply {
                migrationActionListener = actionListener
                this.migrationDomain = migrationDomain
            }

            return fragment
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.setView(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_migration_step_03, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = MigrationPresenter(repository)
        presenter.setView(this)

        edit_text_password.text?.clear()
        edit_text_password_confirm.text?.clear()

        configListeners()
        configPasswordField()
        configPasswordConfirmField()
        configNextStep()
    }

    private fun configNextStep() {
        button_migration_next.setOnClickListener {

            gaSendButton(button_migration_next.text.toString())
            if (Utils.isNetworkAvailable(requireActivity())) {
                if (isAttached()) {
                    if (isValidateFields()) {
                        migrationDomain?.let {
                            it.password = edit_text_password.text.toString()
                            it.currentPassword = textEditCurrentPassword.text.toString()
                            presenter.migrationUser(it)
                        }
                    }
                }
            } else {
                requireContext().showMessage(getString(R.string.title_error_wifi_subtitle),
                        title = getString(R.string.title_error_wifi_title))
            }

        }
    }

    private fun configListeners() {
        this.infPasswordButtonView?.setOnClickListener {
            startActivity(Intent(context, PasswordPoliticActivity::class.java))
        }
    }

    private fun configPasswordField() {
        edit_text_password_confirm.afterTextChangesNotEmptySubscribe {
            callPasswordValidate(it, true)
            callPasswordConfirmValidate(it, false)
        }

        edit_text_password.afterTextChangesEmptySubscribe {
            if (isAttached()) {
                mIsPasswordValidate = false

                errorFieldCancel(text_input_password)
            }
        }
    }

    private fun configPasswordConfirmField() {
        edit_text_password_confirm.afterTextChangesNotEmptySubscribe {
            callPasswordValidate(it, false)
            callPasswordConfirmValidate(it, true)
        }

        edit_text_password_confirm.afterTextChangesEmptySubscribe {
            if (isAttached()) {
                mIsPasswordConfirmValidate = false

                errorFieldCancel(text_input_password_confirm)
            }
        }

        edit_text_password_confirm.isLongClickable = false

    }


    private fun callPasswordValidate(it: Editable, isShowError: Boolean) {
        if (isAttached()) {

            mIsPasswordValidate = false

            val password = it.toString()
            if (TextUtils.isEmpty(password)) {
                if (isShowError) errorFieldShow(text_input_password, getString(R.string.migration03_senha))
                return
            } else {
                errorFieldCancel(text_input_password)
            }

            val passwordConfirm = edit_text_password_confirm.text.toString()
            if (!TextUtils.isEmpty(passwordConfirm) && !TextUtils.isEmpty(password)) {
                if (password != passwordConfirm) {
                    if (isShowError) errorFieldShow(text_input_password, getString(R.string.migration03_senha))
                    return
                } else {
                    if (isShowError) errorFieldCancel(text_input_password)
                }
            }
            mIsPasswordValidate = true
        }
    }


    private fun callPasswordConfirmValidate(it: Editable, isShowError: Boolean) {
        if (isAttached()) {

            mIsPasswordConfirmValidate = false

            val passwordConfirm = it.toString()
            if (TextUtils.isEmpty(passwordConfirm)) {
                if (isShowError) errorFieldShow(text_input_password_confirm, getString(R.string.migration03_senha_confirm))
                return
            } else {
                if (isShowError) errorFieldCancel(text_input_password_confirm)
            }

            val password = edit_text_password.text.toString()
            if (!TextUtils.isEmpty(passwordConfirm) && !TextUtils.isEmpty(password)) {
                if (password != passwordConfirm) {
                    if (isShowError) errorFieldShow(text_input_password_confirm, getString(R.string.migration03_senha_confirm))
                    return
                } else {
                    errorFieldCancel(text_input_password_confirm)
                }
            }

            mIsPasswordConfirmValidate = true
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

        val changeFormResultToFalse = {
            mIsPasswordValidate = false
            result = false
        }

        if (isAttached()) {

            validateInputAndExecute(textInputCurrentPassword, textEditCurrentPassword,
                    { textInputEdit ->
                        textInputEdit.text.toString().trim().isBlank()
                    }, getString(R.string.migration03_senha_antiga, 12),
                    changeFormResultToFalse)


            validateInputAndExecute(text_input_password, edit_text_password,
                    configurePasswordValidation(),
                    getString(R.string.migration03_senha, 6), changeFormResultToFalse)


            validateInputAndExecute(text_input_password_confirm, edit_text_password_confirm,
                    configurePasswordValidation(),
                    getString(R.string.migration03_senha, 6), changeFormResultToFalse)


        }

        return result
    }

    private fun configurePasswordValidation(): (TypefaceEditTextView) -> Boolean {
        return { textInputEdit: TypefaceEditTextView ->
            textInputEdit.text.toString().trim().isBlank()
                    || textInputEdit.text.toString().length < 6
        }
    }

    private fun validateInputAndExecute(textInputLayout: TextInputLayout,
                                textInputEditText: TypefaceEditTextView,
                                predicate: (textInputEdit: TypefaceEditTextView) -> Boolean,
                                errorMessage: String,
                                executeAfterTrue: (() -> Unit)) {
        if (predicate(textInputEditText)) {
            errorFieldShow(textInputLayout, errorMessage)
            executeAfterTrue()
        }
    }

    override fun onMigrationUser(response: MultichannelUserTokenResponse) {
        this.migrationDomain?.tokenExpirationInMinutes = response.tokenExpirationInMinutes
        this.migrationActionListener?.onNextStep(false)
    }

    override fun showLoading() {
        if (isAttached())
            migrationActionListener?.showProgress()
    }

    override fun hideLoading() {
        if (isAttached())
            migrationActionListener?.hideProgress()
    }

    override fun showError(message: String, title: String) {
        if (isAttached()) {
            migrationActionListener?.hideProgress()
            activity?.showMessage(message, title)
        }
    }

    override fun showError(error: ErrorMessage) {
        if (isAttached()) {
//            layout_card_error.visibility = View.VISIBLE
//            text_view_card_error_msg.text = error.message
//            button_try.setOnClickListener {
//                hideError()
//                button_migration_next.performClick()
//            }
            hideLoading()
            errorFieldShow(text_input_password, error.errorMessage)
        }
    }

    override fun showErrorApi(error: Int) {
        hideLoading()
        when {
//            error == 202 -> onMigrationUser()
            error == 401 -> {
                requireContext().showMessage(getString(R.string.error_401),
                        title = getString(R.string.text_title_server_generic_error))
            }
            error == 420 -> {
                requireContext().showMessage(getString(R.string.error_420),
                        title = getString(R.string.text_title_server_generic_error))
            }
            error == 500 -> requireContext().showMessage(getString(R.string.error_500),
                    title = getString(R.string.text_title_server_generic_error))
            else -> {
                requireContext().showMessage(getString(R.string.error_500),
                        title = getString(R.string.text_title_server_generic_error))
            }
        }
    }

    private fun hideError() {
        layout_card_error.visibility = View.GONE
    }

    private fun gaSendButton(labelButton: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, HOME_LOGADA),
                action = listOf(Action.ATUALIZAR_ACESSO, Action.FORMULARIO),
                label = listOf(Label.BOTAO, labelButton.replace("\n", ""))
            )
        }
    }


}
