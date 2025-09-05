package br.com.mobicare.cielo.interactbannersoffers

import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers
import br.com.mobicare.cielo.interactbannersoffers.repository.InteractBannerMapper
import br.com.mobicare.cielo.interactbannersoffers.repository.InteractBannerRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class InteractBannersPresenterImpl(
    private val view: InteractBannersView,
    private val repository: InteractBannerRepository
) : InteractBannersPresenter {

    private var disponsible = CompositeDisposable()
    private var priority: Int = 0
    private var isLoadingFromHome = false
    private var hiringOffer: HiringOffers? = null

    override fun onCreate(priorityShow: Int, isLoadingFromHome: Boolean) {
        this.priority = priorityShow
        this.isLoadingFromHome = isLoadingFromHome
        getHiringOffers()
    }

    private fun getHiringOffers() {
        if (repository.isEnabledFeatureToggleBanners()) {
            if (isLoadingFromHome) {
                repository
                    .getHiringOffers()
                    .configureIoAndMainThread()
                    .doOnSubscribe { view.showLoading() }
                    .doFinally { view.hideLoading() }
                    .subscribe({
                        repository.saveHiringOffersLocal(it)
                        showBannerByPriority(it, priority)
                    }, {
                        val error = APIUtils.convertToErro(it)
                        repository.clearHiringOffersLocal()
                        view.onError(error)
                    })
                    .addTo(disponsible)
            } else {
                val type = object : TypeToken<List<HiringOffers>>() {}.type
                val offers = Gson().fromJson<List<HiringOffers>>(repository.getHiringOffersLocal(), type)

                showBannerByPriority(offers, priority)
            }
        } else repository.clearHiringOffersLocal()
    }

    private fun showBannerByPriority(offers: List<HiringOffers>?, priority: Int) {
        if (offers?.firstOrNull() != null) {
            hiringOffer = InteractBannerMapper.orderBannerByPriority(offers, priority)
            hiringOffer?.let {
                view.showBannerByPriority(hiringOffer)
            }
        }
    }

    override fun onResume() {
        if (disponsible.isDisposed) {
            disponsible = CompositeDisposable()
        }
    }

    override fun goTo() {
        view.goTo(hiringOffer, getScreenName())
    }

    override fun onDispose() {
        disponsible.dispose()
    }

    private fun getScreenName() =
        when (priority) {
            InteractBannerMapper.PRIORITY_BANNER_RECTANGLE_HOME -> Label.HOME
            InteractBannerMapper.PRIORITY_BANNER_LEADERBOARD_RECEBIVEIS -> Label.RECEBIVEIS
            InteractBannerMapper.PRIORITY_BANNER_LEADERBOARD_OUTROS -> Label.OUTROS
            InteractBannerMapper.PRIORITY_BANNER_LEADERBOARD_SERVICOS -> Label.SERVICOS
            InteractBannerMapper.PRIORITY_BANNER_LEADERBOARD_TAXAS_E_PLANOS -> Label.TAXAS_E_PLANOS
            else -> ""
        }
}