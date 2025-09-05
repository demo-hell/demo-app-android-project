package br.com.mobicare.cielo.accessManager.addUser

import br.com.mobicare.cielo.accessManager.AccessManagerRepository
import br.com.mobicare.cielo.accessManager.addUser.model.AccessManagerSendInviteRequest
import br.com.mobicare.cielo.changeEc.domain.Hierarchy
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.TWENTY_FIVE
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.pix.constants.EMPTY

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class AccessManagerAddUserEstablishmentPresenter(
    private val mRepository: AccessManagerRepository,
    private var mView: AccessManagerAddUserEstablishmentContract.View,
    private val userPreferences: UserPreferences
) : AccessManagerAddUserEstablishmentContract.Presenter {

    private var mMerchants: Array<Hierarchy>? = null
    private var disposable = CompositeDisposable()
    private var retryCallback: (() -> Unit)? = null

    override fun getUsername(): String = userPreferences.userName

    override fun loadItens() {
        this.mView.showLoading()
        mRepository.children(
            UserPreferences.getInstance().token,
            TWENTY_FIVE, ONE, null
        )
            .configureIoAndMainThread()
            .subscribe({ response ->
                val merchantList = response.hierarchies

                this@AccessManagerAddUserEstablishmentPresenter.mMerchants = merchantList
                this@AccessManagerAddUserEstablishmentPresenter.mView.hideLoading()
                this@AccessManagerAddUserEstablishmentPresenter.mView.showMerchants(merchantList)
            }, {
                val errorMessage = APIUtils.convertToErro(it)
                this@AccessManagerAddUserEstablishmentPresenter.mView.showError(errorMessage)
            }).addTo(disposable)
    }

    override fun sendInvitation(
        cpfInviteRequest: String?,
        emailInviteRequest: String,
        roleInviteRequest: String,
        foreignInviteRequest: Boolean,
        countryCodeInviteRequest: String,
        otp: String
    ) {
        retryCallback = {
            mRepository.sendInvite(
                otp, AccessManagerSendInviteRequest(
                    cpf = cpfInviteRequest,
                    email = emailInviteRequest,
                    role = roleInviteRequest,
                    foreign = foreignInviteRequest,
                    countryCode = countryCodeInviteRequest
                )
            )
        }

        mView.showLoading()
        mRepository.sendInvite(
            otp, AccessManagerSendInviteRequest(
                cpf = cpfInviteRequest,
                email = emailInviteRequest,
                role = roleInviteRequest,
                foreign = foreignInviteRequest,
                countryCode = countryCodeInviteRequest
            )
        )
            .configureIoAndMainThread()
            .subscribe({
                mView.hideLoading()
                if (it.isSuccessful) {
                    mView.showSuccess(it)
                } else {
                    val error = APIUtils.convertToErro(it)
                    if (error.errorCode.contains(
                            Text.OTP
                        )
                    ) {
                        mView.onErrorOTP()
                    } else {
                        mView.showError(APIUtils.convertToErro(it)) { retry() }
                    }
                }
            }, {
                mView.hideLoading()
                mView.showError(ErrorMessage.fromThrowable(it)) { retry() }
            }).addTo(disposable)

    }

    override fun getCustomerSettings() {
        mView.showLoading()
        retryCallback = { getCustomerSettings() }
        mRepository.getIdOnboardingCustomerSettings()
            .configureIoAndMainThread()
            .doFinally { mView.hideLoading() }
            .subscribe({ customerSettings ->
                mView.hideLoading()
                customerSettings.foreignFlowAllowed?.let { mView.addUserForeignFlowAllowed(it) }
            }, {
                mView.hideLoading()
                mView.showError(ErrorMessage.fromThrowable(it)) { retry() }
            }).addTo(disposable)
    }

    override fun getRootCNPJ(): String {
        return userPreferences.userInformation?.merchant?.cnpj?.number ?: EMPTY
    }


    override fun setView(view: AccessManagerAddUserEstablishmentContract.View) {
        mView = view
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
}