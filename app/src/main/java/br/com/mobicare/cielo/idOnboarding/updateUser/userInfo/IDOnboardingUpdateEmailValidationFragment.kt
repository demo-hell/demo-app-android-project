package br.com.mobicare.cielo.idOnboarding.updateUser.userInfo

import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputFilter
import android.text.InputType
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.commons.utils.spannable.toSpannableString
import br.com.mobicare.cielo.extensions.onlyDigits
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.userStatus
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP1AnalyticsGA
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_VALIDATION_EMAIL_TOKEN
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_VALIDATED_TOKEN
import org.koin.android.ext.android.inject

class IDOnboardingUpdateEmailValidationFragment: IDOnboardingUpdateUserBaseFragment(),
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

            if (isEmptyValue) {
                isEditing = true
            }

            val subTitle = if (userStatus.onboardingStatus?.userStatus?.foreign == true)
                R.string.id_onboarding_update_email_foreign_validation_subtitle
            else
                R.string.id_onboarding_update_email_validation_subtitle

            setupView(
                title = getString(
                    R.string.id_onboarding_update_email_validation_title,
                    firstWordCapitalize(userStatus.name, getString(R.string.hello))
                ),
                subtitle = getString(
                    subTitle,
                    userStatus.email ?: ""
                ).toSpannableString(),
                errorLabel = getString(R.string.id_onboarding_update_email_validation_error),
                editingNextButton = getString(R.string.validate_code)
            )

            btnAction.visible()
            verifyIfUserBlocked()
            analyticsGA.logIDScreenView(ANALYTICS_ID_SCREEN_VIEW_VALIDATION_EMAIL_TOKEN)
        }
    }

    private fun verifyIfUserBlocked() {
        if (userStatus.p1Flow?.emailValidation?.codeRequestsBlockedTimeRemainingAsText != null){
            showErrorEmailMaxTries()
        }
    }

    override fun successValidatingEmailCode() {
        analyticsGA.logIDScreenView(ANALYTICS_ID_SCREEN_VIEW_VALIDATION_EMAIL_TOKEN)
        if (userStatus.onboardingStatus?.userStatus?.foreign == true) {
            findNavController().navigate(
                IDOnboardingUpdateEmailValidationFragmentDirections
                    .actionIdOnboardingUpdateEmailValidationFragmentToIdOnboardingUpdateForeignPhoneFragment()
            )
        } else {
            findNavController().navigate(
                IDOnboardingUpdateEmailValidationFragmentDirections
                    .actionIdOnboardingUpdateEmailValidationFragmentToIdOnboardingUpdatePhoneFragment()
            )
        }
    }

    override fun successExecuteP1() {
        analyticsGA.logIDScreenView(ANALYTICS_ID_SCREEN_VIEW_VALIDATION_EMAIL_TOKEN)
        findNavController().navigate(
            IDOnboardingUpdateEmailValidationFragmentDirections
                .actionToIdOnboardingValidateP1PolicyFragment()
        )
    }

    override fun successSendingEmailCode() {
        binding?.apply {
            resendTimer.start()
            btnAction.isEnabled = false
            activity?.showSoftKeyboard(etTypedValue)
        }
    }

    override fun setupListeners() {
        binding?.apply {
            btnNext.setOnClickListener {
                tvTypedValueErrorLabel?.gone()
                activity?.hideSoftKeyboard()

                analyticsGA.logIDValidateSignUp(
                    ANALYTICS_ID_SCREEN_VIEW_VALIDATION_EMAIL_TOKEN,
                    ANALYTICS_ID_VALIDATED_TOKEN
                )
                val code = etTypedValue?.text?.onlyDigits()
                presenter.checkEmailCode(code)
            }

            btnAction.setOnClickListener {
                etTypedValue?.text?.clear()
                tvTypedValueErrorLabel?.gone()
                activity?.hideSoftKeyboard()

                val email = userStatus.email.orEmpty()
                presenter.requestEmailCode(email)
            }

            tvSubtitle.isClickable = userStatus.onboardingStatus?.userStatus?.foreign != true
            tvSubtitle.isEnabled = userStatus.onboardingStatus?.userStatus?.foreign != true
            tvSubtitle.setOnClickListener {
                findNavController().navigate(
                    IDOnboardingUpdateEmailValidationFragmentDirections
                        .actionIdOnboardingUpdateEmailValidationFragmentToIdOnboardingUpdateEmailFragment()
                )
            }

            etTypedValue.let {
                it.addTextChangedListener { editable ->
                    if (isValid(editable.toString())) {
                        btnNext.isEnabled = true
                        tvTypedValueErrorLabel.gone()
                    } else {
                        if (btnNext.isEnabled) {
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

        resendTimer = object: CountDownTimer(ONE_MINUTE_MILLIS, ONE_SECOND_MILLIS) {
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

    companion object {
        const val CODE_LENGTH = 6
    }
}