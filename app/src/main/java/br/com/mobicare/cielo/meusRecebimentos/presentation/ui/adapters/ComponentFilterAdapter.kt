package br.com.mobicare.cielo.meusRecebimentos.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import kotlinx.android.synthetic.main.item_component_filter.view.*


/**
 * Created by enzo teles on 31/01/2019.
 */
class ComponentFilterAdapter(
    private var list: MutableList<String>
) : androidx.recyclerview.widget.RecyclerView.Adapter<DefaultViewHolderKotlin>() {

    private var lastCheckedRB: RadioButton? = null
    private var isFistAcess: Boolean = true
    var onDateFilterSelectedListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onClicked(view: View, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        return DefaultViewHolderKotlin(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_component_filter,
                parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {
        val view: View = holder.mView
        val value = getItem(position)

        if (position == 0 && isFistAcess) {
            view.radio_option.isChecked = (position == 0)
            configMarkOption(view.radio_option)
            isFistAcess = false
        } else {
            view.filter_wiew_option.visibility = View.GONE
        }

        view.radio_option.text = value
        view.radio_option.setOnClickListener {
            callCheckClick(it, position)
        }

        when (value) {
            holder.mView.context.getString(R.string.filter_hoje),
            holder.mView.context.getString(R.string.filter_outros_periodos) -> {
                view.radio_option.contentDescription =
                    view.context.getString(R.string.description_select_sales_de, value)
            }
            else -> {
                view.radio_option.contentDescription =
                    view.context.getString(R.string.description_select_sales_dos, value)
            }
        }

    }

    private fun callCheckClick(it: View, position: Int) {
        configMarkOption(it)
        onDateFilterSelectedListener?.onClicked(it, position)
    }

    private fun configMarkOption(it: View) {
        val checked_rb = it.findViewById(it.id) as RadioButton

        if (lastCheckedRB != null && lastCheckedRB != checked_rb) {
            lastCheckedRB!!.isChecked = false
        }
        lastCheckedRB = checked_rb
    }

    fun getItem(position: Int): String {
        return list[position]
    }

}