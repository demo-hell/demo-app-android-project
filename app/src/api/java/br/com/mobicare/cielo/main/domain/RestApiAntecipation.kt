package br.com.mobicare.cielo.main.domain

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal


class BodyAntecipationProgramado

data class ResponseAntecipationProgramado(
    val frequency: String?
)

data class BodyAntecipationAvulso(
    val id: String?
)


//{
//    "protocolNumber": "150068547",
//    "grossAmount": 91136.25,
//    "netAmount": 87787.72,
//    "bankTransfers": []
//}

data class ResponseAntecipationAvulso(

    @SerializedName("bankTransfers")
    val bankTransfers: ArrayList<BankTransfer?>?,
    @SerializedName("")
    val contractNumber: Int?,
    @SerializedName("grossAmount")
    val grossAmount: BigDecimal?,
    @SerializedName("netAmount")
    val netAmount: BigDecimal?,
    @SerializedName("protocolNumber")
    val protocolNumber: String?,

    @SerializedName("settlementDate")
    val settlementDate: String?
)

data class BankTransfer(
    val account: String?,
    val agency: String?,
    val ammount: Int?,
    val bankCode: String?
)