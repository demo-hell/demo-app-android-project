package br.com.mobicare.cielo.interactBannersOffersNew.domain.mapper

import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.interactBannersOffersNew.utils.BannerControl
import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers

object InteractBannerMapperNew {
    private val allBannerCases: MutableList<BannerControl>
        get() = BannerControl.values().toMutableList()


    fun getBannersByControl(offers: List<HiringOffers>?, bannerControl: BannerControl): MutableList<HiringOffers> {
        val offersSize = offers?.size ?: ZERO
        if (offersSize == ZERO) {
            return emptyList<HiringOffers>().toMutableList()
        }

        val index = allBannerCases.indexOf(bannerControl)
        val allCasesSliced = allBannerCases.slice(ZERO until index)

        val newBannerIndex = allCasesSliced.fold(ZERO) { accumulatedBanners, itBannerControl ->
            if (itBannerControl.numberOfBanners > offersSize) {
                offersSize
            } else {
                accumulatedBanners + itBannerControl.numberOfBanners
            }
        }

        val multiple = newBannerIndex / offersSize
        val newOfferIndex = newBannerIndex - (offersSize * multiple)
        val newOfferOffset = newOfferIndex + bannerControl.numberOfBanners

        val banners = mutableListOf<HiringOffers>()

        for (i in newOfferIndex until newOfferOffset) {
            offers?.getOrNull(i)?.also {
                banners.add(it)
            }
        }

        if (banners.size < bannerControl.numberOfBanners && bannerControl.numberOfBanners <= offersSize) {
            val remainingBanners = bannerControl.numberOfBanners - banners.size
            for (i in ZERO until remainingBanners) {
                offers?.getOrNull(i)?.also {
                    banners.add(it)
                }
            }
        }

        return banners
    }
}