package br.com.mobicare.cielo.meuCadastroNovo.presetantion.presenter

interface TrocaDomicilioSolicitacoesPresenter {

    fun getDomicile(isLoading:Boolean = true, protocol: String?, status: String?, page:Int?,  pageSize:Int?)
    fun onResume()
    fun onPause()
    fun isToShow(): Boolean
    fun resetPagination()
}