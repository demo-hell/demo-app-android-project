package br.com.mobicare.cielo.migration.presentation.ui.fragment

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.showMessage
import br.com.mobicare.cielo.migration.domains.entities.MigrationDomain
import br.com.mobicare.cielo.migration.presentation.ui.activity.MigrationActionListener
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_migration_step_01.*


class MigrationStep01 : BaseFragment() {

    private var mIsNameValidate = false

    private lateinit var migrationActionListener: MigrationActionListener
    private var migrationDomain: MigrationDomain? = null

    companion object {
        fun create(actionListener: MigrationActionListener, migrationDomain: MigrationDomain): MigrationStep01 {
            val fragment = MigrationStep01().apply {
                migrationActionListener = actionListener
                this.migrationDomain = migrationDomain
            }

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_migration_step_01, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        migrationDomain?.let {
            it.fullName?.let { it
                edit_text_full_name.text = SpannableStringBuilder.valueOf(it)
            }
        }

        configNextStep()
    }

    private fun configNextStep() {
        button_migration_next.setOnClickListener {

            gaSendButton(button_migration_next.text.toString())
            if(Utils.isNetworkAvailable(requireActivity())){
                if (isAttached()) {
                    if (isValidateFields()) {
                        migrationDomain?.fullName = edit_text_full_name.text.toString()
                        migrationActionListener.onNextStep(false)
                    }
                }
            }else{
                requireContext().showMessage(getString(R.string.title_error_wifi_subtitle),
                        title = getString(R.string.title_error_wifi_title))
            }
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

            if (edit_text_full_name.text.toString().trim().isBlank() || edit_text_full_name.text.toString().length < 3) {
                errorFieldShow(text_input_full_name, getString(R.string.sr_invalidate_full_name))
                mIsNameValidate = false
                result = false
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
