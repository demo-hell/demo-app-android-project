package br.com.mobicare.cielo.taxaPlanos.presentation.ui.presenter

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import br.com.mobicare.cielo.recebaRapido.api.RecebaRapidoRepository
import br.com.mobicare.cielo.taxaPlanos.mapper.TaxAndBrandsMapper
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.fragment.cielolivre.PlanFreeTaxesPresenterContract
import io.reactivex.Scheduler

class PlanFreeTaxesPresenter(
        private val view: PlanFreeTaxesPresenterContract.View,
        private val uiScheduler: Scheduler,
        private val ioScheduler: Scheduler,
        private val recebaRapidoRepository: RecebaRapidoRepository,
        private val userPreferences: UserPreferences
) : PlanFreeTaxesPresenterContract.Presenter {

    private val compositeDisposableHandler:
            CompositeDisposableHandler = CompositeDisposableHandler()

    override fun onResume() {
        compositeDisposableHandler.start()
    }

    override fun onDestroy() {
        compositeDisposableHandler.destroy()
    }

    override fun fetchAllSupportedBrands() {
        compositeDisposableHandler
                .compositeDisposable
                .add(recebaRapidoRepository.getBrands(userPreferences.token)
                        .observeOn(uiScheduler)
                        .subscribeOn(ioScheduler)
                        .doOnSubscribe {
                            view.showLoading()
                        }.subscribe({ itResponse ->
                            view.hideLoading()
                            view.loadBrands(TaxAndBrandsMapper.convert(itResponse))
                        }, { errorResponse ->
                            view.hideLoading()
                            view.showError(APIUtils.convertToErro(errorResponse))
                        })
                )
    }
}