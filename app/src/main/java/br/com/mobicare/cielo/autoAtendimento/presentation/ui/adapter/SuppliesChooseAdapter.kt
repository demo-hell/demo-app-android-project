    package br.com.mobicare.cielo.autoAtendimento.presentation.ui.adapter

import android.annotation.SuppressLint
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import br.com.cielo.libflue.util.ONE
import br.com.cielo.libflue.util.TEN
import br.com.cielo.libflue.util.TWO
import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.domain.model.SupplyDTO
import br.com.mobicare.cielo.autoAtendimento.domain.model.SupplyTypes
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceTextView
import br.com.mobicare.cielo.extensions.capitalizePTBR
import br.com.mobicare.cielo.extensions.toLowerCasePTBR

    class SuppliesChooseAdapter(
        val supplies: List<SupplyDTO>) : RecyclerView.Adapter<SuppliesChooseAdapter.SupplyViewHolder>() {

    var onItemSelectedListener: OnItemSelectedListener? = null

    interface OnItemSelectedListener {
        fun onItemAddedQuantity(supplies: List<SupplyDTO>, selectedPosition: Int)
        fun onItemRemovedQuantity(supplies: List<SupplyDTO>, selectedPosition: Int)
    }


    companion object {
        const val STICKER_VIEW_TYPE = ZERO
        //Por enquanto utilizar esse código para película
        const val OTHERS_VIEW_TYPE = ONE
        const val COIL_VIEW_TYPE = TWO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupplyViewHolder {

        return when (viewType) {

            STICKER_VIEW_TYPE -> {
                SupplyViewHolder(LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.card_sticker_select, parent, false))
            }
            OTHERS_VIEW_TYPE -> {
                SupplyViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.card_others_select, parent, false))
            }
            else -> {
                SupplyViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.card_coil_select, parent, false))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (supplies[position].type) {
            //Veririficar essa constante
            SupplyTypes.STICKER.type -> {
                STICKER_VIEW_TYPE
            }
            SupplyTypes.ROLL.type -> {
                COIL_VIEW_TYPE
            }
            else -> {
                OTHERS_VIEW_TYPE
            }
        }
    }

    override fun getItemCount(): Int {
        return supplies.size
    }

    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: SupplyViewHolder, position: Int) {

        holder.bind(supplies, position)

    }

    inner class SupplyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {


        fun bind(supplies: List<SupplyDTO>, selectedPosition: Int) {

            val supply = supplies[selectedPosition]
            val textTitle = view.findViewById<TypefaceTextView>(R.id.text_title)
            textTitle.text = SpannableStringBuilder.valueOf(supply.description.toLowerCasePTBR().capitalizePTBR())

            val stickerQuantity = view.findViewById<TypefaceTextView>(R.id.tv_qtd_sticker)
            stickerQuantity.text = SpannableStringBuilder.valueOf(supply.quantidade.toString())

            val minusButton = view.findViewById<ImageView>(R.id.iv_minus)

            val plusButton = view.findViewById<ImageView>(R.id.iv_plus)

            minusButton.isEnabled = supply.quantidade > ZERO
            plusButton.isEnabled = supply.quantidade < TEN

            minusButton.setOnClickListener {

                if (supply.quantidade > ZERO) {
                    supply.quantidade--
                    onItemSelectedListener?.onItemRemovedQuantity(supplies, selectedPosition)
                    notifyDataSetChanged()
                } else {
                    minusButton.isEnabled = false
                }

            }

            plusButton.setOnClickListener {
                if (supply.quantidade < TEN){
                    supply.quantidade++
                    minusButton.isEnabled = supply.quantidade > ZERO

                    onItemSelectedListener?.onItemAddedQuantity(supplies, selectedPosition)
                    notifyDataSetChanged()
                }
            }
        }
    }
}