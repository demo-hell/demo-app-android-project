package br.com.mobicare.cielo.recebaRapido.api

class RecebaRapidoRepository(private val dataSource: RecebaRapidoDataSource) {
    fun callDeleteRecebaRapido() = dataSource.callDeleteRecebaRapido()

    fun getBrands(token: String) = this.dataSource.getBrands(token)
}