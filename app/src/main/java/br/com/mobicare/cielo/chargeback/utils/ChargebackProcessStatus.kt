package br.com.mobicare.cielo.chargeback.utils

import android.content.Context
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.Text.EMPTY

enum class ChargebackProcessStatus {
    PRIMEIRO_CHARGEBACK,
    SEGUNDO_CHARGEBACK,
    ARBITRAGEM,
    PRE_COMPLIANCE,
    COMPLIANCE,
    COBRANCA_AMIGAVEL,
    CHARGEBACK_ARBITRAGEM,
    REVERSAO_CHARGEBACK,
    REVERSAO_COBRANCA_AMIGAVEL,
    REVERSAO_ARBITRAGEM,
    FEE_COLLECTION,
    REVERSAO_FEE_COLLECTION,
    RECLAMACAO_DEBITO,
    REVERSAO_RECLAMACAO_DEBITO,
    CHARGEBACK_VOUCHER,
    REVERSAO_CHARGEBACK_VOUCHER,
    PRE_ARBITRAGEM
}

fun createChargebackStatusLabel(status: String?,context: Context): String{
    var label: String = EMPTY
    when(status) {
        ChargebackProcessStatus.PRIMEIRO_CHARGEBACK.name -> label = context.getString(R.string.chargeback_status_first_chargeback)
        ChargebackProcessStatus.SEGUNDO_CHARGEBACK.name -> label = context.getString(R.string.chargeback_status_second_chargeback)
        ChargebackProcessStatus.ARBITRAGEM.name -> label = context.getString(R.string.chargeback_status_arbitration)
        ChargebackProcessStatus.PRE_COMPLIANCE.name -> label = context.getString(R.string.chargeback_status_pre_compliance)
        ChargebackProcessStatus.COMPLIANCE.name -> label = context.getString(R.string.chargeback_status_compliance)
        ChargebackProcessStatus.COBRANCA_AMIGAVEL.name -> label = context.getString(R.string.chargeback_status_friendly_billing)
        ChargebackProcessStatus.CHARGEBACK_ARBITRAGEM.name -> label = context.getString(R.string.chargeback_status_chageback_arbitration)
        ChargebackProcessStatus.REVERSAO_CHARGEBACK.name -> label = context.getString(R.string.chargeback_status_reversal)
        ChargebackProcessStatus.REVERSAO_COBRANCA_AMIGAVEL.name -> label = context.getString(R.string.chargeback_status_friendly_reverse)
        ChargebackProcessStatus.REVERSAO_ARBITRAGEM.name -> label = context.getString(R.string.chargeback_status_reversal_arbitration)
        ChargebackProcessStatus.FEE_COLLECTION.name -> label = context.getString(R.string.chargeback_status_fee_collection)
        ChargebackProcessStatus.REVERSAO_FEE_COLLECTION.name -> label = context.getString(R.string.chargeback_status_reversal_fee_collection)
        ChargebackProcessStatus.RECLAMACAO_DEBITO.name -> label = context.getString(R.string.chargeback_status_debit_claim)
        ChargebackProcessStatus.REVERSAO_RECLAMACAO_DEBITO.name -> label = context.getString(R.string.chargeback_status_reversal_debit_claim)
        ChargebackProcessStatus.CHARGEBACK_VOUCHER.name -> label = context.getString(R.string.chargeback_status_voucher)
        ChargebackProcessStatus.REVERSAO_CHARGEBACK_VOUCHER.name -> label = context.getString(R.string.chargeback_status_reversal_voucher)
        ChargebackProcessStatus.PRE_ARBITRAGEM.name -> label = context.getString(R.string.chargeback_status_pre_arbitration)
    }
    return label
}