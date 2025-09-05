package br.com.mobicare.cielo.pix.ui.extract.detail.views

import android.view.LayoutInflater
import br.com.mobicare.cielo.pix.domain.*
import br.com.mobicare.cielo.pix.enums.TransactionTypeEnum

class AmountViewSelector(
    private val layoutInflater: LayoutInflater,
    private val transfer: TransferDetailsResponse,
    private val transactionType: TransactionTypeEnum? = null,
    private val applyStrikeThrough: Boolean = false
) {

    private var onFeeClick: ((Fee?) -> Unit)? = null
    private var onNetAmountClick: ((Settlement?) -> Unit)? = null
    private var onReceivingAmountClick: ((Credit?) -> Unit)? = null
    private var onReceivingDetailsClick: ((Credit?) -> Unit)? = null

    private val selectedTransactionType get() = transactionType?.name ?: transfer.transactionType

    operator fun invoke() = when (selectedTransactionType) {
        TransactionTypeEnum.TRANSFER_CREDIT.name -> buildCreditView()
        TransactionTypeEnum.TRANSFER_DEBIT.name -> buildDebitView()
        else -> buildSimpleView()
    }

    private fun buildCreditView() = CreditAmountViewBuilder(layoutInflater, transfer).run {
        setStrikeThrough(applyStrikeThrough)
        setOnFeeClickListener { onFeeClick?.invoke(it) }
        setOnNetAmountClickListener { onNetAmountClick?.invoke(it) }
        build()
    }

    private fun buildDebitView() = DebitAmountViewBuilder(layoutInflater, transfer).run {
        setStrikeThrough(applyStrikeThrough)
        setOnReceivingAmountClickListener { onReceivingAmountClick?.invoke(it) }
        setOnNetAmountClickListener { onNetAmountClick?.invoke(it) }
        setOnReceivingDetailsClickListener { onReceivingDetailsClick?.invoke(it) }
        build()
    }

    private fun buildSimpleView() = SimpleAmountViewBuilder(layoutInflater, transfer).run {
        setTransactionType(TransactionTypeEnum.TRANSFER_DEBIT)
        setStrikeThrough(applyStrikeThrough)
        build()
    }

    fun setOnFeeClickListener(listener: (Fee?) -> Unit): AmountViewSelector {
        onFeeClick = listener
        return this
    }

    fun setOnNetAmountClickListener(listener: (Settlement?) -> Unit): AmountViewSelector {
        onNetAmountClick = listener
        return this
    }

    fun setOnReceivingAmountClickListener(listener: (Credit?) -> Unit): AmountViewSelector {
        onReceivingAmountClick = listener
        return this
    }

    fun setOnReceivingDetailsClickListener(listener: (Credit?) -> Unit): AmountViewSelector {
        onReceivingDetailsClick = listener
        return this
    }

}