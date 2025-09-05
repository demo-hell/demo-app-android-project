package br.com.mobicare.cielo.eventTracking.presentation.ui.machineRequest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.cielo.libflue.util.ONE
import br.com.cielo.libflue.util.ZERO
import br.com.cielo.libflue.util.extensions.gone
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.databinding.MachineRequestItemBinding
import br.com.mobicare.cielo.databinding.MachineRequestItemShimmerBinding
import br.com.mobicare.cielo.eventTracking.domain.model.EventRequestStatus
import br.com.mobicare.cielo.eventTracking.domain.model.MachineRequest
import br.com.mobicare.cielo.eventTracking.utils.MachineRequestItem

class MachineRequestAdapter(private val requests: List<MachineRequestItem>, private val onClickCallback: (View, MachineRequest) -> Unit) : RecyclerView.Adapter<ViewHolder>() {

    private val context = CieloApplication.context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MachineRequestItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        val bindingShimmer = MachineRequestItemShimmerBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return when (viewType) {
            ZERO -> MachineRequestViewHolder(binding)
            else -> MachineRequestShimmerViewHolder(bindingShimmer)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        requests.getOrNull(position)?.let { machineRequestItem ->
            when (holder) {
                is MachineRequestViewHolder -> {
                    holder.bind(machineRequestItem as MachineRequest)
                }

                is MachineRequestShimmerViewHolder -> {
                    holder.bind()
                }
            }
        }
    }

    override fun getItemCount(): Int = requests.size

    override fun getItemViewType(position: Int): Int {
        return when (requests.getOrNull(position)) {
            is MachineRequest -> ZERO
            else -> ONE
        }
    }

    inner class MachineRequestShimmerViewHolder(private val mBinding: MachineRequestItemShimmerBinding) :
        ViewHolder(mBinding.root) {
        fun bind() {
            mBinding.shimmerMachineRequestItem.itemEventRequestShimmer.startShimmer()
        }
    }

    inner class MachineRequestViewHolder(private val mBinding: MachineRequestItemBinding) :
        ViewHolder(mBinding.root) {
        fun bind(request: MachineRequest) {

            mBinding.root.setOnClickListener {
                onClickCallback(itemView, request)
            }

            mBinding.itemMachineName.text = request.requestMachine?.first()?.name
            mBinding.itemMachineChangeDirectionImage.gone()
            mBinding.itemMachineChangeName.gone()

            mBinding.itemMachineSolicitationTypeValue.text = request.requestType.lowercase().replaceFirstChar { it.uppercase() }
            mBinding.itemMachineSolicitationDateValue.text = request.requestDate
            mBinding.itemMachineSolicitationDateAttendedValue.text = request.requestAttendedDate
            request.requestStatus?.let { machineRequestStatus ->
                mBinding.itemMachineStatusTag.chipIcon = AppCompatResources.getDrawable(context, machineRequestStatus.statusIcon)
                mBinding.itemMachineStatusTag.chipIconTint = context.getColorStateList(machineRequestStatus.statusIconTint)
                mBinding.itemMachineStatusTag.setTextColor(context.getColorStateList(machineRequestStatus.statusIconTint))
                mBinding.itemMachineStatusTag.chipBackgroundColor = context.getColorStateList(machineRequestStatus.statusBackgroundColor)
                mBinding.itemMachineStatusTag.text = context.getString(machineRequestStatus.statusText)

                when (machineRequestStatus) {
                    EventRequestStatus.UNREALIZED -> {
                        mBinding.itemMachineSolicitationDateAttendedTitle.gone()
                        mBinding.itemMachineSolicitationDateAttendedValue.gone()
                    }

                    EventRequestStatus.ATTENDED -> {
                        mBinding.itemMachineSolicitationDateAttendedTitle.text = context.getString(R.string.attended_date_point)
                    }

                    else -> {
                        mBinding.itemMachineSolicitationDateAttendedTitle.text = context.getString(R.string.estimated)
                    }
                }
            }
        }
    }
}