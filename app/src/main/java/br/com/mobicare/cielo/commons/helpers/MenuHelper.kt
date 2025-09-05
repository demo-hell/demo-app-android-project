package br.com.mobicare.cielo.commons.helpers

import br.com.mobicare.cielo.commons.router.Router
import br.com.mobicare.cielo.main.domain.Menu

class MenuHelper {

    companion object {
        private const val APP_ANDROID_HOME = "APP_ANDROID_HOME"

        //TODO temporary selection of main services, remove soon as we get a proper response from Menu API
        private val homeMainServices = listOf(
            Router.APP_ANDROID_POS_VIRTUAL,
            Router.APP_ANDROID_PAYMENT_LINK,
            Router.APP_ANDROID_TAP_PHONE,
            Router.APP_ANDROID_PIX,
            Router.APP_ANDROID_SALES_SIMULATOR,
            Router.APP_ANDROID_REFUNDS,
            Router.APP_ANDROID_RATES,
            Router.APP_ANDROID_RECEIVE_AUTOMATIC
        )

        fun getHome(menu: List<Menu>): List<Menu>? {

            val appHomeMenuItem = menu.filter { it.code == APP_ANDROID_HOME }
            if (appHomeMenuItem.isNotEmpty()) {
                return appHomeMenuItem.first().items?.flatMap { sections ->
                    sections.items.orEmpty()
                }?.filter {
                    it.code in homeMainServices
                }?.sortedBy {
                    homeMainServices.indexOf(it.code)
                }
            }

            return null
        }
    }

}