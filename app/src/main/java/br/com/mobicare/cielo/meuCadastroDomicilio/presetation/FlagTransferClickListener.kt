package br.com.mobicare.cielo.meuCadastroDomicilio.presetation

interface FlagTransferClickListener {
    fun onButtonSelected(isCheck: Boolean)
    fun onButtonStatus()
    fun onButtonName(name: String)
    fun hideTopBar()
    fun showTopBar()
    fun showButtonHome()
    fun hideButtonHome()
}