package br.com.mobicare.cielo.openFinance.presentation.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.CieloApplication.Companion.context
import br.com.mobicare.cielo.commons.utils.SIMPLE_DATE_FORMAT_DATE_TIME_FULL_MINUS
import br.com.mobicare.cielo.commons.utils.SIMPLE_DT_FORMAT_MASK
import br.com.mobicare.cielo.databinding.LayoutOpenFinanceSharedDataItemBinding
import br.com.mobicare.cielo.extensions.formatterDate
import br.com.mobicare.cielo.openFinance.data.model.response.Items
import br.com.mobicare.cielo.openFinance.presentation.manager.sharedData.OpenFinanceSharedDataFragmentDirections
import br.com.mobicare.cielo.openFinance.presentation.utils.CheckStatus.getStatus
import br.com.mobicare.cielo.openFinance.presentation.utils.DefaultIconBank.checkTypeImage

class OpenFinanceSharedDataAdapter : RecyclerView.Adapter<OpenFinanceSharedDataViewHolder>() {
    private val sharedDataList: MutableList<Items> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OpenFinanceSharedDataViewHolder {
        val item = LayoutOpenFinanceSharedDataItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OpenFinanceSharedDataViewHolder(item)
    }

    override fun getItemCount(): Int {
        return sharedDataList.count()
    }

    override fun onBindViewHolder(holder: OpenFinanceSharedDataViewHolder, position: Int) {
        holder.bind(sharedDataList[position])
    }

    fun update(items: List<Items>) {
        sharedDataList.addAll(items)
        notifyDataSetChanged()
    }

    fun clear() {
        sharedDataList.clear()
        notifyDataSetChanged()
    }
}

class OpenFinanceSharedDataViewHolder(private val binding: LayoutOpenFinanceSharedDataItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(sharedDataOPF: Items) {
        binding.apply {
            tvBank.text = sharedDataOPF.organizationName
            tvDescDate.text =
                context.getString(getStatus(context, sharedDataOPF.consentStatus).desc)
            tvDate.text = sharedDataOPF.expirationDateTime.formatterDate(
                SIMPLE_DATE_FORMAT_DATE_TIME_FULL_MINUS,
                SIMPLE_DT_FORMAT_MASK
            )
            tvStatus.text = sharedDataOPF.consentStatus
            tvStatus.setTextAppearance(getStatus(context, sharedDataOPF.consentStatus).textStyle)
            iconStatus.setImageResource(getStatus(context, sharedDataOPF.consentStatus).icon)
            contentStatus.setBackgroundResource(
                getStatus(
                    context,
                    sharedDataOPF.consentStatus
                ).background
            )
            checkTypeImage(sharedDataOPF.logo, iconBank, context)
            containerItemSharedData.setOnClickListener {
                binding.root.findNavController().navigate(
                    OpenFinanceSharedDataFragmentDirections
                        .actionOpenFinanceSharedDataFragmentToConsentDetailFragment(
                            sharedDataOPF.consentId,
                            sharedDataOPF.logo,
                            false
                        )
                )
            }
        }
    }
}