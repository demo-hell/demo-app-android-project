package br.com.mobicare.cielo.commons.utils

import android.content.Context
import android.text.SpannableStringBuilder
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.*
import br.com.mobicare.cielo.extrato.domains.entities.OperationType
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoStatusDef
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoTransicaoObj
import br.com.mobicare.cielo.meusCartoes.domains.entities.Statement
import br.com.mobicare.cielo.recebaMais.domain.Bank
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

private const val HYPHEN = "-"

fun Statement.convertToExtrato(): ExtratoTransicaoObj {
    val element = ExtratoTransicaoObj()

    val operationType: OperationType = if (this.operationType.startsWith("D"))
        OperationType.DEBIT
    else
        OperationType.CREDIT

    element.time = this.establishment
    element.description = operationType.description
    element.statusCode = when (operationType) {
        OperationType.DEBIT -> ExtratoStatusDef.NEGADA.toString()
        OperationType.CREDIT -> ExtratoStatusDef.APROVADA.toString()
    }

    element.amount = Utils.formatValue(this.amount)
    element.status = formatDateString(this.dateHourTransaction)
    return element
}


fun formatDateString(dateString: String): String? {

    val dateformat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val output = SimpleDateFormat("dd/MM/yyyy")
    var d: Date? = null
    try {
        d = dateformat.parse(dateString)
        return output.format(d)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return null
}

fun Bank.getAgencyFormatted(context: Context): SpannableStringBuilder {
    val agencyFormatted = SpannableStringBuilder.valueOf(context
            .getString(R.string.text_bank_agency_template, this.agency))

    if (this.agencyDigit.isNotEmpty()) {
        agencyFormatted.append(context.getString(R.string.text_bank_agency_digit,
                this.agencyDigit))
    }

    return agencyFormatted
}

fun Bank.getAccountFormatted(context: Context): SpannableStringBuilder {
    val accountFormatted = SpannableStringBuilder.valueOf(context
            .getString(R.string.text_bank_account_number_template, this.accountNumber))

    if (this.accountDigit.isNotEmpty()) {
        accountFormatted.append(context
                .getString(R.string.text_bank_account_digit_template,
                        this.accountDigit))
    }

    return accountFormatted
}

fun String.getAccountFormatted(): String {
    if (this.length <= FIVE)
        return this
    val cleanAccountString = this.filter { char -> char.isDigit() }
    val maxLength = minOf(cleanAccountString.length, TWENTY)
    val limitedString = cleanAccountString.substring(ZERO until maxLength)
    return StringBuilder(limitedString).apply { insert(maxLength - ONE, HYPHEN) }.toString()
}