package br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.term

import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.component.requiredDataField.data.model.request.*
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.OfferResponse
import br.com.mobicare.cielo.component.requiredDataField.presentation.model.UiRequiredDataField
import br.com.mobicare.cielo.component.requiredDataField.utils.RequiredDataFieldConstants.REQUIRED_DATA_FIELD_ORDER_TYPE
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.POS_VIRTUAL_REQUIRED_DATA_FIELD
import br.com.mobicare.cielo.tapOnPhone.constants.REQUIRED_FIELDS
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneAccount
import br.com.mobicare.cielo.tapOnPhone.domain.repository.TapOnPhoneAccreditationRepository
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.Offer as OfferRes

class TapOnPhoneTermAndConditionPresenter(
    private val view: TapOnPhoneTermAndConditionContract.View,
    private val repository: TapOnPhoneAccreditationRepository,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    private val featureTogglePreference: FeatureTogglePreference
) : TapOnPhoneTermAndConditionContract.Presenter {

    private var disposable = CompositeDisposable()

    private val isEnabledRequiredDataField
        get() = featureTogglePreference.getFeatureTogle(
            POS_VIRTUAL_REQUIRED_DATA_FIELD
        )

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun requestAccreditation(
        account: TapOnPhoneAccount,
        offer: OfferResponse,
        sessionId: String
    ) {
        if (verifyOpenRequiredDataField(offer) && isEnabledRequiredDataField) {
            openRequiredDataField(account, offer, sessionId)
        } else {
            postAccreditation(account, offer, sessionId)
        }
    }

    private fun openRequiredDataField(
        account: TapOnPhoneAccount,
        offer: OfferResponse,
        sessionId: String
    ) {
        offer.required?.let {
            view.onShowRequiredDataField(
                UiRequiredDataField(
                    offer.required,
                    generateOffer(account, offer.offer, sessionId)
                )
            )
        }
    }

    private fun postAccreditation(
        account: TapOnPhoneAccount,
        offer: OfferResponse,
        sessionId: String
    ) {
        disposable.add(
            repository.requestTapOnPhoneOrder(createRequest(account, offer, sessionId))
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .doOnSubscribe {
                    view.showLoading()
                }
                .subscribe({ response ->
                    view.hideLoading()
                    view.onRequestTapOnPhoneOrderSuccess(response.orderId.orEmpty())
                }, { itError ->
                    val error = APIUtils.convertToErro(itError)
                    view.hideLoading()
                    if (error.errorCode == REQUIRED_FIELDS)
                        view.onShowCallCenter(error)
                    else
                        view.showError(error)
                })
        )
    }

    private fun createRequest(
        account: TapOnPhoneAccount,
        offerResponse: OfferResponse,
        sessionId: String
    ) = OrdersRequest(
        type = REQUIRED_DATA_FIELD_ORDER_TYPE,
        order = generateOffer(account, offerResponse.offer, sessionId),
        registrationData = null
    )

    private fun generateOffer(
        account: TapOnPhoneAccount,
        offer: OfferRes?,
        sessionId: String
    ) = Order(
        offerId = offer?.id,
        payoutData = PayoutData(
            payoutMethod = BANK_ACCOUNT,
            targetBankAccount = TargetBankAccount(
                bankNumber = account.bankNumber,
                agency = account.agency,
                accountNumber = account.account + account.accountDigit,
                accountType = CHECKING
            )
        ),
        agreements = offer?.agreements?.map {
            Agreement(
                it.code.orEmpty(),
                AUTHORIZED
            )
        }.orEmpty(),
        itemsConfigurations = offer?.itemsConfigurations.orEmpty(),
        sessionId = sessionId
    )

    private fun verifyOpenRequiredDataField(offerResponse: OfferResponse): Boolean {
        val required = offerResponse.required

        return (required != null) &&
                (required.addressFields.isNullOrEmpty().not()
                        || required.companyFields.isNullOrEmpty().not()
                        || required.individualFields.isNullOrEmpty().not()
                        || required.phoneFields.isNullOrEmpty().not())
    }

    override fun onDestroy() {
        disposable.dispose()
    }

    private companion object {
        const val AUTHORIZED = "AUTHORIZED"
        const val BANK_ACCOUNT = "BANK_ACCOUNT"
        const val CHECKING = "CHECKING"

    }
}