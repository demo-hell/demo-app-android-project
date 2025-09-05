package br.com.mobicare.cielo.component.impersonate.presentation.fragment.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.LayoutImpersonateItemOptionEcBinding
import br.com.mobicare.cielo.component.impersonate.presentation.fragment.viewHolder.ImpersonateMerchantsViewHolder
import br.com.mobicare.cielo.component.impersonate.presentation.model.MerchantUI

class ImpersonateMerchantsAdapter(
    private val onTap: (String) -> Unit
) : RecyclerView.Adapter<ImpersonateMerchantsViewHolder>() {

    private var merchants: ArrayList<MerchantUI> = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImpersonateMerchantsViewHolder {
        val merchant = LayoutImpersonateItemOptionEcBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImpersonateMerchantsViewHolder(merchant)
    }

    override fun onBindViewHolder(
        holder: ImpersonateMerchantsViewHolder,
        position: Int
    ) = holder.bind(merchants[position], onTap)

    override fun getItemCount() = merchants.size

    @SuppressLint("NotifyDataSetChanged")
    fun setMerchants(list: List<MerchantUI>) {
        merchants.clear()
        merchants.addAll(list)
        notifyDataSetChanged()
    }

}