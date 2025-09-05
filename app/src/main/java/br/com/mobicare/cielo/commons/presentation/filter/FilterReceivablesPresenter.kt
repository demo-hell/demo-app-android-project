package br.com.mobicare.cielo.commons.presentation.filter

import br.com.mobicare.cielo.commons.data.filter.FilterRepository
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import io.reactivex.Scheduler

class FilterReceivablesPresenter(val view: FilterContract.View,
                                 val uiScheduler: Scheduler,
                                 val ioScheduler: Scheduler,
                                 val filterRepository: FilterRepository) : FilterContract.Presenter {


    private val compositeDisposableHandler: CompositeDisposableHandler =
            CompositeDisposableHandler()

    override fun avaiableFilters(quickFilter: QuickFilter) {

        compositeDisposableHandler.compositeDisposable.add(filterRepository
                .avaiableFilters(quickFilter)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe {
                    view.showLoading()
                }
                .subscribe({ filterResponse ->
                    view.hideLoading()
                    view.displayAvaiableFilters(filterResponse)
                }, { errorResponse ->
                    view.hideLoading()
                    view.showError(APIUtils.convertToErro(errorResponse))
                })
        )
    }

    override fun onResume() {
        compositeDisposableHandler.start()
    }

    override fun onDestroy() {
        compositeDisposableHandler.destroy()
    }
}