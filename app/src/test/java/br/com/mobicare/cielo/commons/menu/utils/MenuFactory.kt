package br.com.mobicare.cielo.commons.menu.utils

import br.com.mobicare.cielo.main.domain.AppMenuResponse
import com.google.gson.Gson

object MenuFactory {

    private val menuJson = """
        {
            "menu": [
              {
                "code": "APP_ANDROID_HOME",
                "name": "Início",
                "showItems": true,
                "show": true,
                "showIcons": true,
                "target": {
                  "external": false
                },
                "privileges": [
                  "master",
                  "ADMIN",
                  "ANALYST",
                  "READER",
                  "interno",
                  "TECHNICAL"
                ],
                "items": [
                  {
                    "code": "APP_ANDROID_SELL",
                    "name": "Fazer uma venda",
                    "showItems": true,
                    "show": true,
                    "icon": "https://digitaldev.hdevelo.com.br/menu/static/assets/img/income.png",
                    "shortIcon": "https://digitaldev.hdevelo.com.br/menu/static/assets/img/short_pos.png",
                    "showIcons": false,
                    "target": {
                      "external": false
                    },
                    "privileges": []
                  }
                ]
              }
            ]
        }
    """.trimIndent()

    val menuResponse: AppMenuResponse = Gson().fromJson(menuJson, AppMenuResponse::class.java)

}