package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.adapters

import android.view.View
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domain.HelpCategory
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_home_shortcut_suporte_tecnico.view.*
import kotlinx.android.synthetic.main.item_new_technical_support.view.imageCategoryIcon
import kotlinx.android.synthetic.main.item_new_technical_support.view.textNewTechnicalSupportTitle

class HelpSuportTecnicoAdapterViewHolderCallback : DefaultViewListAdapter.OnBindViewHolderPositon<HelpCategory> {

    var onCategoryItemClickListener: OnCategoryItemClickListener? = null


    interface OnCategoryItemClickListener {
        fun onClick(helpCategorySelected: HelpCategory, position: Int)
    }


    override fun onBind(item: HelpCategory, holder: DefaultViewHolderKotlin, position: Int, lastPositon: Int) {
        holder.mView.imageCategoryIcon.visibility = View.VISIBLE
        holder.mView.textNewTechnicalSupportTitle.text = item.description

        holder.mView.cardItemShortcutServices.setOnClickListener {

            onCategoryItemClickListener?.onClick(item, position)
        }

        Picasso.get()
                .load(item.icon)
                .error(R.drawable.ic_error)
                .into(holder.mView.imageCategoryIcon)
    }
}