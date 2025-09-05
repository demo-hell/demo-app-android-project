package br.com.mobicare.cielo.solesp.ui.infoSend

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.solesp.api.SolespRepository
import br.com.mobicare.cielo.solesp.domain.SolespRequest
import br.com.mobicare.cielo.solesp.model.SolespModel
import br.com.mobicare.cielo.solesp.ui.infoSend.SolespInfoSendContract.Presenter
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class SolespInfoSendPresenter(
    private val view: SolespInfoSendContract.View,
    private val userPreferences: UserPreferences,
    private val repository: SolespRepository,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler,
) : Presenter {

    private var disposable = CompositeDisposable()

    override fun onPause() {
        disposable.dispose()
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun getNumberEc(): String = userPreferences.numeroEC

    override fun getUserName(): String = userPreferences.currentUserName ?: EMPTY

    override fun sendSolespRequest(solespModel: SolespModel) {
        val solespRequest = SolespRequest(
            initialDate = solespModel.startDate?.formatDateToAPI(),
            finalDate = solespModel.endDate?.formatDateToAPI(),
            email = solespModel.sendingEmail,
            phone = solespModel.sendingPhone,
            reason = solespModel.typeSelected?.value
        )
        disposable.add(
            repository.sendSolespRequest(solespRequest)
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .doOnSubscribe {
                    view.showLoading()
                }
                .doFinally {
                    view.hideLoading()
                }.subscribe({
                    view.showSuccess()
                }, {
                    view.showError()
                })
        )
    }

}