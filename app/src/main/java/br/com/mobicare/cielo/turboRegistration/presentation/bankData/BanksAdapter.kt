package br.com.mobicare.cielo.turboRegistration.presentation.bankData

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.helpers.BrandCardHelper
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.databinding.BankAccountItemBinding
import br.com.mobicare.cielo.extensions.capitalizeWords
import br.com.mobicare.cielo.extensions.formatBankName
import br.com.mobicare.cielo.turboRegistration.domain.model.Bank

class BanksAdapter(
    private val onBankSelected: (Bank) -> Unit,
    private var selectedBank: Bank? = null
) : RecyclerView.Adapter<BanksAdapter.BanksViewHolder>() {

    private val bankList = mutableListOf<Bank>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BanksViewHolder {
        val binding =
            BankAccountItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BanksViewHolder(binding)
    }

    override fun getItemCount() = bankList.size

    override fun onBindViewHolder(holder: BanksViewHolder, position: Int) {
        holder.bind(bankList[position])
    }

    fun setData(banks: List<Bank>) {
        bankList.clear()
        bankList.addAll(banks)
        notifyDataSetChanged()
    }

    fun setSelectedBank(bank: Bank) {
        selectedBank = bank
        notifyDataSetChanged()
    }

    inner class BanksViewHolder(private val binding: BankAccountItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(bank: Bank) {
            binding.apply {
                BrandCardHelper.getLoadBrandImageGeneric(bank.code ?: EMPTY)
                    .let { itUrl ->
                        ImageUtils.loadImage(ivIconBank, itUrl, R.drawable.bank_generic)
                    }
                tvBankName.text = bank.name.capitalizeWords().formatBankName()
            }
            binding.rbChoose.isSelected = bank == selectedBank
            binding.constraintLayout.setOnClickListener {
                onBankSelected(bank)
                setSelectedBank(bank)
            }
        }
    }
}