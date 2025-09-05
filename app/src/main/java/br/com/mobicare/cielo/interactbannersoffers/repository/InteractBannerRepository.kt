package br.com.mobicare.cielo.interactbannersoffers.repository

import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannersConstants
import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers
import io.reactivex.Observable

class InteractBannerRepository(
    private val api: CieloAPIServices,
    private val userPreferences: UserPreferences,
    private val featureTogglePreference: FeatureTogglePreference
) {
    fun getHiringOffers(): Observable<List<HiringOffers>> = api.getHiringOffers()
    fun getHiringOffersLocal(): String? = userPreferences.getInteractOffers()
    fun clearHiringOffersLocal() = userPreferences.delete(InteractBannersConstants.LIST_INTERACT_OFFER)
    fun saveHiringOffersLocal(offers: List<HiringOffers>) = userPreferences.putInteractOffers(offers)
    fun isEnabledFeatureToggleBanners() = featureTogglePreference.getFeatureTogle(FeatureTogglePreference.INTERACT_BANNERS)
    fun postTermoAceite(bannerId: Int) = api.postTermoAceite(bannerId)
}