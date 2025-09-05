package br.com.mobicare.cielo.pixMVVM.presentation.account.ui.receiptMethod.builders

import android.content.Context
import android.view.LayoutInflater
import br.com.mobicare.cielo.pixMVVM.presentation.account.enums.PixReceiptMethod

class PixCieloAccountActiveListViewBuilder(
    context: Context,
    layoutInflater: LayoutInflater,
    private val onTap: (PixReceiptMethod) -> Unit
) : PixReceiptMethodListViewBuilder(context, layoutInflater) {

    override fun build() = listOf(
        buildCardView(
            receiptMethod = PixReceiptMethod.CIELO_ACCOUNT,
            isActiveModel = true,
        ),
        buildCardView(
            receiptMethod = PixReceiptMethod.TRANSFER_BY_SALE,
            onTap = { onTap(it) }
        ),
        buildCardView(
            receiptMethod = PixReceiptMethod.SCHEDULED_TRANSFER,
            onTap = { onTap(it) }
        )
    )

}