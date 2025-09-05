package br.com.mobicare.cielo.interactBannersOffersNew.utils

import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.constants.FOUR
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.TWO

enum class BannerControl(val numberOfBanners: Int, val bannerScreenName: String) {
    LeaderboardHome(FOUR, Label.HOME),
    RectangleHome(TWO, Label.HOME),
    LeaderboardReceivables(ONE, Label.RECEBIVEIS),
    LeaderboardServices(ONE, Label.SERVICOS),
    LeaderboardOthers(ONE, Label.OUTROS),
    LeaderboardFeesAndPlans(ONE, Label.TAXAS_E_PLANOS)
}