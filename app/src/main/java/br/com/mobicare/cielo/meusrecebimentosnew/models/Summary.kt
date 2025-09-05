package br.com.mobicare.cielo.meusrecebimentosnew.models

import java.io.Serializable

class Summary(
        val totalAmount: Double,
        val pendingAmount: Double,
        val expectedAmount: Double? = null,
        val paidAmount: Double? = null,
        val date: String? = null,
        val status: String? = null) : Serializable