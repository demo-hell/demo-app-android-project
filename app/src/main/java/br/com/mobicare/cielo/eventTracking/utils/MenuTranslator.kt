package br.com.mobicare.cielo.eventTracking.utils

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.main.domain.Menu
import br.com.mobicare.cielo.main.domain.MenuTarget

data class MenuTranslator(
    val code: String,
    val name: String
) {
    fun toMenu(): Menu {
        return Menu(
            code = code,
            icon = EMPTY,
            items = emptyList(),
            name = name,
            showIcons = false,
            shortIcon = EMPTY,
            privileges = emptyList(),
            show = false,
            showItems = false,
            menuTarget = MenuTarget(false, type = EMPTY, mail = EMPTY, url = EMPTY)
        )
    }
}