package br.com.mobicare.cielo.idOnboarding.updateUser.userInfo

import android.text.InputFilter
import android.text.InputType
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.bottomsheet.callhelpcenter.CallHelpCenterBottomSheet
import br.com.mobicare.cielo.commons.helpers.EditTextHelper
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.extensions.onlyDigits
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.userStatus
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP1AnalyticsGA
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_VALIDATION_CPF_PUT_DATA
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_VALIDATED_CPF
import br.com.mobicare.cielo.pix.constants.EMPTY
import org.koin.android.ext.android.inject

class IDOnboardingNewCpfFragment : IDOnboardingUpdateUserBaseFragment(),
    IDOnboardingUpdateUserContract.View {

    override val canEdit: Boolean = true
    override val isAnUpdate: Boolean = true
    private val analyticsGA: IDOnboardingP1AnalyticsGA by inject()

    override fun updateView() {
        binding?.apply {
            etTypedValue.inputType = InputType.TYPE_CLASS_NUMBER
            etTypedValue.filters = arrayOf(InputFilter.LengthFilter(CPF_LENGTH))
            etTypedValue.hint = getString(R.string.cpf_hint)

            setupView(
                title = getString(
                    R.string.id_onboarding_new_cpf_title_greeting,
                    firstWordCapitalize(userStatus.name, getString(R.string.hello))
                ),
                subtitle = getString(R.string.id_onboarding_new_cpf_subtitle),
                errorLabel = getString(R.string.id_onboarding_update_cpf_error),
                nextButton = getString(R.string.text_next_label)
            )

            tvTypedValueErrorLabel.gone()
            tvIncorrectCpf.gone()
            analyticsGA.logIDScreenView(ANALYTICS_ID_SCREEN_VIEW_VALIDATION_CPF_PUT_DATA)
            presenter.verifyCpfNameBlocked()
        }
    }

    override fun setupListeners() {
        setupNextButtonListener()
        setupFieldValidatorListener()
    }

    private fun setupNextButtonListener() {
        binding?.apply {
            btnNext.setOnClickListener {
                tvTypedValueErrorLabel?.gone()
                activity?.hideSoftKeyboard()

                etTypedValue.text?.toString()?.let {
                    userStatus.cpf = it.onlyDigits()
                }
                analyticsGA.logIDValidateNewCpfClick(
                    ANALYTICS_ID_SCREEN_VIEW_VALIDATION_CPF_PUT_DATA
                )
                analyticsGA.logIDValidateSignUp(
                    ANALYTICS_ID_SCREEN_VIEW_VALIDATION_CPF_PUT_DATA,
                    ANALYTICS_ID_VALIDATED_CPF
                )
                presenter.validateCpfName(userStatus.cpf.orEmpty(), userStatus.name.orEmpty())
            }
        }
    }

    private fun setupFieldValidatorListener() {
        binding?.apply {
        etTypedValue.let {
            EditTextHelper.cpfField(it)

            it.addTextChangedListener { editable ->
                if (editable.toString().length >= CPF_LENGTH) {
                    if (isValid(editable.toString())) {
                        btnNext.isEnabled = isEditing &&
                                editable.onlyDigits() != userStatus.cpf
                        tvTypedValueErrorLabel.gone()
                    } else {
                        btnNext.isEnabled = false

                        if (isEditing) {
                            showTypedValueErrorLabel()
                        }
                    }
                } else {
                    btnNext.isEnabled = false
                    tvTypedValueErrorLabel.gone()
                }

            }
        }
        }
    }

    override fun isValid(value: String?): Boolean {
        return value?.let{
            ValidationUtils.isCPF(value)
        } ?: false
    }

    override fun showErrorInvalidCPF() {
        navigation?.showCustomBottomSheet(
            image = R.drawable.img_aguardando_doc,
            title = getString(R.string.id_onboarding_update_cpf_invalid_cpf_bs_title),
            message = getString(R.string.id_onboarding_update_cpf_invalid_cpf_bs_message),
            bt1Title = getString(R.string.text_call_center_action),
            bt1Callback = {
                CallHelpCenterBottomSheet.newInstance().show(childFragmentManager,tag)
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
    }

    override fun showCpfAlreadyExists() {
        IDOnboardingCpfAlreadyUsedBS.onCreate(userStatus.cpf.orEmpty(), this)
            .show(childFragmentManager, IDOnboardingCpfAlreadyUsedBS::class.java.simpleName)
    }

    override fun successValidatingCpfName() {
        if (isAttached() && context != null)
            findNavController().navigate(
                R.id.action_idOnboardingNewCpfFragment_to_idOnboardingUpdateEmailFragment
            )
    }

    fun clearCpf() {
        userStatus.cpf = EMPTY
        binding?.etTypedValue?.setText(EMPTY)
        showTypedValueErrorLabel(EMPTY)
    }
}