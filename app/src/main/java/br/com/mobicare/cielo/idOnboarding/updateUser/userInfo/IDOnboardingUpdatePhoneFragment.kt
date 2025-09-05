package br.com.mobicare.cielo.idOnboarding.updateUser.userInfo

import android.text.InputType
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.helpers.EditTextHelper
import br.com.mobicare.cielo.commons.utils.PHONE_MASK_FORMAT
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.firstWordCapitalize
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.commons.utils.spannable.toSpannableString
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.orZero
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.userStatus
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP1AnalyticsGA
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_RECEIVE_BY_SMS
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_RECEIVE_BY_WHATSAPP
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_VALIDATION_PHONE
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_VALIDATION_PHONE_PUT_DATA
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_VALIDATION_PHONE_PUT_DATA_SEND
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_VALIDATION_PHONE_SEND
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_VALIDATION_PHONE_SUCCESS
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_VALIDATED_PHONE
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointP1
import br.com.mobicare.cielo.pix.constants.EMPTY
import org.koin.android.ext.android.inject

class IDOnboardingUpdatePhoneFragment : IDOnboardingUpdateUserBaseFragment(),
    IDOnboardingUpdateUserContract.View {

    override val isEmptyValue: Boolean = userStatus.cellphone.isNullOrEmpty()
    override val isReviewEditing: Boolean =
        IDOnboardingFlowHandler.checkpointP1 == IDOCheckpointP1.CELLPHONE_VALIDATION_STARTED
    var isCustomerSettings = false
    var sendTarget = IDOnboardingFlowHandler.WHATSAPP
    private val analyticsGA: IDOnboardingP1AnalyticsGA by inject()
    private var analyticsScreenNameGA: String = EMPTY

    override fun updateView() {
        binding?.apply {
            etTypedValue.inputType = InputType.TYPE_CLASS_PHONE
            etTypedValue.hint = getString(R.string.id_onboarding_update_phone_hint)
        }

        if (isEmptyValue) {
            isEditing = true
        }

        setupView(
            title = getString(
                R.string.id_onboarding_update_phone_title,
                firstWordCapitalize(userStatus.name, getString(R.string.hello))
            ),
            emptyValueTitle = getString(
                R.string.id_onboarding_update_phone_empty_title,
                firstWordCapitalize(userStatus.name, getString(R.string.hello))
            ),
            subtitle = getString(R.string.id_onboarding_update_phone_subtitle),
            editingSubtitle = getString(R.string.id_onboarding_update_phone_editing_subtitle),
            emptyValueSubtitle = getString(R.string.id_onboarding_update_phone_empty_subtitle),
            textAlertMessage = getString(
                R.string.id_onboarding_update_phone_alert_message
            ).toSpannableString(),
            currentValue = userStatus.cellphone,
            errorLabel = getString(R.string.id_onboarding_update_phone_error),
        )

        if (FeatureTogglePreference.instance
                .getFeatureTogle(FeatureTogglePreference.ONBOARDING_CUSTOMER_SETTINGS)
        ) {
            isCustomerSettings = true
            presenter.getCustomerSettings()
        } else {

            val isSmsEnabled = FeatureTogglePreference.instance
                .getFeatureTogle(FeatureTogglePreference.ONBOARDING_SEND_SMS)
            val isWhatsAppEnabled = FeatureTogglePreference.instance
                .getFeatureTogle(FeatureTogglePreference.ONBOARDING_SEND_WHATSAPP)

            successShowButtons(isSmsEnabled, isWhatsAppEnabled)
        }
    }

    private fun verifyIfUserBlocked() {
        if (userStatus.p1Flow?.cellphoneValidation?.codeRequestsBlockedTimeRemainingInMinutes.orZero > 0) {
            showErrorPhoneMaxTries()
        }
    }

    override fun successSendingPhoneCode() {
        analyticsGA.logIDScreenView(ANALYTICS_ID_SCREEN_VIEW_VALIDATION_PHONE_SUCCESS)
        doWhenResumed(
            action = {
                findNavController().navigate(
                    IDOnboardingUpdatePhoneFragmentDirections
                        .actionIdOnboardingUpdatePhoneFragmentToIDOnboardingUpdatePhoneValidationFragment()
                )
            },
            errorCallback = { baseLogout() }
        )
    }

    override fun successShowButtons(isSmsEnabled: Boolean, isWhatsAppEnabled: Boolean) {
        binding?.apply {
            if (isSmsEnabled && isWhatsAppEnabled) {
                isCustomerSettings = false
                analyticsGA.logIDScreenView(ANALYTICS_ID_SCREEN_VIEW_VALIDATION_PHONE_SEND)
            }

            if (isCustomerSettings) {
                btnAction.gone()
                btnNext.visible()

                var labelBtn = R.string.id_onboarding_update_phone_btn_whatsapp
                if (isSmsEnabled) {
                    labelBtn = R.string.id_onboarding_update_phone_btn_sms
                    sendTarget = IDOnboardingFlowHandler.SMS
                }
                btnNext.text = getString(labelBtn)
            } else {
                btnAction.visible(isSmsEnabled)
                btnNext.visible(isWhatsAppEnabled)

                btnAction.text = getString(R.string.id_onboarding_update_phone_btn_sms)
                btnNext.text = getString(R.string.id_onboarding_update_phone_btn_whatsapp)
            }
            verifyIfUserBlocked()
        }
    }

    override fun editValueOnclick() {
        analyticsGA.logIDScreenView(ANALYTICS_ID_SCREEN_VIEW_VALIDATION_PHONE)
        analyticsGA.logIDEditPhoneClick(ANALYTICS_ID_SCREEN_VIEW_VALIDATION_PHONE)
    }

    override fun setupListeners() {
        binding?.apply {
            btnAction.setOnClickListener {
                tvTypedValueErrorLabel.gone()
                activity?.hideSoftKeyboard()

                if (isEditing) {
                    analyticsScreenNameGA = ANALYTICS_ID_SCREEN_VIEW_VALIDATION_PHONE_PUT_DATA_SEND
                } else {
                    analyticsScreenNameGA = ANALYTICS_ID_SCREEN_VIEW_VALIDATION_PHONE_SEND
                }

                analyticsGA.logIDScreenView(analyticsScreenNameGA)
                analyticsGA.logIDValidateSignUp(
                    analyticsScreenNameGA,
                    ANALYTICS_ID_RECEIVE_BY_SMS
                )
                val phone = etTypedValue.text?.toString()?.trim().orEmpty()
                presenter.requestPhoneCode(phone, IDOnboardingFlowHandler.SMS)
            }

            btnNext.setOnClickListener {
                tvTypedValueErrorLabel.gone()
                activity?.hideSoftKeyboard()

                var stepSignUp = ANALYTICS_ID_VALIDATED_PHONE

                if (btnNext.text.equals(getString(R.string.id_onboarding_update_phone_btn_whatsapp))) {
                    stepSignUp = ANALYTICS_ID_RECEIVE_BY_WHATSAPP

                    if (isEditing) {
                        analyticsScreenNameGA =
                            ANALYTICS_ID_SCREEN_VIEW_VALIDATION_PHONE_PUT_DATA_SEND
                    } else {
                        analyticsScreenNameGA = ANALYTICS_ID_SCREEN_VIEW_VALIDATION_PHONE_SEND
                    }
                } else {
                    analyticsScreenNameGA = ANALYTICS_ID_SCREEN_VIEW_VALIDATION_PHONE_PUT_DATA
                }

                analyticsGA.logIDScreenView(analyticsScreenNameGA)
                analyticsGA.logIDValidateSignUp(analyticsScreenNameGA, stepSignUp)

                val phone = etTypedValue.text?.toString()?.trim().orEmpty()
                presenter.requestPhoneCode(phone, sendTarget)
            }

            etTypedValue.let {
                EditTextHelper.phoneField(editTextField = it, phoneMask = PHONE_MASK_FORMAT)

                it.addTextChangedListener { editable ->
                    if (isValid(editable.toString())) {
                        val enabled = isEmptyValue || (isEditing && editable.toString() !=
                                userStatus.cellphone)
                        btnAction.isEnabled = enabled && editable?.length == CELLPHONE_LENGTH
                        tvTypedValueErrorLabel.gone()
                        btnNext.isEnabled = enabled
                    } else {
                        if (btnNext.isEnabled) {
                            btnNext.isEnabled = false
                            btnAction.isEnabled = false
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
        return value?.let {
            it.length >= MIN_PHONE_LENGTH && ValidationUtils.isValidPhoneNumber(value) && value != userStatus.cellphone
        } ?: false
    }

    override fun showErrorPhoneUnavailable() {
        doWhenResumed(
            action = {
                navigation?.showCustomBottomSheet(
                    image = R.drawable.img_celular_impedido,
                    title = getString(R.string.id_onboarding_update_phone_unavailable_bs_title),
                    message = getString(R.string.id_onboarding_update_phone_unavailable_bs_message),
                    bt2Title = getString(R.string.id_onboarding_update_phone_unavailable_bs_button),
                    bt2Callback = {
                        false
                    }
                )
            },
            errorCallback = { baseLogout() }
        )
    }

    override fun showErrorPhoneMaxTries() {
        doWhenResumed(
            action = {
                val canPostpone = IDOnboardingFlowHandler.canPostponeOnboarding
                navigation?.showCustomBottomSheet(
                    image = R.drawable.ic_generic_error_image,
                    title = getString(R.string.id_onboarding_update_phone_max_tries_bs_title),
                    message = getString(
                        R.string.id_onboarding_update_phone_max_tries_bs_message,
                        userStatus.p1Flow?.cellphoneValidation?.codeRequestsBlockedTimeRemainingAsText
                            ?: getString(R.string.twenty_four_hours)
                    ),
                    bt2Title = getString(R.string.entendi),
                    bt2Callback = {
                        if (canPostpone)
                            goToHome()
                        else
                            baseLogout()

                        false
                    },
                    closeCallback = {
                        if (canPostpone)
                            goToHome()
                        else
                            baseLogout()
                    }
                )
            },
            errorCallback = { baseLogout() }
        )
    }

    override fun showErrorTryAgain() {
        doWhenResumed(
            action = {
                navigation?.showCustomBottomSheet(
                    image = R.drawable.ic_generic_error_image,
                    title = getString(R.string.id_onboarding_bs_title_error_generic),
                    message = getString(R.string.id_onboarding_bs_message_error_generic),
                    bt2Title = getString(R.string.text_button_try_again),
                    bt2Callback = {
                        goBackPressed()
                        false
                    },
                    closeCallback = {
                        goBackPressed()
                    }
                )
            },
            errorCallback = { goBackPressed() }
        )
    }

    companion object {
        const val MIN_PHONE_LENGTH = 14
        const val CELLPHONE_LENGTH = 15
    }
}