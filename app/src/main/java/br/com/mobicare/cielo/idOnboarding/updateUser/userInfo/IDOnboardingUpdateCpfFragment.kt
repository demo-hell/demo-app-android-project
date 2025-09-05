package br.com.mobicare.cielo.idOnboarding.updateUser.userInfo

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events
import br.com.mobicare.cielo.commons.bottomsheet.callhelpcenter.CallHelpCenterBottomSheet
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.helpers.EditTextHelper
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.extensions.onlyDigits
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.userStatus
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP1AnalyticsGA
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_FAIL_VALIDATION_CPF
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_FAIL_VALIDATION_CPF_CALL_HELP_CENTER
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_VALIDATION_CPF
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_VALIDATION_CPF_INCORRECT
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_VALIDATION_CPF_PUT_DATA
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_VALIDATED_CPF
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.android.ext.android.inject

class IDOnboardingUpdateCpfFragment : IDOnboardingUpdateUserBaseFragment(),
    IDOnboardingUpdateUserContract.View {

    private val analyticsGA: IDOnboardingP1AnalyticsGA by inject()
    override val canEdit: Boolean = false

    override fun updateView() {
        binding?.apply {
            etTypedValue.inputType = InputType.TYPE_CLASS_NUMBER
            etTypedValue.filters = arrayOf(InputFilter.LengthFilter(CPF_LENGTH))
            etTypedValue.hint = getString(R.string.cpf_hint)

            setupView(
                title = getString(
                    R.string.id_onboarding_update_cpf_title,
                    firstWordCapitalize(userStatus.name, getString(R.string.hello))
                ),
                editingSubtitle = getString(R.string.id_onboarding_update_cpf_edit_subtitle),
                currentValue = userStatus.cpf,
                errorLabel = getString(R.string.id_onboarding_update_cpf_error)
            )

            tvTypedValueErrorLabel.gone()
            tvIncorrectCpf.visible()

            analyticsGA.logIDScreenView(ANALYTICS_ID_SCREEN_VIEW_VALIDATION_CPF)
            presenter.verifyCpfNameBlocked()
        }
    }

    override fun onStart() {
        super.onStart()
        binding?.etTypedValue?.setText(userStatus.cpf.orEmpty())
    }

    override fun setupListeners() {
        binding?.apply {
            btnNext.setOnClickListener {
                tvTypedValueErrorLabel.gone()
                activity?.hideSoftKeyboard()

                etTypedValue.text?.toString()?.let {
                    userStatus.cpf = it.onlyDigits()
                }
                analyticsGA.logIDValidateSignUp(
                    ANALYTICS_ID_SCREEN_VIEW_VALIDATION_CPF,
                    ANALYTICS_ID_VALIDATED_CPF
                )
                presenter.validateCpfName(userStatus.cpf.orEmpty(), userStatus.name.orEmpty())
            }

            etTypedValue.let {
                EditTextHelper.cpfField(it)

                it.addTextChangedListener { editable ->
                    if (isValid(editable.toString())) {
                        btnNext.isEnabled = isEditing &&
                                editable.onlyDigits() != userStatus.cpf
                        tvTypedValueErrorLabel.gone()
                    } else {
                        if (btnNext.isEnabled == true) {
                            btnNext.isEnabled = false
                        }
                        if (isEditing) {
                            showTypedValueErrorLabel()
                        }
                    }
                }
            }

            tvIncorrectCpf.setOnClickListener {
                showConfirmationDialog()
            }
        }
    }

    override fun successValidatingCpfName() {
        if (isAttached() && context != null)
            try {
                findNavController().navigate(
                    R.id.action_to_idOnboardingUpdateEmailFragment
                )
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                if (e.stackTrace[ZERO].lineNumber < ZERO)
                    FirebaseCrashlytics.getInstance().setCustomKey(
                        IDOnboardingUpdateCpfFragment::class.java.simpleName,
                        getString(R.string.text_message_line_number_unavailable)
                    )
                else
                    FirebaseCrashlytics.getInstance().setCustomKey(
                        IDOnboardingUpdateCpfFragment::class.java.simpleName,
                        getString(
                            R.string.text_firebase_custom_key,
                            e.stackTrace[ZERO].lineNumber,
                            e.stackTrace[ZERO].className
                        )
                    )
            }
    }

    private fun showConfirmationDialog() {
        val cieloDialog = CieloDialog.create(
            getString(R.string.id_onboarding_update_cpf_help_link),
            getString(R.string.id_onboarding_update_cpf_text),
        )
        cieloDialog.setImage(R.drawable.ic_8)
            .closeButtonVisible(true)
            .setPrimaryButton(getString(R.string.id_onboarding_new_cpf_text))
            .setTitleColor(R.color.brand_600)
            .setOnPrimaryButtonClickListener {
                analyticsGA.logIDValidateCpfClick(ANALYTICS_ID_SCREEN_VIEW_VALIDATION_CPF_INCORRECT)
                goToNewCpfScreen()
            }
            .show(
                parentFragmentManager,
                getString(R.string.bottom_sheet_generic)
            )
        analyticsGA.logIDValidateCpfDisplay(ANALYTICS_ID_SCREEN_VIEW_VALIDATION_CPF_INCORRECT)
    }

    private fun goToNewCpfScreen() {
        findNavController().navigate(
            IDOnboardingUpdateCpfFragmentDirections
                .actionIdOnboardingUpdateCpfFragmentToIdOnboardingNewCpfFragment()
        )
    }

    override fun isValid(value: String?): Boolean {
        return value?.length == CPF_LENGTH
                && ValidationUtils.isCPF(value)
    }

    override fun showErrorInvalidCPF() {
        analyticsGA.logIDUnableValidateDisplay(ANALYTICS_ID_SCREEN_VIEW_FAIL_VALIDATION_CPF, EMPTY)
        navigation?.showCustomBottomSheet(
            image = R.drawable.img_aguardando_doc,
            title = getString(R.string.id_onboarding_update_cpf_invalid_cpf_bs_title),
            message = getString(R.string.id_onboarding_update_cpf_invalid_cpf_bs_message),
            bt1Title = getString(R.string.text_call_center_action),
            bt1Callback = {
                analyticsGA.logIDUnableValidateClick(ANALYTICS_ID_SCREEN_VIEW_FAIL_VALIDATION_CPF)
                val result = Bundle()
                result.putString(GoogleAnalytics4Events.ScreenView.SCREEN_NAME, ANALYTICS_ID_SCREEN_VIEW_FAIL_VALIDATION_CPF_CALL_HELP_CENTER)
                CallHelpCenterBottomSheet.newInstance().apply{
                    arguments = result
                }.show(childFragmentManager, EMPTY)
                false
            },
            bt2Title = getString(R.string.entendi),
            bt2Callback = {
                baseLogout()
                false
            },
            closeCallback = {
                baseLogout()
            }
        )
        analyticsGA.logIDIrregularCpfDisplay(ANALYTICS_ID_SCREEN_VIEW_VALIDATION_CPF_PUT_DATA, EMPTY)
    }
}