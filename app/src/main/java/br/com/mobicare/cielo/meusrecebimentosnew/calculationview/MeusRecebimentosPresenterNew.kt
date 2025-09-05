package br.com.mobicare.cielo.meusrecebimentosnew.calculationview

interface MeusRecebimentosPresenterNew {

    fun onCreate(initialDate: String, finalDate: String)
    fun onClickPendingAmount()
    fun onCalculationVision()
    fun onLoadReceivablesBankAccounts()
    fun onLoadAlerts()
    fun onGeneratePdfAlerts()
    fun onDestroy()
    fun onResume()
    fun initializeAlerts()
}