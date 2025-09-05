package br.com.mobicare.cielo.posVirtual.utils

import br.com.mobicare.cielo.posVirtual.analytics.PosVirtualAnalytics.Companion.PRODUCT_CIELO_TAP
import br.com.mobicare.cielo.posVirtual.analytics.PosVirtualAnalytics.Companion.PRODUCT_PAYMENT_LINK
import br.com.mobicare.cielo.posVirtual.analytics.PosVirtualAnalytics.Companion.QRCODE_PIX

enum class PosVirtualProductTypeEnum(val label: String, val labelGa4: String) {
    CIELO_TAP("Cielo Tap", PRODUCT_CIELO_TAP),
    PAYMENT_LINK("Link de Pagamento", PRODUCT_PAYMENT_LINK),
    QR_CODE_PIX("QR Code Pix", QRCODE_PIX)
}