package br.com.mobicare.cielo.idOnboarding.updateUser.userInfo

import android.text.InputType
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.firstWordCapitalize
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.commons.utils.spannable.toSpannableString
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.checkpointP1
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.userStatus
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP1AnalyticsGA
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_VALIDATION_EMAIL
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_VALIDATION_EMAIL_PUT_DATA
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_VALIDATION_EMAIL_SUCCESS
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_VALIDATED_EMAIL
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointP1
import org.koin.android.ext.android.inject

class IDOnboardingUpdateEmailFragment: IDOnboardingUpdateUserBaseFragment(),
    IDOnboardingUpdateUserContract.View {

    override val isEmptyValue: Boolean = userStatus.email.isNullOrEmpty()
    override val isReviewEditing: Boolean = checkpointP1 == IDOCheckpointP1.EMAIL_VALIDATION_STARTED

    private val analyticsGA: IDOnboardingP1AnalyticsGA by inject()
    private var analyticsScreenNameGA: String = ANALYTICS_ID_SCREEN_VIEW_VALIDATION_EMAIL_PUT_DATA
    override fun updateView() {
        binding?.apply {
            etTypedValue.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            etTypedValue.hint = getString(R.string.id_onboarding_update_email_hint)

            if (isEmptyValue) {
                isEditing = true
            }

            setupView(
                title = getString(
                    R.string.id_onboarding_update_email_title,
                    firstWordCapitalize(userStatus.name, getString(R.string.hello))
                ),
                emptyValueTitle = getString(
                    R.string.id_onboarding_update_email_empty_title,
                    firstWordCapitalize(userStatus.name, getString(R.string.hello))
                ),
                subtitle = getString(R.string.id_onboarding_update_email_subtitle),
                editingSubtitle = getString(R.string.id_onboarding_update_email_edit_subtitle),
                emptyValueSubtitle = getString(R.string.id_onboarding_update_email_empty_subtitle),
                currentValue = userStatus.email,
                errorLabel = getString(R.string.id_onboarding_update_email_error),
                textAlertMessage = getString(
                    R.string.id_onboarding_update_email_alert_message
                ).toSpannableString(),
                nextButton = getString(
                    R.string.id_onboarding_update_email_confirm_email
                ),
                editingNextButton = getString(R.string.id_onboarding_update_email_confirm)
            )

            if (userStatus.onboardingStatus?.userStatus?.foreign == true) {
                etTypedValue.isEnabled = false
                btEditValue.gone()
            }

            if (btEditValue.visibility == View.VISIBLE) {
                analyticsScreenNameGA = ANALYTICS_ID_SCREEN_VIEW_VALIDATION_EMAIL
            }

            verifyIfUserBlocked()
            analyticsGA.logIDScreenView(analyticsScreenNameGA)
        }
    }

    private fun verifyIfUserBlocked() {
        userStatus.p1Flow?.emailValidation?.codeRequestsBlockedTimeRemainingAsText?.let {
            showErrorEmailMaxTries()
        }
    }

    override fun successSendingEmailCode() {
        analyticsGA.logIDScreenView(ANALYTICS_ID_SCREEN_VIEW_VALIDATION_EMAIL_SUCCESS)
        findNavController().navigate(
            IDOnboardingUpdateEmailFragmentDirections
                .actionIdOnboardingUpdateEmailFragmentToIdOnboardingUpdateEmailValidationFragment()
        )
    }

    override fun editValueOnclick() {
        analyticsGA.logIDEditEmailClick(analyticsScreenNameGA)
    }

    override fun setupListeners() {
        binding?.apply {
            btnNext.setOnClickListener {
                tvTypedValueErrorLabel?.gone()
                activity?.hideSoftKeyboard()

                analyticsGA.logIDValidateSignUp(
                    analyticsScreenNameGA,
                    ANALYTICS_ID_VALIDATED_EMAIL
                )

                val email = etTypedValue.text?.trim()?.toLowerCasePTBR().orEmpty()
                presenter.requestEmailCode(email)
            }

            etTypedValue.let {
                it.addTextChangedListener { editable ->
                    if (isValid(editable.toString())) {
                        btnNext.isEnabled = isEmptyValue || isEditing
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
        }
    }

    override fun isValid(value: String?): Boolean {
        return value?.let {
            it.length >= MIN_EMAIL_LENGTH && ValidationUtils.isEmail(value)
        } ?: false
    }

    override fun showErrorEmailUnavailable() {
        navigation?.showCustomBottomSheet(
                image = R.drawable.img_light_email_error,
                title = getString(R.string.id_onboarding_update_email_unavailable_bs_title),
                message = getString(R.string.id_onboarding_update_email_unavailable_bs_message),
                bt2Title = getString(R.string.id_onboarding_update_email_unavailable_bs_button),
                bt2Callback = {
                    binding?.tvTypedValueErrorLabel?.visible()
                    false
                }
        )
    }

    override fun showErrorEmailMaxTries() {
        val canPostpone = IDOnboardingFlowHandler.canPostponeOnboarding
        navigation?.showCustomBottomSheet(
                image = R.drawable.ic_generic_error_image,
                title = getString(R.string.id_onboarding_update_email_max_tries_bs_title),
                message = getString(
                        R.string.id_onboarding_update_email_max_tries_bs_message,
                    userStatus.p1Flow?.emailValidation?.codeRequestsBlockedTimeRemainingAsText
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
    }

    override fun showErrorEmailDomainRestricted(emailDomain: String) {
        val message =
            getString(R.string.access_manager_add_user_error_email_restricted, emailDomain)
        navigation?.showCustomBottomSheet(
            image = R.drawable.img_light_email_error,
            title = getString(R.string.id_onboarding_update_email_domain_restricted_bs_title),
            message = message,
            bt2Title = getString(R.string.id_onboarding_update_email_unavailable_bs_button),
            bt2Callback = {
                binding?.apply {
                    tvTypedValueErrorLabel.text = message
                    tvTypedValueErrorLabel.visible()
                }
                    false
            }
        )
    }

    companion object {
        const val MIN_EMAIL_LENGTH = 6
    }
}