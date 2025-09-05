package br.com.mobicare.cielo.pixMVVM.presentation.account.ui.receiptMethod.builders

import android.content.Context
import android.view.LayoutInflater
import br.com.mobicare.cielo.pixMVVM.presentation.account.enums.PixReceiptMethod

class PixTransferBySaleActiveListViewBuilder(
    context: Context,
    layoutInflater: LayoutInflater,
    private val onTap: (PixReceiptMethod) -> Unit
) : PixReceiptMethodListViewBuilder(context, layoutInflater) {

    override fun build() = listOf(
        buildCardView(
            receiptMethod = PixReceiptMethod.TRANSFER_BY_SALE,
            isActiveModel = true
        ),
        buildCardView(
            receiptMethod = PixReceiptMethod.SCHEDULED_TRANSFER,
            onTap = { onTap(it) }
        ),
        buildCardView(
            receiptMethod = PixReceiptMethod.CIELO_ACCOUNT,
            onTap = { onTap(it) }
        )
    )

}