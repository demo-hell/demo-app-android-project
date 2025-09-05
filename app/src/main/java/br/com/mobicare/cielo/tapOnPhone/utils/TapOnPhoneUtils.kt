package br.com.mobicare.cielo.tapOnPhone.utils

import android.os.Bundle
import br.com.mobicare.cielo.commons.constants.ONE_TEXT
import br.com.mobicare.cielo.tapOnPhone.constants.DEVICE_TAP_ARGS
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneTerminalResponse
import br.com.mobicare.cielo.tapOnPhone.enums.TapOnPhonePaymentTypeEnum
import com.symbiotic.taponphone.Enums.AidType
import com.symbiotic.taponphone.Enums.TransactionType

fun tapPaymentType(transactionType: String?, installment: String?) =
    when {
        transactionType == TransactionType.Payment.name
                || transactionType == AidType.Debit.name -> TapOnPhonePaymentTypeEnum.DEBIT

        (installment ?: ONE_TEXT) > ONE_TEXT -> TapOnPhonePaymentTypeEnum.INSTALLMENT
        else -> TapOnPhonePaymentTypeEnum.CREDIT
    }

fun deviceBundle(device: TapOnPhoneTerminalResponse?) = Bundle().apply {
    putParcelable(
        DEVICE_TAP_ARGS, device
    )
}