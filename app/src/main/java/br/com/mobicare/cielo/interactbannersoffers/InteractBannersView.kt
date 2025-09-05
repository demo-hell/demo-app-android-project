package br.com.mobicare.cielo.interactbannersoffers

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers

interface InteractBannersView {

    fun showBannerByPriority(offers: HiringOffers?)
    fun onError(error: ErrorMessage)
    fun goTo(hiringOffers: HiringOffers?, screenName: String)
    fun showLoading()
    fun hideLoading()
}