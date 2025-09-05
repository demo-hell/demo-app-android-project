package br.com.mobicare.cielo.cieloFarol.presentation

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.cielo.libflue.util.imageUtils.setCustomDrawable
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.cieloFarol.enums.CieloFarolPillsEnum
import br.com.mobicare.cielo.commons.constants.THIRTY_SIX
import br.com.mobicare.cielo.commons.constants.TWENTY_FOUR
import br.com.mobicare.cielo.commons.constants.TWO_HUNDRED_SEVENTY_TWO
import br.com.mobicare.cielo.commons.utils.AccessibilityUtils
import br.com.mobicare.cielo.databinding.CieloFarolItemBinding
import kotlinx.android.synthetic.main.cielo_farol_item.view.cieloFarolDescription
import kotlinx.android.synthetic.main.cielo_farol_item.view.cieloFarolIcon
import kotlinx.android.synthetic.main.cielo_farol_item.view.cieloFarolTitle

class CieloFarolAdapter(
    private val items: List<String?>
) : RecyclerView.Adapter<CieloFarolAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CieloFarolItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(private val binding: CieloFarolItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(descriptionValue: String?) {
            descriptionValue?.let { description ->
                binding.root.apply {
                    CieloFarolPillsEnum.values().getOrNull(adapterPosition)?.let { pill ->
                        cieloFarolTitle.text = context?.getString(pill.title)
                        cieloFarolDescription.text = pill.formatValue(description)
                        cieloFarolDescription.setTextColor(ContextCompat.getColor(context, pill.textColor))

                        defineCardIcon(context, pill)

                        setCustomDrawable {
                            radius = R.dimen.dimen_8dp
                            solidColor = pill.backgroundColor
                        }

                        setAcessibilityForCards(context, itemView)
                        configureTheLastCard(itemView)
                    }
                }
            }
        }

        private fun defineCardIcon(context: Context, pill: CieloFarolPillsEnum) {
            binding.cieloFarolIcon.apply {
                setBackgroundResource(pill.icon)

                ResourcesCompat.getDrawable(context.resources, pill.icon, null)?.let {
                    DrawableCompat.wrap(it)
                    DrawableCompat.setTint(it, ContextCompat.getColor(context, pill.textColor))

                    setImageDrawable(it)
                }
            }
        }

        private fun setAcessibilityForCards(context: Context, itemView: View) {
            itemView.contentDescription = AccessibilityUtils.descriptionForSimpleListHorizontal(
                context,
                items,
                position,
                items.size
            )
        }

        private fun configureTheLastCard(itemView: View) {
            itemView.rootView?.apply {
                if(position == itemCount - 1) {
                    val layoutParams = ConstraintLayout.LayoutParams(
                        TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            TWO_HUNDRED_SEVENTY_TWO.toFloat(),
                            resources.displayMetrics
                        ).toInt(),
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                    ).also {
                        it.marginEnd = THIRTY_SIX
                    }

                    this.layoutParams = layoutParams
                }
            }
        }
    }
}