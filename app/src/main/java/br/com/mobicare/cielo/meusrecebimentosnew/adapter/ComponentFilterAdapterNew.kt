package br.com.mobicare.cielo.meusrecebimentosnew.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.utils.DataCustomNew
import br.com.mobicare.cielo.meusrecebimentosnew.fragments.ComponentFilterListener
import br.com.mobicare.cielo.meusrecebimentosnew.models.DayType
import kotlinx.android.synthetic.main.item_component_filter.view.*

class ComponentFilterAdapterNew(private var list: List<DayType>,
                                private var listener: ComponentFilterListener,
                                private var listenerShowAnotherDate: ComponentFilterListener.VisibilityOnShowAnotherDates)
    : RecyclerView.Adapter<DefaultViewHolderKotlin>() {

    private var lastCheckedRB: RadioButton? = null
    private var isFistAcess: Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        return DefaultViewHolderKotlin(
            LayoutInflater.from(parent.context).inflate(R.layout.item_component_filter,
                parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {

        val view: View = holder.mView
        val dayType = list[position]

        var initialDate = DataCustomNew()
        var finalDate = DataCustomNew()
        view.radio_option.setOnClickListener {
            listenerShowAnotherDate.onHideAnotherDates()

            when (holder.itemViewType) {
                DayType.Type.DAY.type -> {
                    initialDate.setDate()
                    listenerShowAnotherDate.onShowDailyDate(initialDate)
                    listener.showGraph(initialDate)
                    callCheckClick(it, initialDate, finalDate,DayType.Type.DAY)
                }
                DayType.Type.DAY7.type -> {
                    initialDate.setDate(-6)
                    callCheckClick(it, initialDate, finalDate,DayType.Type.DAY7)
                }
                DayType.Type.DAY15.type -> {
                    initialDate.setDate(-14)
                    callCheckClick(it, initialDate, finalDate,DayType.Type.DAY15)
                }
                DayType.Type.DAY30.type -> {
                    initialDate.setDate(-29)
                    callCheckClick(it, initialDate, finalDate,DayType.Type.DAY30)
                }
                DayType.Type.OTHERDAY.type -> {
                    configMarkOption(it)
                    initialDate.setDate(-29)
                    listenerShowAnotherDate.onShowAnotherDates(initialDate, finalDate)
                }
            }
        }
        if (position == 0 && isFistAcess) {
            view.radio_option.isChecked = (position == 0)
            configMarkOption(view.radio_option)
            view.radio_option.performClick()
            isFistAcess = false
        } else {
            view.filter_wiew_option.visibility = View.GONE
        }
        view.radio_option.text = "${dayType.day}"
    }

    private fun callCheckClick(it: View, initialDate: DataCustomNew, finalDate: DataCustomNew, selectedDateType: DayType.Type?) {
        configMarkOption(it)
        listener.onClickDate(initialDate.formatDateToAPI(), finalDate.formatDateToAPI(),false,selectedDateType)
    }

    private fun configMarkOption(it: View) {
        val checked_rb = it.findViewById(it.id) as RadioButton

        if (lastCheckedRB != null && lastCheckedRB != checked_rb) {
            lastCheckedRB!!.isChecked = false
        }
        lastCheckedRB = checked_rb
    }

    override fun getItemViewType(position: Int) = list[position].type
}