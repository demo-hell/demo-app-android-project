package br.com.mobicare.cielo.idOnboarding.updateUser.userInfo

import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputFilter
import android.text.InputType
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.SMS_OTP_AUTOFILL_CODE
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.commons.utils.spannable.toSpannableString
import br.com.mobicare.cielo.extensions.onlyDigits
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.userStatus
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP1AnalyticsGA
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_RESEND_CODE
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_VALIDATION_PHONE_CODE
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_VALIDATED_CODE_SMS
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_VALIDATED_CODE_WHATSAPP
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_VALIDATE_CODE
import br.com.mobicare.cielo.pix.constants.EMPTY
import org.koin.android.ext.android.inject

class IDOnboardingUpdatePhoneValidationFragment : IDOnboardingUpdateUserBaseFragment(),
    IDOnboardingUpdateUserContract.View {

    override val isEmptyValue: Boolean = true
    lateinit var resendTimer: CountDownTimer
    private val analyticsGA: IDOnboardingP1AnalyticsGA by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startResendTimer()
    }

    override fun updateView() {
        binding?.apply {
            etTypedValue.inputType = InputType.TYPE_CLASS_NUMBER
            etTypedValue.filters = arrayOf(InputFilter.LengthFilter(CODE_LENGTH))
            etTypedValue.setAutofillHints(SMS_OTP_AUTOFILL_CODE)
            etTypedValue.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_YES
        }

        if (isEmptyValue) {
            isEditing = true
        }

        val phone = userStatus.cellphone
        val phoneFormatted = phone?.getNumber()?.phone() ?: EMPTY

        setupView(
            title = getString(
                R.string.id_onboarding_update_phone_validation_title,
                firstWordCapitalize(userStatus.name, getString(R.string.hello)),
                if (userStatus.phoneTarget == IDOnboardingFlowHandler.WHATSAPP)
                    getString(R.string.whatsapp)
                else
                    IDOnboardingFlowHandler.SMS
            ),
            subtitle = getString(
                R.string.id_onboarding_update_phone_validation_subtitle,
                phoneFormatted
            ).toSpannableString(),
            errorLabel = getString(R.string.id_onboarding_update_phone_validation_error),
            editingNextButton = getString(R.string.validate_code)
        )

        binding?.btnAction?.visible()
        verifyIfUserBlocked()

        analyticsGA.logIDScreenView(ANALYTICS_ID_SCREEN_VIEW_VALIDATION_PHONE_CODE)
    }

    private fun verifyIfUserBlocked() {
        if (userStatus.p1Flow?.cellphoneValidation?.codeRequestsBlockedTimeRemainingAsText != null) {
            showErrorPhoneMaxTries()
        }
    }

    override fun successValidatingPhoneCode() {
        val stepValidateCode = if (userStatus.phoneTarget == IDOnboardingFlowHandler.WHATSAPP)
            ANALYTICS_ID_VALIDATED_CODE_WHATSAPP
        else
            ANALYTICS_ID_VALIDATED_CODE_SMS

        analyticsGA.logIDValidateSignUp(ANALYTICS_ID_SCREEN_VIEW_VALIDATION_PHONE_CODE,
            stepValidateCode)

        findNavController().navigate(
            IDOnboardingUpdatePhoneValidationFragmentDirections
                .actionIdOnboardingUpdatePhoneValidationFragmentToIdOnboardingValidateP1PolicyFragment()
        )
    }

    override fun successSendingPhoneCode() {
        binding?.apply {
            resendTimer.start()
            btnAction.isEnabled = false
            activity?.showSoftKeyboard(etTypedValue)
        }
    }

    override fun setupListeners() {
        binding?.apply {
            btnNext.setOnClickListener {
                tvTypedValueErrorLabel.gone()
                activity?.hideSoftKeyboard()
                analyticsGA.logIDValidateCodeClick(
                    ANALYTICS_ID_SCREEN_VIEW_VALIDATION_PHONE_CODE,
                    ANALYTICS_ID_VALIDATE_CODE
                )
                val code = etTypedValue.text?.onlyDigits()
                presenter.checkPhoneCode(code)
            }

            btnAction.setOnClickListener {
                etTypedValue.text?.clear()
                tvTypedValueErrorLabel.gone()
                activity?.hideSoftKeyboard()
                analyticsGA.logIDValidateCodeClick(
                    ANALYTICS_ID_SCREEN_VIEW_VALIDATION_PHONE_CODE,
                    ANALYTICS_ID_RESEND_CODE
                )

                val phone = userStatus.cellphone.orEmpty()
                presenter.requestPhoneCode(phone, userStatus.phoneTarget)
            }

            tvSubtitle.isClickable = true
            tvSubtitle.setOnClickListener {
                findNavController().navigate(
                    IDOnboardingUpdatePhoneValidationFragmentDirections
                        .actionIdOnboardingUpdatePhoneValidationFragmentToIdOnboardingUpdatePhoneFragment()
                )
            }

            etTypedValue.let {
                it.addTextChangedListener { editable ->
                    if (isValid(editable.toString())) {
                        btnNext.isEnabled = true
                        tvTypedValueErrorLabel.gone()
                    } else {
                        if (btnNext.isEnabled == true) {
                            btnNext.isEnabled = false
                        }
                        if (isEditing && editable?.isNotBlank() == true) {
                            showTypedValueErrorLabel()
                        }
                    }
                }
            }
        }
    }

    private fun startResendTimer() {
        if (::resendTimer.isInitialized) resendTimer.cancel()

        resendTimer = object : CountDownTimer(ONE_MINUTE_MILLIS, ONE_SECOND_MILLIS) {
            override fun onTick(millisUntilFinished: Long) {
                if (isAttached()) {
                    val time = getString(
                        R.string.int_x_dots_y,
                        millisUntilFinished.toMinutes(),
                        millisUntilFinished.toSeconds(),
                    )

                    binding?.btnAction?.text = getString(R.string.wait_x_before_resending, time)
                } else {
                    this.cancel()
                }
            }

            override fun onFinish() {
                binding?.apply {
                    btnAction.text = getString(R.string.resend_code)
                    btnAction.isEnabled = true
                }
            }
        }.start()

        binding?.btnAction?.isEnabled = false
    }

    override fun isValid(value: String?): Boolean {
        return value?.let {
            it.length == CODE_LENGTH
        } ?: false
    }

    override fun showErrorPhoneUnavailable() {
        navigation?.showCustomBottomSheet(
            image = R.drawable.img_celular_impedido,
            title = getString(R.string.id_onboarding_update_phone_unavailable_bs_title),
            message = getString(R.string.id_onboarding_update_phone_unavailable_bs_message),
            bt2Title = getString(R.string.id_onboarding_update_phone_unavailable_bs_button),
            bt2Callback = {
                false
            }
        )
    }

    override fun showErrorPhoneMaxTries() {
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
    }

    companion object {
        const val CODE_LENGTH = 6
    }
}