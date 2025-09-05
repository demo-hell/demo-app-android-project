package br.com.mobicare.cielo.accessManager.addUser

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.changeEc.domain.Hierarchy
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.databinding.CardAccessManagerRoleGroupEstablishmentBinding

class AccessManagerAddUserEstablishmentAdapter(
    val merchants: ArrayList<Hierarchy>
) : RecyclerView.Adapter<AccessManagerAddUserEstablishmentAdapter.AccessManagerAddUserEstablishmentViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AccessManagerAddUserEstablishmentViewHolder {
        val binding = CardAccessManagerRoleGroupEstablishmentBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AccessManagerAddUserEstablishmentViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(
        holder: AccessManagerAddUserEstablishmentViewHolder,
        position: Int
    ) {
        holder.bind(merchants[position])
    }

    override fun getItemCount(): Int {
        return merchants.size
    }

    inner class AccessManagerAddUserEstablishmentViewHolder(
        private val binding: CardAccessManagerRoleGroupEstablishmentBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(merchant: Hierarchy) {
            if (ValidationUtils.isCPF(merchant.cnpj.completo)) {
                binding.tvsubtitleLabel.text = context.getString(R.string.cpf_label)
            }
            binding.tvtitle.text = merchant.nomeFantasia
            binding.tvsubtitle.text = merchant.cnpj.completo
            binding.tvec.text = merchant.id
        }
    }
}

