package br.com.mobicare.cielo.suporteTecnico.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.cielo.libflue.util.ONE_NEGATIVE
import br.com.mobicare.cielo.databinding.BottomSheetRadioButtonBinding

class RadioButtonAdapter(
    private val options: List<String>
) : RecyclerView.Adapter<RadioButtonAdapter.RadioButtonViewHolder>() {

    private var selectedPosition = ONE_NEGATIVE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadioButtonViewHolder {
        val binding = BottomSheetRadioButtonBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RadioButtonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RadioButtonViewHolder, position: Int) {
        holder.bind(options[position], position)
    }

    override fun getItemCount(): Int = options.size

    inner class RadioButtonViewHolder(private val binding: BottomSheetRadioButtonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(option: String, position: Int) {
            binding.apply {
                radioButton.text = option
                radioButton.isChecked = position == selectedPosition
                radioButton.setOnClickListener {
                    val currentPosition = bindingAdapterPosition
                    if (currentPosition == RecyclerView.NO_POSITION && selectedPosition == currentPosition) {
                        selectedPosition = currentPosition
                        notifyItemChanged(selectedPosition)
                    }
                }
            }
        }
    }
}