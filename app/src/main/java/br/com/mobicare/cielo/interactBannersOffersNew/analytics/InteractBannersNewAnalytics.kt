package br.com.mobicare.cielo.interactBannersOffersNew.analytics

import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Action.GERAL
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.interactBannersOffersNew.utils.BannerControl

class InteractBannersNewAnalytics {

    fun logScreenActionsByControl(
        action: String = Label.CLIQUE,
        bannerTypeName: String,
        bannerName: String,
        bannerControl: BannerControl,
        position: Int
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, GERAL),
            action = listOf(Action.BANNER, action),
            label = mutableListOf<String>().apply {
                add(bannerControl.bannerScreenName)
                add(bannerTypeName.toLowerCasePTBR())
                if (bannerControl.numberOfBanners > ONE) {
                    add(position.toString())
                }
                add(bannerName)
            }
        )
    }
}