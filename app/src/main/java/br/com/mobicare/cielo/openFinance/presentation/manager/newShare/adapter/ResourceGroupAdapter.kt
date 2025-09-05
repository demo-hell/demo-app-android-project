package br.com.mobicare.cielo.openFinance.presentation.manager.newShare.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.LayoutOpenFinanceResourceGroupItemBinding
import br.com.mobicare.cielo.openFinance.domain.model.ResourceGroup

class ResourceGroupAdapter(private val resourceGroupList: MutableList<ResourceGroup>) : RecyclerView.Adapter<OpenFinanceResourceGroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpenFinanceResourceGroupViewHolder {
        val item = LayoutOpenFinanceResourceGroupItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )
        return OpenFinanceResourceGroupViewHolder(item)
    }

    override fun getItemCount(): Int {
        return resourceGroupList.count()
    }

    override fun onBindViewHolder(holder: OpenFinanceResourceGroupViewHolder, position: Int) {
        holder.bind(resourceGroupList[position])
    }
}

class OpenFinanceResourceGroupViewHolder(private val binding: LayoutOpenFinanceResourceGroupItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(resourceGroup: ResourceGroup) {
        binding.apply {
            tvResourceGroup.text = resourceGroup.displayName
            rvPermission.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = PermissionAdapter(resourceGroup.permission.toMutableList())
            }
        }
    }

}