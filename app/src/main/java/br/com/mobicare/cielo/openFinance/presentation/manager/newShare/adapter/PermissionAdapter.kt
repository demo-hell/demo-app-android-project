package br.com.mobicare.cielo.openFinance.presentation.manager.newShare.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.LayoutOpenFinancePermissionItemBinding
import br.com.mobicare.cielo.openFinance.domain.model.Permission

class PermissionAdapter(private val permissionList: MutableList<Permission>): RecyclerView.Adapter<OpenFinancePermisionViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpenFinancePermisionViewHolder {
            val item = LayoutOpenFinancePermissionItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return OpenFinancePermisionViewHolder(item)
        }

        override fun getItemCount(): Int {
            return permissionList.count()
        }

        override fun onBindViewHolder(holder: OpenFinancePermisionViewHolder, position: Int) {
            holder.bind(permissionList[position])
        }
}

class OpenFinancePermisionViewHolder(private val binding: LayoutOpenFinancePermissionItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(permission: Permission) {
        binding.apply {
            tvTitlePermission.text = permission.displayName
            tvDetailPermission.text = permission.detail
        }
    }
}