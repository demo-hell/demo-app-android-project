package br.com.mobicare.cielo.chargeback.domain.enum

import br.com.mobicare.cielo.R

enum class DescriptionReasonEnum(val position: Int, val message: Int) {
    ONE(1, R.string.chargeback_reason_message_type1),
    TWO(2, R.string.chargeback_reason_message_type2),
    THREE(3, R.string.chargeback_reason_message_type3)
}