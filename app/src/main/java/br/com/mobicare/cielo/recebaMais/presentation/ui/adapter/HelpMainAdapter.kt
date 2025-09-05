package br.com.mobicare.cielo.recebaMais.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.recebaMais.domain.Doub
import kotlinx.android.synthetic.main.item_receba_mais_help.view.*

class HelpMainAdapter(var doubts: List<Doub>,
                      val selectItem: (item: Doub) -> Unit) : RecyclerView.Adapter<DefaultViewHolderKotlin>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        val vh: RecyclerView.ViewHolder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_receba_mais_help, parent, false)
        vh = DefaultViewHolderKotlin(view)
        return vh
    }

    override fun getItemCount(): Int {
        return doubts.size
    }

    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {
        val view = holder.mView
        val item = getItem(position)

        view.text_title.text = item.title
        val lines = item.subtitle.split("\n")
        view.text_description.text = lines[0]

        view.content_view.setOnClickListener {
            selectItem(getItem(position))
        }

    }

    fun getItem(position: Int): Doub {
        return doubts.get(position)
    }


}
