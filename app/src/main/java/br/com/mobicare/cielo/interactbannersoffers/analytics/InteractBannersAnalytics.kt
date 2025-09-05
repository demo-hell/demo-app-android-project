package br.com.mobicare.cielo.interactbannersoffers.analytics

import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Category.CAMPANHAS_INTERACT
import br.com.mobicare.cielo.commons.analytics.Label

class InteractBannersAnalytics {

    fun logScreenActions(screenName: String,
                         action: String = Label.CLIQUE,
                         bannerSize: String, bannerName:
                         String, isFromHome: Boolean = false) {
        Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, if (isFromHome) CAMPANHAS_INTERACT else INTERACT_CAMPAIGN_HOME),
                action = listOf(Action.BANNER, action),
                label = listOf(screenName, bannerSize, bannerName)
        )
    }

    companion object {
        const val INTERACT_CAMPAIGN_HOME = "campanhas-interact"
    }
}