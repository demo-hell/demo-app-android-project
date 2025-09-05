package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.ui.viewHolder.ProgressBarViewHolder
import br.com.mobicare.cielo.commons.utils.ifNull
import br.com.mobicare.cielo.databinding.ItemPixExtractTransactionBinding
import br.com.mobicare.cielo.databinding.LayoutPixExtractClearFilterBinding
import br.com.mobicare.cielo.databinding.LayoutPixExtractFilterBinding
import br.com.mobicare.cielo.databinding.LayoutProgressBarBinding
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.PixReceiptsTab
import br.com.mobicare.cielo.pixMVVM.domain.model.PixExtract.PixExtractReceipt
import br.com.mobicare.cielo.pixMVVM.domain.model.PixReceiptsScheduled
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.PixExtractTransactionAdapterTypeEnum.CLEAR_FILTER
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.PixExtractTransactionAdapterTypeEnum.FILTER
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.PixExtractTransactionAdapterTypeEnum.LOADING
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.PixExtractTransactionAdapterTypeEnum.RECEIPT
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.PixExtractTransactionAdapterTypeEnum.RECEIPT_SCHEDULED
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.listener.PixExtractFilterListener
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.model.PixExtractFilterModel
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.viewHolder.PixExtractClearFilterViewHolder
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.viewHolder.PixExtractFilterViewHolder
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.viewHolder.PixExtractTransactionViewHolder

@SuppressLint("NotifyDataSetChanged")
class PixExtractTransactionAdapter(
    private val pageType: PixReceiptsTab,
    private val listener: PixExtractFilterListener,
    private val onItemClick: (Any) -> Unit,
) : RecyclerView.Adapter<ViewHolder>() {
    private var items: ArrayList<PixExtractItemAdapterModel> = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder =
        when (viewType) {
            RECEIPT.ordinal, RECEIPT_SCHEDULED.ordinal -> {
                val binding =
                    ItemPixExtractTransactionBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                PixExtractTransactionViewHolder(binding)
            }

            LOADING.ordinal -> {
                val binding =
                    LayoutProgressBarBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                ProgressBarViewHolder(binding)
            }

            FILTER.ordinal -> {
                val binding =
                    LayoutPixExtractFilterBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                PixExtractFilterViewHolder(binding, pageType, listener)
            }

            else -> {
                val binding =
                    LayoutPixExtractClearFilterBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                PixExtractClearFilterViewHolder(binding, listener)
            }
        }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        when (holder) {
            is PixExtractTransactionViewHolder -> {
                items[position].receipt.ifNull { items[position].receiptScheduled }?.let { holder.bind(it, onItemClick) }
            }

            is PixExtractFilterViewHolder -> {
                items[position].filterData?.let { holder.bind(it) }
            }

            is PixExtractClearFilterViewHolder -> {
                holder.bind()
            }

            is ProgressBarViewHolder -> {
                holder.bind()
            }
        }
    }

    override fun getItemViewType(position: Int): Int = items[position].type.ordinal

    fun setFilterData(value: PixExtractFilterModel) {
        if (items.isNotEmpty()) {
            items[ZERO].filterData = value
            notifyDataSetChanged()
        } else {
            addFilter()
        }
    }

    fun addTransactions(list: ArrayList<Any>) {
        if (items.isEmpty()) addFilter()

        items.addAll(
            list.map {
                when (it) {
                    is PixExtractReceipt ->
                        PixExtractItemAdapterModel(
                            type = RECEIPT,
                            receipt = it,
                        )

                    is PixReceiptsScheduled.Item.Receipt ->
                        PixExtractItemAdapterModel(
                            type = RECEIPT_SCHEDULED,
                            receiptScheduled = it,
                        )

                    else -> throw IllegalArgumentException("Invalid type")
                }
            },
        )

        notifyDataSetChanged()
    }

    fun clearTransactions() {
        items.removeIf { it.type != FILTER }
        notifyDataSetChanged()
    }

    fun showLoading(isShow: Boolean) {
        if (isShow) showProgressBar() else hideProgressBar()
    }

    fun showClearFilter() {
        clearTransactions()

        items.add(PixExtractItemAdapterModel(type = CLEAR_FILTER))
        notifyDataSetChanged()
    }

    private fun showProgressBar() {
        items.add(PixExtractItemAdapterModel(type = LOADING, receipt = null))
        notifyDataSetChanged()
    }

    private fun hideProgressBar() {
        val lastItem = items.lastOrNull()
        if (lastItem?.type == LOADING) {
            items.removeLast()
            notifyDataSetChanged()
        }
    }

    private fun addFilter() {
        items.add(PixExtractItemAdapterModel(type = FILTER, filterData = PixExtractFilterModel()))
        notifyDataSetChanged()
    }
}
