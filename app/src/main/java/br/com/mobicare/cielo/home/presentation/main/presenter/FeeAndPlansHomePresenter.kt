package br.com.mobicare.cielo.home.presentation.main.presenter

import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.home.presentation.main.ui.fragment.FeeAndPlansHomeContract
import br.com.mobicare.cielo.home.utils.BrandsRepository
import br.com.mobicare.cielo.meuCadastroNovo.domain.Brand
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class FeeAndPlansHomePresenter(
    private val view: FeeAndPlansHomeContract.View,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    private val brandsRepository: BrandsRepository,
    private val userPreferences: UserPreferences,
    private val featureTogglePreference: FeatureTogglePreference,
) : FeeAndPlansHomeContract.Presenter {

    private var disposable = CompositeDisposable()

    override fun getBrands() {
        if (featureTogglePreference.getFeatureTogle(FeatureTogglePreference.FEES_PER_FLAG_HOME).not()) {
            view.hideFeesAndPlans()
            return
        }
        disposable.add(
            brandsRepository
                .loadAllBrands(userPreferences.token)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    val brands = arrayListOf<Brand>()

                    it.forEach { itSolution ->
                        itSolution.banks.forEach { itBank ->
                            itBank.brands?.forEach { itBrand ->
                                brands.add(itBrand)
                            }
                        }
                    }

                    if(brands.isEmpty()) view.hideFeesAndPlans()
                    else view.showFeePerBrand(ArrayList(brands.take(THREE)), false)
                }, {
                    view.showFeePerBrand(arrayListOf(), true)
                })
        )
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onDestroy() {
        disposable.dispose()
    }
}