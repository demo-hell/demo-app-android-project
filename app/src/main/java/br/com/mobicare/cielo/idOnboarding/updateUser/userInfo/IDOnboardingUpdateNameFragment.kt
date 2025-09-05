package br.com.mobicare.cielo.idOnboarding.updateUser.userInfo

import android.text.InputType
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.firstWordCapitalize
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.extensions.capitalizeWords
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.userStatus
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP1AnalyticsGA
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_FULL_NAME_VALIDATION
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_FULL_NAME_VALIDATION_PUT_DATA
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_VALIDATED_FULL_NAME
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_FULL_NAME_VALIDATION_SUCCESS
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointP1
import org.koin.android.ext.android.inject

class IDOnboardingUpdateNameFragment : IDOnboardingUpdateUserBaseFragment(),
    IDOnboardingUpdateUserContract.View {

    private val analyticsGA: IDOnboardingP1AnalyticsGA by inject()

    override fun onResume() {
        if (userStatus.onboardingStatus == null || IDOnboardingFlowHandler.checkpointP1 != IDOCheckpointP1.NONE) {
            baseLogout()
        }
        super.onResume()
    }

    override fun updateView() {
        binding?.apply {
            etTypedValue.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            etTypedValue.hint = getString(R.string.id_onboarding_update_name_hint)
        }

        setupView(
            title = getString(
                R.string.id_onboarding_update_name_title,
                firstWordCapitalize(userStatus.name, getString(R.string.hello))
            ),
            subtitle = getString(R.string.id_onboarding_update_name_subtitle),
            label = getString(R.string.id_onboarding_update_name_label),
            currentValue = userStatus.name,
            errorLabel = getString(R.string.id_onboarding_update_name_error)
        )
        analyticsGA.logIDScreenView(ANALYTICS_ID_SCREEN_VIEW_FULL_NAME_VALIDATION)
        presenter.verifyCpfNameBlocked()
    }

    override fun successValidatingCpfName() {
        analyticsGA.logIDScreenView(ANALYTICS_ID_SCREEN_VIEW_FULL_NAME_VALIDATION_SUCCESS)
        if (isAttached() && context != null)
            findNavController().navigate(
                R.id.action_idOnboardingUpdateNameFragment_to_idOnboardingUpdateEmailFragment
            )
    }

    override fun editValueOnclick() {
        analyticsGA.logIDEditNameClick(ANALYTICS_ID_SCREEN_VIEW_FULL_NAME_VALIDATION)
    }

    override fun setupListeners() {
        binding?.apply {
            btnNext.setOnClickListener {
                tvTypedValueErrorLabel.invisible()
                activity?.hideSoftKeyboard()

                analyticsGA.logIDValidateSignUp(
                    if (isEditing) ANALYTICS_ID_SCREEN_VIEW_FULL_NAME_VALIDATION
                    else ANALYTICS_ID_SCREEN_VIEW_FULL_NAME_VALIDATION_PUT_DATA,
                    ANALYTICS_ID_VALIDATED_FULL_NAME
                )

                val name = etTypedValue.text?.trim()?.capitalizeWords().orEmpty()
                presenter.validateCpfName(userStatus.cpf.orEmpty(), name)
            }

            etTypedValue.let {
                it.addTextChangedListener { editable ->
                    if (isValid(editable.toString())) {
                        btnNext.isEnabled = isEditing
                        tvTypedValueErrorLabel.gone()
                    } else {
                        if (btnNext.isEnabled) {
                            btnNext.isEnabled = false
                        }
                        if (isEditing) {
                            showTypedValueErrorLabel()
                        }
                    }
                }
            }
        }
    }

    override fun isValid(value: String?): Boolean {
        return value?.trim()?.let {
            val atLeastTwoNames = it.split(" ").size > 1

            it.length >= MIN_NAME_LENGTH && atLeastTwoNames
        } ?: false
    }

    override fun showErrorInvalidCPF() {
        navigation?.showCustomBottomSheet(
            image = R.drawable.img_aguardando_doc,
            title = getString(R.string.id_onboarding_update_cpf_invalid_cpf_bs_title),
            message = getString(R.string.id_onboarding_validate_p1_denied_bs_message),
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

    companion object {
        const val MIN_NAME_LENGTH = 3
    }
}