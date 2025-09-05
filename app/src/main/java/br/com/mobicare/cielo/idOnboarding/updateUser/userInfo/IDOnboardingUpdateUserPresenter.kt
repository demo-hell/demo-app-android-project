package br.com.mobicare.cielo.idOnboarding.updateUser.userInfo

import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ERROR_500
import br.com.mobicare.cielo.commons.constants.ERROR_CODE_MAIL_DOMAIN_NOT_ALLOWED
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.captureEmailDomain
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.extensions.orZero
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.SMS
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.WHATSAPP
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.userStatus
import br.com.mobicare.cielo.idOnboarding.IDOnboardingRepository
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointP1
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import java.util.*

class IDOnboardingUpdateUserPresenter(
    private val repository: IDOnboardingRepository
) : IDOnboardingUpdateUserContract.Presenter {

    lateinit var view: IDOnboardingUpdateUserContract.View

    private var retryCallback: (() -> Unit)? = null
    private var disposable = CompositeDisposable()

    private fun updateStatusAndThen(callback: () -> Unit) {
        repository.getIdOnboardingStatus()
            .configureIoAndMainThread()
            .doFinally {
                callback.invoke()
            }
            .subscribe { onboardingStatus ->
                userStatus.onboardingStatus = onboardingStatus
            }.addTo(disposable)
    }

    //region CPF/Name
    fun validateCpfName(cpf: String, name: String) {
        view.showLoading(R.string.loading_validating_infos)

        onResume()
        retryCallback = { validateCpfName(cpf, name) }

        repository.validateCpfName(cpf, name)
            .configureIoAndMainThread()
            .subscribe({
                if (it.p1Flow?.cpfValidation?.validated == true) {
                    userStatus.onboardingStatus = it
                    userStatus.cpf = cpf
                    userStatus.name = name

                    view.hideLoading(
                        successMessage = R.string.loading_validating_infos_success,
                        loadingSuccessCallback = {
                            view.successValidatingCpfName()
                        }
                    )
                } else {
                    view.hideLoading()
                }
            }, {
                val error = ErrorMessage.fromThrowable(it)
                when (error.errorCode) {
                    ERROR_CODE_CPF_NAME_MAX_TRIES_EXCEEDED, ERROR_CODE_IRREGULAR_CPF -> {
                        updateStatusAndThen {
                            view.hideLoading()
                            showErrorCpfName(error)
                        }
                    }
                    ACCOUNT_WITH_CPF_EXISTS -> {
                        view.hideLoading()
                        view.showCpfAlreadyExists()
                    }
                    else -> {
                        view.hideLoading()
                        showErrorCpfName(error)
                    }
                }
            }).addTo(disposable)
    }

    fun verifyCpfNameBlocked() {
        userStatus.p1Flow?.cpfValidation?.error?.let {
            if (it.code != null) {
                showErrorCpfName(
                    ErrorMessage().apply {
                        errorCode = it.code
                        it.message?.let { itMessage ->
                            errorMessage = itMessage
                        }
                    },
                    isLiveResponse = false
                )
            }
        }
    }

    private fun showErrorCpfName(error: ErrorMessage, isLiveResponse: Boolean = true) {
        val countTries = userStatus.onboardingStatus?.p1Flow?.cpfValidation?.countTries
        val maxTries = userStatus.onboardingStatus?.p1Flow?.cpfValidation?.maxTries

        when (error.errorCode) {
            ERROR_CODE_INVALID_CPF -> view.showErrorInvalidCPF()
            ERROR_CODE_IRREGULAR_CPF ->
                if (countTries != null && maxTries != null && countTries >= maxTries)
                    view.showErrorBlockedIrregularCPF()
                else
                    view.showErrorIrregularCPF()
            ERROR_CODE_CPF_NAME_MAX_TRIES_EXCEEDED ->
                if (isLiveResponse || userStatus.p1Flow?.cpfValidation?.validationBlockedTimeRemainingInMinutes.orZero > 0) {
                    view.showErrorNameMaxTries()
                }
            else -> if (isLiveResponse) showErrorOrGeneric(error)
        }
    }
    //endregion CPF/Name

    //region Email
    fun requestEmailCode(email: String) {
        view.showLoading(R.string.loading_sending_email)

        onResume()
        retryCallback = { requestEmailCode(email) }

        repository.requestEmailCode(email)
            .configureIoAndMainThread()
            .subscribe({
                IDOnboardingFlowHandler.checkpointP1 = IDOCheckpointP1.EMAIL_VALIDATION_STARTED
                userStatus.email = email

                view.hideLoading(
                    successMessage = R.string.loading_sending_email_success,
                    loadingSuccessCallback = {
                        view.successSendingEmailCode()
                    }
                )
            }, {
                processError(ErrorMessage.fromThrowable(it), email) {
                    view.hideLoading()
                    view.showErrorEmailUnavailable()
                }
            }).addTo(disposable)
    }

    fun checkEmailCode(code: String?) {
        view.showLoading(R.string.loading_validating_code)

        onResume()
        retryCallback = { checkEmailCode(code) }

        repository.checkEmailCode(code)
            .configureIoAndMainThread()
            .subscribe({
                userStatus.onboardingStatus = it

                view.hideLoading(
                    successMessage = R.string.loading_validating_code_success,
                    loadingSuccessCallback = {
                        when (IDOCheckpointP1.fromCode(it.p1Flow?.p1CheckpointCode)) {
                            IDOCheckpointP1.CELLPHONE_VALIDATION_CONFIRM ->
                                view.successExecuteP1()
                            else ->
                                view.successValidatingEmailCode()
                        }
                    }
                )
            }, {
                processError(ErrorMessage.fromThrowable(it)) {
                    view.hideLoading()
                    view.showErrorEmailUnavailable()
                }
            }).addTo(disposable)
    }
    //endregion Email

    //region Phone
    fun requestPhoneCode(phoneNumber: String?, target: String? = SMS) {
        view.showLoading(
            if (target == WHATSAPP) R.string.loading_sending_whatsapp
            else R.string.loading_sending_sms
        )

        onResume()
        retryCallback = { requestPhoneCode(phoneNumber, target) }

        repository.requestPhoneCode(phoneNumber, target ?: SMS)
            .configureIoAndMainThread()
            .subscribe({
                IDOnboardingFlowHandler.checkpointP1 = IDOCheckpointP1.CELLPHONE_VALIDATION_STARTED
                userStatus.phoneTarget = target

                view.hideLoading(
                    successMessage = if (target == WHATSAPP) R.string.loading_sending_whatsapp_success
                    else R.string.loading_sending_sms_success,
                    loadingSuccessCallback = {
                        view.successSendingPhoneCode()
                    }
                )
            }, {
                processError(ErrorMessage.fromThrowable(it)) {
                    view.hideLoading()
                    view.showErrorPhoneUnavailable()
                }
            }).addTo(disposable)
    }

    fun checkPhoneCode(code: String?) {
        view.showLoading(R.string.loading_validating_code)

        onResume()
        retryCallback = { checkPhoneCode(code) }

        repository.checkPhoneCode(code)
            .configureIoAndMainThread()
            .subscribe({
                userStatus.onboardingStatus = it

                view.hideLoading(
                    successMessage = R.string.loading_validating_code_success,
                    loadingSuccessCallback = {
                        view.successValidatingPhoneCode()
                    }
                )
            }, {
                processError(ErrorMessage.fromThrowable(it)) {
                    view.hideLoading()
                    view.showErrorPhoneUnavailable()
                }
            }).addTo(disposable)
    }
    //endregion Phone
    fun getCustomerSettings() {
        view.showLoading()
        retryCallback = { getCustomerSettings() }
        repository.getIdOnboardingCustomerSettings()
            .configureIoAndMainThread()
            .doFinally { view.hideLoading() }
            .subscribe({ customerSettings ->
                if (customerSettings.allowedCellphoneValidationChannels.isNullOrEmpty()){
                    view.showErrorTryAgain()
                }else{
                    val enableSms = Arrays.stream(customerSettings.allowedCellphoneValidationChannels)
                        .anyMatch { t -> t == SMS }
                    val enableWhatsApp = Arrays.stream(customerSettings.allowedCellphoneValidationChannels)
                        .anyMatch { t -> t == WHATSAPP }

                    view.successShowButtons(enableSms, enableWhatsApp)
                }
            }, {
                 view.hideLoading()
                 view.showErrorTryAgain()
            }).addTo(disposable)
    }

    private fun processError(error: ErrorMessage, email: String? = null, action: (() -> Unit)) {
        when (error.errorCode) {
            ERROR_EMAIL_VALIDATION_CODE_NO_MORE_TRIES,
            ERROR_EMAIL_VALIDATION_CODE_EXPIRED,
            ERROR_EMAIL_VALIDATION_CODE_WRONG,
            MAX_GENERATE_CODE_EXCEEDED -> updateStatusAndThen {
                view.hideLoading()
                view.showErrorLabel(error)
            }
            CONTACT_ALREADY_EXISTS -> action.invoke()
            ERROR_CODE_MAIL_DOMAIN_NOT_ALLOWED -> {
                view.hideLoading()
                view.showErrorEmailDomainRestricted(captureEmailDomain(email))
            }
            else -> showErrorOrGeneric(error)
        }
    }

    override fun retry() {
        retryCallback?.invoke()
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }

    private fun showErrorOrGeneric(error: ErrorMessage) {
        view.hideLoading()
        if (error.code == ERROR_500)
            view.showErrorGeneric()
        else
            view.showError(error)
    }

    companion object ErrorCode {
        const val ERROR_CODE_INVALID_CPF = "INVALID_CPF"
        const val ERROR_CODE_CPF_NAME_MAX_TRIES_EXCEEDED = "CPF_NAME_MAX_TRIES_EXCEEDED"
        const val ACCOUNT_WITH_CPF_EXISTS = "ACCOUNT_WITH_CPF_EXISTS"
        const val ERROR_CODE_IRREGULAR_CPF = "IRREGULAR_CPF"

        const val ERROR_EMAIL_VALIDATION_CODE_NO_MORE_TRIES =
            "ONBOARDING_VALIDATION_CODE_NO_MORE_TRIES"
        const val ERROR_EMAIL_VALIDATION_CODE_EXPIRED = "ONBOARDING_VALIDATION_CODE_EXPIRED"
        const val ERROR_EMAIL_VALIDATION_CODE_WRONG = "ONBOARDING_VALIDATION_CODE_WRONG"

        const val MAX_GENERATE_CODE_EXCEEDED = "MAX_GENERATE_CODE_EXCEEDED"
        const val CONTACT_ALREADY_EXISTS = "CONTACT_ALREADY_EXISTS"
    }
}