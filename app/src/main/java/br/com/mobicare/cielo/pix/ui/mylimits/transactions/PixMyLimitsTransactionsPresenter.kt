package br.com.mobicare.cielo.pix.ui.mylimits.transactions

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.pix.api.myLimits.PixMyLimitsRepositoryContract
import br.com.mobicare.cielo.pix.api.myLimits.timeManagement.PixTimeManagementRepositoryContract
import br.com.mobicare.cielo.pix.domain.LimitsRequest
import br.com.mobicare.cielo.pix.domain.PixMyLimitsRequest
import br.com.mobicare.cielo.pix.domain.PixMyLimitsResponse
import br.com.mobicare.cielo.pix.enums.BeneficiaryTypeEnum
import br.com.mobicare.cielo.pix.enums.PixServicesGroupEnum
import br.com.mobicare.cielo.pix.enums.PixTimeManagementEnum
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import java.net.HttpURLConnection.HTTP_NO_CONTENT
import java.net.HttpURLConnection.HTTP_OK

class PixMyLimitsTransactionsPresenter(
    private val view: PixMyLimitsTransactionsContract.View,
    private val userPreferences: UserPreferences,
    private val repository: PixMyLimitsRepositoryContract,
    private val timeManagementRepository: PixTimeManagementRepositoryContract,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler,
) : PixMyLimitsTransactionsContract.Presenter {
    private var disposable = CompositeDisposable()
    private var timeManagement: PixTimeManagementEnum? = null

    override fun getUsername(): String = userPreferences.userName

    override fun getMyLimits(beneficiaryType: BeneficiaryTypeEnum) {
        disposable.add(
            repository.getLimits(PixServicesGroupEnum.PIX.name, beneficiaryType.personType)
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .doOnSubscribe {
                    view.showLoading()
                }
                .subscribe({ response ->
                    view.hideLoading()
                    validateLimits(response)
                }, { error ->
                    view.hideLoading()
                    view.onErrorGetLimits(APIUtils.convertToErro(error))
                }),
        )
    }

    private fun validateLimits(limitsResponse: PixMyLimitsResponse) {
        val limits = limitsResponse.limits

        if (limits.isNullOrEmpty()) {
            view.onErrorGetLimits()
        } else {
            view.onShowMyLimits(limitsResponse)
        }
    }

    override fun onUpdateLimit(
        otp: String?,
        listLimits: MutableList<LimitsRequest>?,
        fingerprint: String,
        beneficiaryType: BeneficiaryTypeEnum,
    ) {
        if (listLimits.isNullOrEmpty()) {
            view.onErrorUpdateLimit()
        } else {
            disposable.add(
                repository.updateLimits(otp, request(listLimits, fingerprint, beneficiaryType))
                    .observeOn(uiScheduler)
                    .subscribeOn(ioScheduler)
                    .subscribe({
                        if (it.code() in HTTP_OK..HTTP_NO_CONTENT) {
                            view.onSuccessUpdateLimit()
                        } else {
                            showError(APIUtils.convertToErro(it))
                        }
                    }, { error ->
                        showError(APIUtils.convertToErro(error))
                    }),
            )
        }
    }

    override fun getNightTime() {
        if (timeManagement != null) {
            view.onSuccessGetNightTime(timeManagement)
        } else {
            disposable.add(
                timeManagementRepository
                    .getNightTime()
                    .observeOn(uiScheduler)
                    .subscribeOn(ioScheduler)
                    .subscribe({
                        timeManagement = PixTimeManagementEnum.findByTime(it.nighttimeStart)
                        view.onSuccessGetNightTime(timeManagement)
                    }, { error ->
                        view.onErrorGetLimits(APIUtils.convertToErro(error))
                    }),
            )
        }
    }

    private fun request(
        listLimits: MutableList<LimitsRequest>?,
        fingerprint: String,
        beneficiaryType: BeneficiaryTypeEnum,
    ) = PixMyLimitsRequest(
        serviceGroup = PixServicesGroupEnum.PIX.name,
        limits = listLimits,
        fingerprint = fingerprint,
        beneficiaryType = beneficiaryType.personType,
    )

    private fun showError(errorMessage: ErrorMessage? = null) {
        view.onErrorUpdateLimits(onGenericError = {
            view.onErrorUpdateLimit(errorMessage)
        })
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }
}
