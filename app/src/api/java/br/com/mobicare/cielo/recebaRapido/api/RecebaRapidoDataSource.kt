package br.com.mobicare.cielo.recebaRapido.api

import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices

class RecebaRapidoDataSource(private val api: CieloAPIServices) {

    fun callDeleteRecebaRapido() = api.callDeleteRecebaRapido()

    fun getBrands(token: String) = api.loadBrands(token)

}