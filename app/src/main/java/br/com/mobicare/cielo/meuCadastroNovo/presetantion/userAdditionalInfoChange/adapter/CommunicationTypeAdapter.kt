package br.com.mobicare.cielo.meuCadastroNovo.presetantion.userAdditionalInfoChange.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.ItemContactTypeBinding
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.TypeOfCommunication

class CommunicationTypeAdapter(
    private val onItemClicked: () -> Unit,
) :
    RecyclerView.Adapter<CommunicationTypeAdapter.CommunicationViewHolder>() {

    private val communicationTypeList = mutableListOf<TypeOfCommunication>()
    private val selectedCommunicationType = mutableListOf<TypeOfCommunication>()

    inner class CommunicationViewHolder(private val binding: ItemContactTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(typeOfCommunication: TypeOfCommunication) {
            binding.apply {
                title.text = typeOfCommunication.description

                constraint.setOnClickListener {
                    checkBox.isChecked = checkBox.isChecked.not()
                }

                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedCommunicationType.add(typeOfCommunication)
                    } else {
                        selectedCommunicationType.remove(typeOfCommunication)
                    }
                    onItemClicked.invoke()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunicationViewHolder {
        val binding =
            ItemContactTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommunicationViewHolder(binding)
    }

    override fun getItemCount() = communicationTypeList.size

    override fun onBindViewHolder(holder: CommunicationViewHolder, position: Int) {
        holder.bind(communicationTypeList[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(typeOfCommunication: List<TypeOfCommunication>) {
        communicationTypeList.clear()
        communicationTypeList.addAll(typeOfCommunication)
        notifyDataSetChanged()
    }

    fun getSelectedList() = selectedCommunicationType
}