package br.com.mobicare.cielo.pixMVVM.presentation.account.ui.scheduledTransfer.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.databinding.ItemPixScheduledTransferHourBinding

class PixScheduledTransferHoursAdapter
    : RecyclerView.Adapter<PixScheduledTransferHoursAdapter.PixScheduledTransferHourViewHolder>() {

    private val data = listOf(
        TimeData(HOUR_06_00),
        TimeData(HOUR_12_00),
        TimeData(HOUR_17_30, isEnabled = false, isChecked = true),
        TimeData(HOUR_22_00, isEnabled = false, isChecked = true)
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PixScheduledTransferHourViewHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pix_scheduled_transfer_hour, parent, false)
            .let { PixScheduledTransferHourViewHolder(it) }
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: PixScheduledTransferHourViewHolder, position: Int) {
        holder.bind(data[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCheckedHours(hours: List<String>) {
        hours.forEach { hour -> data.find { it.hour == hour }?.isChecked = true }
        notifyDataSetChanged()
    }

    fun getCheckedHours() = data
        .filter { it.isChecked }
        .map { it.hour }

    inner class PixScheduledTransferHourViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemPixScheduledTransferHourBinding.bind(itemView)

        fun bind(data: TimeData) {
            binding.checkButton.apply {
                text = context.getString(R.string.pix_account_scheduled_transfer_configure_daily_at, data.hour)
                isChecked = data.isChecked
                isCardEnabled = data.isEnabled
                setOnCheckedChangeListener { isChecked, _ -> data.isChecked = isChecked }
            }
        }
    }

    data class TimeData(
        val hour: String,
        val isEnabled: Boolean = true,
        var isChecked: Boolean = false
    )

    companion object {
        private const val HOUR_06_00 = "06:00"
        private const val HOUR_12_00 = "12:00"
        private const val HOUR_17_30 = "17:30"
        private const val HOUR_22_00 = "22:00"
    }

}