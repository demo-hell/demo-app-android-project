package br.com.mobicare.cielo.idOnboarding.analytics

import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.idOnboarding.analytics.constants.*
import br.com.mobicare.cielo.pix.constants.EMPTY

class IDOnboardingP1Analytics {

    fun logScreenView(screenName: String, screenClass: Class<Any>) {
        Analytics.trackScreenView(screenName, screenClass)
    }

    fun logIDStartAlertClick(subCategory: String, days: Int, label: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.ID_ONBOARDING),
            action = listOf(
                subCategory,
                if (subCategory == ANALYTICS_ID_REMEMBER_UPDATE_DATA_30_DAYS || subCategory == ANALYTICS_ID_REMEMBER_UPDATE_DATA_5_DAYS)
                    days.toString()
                else EMPTY,
                Action.CLIQUE
            ),
            label = listOf(Label.BOTAO, label)
        )
    }

}