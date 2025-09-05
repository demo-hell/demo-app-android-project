package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.addMaskCPForCNPJ
import br.com.mobicare.cielo.commons.utils.dateFormatToBr
import br.com.mobicare.cielo.databinding.McnFragmentItemOwnerBinding
import br.com.mobicare.cielo.extensions.activity
import br.com.mobicare.cielo.meuCadastroNovo.domain.Owner
import br.com.mobicare.cielo.meuCadastroNovo.domain.Phone

class OwnersAdapter(
    private val context: Context,
    private val list: ArrayList<Owner>,
    private var action: (() -> Unit)? = null
) : RecyclerView.Adapter<OwnersAdapter.OwnersAdapterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnersAdapterViewHolder {
        val binding = McnFragmentItemOwnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OwnersAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OwnersAdapterViewHolder, position: Int) {
        holder.bind(list[position], context, action)
    }

    override fun getItemCount(): Int = list.size

    fun setAction(action: () -> Unit) {
        this.action = action
    }

    class OwnersAdapterViewHolder(private val binding: McnFragmentItemOwnerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(owner: Owner, context: Context, action: (() -> Unit)?) {
            binding.apply {
                textNameOwner.text = owner.name
                textCpfOwner.text = owner.cpf
                textDnOwner.text = owner.birthDate?.dateFormatToBr()

                viewPhone(owner.phones, context)

                ibInfo.setOnClickListener {
                    action?.invoke()
                }
            }
        }

        private fun viewPhone(phones: List<Phone>, context: Context) {
            val phoneViewList = listOf(
                Pair(binding.textPhoneOwner, binding.textTitlePhone1Owner),
                Pair(binding.textPhone2Owner, binding.textTitlePhone2Owner),
                Pair(binding.textPhone3Owner, binding.textTitlePhone3Owner)
            )

            phones.forEachIndexed { index, phone ->
                if (index < phoneViewList.size) {
                    val (textViewPhone, textViewTitle) = phoneViewList[index]
                    textViewPhone.visibility = View.VISIBLE
                    textViewTitle.visibility = View.VISIBLE

                    val phoneNumber = "${phone.areaCode ?: ""}${phone.number}"
                    textViewPhone.text = context.activity()?.addMaskCPForCNPJ(
                        phoneNumber, context.getString(R.string.mask_cellphone_step4)
                    )
                }
            }
        }
    }
}
