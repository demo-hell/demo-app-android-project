package br.com.mobicare.cielo.pix.ui.keys.myKeys.register.insert

import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.utils.getKey
import br.com.mobicare.cielo.pix.api.keys.PixKeysRepositoryContract
import br.com.mobicare.cielo.pix.domain.ValidateCode
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class PixInsertKeyToRegisterPresenter(
    private val view: PixInsertKeyToRegisterContract.View,
    private val repository: PixKeysRepositoryContract,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) :
    PixInsertKeyToRegisterContract.Presenter {

    private var disposible = CompositeDisposable()

    override fun onSendValidationCode(key: String?, type: PixKeyTypeEnum?) {
        key?.let { itKey ->
            disposible.add(
                repository.requestValidateCode(
                    ValidateCode(
                        getKey(itKey, type?.name, isCodeCountry = true),
                        type?.name
                    )
                )
                    .observeOn(uiScheduler)
                    .subscribeOn(ioScheduler)
                    .doOnSubscribe {
                        view.showLoading()
                    }
                    .subscribe({
                        view.hideLoading()
                        view.onSuccessSendCode()
                    }, { error ->
                        view.hideLoading()
                        view.showError(APIUtils.convertToErro(error))
                    })
            )
        } ?: run {
            view.showError()
        }
    }

    override fun onResume() {
        if (disposible.isDisposed) disposible = CompositeDisposable()
    }

    override fun onPause() {
        disposible.dispose()
    }
}