package br.com.mobicare.cielo.recebaMais.managers

import br.com.mobicare.cielo.recebaMais.api.RecebaMaisApiDataSource

class RecebaMaisHelpRepository(private val dataSource: RecebaMaisApiDataSource) {


    fun getHelpCenter() = dataSource.getHelpCenter()

}