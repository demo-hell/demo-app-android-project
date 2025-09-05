package br.com.mobicare.cielo.meusrecebimentosnew.repository

import java.math.BigDecimal

data class AlertsResponse(
    val tipoDocumento: String,
    val documento: String,
    val nome: String,
    val vlOriginalDebito: Double,
    val vlEfetivoDebito: Double,
    val vlOriginalCredito: Double,
    val msgDebito: String,
    val msgCredito: String,
    val msgPdfDebito: String,
    val msgPdfCredito: String,
    val textCreditApp: String,
    val textDebitApp: String,
    val textCreditAdjust: String,
    val textDebitAdjust: String,
    val textCreditPDF: String,
    val textDebitPDF: String,
    val hasTextCredit: Boolean,
    val hasTextDebit: Boolean,
    val hasTextCreditAdjust: Boolean,
    val hasTextDebitAdjust: Boolean
)

data class FileResponse(
    val file: String,
    val size: String
)

interface AlertsResponseRepository {
    fun save(documento: AlertsResponse)
    fun findById(tipoDocumento: String): AlertsResponse?
    fun findAll(): List<AlertsResponse>
    fun deleteById(tipoDocumento: String)
}