package br.com.mobicare.cielo.eventTracking.presentation.ui.machineRequestDetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.databinding.MachineEquipmentsItemBinding
import br.com.mobicare.cielo.eventTracking.domain.model.Machine
import br.com.mobicare.cielo.extensions.ifNullSimpleLine

class MachineDetailsAdapter(private val requestType: String, private val machineList: List<Machine?>?) :
    RecyclerView.Adapter<MachineDetailsAdapter.MachineDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MachineDetailViewHolder {
        val binding = MachineEquipmentsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MachineDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MachineDetailViewHolder, position: Int) {
        holder.bind(machineList?.getOrNull(position))
    }

    override fun getItemCount(): Int = machineList?.size ?: 0

    inner class MachineDetailViewHolder(private val mBinding: MachineEquipmentsItemBinding) : ViewHolder(mBinding.root) {
        fun bind(machine: Machine?) {
            mBinding.apply {
                machineDetailNameValue.text = machine?.name.ifNullSimpleLine()
                machineDetailTechnicalIdValue.text =
                    itemView.context.getString(
                        R.string.machine_logical_number_value,
                        machine?.logicalID.ifNullSimpleLine()
                    )
                machine?.photo?.let { photo ->
                    machineDetailPhoto.setImageDrawable(ContextCompat.getDrawable(itemView.context, photo))
                }
                itemMachineSolicitationTypeValue.text = requestType.lowercase().replaceFirstChar { it.uppercase() }
            }
        }
    }
}