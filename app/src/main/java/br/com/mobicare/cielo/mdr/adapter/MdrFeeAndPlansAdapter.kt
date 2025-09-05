package br.com.mobicare.cielo.mdr.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.databinding.BrandsTaxMdrItemBinding
import br.com.mobicare.cielo.mdr.domain.model.CardFees

class MdrFeeAndPlansAdapter() : RecyclerView.Adapter<MdrFeeAndPlansViewHolder>() {
    private var mCardFeesList: List<CardFees>? = null

    constructor(
        cardFeesList: List<CardFees>?,
    ) : this() {
        mCardFeesList = cardFeesList
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MdrFeeAndPlansViewHolder {
        val binding =
            BrandsTaxMdrItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MdrFeeAndPlansViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MdrFeeAndPlansViewHolder,
        position: Int,
    ) {
        val currentItem = mCardFeesList?.get(position)
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return mCardFeesList?.size ?: ZERO
    }
}
