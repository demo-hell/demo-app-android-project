package br.com.mobicare.cielo.recebaRapido.cancellation.comparationscreen

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.recebaRapido.api.RecebaRapidoRepository
import br.com.mobicare.cielo.taxaPlanos.mapper.TaxAndBrandsMapper
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class CancelattionRecebaRapidoComparationPresenterImpl(private val view: CancelattionRecebaRapidoComparationView,
                                                       private val repository: RecebaRapidoRepository,
                                                       private val userPreferences: UserPreferences,
                                                       private val uiScheduler: Scheduler,
                                                       private val ioScheduler: Scheduler)
    : CancelattionRecebaRapidoComparationPresenter {

    private val disposable = CompositeDisposable()

    override fun getTaxAndBrand() {
        disposable.add(
                repository.getBrands(userPreferences.token)
                        .observeOn(uiScheduler)
                        .subscribeOn(ioScheduler)
                        .doOnSubscribe {
                            view.showLoading()
                        }
                        .subscribe({
                            view.onTaxAndBrandSuccess(TaxAndBrandsMapper.mapper(it))
                        }, {
                            view.onTaxAndBrandError()
                        }))

    }
}