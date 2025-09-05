package br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.fees.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.Brand
import br.com.mobicare.cielo.databinding.LayoutTapOnPhoneBrandsItemBinding

class TapOnPhoneInstallmentFeesAdapter(
    private val items: List<Brand>,
    private val context: Context,
) : RecyclerView.Adapter<TapOnPhoneInstallmentFeesViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TapOnPhoneInstallmentFeesViewHolder {
        val binding = LayoutTapOnPhoneBrandsItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TapOnPhoneInstallmentFeesViewHolder(binding, context)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: TapOnPhoneInstallmentFeesViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
