package br.com.mobicare.cielo.pixMVVM.presentation.account.ui.receiptMethod.builders

import android.content.Context
import android.view.LayoutInflater
import br.com.mobicare.cielo.pixMVVM.presentation.account.enums.PixReceiptMethod

class PixScheduledTransferActiveListViewBuilder(
    context: Context,
    layoutInflater: LayoutInflater,
    private val scheduleHourList: List<String>? = null,
    private val onTap: (PixReceiptMethod) -> Unit
) : PixReceiptMethodListViewBuilder(context, layoutInflater) {

    override fun build() = listOf(
        buildCardView(
            receiptMethod = PixReceiptMethod.SCHEDULED_TRANSFER,
            isActiveModel = true,
            scheduleHourList = if (scheduleHourList.isNullOrEmpty()) null else scheduleHourList,
            onTap = { onTap(it) }
        ),
        buildCardView(
            receiptMethod = PixReceiptMethod.TRANSFER_BY_SALE,
            onTap = { onTap(it) }
        ),
        buildCardView(
            receiptMethod = PixReceiptMethod.CIELO_ACCOUNT,
            onTap = { onTap(it) }
        )
    )

}