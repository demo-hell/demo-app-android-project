package br.com.mobicare.cielo.centralDeAjuda.data.clients.domains

data class Contact(val category: String,
                   val description: String,
                   val enabled: String,
                   val types: List<ContactType>,
                   val id: String?
)

data class ContactType(val contact: String,
                       val description: String,
                       val type: String
)