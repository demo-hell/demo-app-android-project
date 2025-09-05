package br.com.mobicare.cielo.changeEc.domain


data class MerchantsObj (
        var token: String = "",
        val merchants: ArrayList<Merchant> = ArrayList()
)