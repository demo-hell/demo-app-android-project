package br.com.mobicare.cielo.openFinance.presentation.manager.newShare.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.LayoutOpenFinanceInstitutionItemBinding
import br.com.mobicare.cielo.openFinance.domain.model.Institution

class InstitutionAdapter(private val institutionList: List<Institution>) : RecyclerView.Adapter<OpenFinanceInstitutionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpenFinanceInstitutionViewHolder {
        val item = LayoutOpenFinanceInstitutionItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )
        return OpenFinanceInstitutionViewHolder(item)
    }

    override fun getItemCount(): Int {
        return institutionList.count()
    }

    override fun onBindViewHolder(holder: OpenFinanceInstitutionViewHolder, position: Int) {
        holder.bind(institutionList[position])
    }
}

class OpenFinanceInstitutionViewHolder(private val binding: LayoutOpenFinanceInstitutionItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(institution: Institution) {
        binding.apply {
            tvInstitution.text = institution.organizationName
        }
    }
}