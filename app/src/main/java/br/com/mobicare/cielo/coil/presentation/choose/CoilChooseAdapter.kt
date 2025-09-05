package br.com.mobicare.cielo.coil.presentation.choose

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.coil.domains.CoilOptionObj
import br.com.mobicare.cielo.databinding.CardCoilSelectTypeBinding
import java.util.*

class CoilChooseAdapter(
    private val supplies: ArrayList<CoilOptionObj>,
    private val chosen: (CoilOptionObj) -> Unit
) : RecyclerView.Adapter<CoilChooseAdapter.CoilChooseViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CoilChooseViewHolder {
        val binding = CardCoilSelectTypeBinding.inflate(
            LayoutInflater
                .from(parent.context), parent, false
        )
        return CoilChooseViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return supplies.size
    }

    override fun onBindViewHolder(
        holder: CoilChooseViewHolder,
        position: Int
    ) {
        holder.bind(supplies[position])
    }

    inner class CoilChooseViewHolder(
        private val binding: CardCoilSelectTypeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(coilOptionObj: CoilOptionObj) {
            binding.apply {
                textTitle.text = coilOptionObj.title
                textDescription.text = coilOptionObj.description
                textDescriptionComplement.text = coilOptionObj.descriptionComplement
                constraintView.setOnClickListener {
                    chosen(coilOptionObj)
                }
            }
        }
    }

    private fun item(position: Int): CoilOptionObj = supplies.get(position)
}