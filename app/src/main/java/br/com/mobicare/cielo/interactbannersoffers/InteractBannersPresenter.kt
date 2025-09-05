package br.com.mobicare.cielo.interactbannersoffers

import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers

interface InteractBannersPresenter {

    fun onCreate(priorityShow: Int, isLoadingFromHome: Boolean = false)
    fun goTo()
    fun onResume()
    fun onDispose()
}