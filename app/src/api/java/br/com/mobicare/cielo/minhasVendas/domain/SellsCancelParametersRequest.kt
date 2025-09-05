package br.com.mobicare.cielo.minhasVendas.domain

data class SellsCancelParametersRequest(val nsu: String?,
                                        val tid: String?,
                                        val initialDate: String,
                                        val finalDate: String,
                                        val saleAmount: Double?,
                                        val refundAmount: Double?,
                                        val cardBrands: List<Int>?,
                                        val paymentTypes: List<Int>?,
                                        val authorizationCode: String?)