package br.com.mobicare.cielo.autoAtendimento.domain.api

import br.com.mobicare.cielo.autoAtendimento.domain.AutoAtendimentoDataSource
import br.com.mobicare.cielo.autoAtendimento.domain.model.Supply
import br.com.mobicare.cielo.autoAtendimento.presentation.presenter.AutoAtendimentoContract
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AutoAtendimentoRepository(private var dataSource: AutoAtendimentoDataSource): AutoAtendimentoContract.Repository {

    private var composite = CompositeDisposable()
    private var uiScheduler: Scheduler? = AndroidSchedulers.mainThread()
    private var ioScheduler: Scheduler? = Schedulers.io()
    lateinit var callback: AutoAtendimentoContract.Presenter

    override fun disposable() {
        composite.clear()
    }

    override fun callBack(callback: AutoAtendimentoContract.Presenter) {
        this.callback = callback
    }

    override fun loadSuplies(accessToken: String, authoziration: String) {

        composite.add(dataSource.loadSuplies(accessToken, authoziration)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({data->
                    callback.responseListSuplies(data.supplies)
                },{error->
                    callback.errorResponse(error)
                })
        )

    }

    override fun loadSupplies(accessToken: String, authoziration: String, callback: APICallbackDefault<List<Supply>, String>) {

        composite.add(dataSource.loadSuplies(accessToken, authoziration)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({data->
                    callback.onSuccess(data.supplies)
                },{error->
                    callback.onError(APIUtils.convertToErro(error))
                })
        )

    }



}