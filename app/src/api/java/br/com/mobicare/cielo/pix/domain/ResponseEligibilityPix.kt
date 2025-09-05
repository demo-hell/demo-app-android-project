package br.com.mobicare.cielo.pix.domain

data class ResponseEligibilityPix(
    val elegible: Boolean,
    val mdr: Double,
    val type : String
)