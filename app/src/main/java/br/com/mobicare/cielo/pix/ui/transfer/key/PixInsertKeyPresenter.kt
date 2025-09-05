package br.com.mobicare.cielo.pix.ui.transfer.key

import br.com.mobicare.cielo.commons.constants.HTTP_ENHANCE
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.getKey
import br.com.mobicare.cielo.commons.utils.getKeyType
import br.com.mobicare.cielo.pix.api.keys.PixKeysRepositoryContract
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class PixInsertKeyPresenter(
    private val view: PixInsertKeyContract.View,
    private val repository: PixKeysRepositoryContract,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) :
    PixInsertKeyContract.Presenter {

    private var disposible = CompositeDisposable()

    override fun onValidateKey(key: String, type: PixKeyTypeEnum?) {
        disposible.add(
            repository.validateKey(getKey(key, type?.name, isCodeCountry = true), getKeyType(key, type?.name, true))
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .doOnSubscribe {
                    view.showLoading()
                }
                .subscribe({
                    view.hideLoading()
                    view.onValidKey(it)
                }, {
                    view.hideLoading()
                    processStatusError(APIUtils.convertToErro(it))
                })
        )
    }

    private fun processStatusError(error: ErrorMessage?) {
        if (error?.httpStatus == HTTP_ENHANCE)
            view.onErrorInput(error)
        else view.showError(error)
    }

    override fun onResume() {
        if (disposible.isDisposed) disposible = CompositeDisposable()
    }

    override fun onPause() {
        disposible.dispose()
    }
}