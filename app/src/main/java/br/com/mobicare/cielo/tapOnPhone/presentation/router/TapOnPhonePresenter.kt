package br.com.mobicare.cielo.tapOnPhone.presentation.router

import android.content.Context
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.tapOnPhone.constants.REQUIRED_FIELDS
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneEligibilityResponse
import br.com.mobicare.cielo.tapOnPhone.domain.repository.TapOnPhoneEligibilityRepository
import br.com.mobicare.cielo.tapOnPhone.enums.TapOnPhoneStatusEnum
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class TapOnPhonePresenter(
    private val view: TapOnPhoneContract.View,
    private val repository: TapOnPhoneEligibilityRepository,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    private val userPreferences: UserPreferences
) : TapOnPhoneContract.Presenter {

    private var disposable = CompositeDisposable()

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onGetTapStatus(wasOpenedByPOSVirtual: Boolean) {
        if (wasOpenedByPOSVirtual) {
            view.onTapIsActive(wasOpenedByPOSVirtual)
        } else {
            checkStatus()
        }
    }

    private fun checkStatus() {
        disposable.add(
            repository.getEligibilityStatus()
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .doOnSubscribe {
                    view.onShowLoading(R.string.tap_on_phone_wait_a_momenty)
                }
                .subscribe({ response ->
                    checkTapStatus(response)
                }, { itError ->
                    view.onHideLoading()
                    val error = APIUtils.convertToErro(itError)
                    if (error.errorCode == REQUIRED_FIELDS)
                        view.onShowCallCenter(error)
                    else
                        view.onShowError(error)
                })
        )
    }

    private fun checkTapStatus(response: TapOnPhoneEligibilityResponse) {
        when (response.status) {
            TapOnPhoneStatusEnum.ELIGIBLE.name -> view.onToDoAccreditation()
            TapOnPhoneStatusEnum.ORDER_IN_PROGRESS.name -> view.onAccreditationInProgress()
            TapOnPhoneStatusEnum.NOT_ACTIVE.name -> view.onEstablishmentCreationInProgress()
            TapOnPhoneStatusEnum.ENABLED.name -> tapEnable(response)
            TapOnPhoneStatusEnum.NOT_ELIGIBLE.name -> view.onNonEligible()
            TapOnPhoneStatusEnum.CANCELED.name -> view.onTapIsDisabled()
            else -> view.onShowError()
        }
        if (response.status != TapOnPhoneStatusEnum.ENABLED.name)
            view.onHideLoading()
    }

    private fun tapEnable(response: TapOnPhoneEligibilityResponse) {
        if (response.impersonateRequired == true) {
            response.merchant?.let {
                view.onExchangeEstablishment(response)
            } ?: view.onShowError()
            view.onHideLoading()
        } else
            view.onTapIsActive()
    }

    override fun onPause() {
        disposable.dispose()
    }

    override fun onDeleteCache(context: Context) = userPreferences.cacheClear(context)

}