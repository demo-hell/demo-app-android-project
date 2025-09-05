package br.com.mobicare.cielo.posVirtual.presentation.accreditation.offer.ratesDetails.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.LayoutPosVirtualAccreditationRatesDetailsCardItemBinding
import br.com.mobicare.cielo.posVirtual.domain.model.RateUI
import br.com.mobicare.cielo.posVirtual.presentation.accreditation.offer.ratesDetails.viewHolder.PosVirtualAccreditationRatesDetailsBrandRateViewHolder

class PosVirtualAccreditationRatesDetailsBrandRateAdapter() :
    RecyclerView.Adapter<PosVirtualAccreditationRatesDetailsBrandRateViewHolder>() {

    private var rates: ArrayList<RateUI> = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PosVirtualAccreditationRatesDetailsBrandRateViewHolder {
        val installment = LayoutPosVirtualAccreditationRatesDetailsCardItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PosVirtualAccreditationRatesDetailsBrandRateViewHolder(installment)
    }

    override fun onBindViewHolder(
        holder: PosVirtualAccreditationRatesDetailsBrandRateViewHolder,
        position: Int
    ) = holder.bind(rates[position])

    override fun getItemCount() = rates.size

    fun setRates(list: List<RateUI>) {
        rates.clear()
        rates.addAll(list)
    }

}