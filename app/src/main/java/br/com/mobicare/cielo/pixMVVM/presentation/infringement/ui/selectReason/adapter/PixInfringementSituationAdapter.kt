package br.com.mobicare.cielo.pixMVVM.presentation.infringement.ui.selectReason.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.databinding.ItemPixInfringementReasonBinding
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixEligibilityInfringementResponse
import br.com.mobicare.cielo.pixMVVM.presentation.infringement.ui.selectReason.viewHolder.PixInfringementSituationViewHolder

class PixInfringementSituationAdapter(
    private val onClick: (PixEligibilityInfringementResponse.Situation) -> Unit
) : RecyclerView.Adapter<PixInfringementSituationViewHolder>() {

    private var items: ArrayList<PixEligibilityInfringementResponse.Situation> = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PixInfringementSituationViewHolder {
        val binding = ItemPixInfringementReasonBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PixInfringementSituationViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: PixInfringementSituationViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun setItems(items: List<PixEligibilityInfringementResponse.Situation>) {
        this.items.clear()
        this.items.addAll(items)
        notifyItemRangeInserted(ZERO, itemCount)
    }

}