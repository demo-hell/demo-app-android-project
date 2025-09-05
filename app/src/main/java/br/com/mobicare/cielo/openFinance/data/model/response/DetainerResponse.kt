package br.com.mobicare.cielo.openFinance.data.model.response

import androidx.annotation.Keep

@Keep
data class DetainerResponse(
    val consentId: String,
    val status: String,
    val issuer: String,
    val creationDate: String,
    val expirationDate: String,
    val loggedUser: Loggeduser,
    val creditor: Creditor,
    val payment: Payment,
    val companyName: String,
    val apiVersion: String
)

@Keep
data class Loggeduser(
    val document: Document,
)

@Keep
data class Document(
    val type: String,
    val identification: String
)

@Keep
data class Creditor(
    val document: Document,
    val personType: String,
    val name: String,
)

@Keep
data class Payment(
    val type: String,
    val currency: String,
    val issuer: String?,
    val date: String,
    val amount: Double,
    val detail: Detail,
    val schedule: Schedule?
)

@Keep
data class Detail(
    val localInstrument: String,
    val proxy: String,
    val creditorAccount: CreditorAccount
)

@Keep
data class CreditorAccount(
    val agency: String,
    val account: String
)

@Keep
data class Schedule(
    val single: Single
)

@Keep
data class Single(
    val date: String
)