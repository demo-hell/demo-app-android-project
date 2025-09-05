package br.com.mobicare.cielo.merchant.domain.entity

data class Agreement(
    val debitAgreement: String,
    val domiciles: List<Domicile>,
    val merchantId: Int,
    val message: String,
    val messageId: Int
)

data class Domicile(
    val accountNumber: String,
    val accountType: Int,
    val agency: Int,
    val agencyDigit: String,
    val cdBank: Int,
    val digit: String
)


const val PENDING = "PENDING"
const val WAITING = "WAITING"
const val ACTIVE = "ACTIVE"