package br.com.mobicare.cielo.commons.domains.entities

import java.io.Serializable

class SystemMessage : Serializable {
    @JvmField
    var key: String? = null
    @JvmField
    var value: String? = null
}

class ItemDetalhesVendas(val key: String?, val value: String?)