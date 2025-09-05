package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.addMaskCPForCNPJ
import br.com.mobicare.cielo.databinding.McnFragmentItemContatoBinding
import br.com.mobicare.cielo.extensions.activity
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.meuCadastroNovo.domain.Contact
import br.com.mobicare.cielo.meuCadastroNovo.domain.PhoneContato

class ContatosAdapter(
    private val context: Context,
    private val list: ArrayList<Contact>,
    private var action: (() -> Unit)? = null
) :
    RecyclerView.Adapter<ContatosAdapter.ContatosAdapterViewHolder>() {

    private var _binding: McnFragmentItemContatoBinding? = null
    private val binding get() = _binding!!


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContatosAdapterViewHolder {
        _binding = McnFragmentItemContatoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContatosAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContatosAdapterViewHolder, position: Int) {
        holder.bind(list[position], context, action)
    }

    override fun getItemCount() = list.size

    fun setAction(action: () -> Unit) {
        this.action = action
    }

    class ContatosAdapterViewHolder(val binding: McnFragmentItemContatoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact, context: Context, action: (() -> Unit)?) {
            binding.apply {
                textName.text = contact.name

                if (contact.email.isNullOrEmpty().not()) {
                    textEmail.text = contact.email
                    textEmail.visible()
                    textTitleEmail.visible()
                }

                viewPhone(contact.phones, binding, context)

                ibInfo.setOnClickListener {
                    action?.invoke()
                }
            }
        }

        private fun viewPhone(phones: List<PhoneContato>, binding: McnFragmentItemContatoBinding, context: Context) {
            if (phones.isNotEmpty()) {
                val phoneViewList = listOf(
                    Pair(binding.textPhone1, binding.textTitlePhone1),
                    Pair(binding.textPhone2, binding.textTitlePhone2),
                    Pair(binding.textPhone3, binding.textTitlePhone3)
                )

                phones.forEachIndexed { index, phone ->
                    if (index < phoneViewList.size) {
                        val textViewPhone = phoneViewList[index].first
                        val textViewTitle = phoneViewList[index].second
                        textViewPhone.visible()
                        textViewTitle.visible()

                        val phoneNumber = "${phone.areaCode ?: ""}${phone.number}"
                        textViewPhone.text = context.activity()?.addMaskCPForCNPJ(
                            phoneNumber, context.getString(
                                R.string.mask_cellphone_step4
                            )
                        )
                    }
                }
            }
        }
    }
}