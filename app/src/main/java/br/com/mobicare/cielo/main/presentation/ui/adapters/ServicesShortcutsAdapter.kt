package br.com.mobicare.cielo.main.presentation.ui.adapters

import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceTextView
import br.com.mobicare.cielo.main.domain.Menu
import com.squareup.picasso.Picasso

class ServicesShortcutsAdapter(private val shortcuts: List<Menu>) :
        RecyclerView.Adapter<ServicesShortcutsAdapter.ShortcutViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(itemClicked: Menu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShortcutViewHolder {
        return ShortcutViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_shortcut, parent, false))
    }

    override fun getItemCount(): Int = shortcuts.size

    override fun onBindViewHolder(holder: ShortcutViewHolder, position: Int) =
            holder.bind(shortcuts[position])


    inner class ShortcutViewHolder(val view: View) : RecyclerView.ViewHolder(view) {


        fun bind(currentItem: Menu) {

            val itemLabel = view.findViewById<TypefaceTextView>(R.id.textHeaderLabel)
            val itemIcon = view.findViewById<ImageView>(R.id.imageHeaderButton)

            val cardContent = view.findViewById<CardView>(R.id.cardItemShortcutServices)

            itemLabel.text = SpannableStringBuilder.valueOf(currentItem.name)
            Picasso.get().load(currentItem.icon).into(itemIcon)

            cardContent.setOnClickListener {
                onItemClickListener?.onItemClick(currentItem)
            }


        }


    }

}

