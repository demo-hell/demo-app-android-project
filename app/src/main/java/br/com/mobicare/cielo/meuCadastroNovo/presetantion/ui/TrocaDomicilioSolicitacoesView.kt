package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.meuCadastroNovo.domain.Item

interface TrocaDomicilioSolicitacoesView : BaseView{

    fun onSuccess(domiciles: List<Item>?)
    fun showEmptyList()
}