package br.com.mobicare.cielo.posVirtual.presentation.accreditation.offer.ratesDetails.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.Brand
import br.com.mobicare.cielo.databinding.LayoutPosVirtualAccreditationRatesDetailsCardBinding
import br.com.mobicare.cielo.posVirtual.presentation.accreditation.offer.ratesDetails.viewHolder.PosVirtualAccreditationRatesDetailsBrandViewHolder

class PosVirtualAccreditationRatesDetailsBrandAdapter :
    RecyclerView.Adapter<PosVirtualAccreditationRatesDetailsBrandViewHolder>() {

    private var brands: ArrayList<Brand> = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PosVirtualAccreditationRatesDetailsBrandViewHolder {
        val brand = LayoutPosVirtualAccreditationRatesDetailsCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PosVirtualAccreditationRatesDetailsBrandViewHolder(brand)
    }

    override fun onBindViewHolder(
        holder: PosVirtualAccreditationRatesDetailsBrandViewHolder,
        position: Int
    ) = holder.bind(brands[position])

    override fun getItemCount() = brands.size

    fun setBrands(list: List<Brand>) {
        brands.clear()
        brands.addAll(list)
    }

}