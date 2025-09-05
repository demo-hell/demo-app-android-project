package br.com.mobicare.cielo.taxaPlanos.presentation.ui.postecipado.meuAluguel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.Postecipate.ZERO_VALUE_MONEY
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import kotlinx.android.synthetic.main.equipment_trade_postecipado_item.view.*
import kotlinx.android.synthetic.main.layout_equipment_details_postecipado.view.*

class PostecipadoEquipmentsAdapter(private val terminals: List<Terminal>, private val context: Context, private val limitQuantityItems: Int = ZERO) : RecyclerView.Adapter<DefaultViewHolderKotlin>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        return DefaultViewHolderKotlin(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.equipment_trade_postecipado_item, parent, false
                )
        )
    }

    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {
        val view: View = holder.mView
        val terminal = terminals[position]
        val valueDiscountNegotiated = terminal.valueDiscountNegotiated
        val valueDiscountPartial = terminal.valueDiscountPartial
        val percentageDiscountNegotiated = terminal.percentageDiscountNegotiated
        val percentageDiscountPartial = terminal.percentageDiscountPartial

        view.tvEquipmentModel?.text = context.getString(R.string.equipment_model_x, position + ONE)
        view.tvQuantityValue?.text = terminal.terminalQuantity?.let { context.resources.getQuantityString(R.plurals.x_equipments, it, terminal.terminalQuantity) }
        view.tvRentValue?.text = setupMessage(valueDiscountPartial, percentageDiscountPartial, true, view)
        view.tvValueDiscount?.text = setupMessage(valueDiscountNegotiated, percentageDiscountNegotiated, false, view)
    }

    private fun setupMessage(moneyValue: Double?, percentage: String?, isRentValue: Boolean, view: View) =
            when {
                moneyValue == null && percentage == null -> {
                    context.getString(R.string.rs_value_null)
                }
                moneyValue != null && moneyValue > ZERO_VALUE_MONEY -> {
                    moneyValue?.toPtBrRealString()
                }
                else -> {
                    if (isRentValue) view.tvRentTitle?.text = context.getString(R.string.discount_partial_per_equipment)
                    else view.tvDiscountRentTitle?.text = context.getString(R.string.discount_per_equipment)
                    context.getString(R.string.mask_percentual_value,  percentage)
                }
            }

    override fun getItemCount(): Int {
        return when {
            limitQuantityItems == ZERO || limitQuantityItems > terminals.size -> terminals.size
            else -> limitQuantityItems
        }
    }
}