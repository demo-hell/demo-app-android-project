package br.com.mobicare.cielo.home.utils

import br.com.mobicare.cielo.commons.router.Router
import br.com.mobicare.cielo.main.domain.AppMenuResponse
import br.com.mobicare.cielo.main.domain.Menu
import br.com.mobicare.cielo.main.domain.MenuTarget

object HomeFactory {

    private val menu = Menu(
        code = "code",
        icon = "icon",
        items = null,
        name = "name",
        showIcons = true,
        shortIcon = null,
        privileges = emptyList(),
        show = true,
        showItems = true,
        menuTarget = MenuTarget(),
    )

    val appMenuResponseWithTapAndPosOptions = AppMenuResponse(
        menu = listOf(
            menu.copy(
                code = "APP_ANDROID_HOME",
                items = listOf(
                    menu.copy(
                        code = "APP_ANDROID_SELL",
                        items = listOf(
                            menu.copy(code = Router.APP_ANDROID_TAP_PHONE),
                            menu.copy(code = Router.APP_ANDROID_POS_VIRTUAL)
                        )
                    )
                )
            )
        )
    )

}