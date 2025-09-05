package br.com.mobicare.cielo.meuCadastroNovo.domain

data class PutEditContactRequest(
    val name: String,
    val types: List<String>,
    val email: String? = null,
    val phones: List<PhoneContato>
)