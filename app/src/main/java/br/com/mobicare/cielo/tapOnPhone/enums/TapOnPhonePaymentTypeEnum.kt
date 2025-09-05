package br.com.mobicare.cielo.tapOnPhone.enums

import br.com.mobicare.cielo.tapOnPhone.analytics.TapOnPhoneAnalytics
import br.com.mobicare.cielo.tapOnPhone.analytics.TapOnPhoneGA4

enum class TapOnPhonePaymentTypeEnum(val tag: String, val tagGa4: String) {
    DEBIT(TapOnPhoneAnalytics.DEBIT, TapOnPhoneGA4.DEBIT),
    CREDIT(TapOnPhoneAnalytics.CREDIT, TapOnPhoneGA4.CREDIT),
    INSTALLMENT(TapOnPhoneAnalytics.INSTALLMENT_CREDIT, TapOnPhoneGA4.INSTALLMENT_CREDIT)
}