package br.com.mobicare.cielo.meuCadastroNovo.domain

data class PutEditOwnerRequest(val email: String?, val phones: List<Phone>)