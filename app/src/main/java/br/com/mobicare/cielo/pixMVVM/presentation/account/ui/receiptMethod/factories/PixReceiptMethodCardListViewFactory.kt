package br.com.mobicare.cielo.pixMVVM.presentation.account.ui.receiptMethod.factories

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.databinding.LayoutPixReceiptMethodCardBinding
import br.com.mobicare.cielo.pixMVVM.presentation.account.enums.PixReceiptMethod
import br.com.mobicare.cielo.pixMVVM.presentation.account.ui.receiptMethod.builders.PixCieloAccountActiveListViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.account.ui.receiptMethod.builders.PixScheduledTransferActiveListViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.account.ui.receiptMethod.builders.PixTransferBySaleActiveListViewBuilder

class PixReceiptMethodCardListViewFactory(
    private val context: Context,
    private val layoutInflater: LayoutInflater,
    private val containerView: ViewGroup,
    private val onCieloAccountTap: (PixReceiptMethod) -> Unit,
    private val onTransferBySaleTap: (PixReceiptMethod) -> Unit,
    private val onScheduledTransferTap: (PixReceiptMethod) -> Unit,
) {
    private var receiptMethod: PixReceiptMethod? = null
    private var ftScheduledTransferEnabled: Boolean? = null

    fun create(
        receiptMethod: PixReceiptMethod?,
        ftScheduledTransferEnabled: Boolean,
        scheduledList: List<String>? = null,
    ) {
        this.receiptMethod = receiptMethod
        this.ftScheduledTransferEnabled = ftScheduledTransferEnabled

        containerView.removeAllViews()

        when (receiptMethod) {
            PixReceiptMethod.CIELO_ACCOUNT -> buildCieloAccountActiveListView()
            PixReceiptMethod.TRANSFER_BY_SALE -> buildTransferBySaleActiveListView()
            PixReceiptMethod.SCHEDULED_TRANSFER -> buildScheduledTransferActiveListView(scheduledList)
            else -> throw IllegalArgumentException(EXCEPTION_MESSAGE)
        }
    }

    private fun buildCieloAccountActiveListView() {
        PixCieloAccountActiveListViewBuilder(
            context,
            layoutInflater,
            onTap = { onTap(it, PixReceiptMethod.CIELO_ACCOUNT) },
        )
            .build()
            .filterButtons()
            .forEach { containerView.addView(it) }
    }

    private fun buildTransferBySaleActiveListView() {
        PixTransferBySaleActiveListViewBuilder(
            context,
            layoutInflater,
            onTap = { onTap(it, PixReceiptMethod.TRANSFER_BY_SALE) },
        )
            .build()
            .filterButtons()
            .forEach { containerView.addView(it) }
    }

    private fun buildScheduledTransferActiveListView(scheduledList: List<String>?) {
        PixScheduledTransferActiveListViewBuilder(
            context,
            layoutInflater,
            scheduledList,
            onTap = { onTap(it, PixReceiptMethod.SCHEDULED_TRANSFER) },
        )
            .build()
            .filterButtons()
            .forEach { containerView.addView(it) }
    }

    private fun onTap(
        receiptMethod: PixReceiptMethod,
        activeReceiptMethod: PixReceiptMethod,
    ) {
        when (receiptMethod) {
            PixReceiptMethod.CIELO_ACCOUNT -> onCieloAccountTap(activeReceiptMethod)
            PixReceiptMethod.TRANSFER_BY_SALE -> onTransferBySaleTap(activeReceiptMethod)
            PixReceiptMethod.SCHEDULED_TRANSFER -> onScheduledTransferTap(activeReceiptMethod)
        }
    }

    private fun List<View>.filterButtons(): List<View> {
        return if (receiptMethod == PixReceiptMethod.SCHEDULED_TRANSFER || ftScheduledTransferEnabled == true) {
            this
        } else {
            this.filter {
                val bind = LayoutPixReceiptMethodCardBinding.bind(it)
                bind.tvTitle.text != context.getString(PixReceiptMethod.SCHEDULED_TRANSFER.titleRes)
            }
        }
    }

    companion object {
        private const val EXCEPTION_MESSAGE = "A valid receipt method should be passed"
    }
}
