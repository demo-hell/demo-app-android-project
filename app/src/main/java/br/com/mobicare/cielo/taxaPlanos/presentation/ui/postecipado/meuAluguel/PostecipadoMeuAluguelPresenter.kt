package br.com.mobicare.cielo.taxaPlanos.presentation.ui.postecipado.meuAluguel

import br.com.mobicare.cielo.commons.constants.Postecipate.ZERO_VALUE_MONEY
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.POSTECIPADO
import br.com.mobicare.cielo.taxaPlanos.ERROR_420
import br.com.mobicare.cielo.taxaPlanos.TAXA_PLANOS_POSTECIPADO
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosSolutionResponse
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.api.PostecipadoRepository
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class PostecipadoMeuAluguelPresenter(
    private val view: PostecipadoMeuAluguelContract.View,
    private val repository: PostecipadoRepository,
    private val featureTogglePreference: FeatureTogglePreference,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : PostecipadoMeuAluguelContract.Presenter {

    private var disposable = CompositeDisposable()

    override fun loadRentInformation(plansSolutionsResponse: TaxaPlanosSolutionResponse?) {
        val featureToggle = featureTogglePreference.getFeatureToggleObject(POSTECIPADO)

        if (featureToggle?.show?.not() == true) {
            view.hideLoading()
            view.unavailableService(message = featureToggle.statusMessage)
            return
        }

        disposable.add(
            repository.getPlanInformation(TAXA_PLANOS_POSTECIPADO)
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({ response ->
                    var rentAmount =
                        if (plansSolutionsResponse?.pos?.isNotEmpty() == true) ZERO_VALUE_MONEY
                        else null
                    plansSolutionsResponse?.pos?.forEach { pos ->
                        rentAmount = pos.rentalAmount?.let { rentAmount?.plus(it) }
                    }
                    view.hideLoading()
                    view.showRentInformation(response, rentAmount)
                }, {
                    view.hideLoading()
                    val error = APIUtils.convertToErro(it)
                    if (error.httpStatus == ERROR_420)
                        view.notEligibleForPostecipate()
                    else
                        view.showError(error)
                })
        )
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onStop() {
        disposable.dispose()
    }
}