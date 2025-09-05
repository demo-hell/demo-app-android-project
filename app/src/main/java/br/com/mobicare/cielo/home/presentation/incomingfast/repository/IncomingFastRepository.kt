package br.com.mobicare.cielo.home.presentation.incomingfast.repository

class IncomingFastRepository(private val dataSource: IncomingFastDataSource) {

    fun getEligibleToOffer(token: String) = dataSource.getEligibleToOffer(token)

    fun isEnabledIncomingFastFT() = dataSource.isEnabledIncomingFastFT()
}