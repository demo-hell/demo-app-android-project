package br.com.mobicare.cielo.accessManager.invite.receive.ui.newUser.create.base

import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.AccessManagerRepository
import br.com.mobicare.cielo.accessManager.invite.receive.domain.InviteDetails
import br.com.mobicare.cielo.accessManager.invite.receive.domain.InviteErrors
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.selfRegistration.domains.AccountRegistrationPayLoadRequest
import br.com.mobicare.cielo.selfRegistration.domains.PayIdRequest
import br.com.mobicare.cielo.selfRegistration.domains.SelfRegistrationResponse
import br.com.mobicare.cielo.selfRegistration.register.SelfRegistrationRepository
import com.akamai.botman.CYFMonitor
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import java.net.ConnectException

class InviteReceivePresenter(
    private val accessManagerRepository: AccessManagerRepository,
    private val userCreateRepository: SelfRegistrationRepository,
    private val view: InviteReceiveContract.View,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : InviteReceiveContract.Presenter {

    private var disposable = CompositeDisposable()
    private var retryCallback: (() -> Unit)? = null

    fun createUser(
        password: String,
        inviteToken: String,
        invitedetailsargs: InviteDetails,
    ) {
        retryCallback = { createUser(password, inviteToken, invitedetailsargs) }

        val name = if (invitedetailsargs.foreignName.isNullOrEmpty()) "*" else invitedetailsargs.foreignName.orEmpty()
        val email = invitedetailsargs.email.ifEmpty { "*@*" }

        val request = AccountRegistrationPayLoadRequest(
            fullName = name,
            cpf = null,
            email = email,
            password = password,
            passwordConfirmation = password,
            pid = PayIdRequest(
                merchantId = "0",
                null,
                null
            )
        )

        userCreateRepository.registrationAccount(
            request,
            inviteToken = inviteToken,
            callback = object : APICallbackDefault<SelfRegistrationResponse, String> {
                override fun onStart() {
                    view.showLoading()
                }

                override fun onError(error: ErrorMessage) {
                    view.hideLoading()
                    showInviteError(error)
                }

                override fun onSuccess(response: SelfRegistrationResponse) {
                    view.hideLoading(
                        successMessage = R.string.loading_validating_infos_success,
                        loadingSuccessCallback = {
                            view.onUserCreatedSuccess()
                        }
                    )
                }
            }, akamaiSensorData = CYFMonitor.getSensorData()
        )
    }

    private fun showInviteError(error: ErrorMessage) {
        when (InviteErrors.valueOf(error)) {
            InviteErrors.PASSWORD -> view.onPasswordError(error)
            InviteErrors.INVITE_EXPIRED -> view.onInviteExpiredError(error)
            InviteErrors.INVALID_CPF, InviteErrors.ERROR_CODE_INVALID_CPF -> view.onInvalidCpf(error)
            InviteErrors.CPF_NAME_MAX_TRIES_EXCEEDED -> view.onCpfValidateMaxTriesExceeded(error)
            InviteErrors.ERROR_NOT_BOOTING -> view.onErrorNotBooting()
            InviteErrors.GENERIC -> view.onGenericError(error)
        }
    }

    override fun retry() {
        onResume()
        retryCallback?.invoke()
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }

    fun getPendingInvites() {
        retryCallback = { getPendingInvites() }

        view.showLoading()

        accessManagerRepository.getPendingInvites()
            .configureIoAndMainThread()
            .subscribe({ pendingInvitesResponse ->
                view.hideLoading()
                if (pendingInvitesResponse.summary.totalQuantity > 0) {
                    view.showPendingInvite(pendingInvitesResponse.items.first())
                }
            }, {
                view.hideLoading()
                if (it.cause is ConnectException) {
                    view.showConnectionError { retry() }
                } else {
                    view.showError(ErrorMessage.fromThrowable(it))
                }
            }).addTo(disposable)
    }

    fun getInviteData(inviteToken: String) {
        retryCallback = { getInviteData(inviteToken) }

        view.showLoading()

        accessManagerRepository.getInviteDetails(inviteToken)
            .configureIoAndMainThread()
            .subscribe({ inviteDetailsResponse ->
                view.hideLoading()
                inviteDetailsResponse?.run {
                    InviteDetails(userExists, legalEntity, companyName, cpf, email, role, foreign,
                        unauthenticatedAnswerMandatory).let {
                        view.onInviteDetails(it)
                    }
                }
            }, {
                view.hideLoading()
                if (it.cause is ConnectException) {
                    view.showConnectionError { retry() }
                } else {
                    showInviteError(ErrorMessage.fromThrowable(it))
                }
            }).addTo(disposable)
    }

    fun acceptInvite(inviteId: String) {
        retryCallback = { acceptInvite(inviteId) }

        view.showLoading()

        accessManagerRepository.acceptInvite(inviteId)
            .configureIoAndMainThread()
            .subscribe({
                view.hideLoading()
                if (it.isSuccessful) {
                    view.onInviteAcceptSuccess()
                } else {
                    view.showError(APIUtils.convertToErro(it))
                }
            }, {
                view.hideLoading()
                if (it.cause is ConnectException) {
                    view.showConnectionError { retry() }
                } else {
                    view.showError(ErrorMessage.fromThrowable(it))
                }
            }).addTo(disposable)
    }

    fun acceptInviteToken(inviteToken: String){
        retryCallback = { getInviteData(inviteToken) }

        view.showLoading()

        disposable.add(
            accessManagerRepository.acceptInviteToken(inviteToken)
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    view.hideLoading()
                    if (it.isSuccessful) {
                        view.onAcceptInviteTokenSuccess()
                    } else {
                        view.onShowGenericError()
                    }
                }, {
                    view.hideLoading()
                    view.onShowGenericError()
                })
        )
    }

    fun declineInviteToken(inviteToken: String){
        retryCallback = { getInviteData(inviteToken) }

        view.showLoading()

        disposable.add(
            accessManagerRepository.declineInviteToken(inviteToken)
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    view.hideLoading()
                    if (it.isSuccessful) {
                        view.onDeclineInviteTokenSuccess()
                    } else {
                        view.onShowGenericError()
                    }
                }, {
                    view.hideLoading()
                    view.onShowGenericError()
                })
        )
    }
}
