package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment

import br.com.mobicare.cielo.meuCadastroNovo.domain.Bank

interface ListenerCadastroScreen {
    fun callAccountEngine(
        list: ArrayList<Bank>?,
        elegibility: Boolean
    )
    fun showMask()
    fun hideMask()
    fun callStatusError(){}
}