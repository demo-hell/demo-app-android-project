package br.com.mobicare.cielo.autoAtendimento.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.presentation.presenter.AutoAtendimentoContract
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.migration.presentation.presenter.ItemPraVenderMais
import kotlinx.android.synthetic.main.itens_pra_vender_mais.view.*

/**
 * email: enzo.carvalho.teles@gmail.com
 */
class PraVenderMaisAdapter(var listVenderMais: ArrayList<ItemPraVenderMais>?, var activity: FragmentActivity, var mView: AutoAtendimentoContract.View) : RecyclerView.Adapter<DefaultViewHolderKotlin>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        return DefaultViewHolderKotlin(LayoutInflater
                .from(parent.context).inflate(R.layout.itens_pra_vender_mais, parent, false))
    }

    override fun getItemCount(): Int {
        return listVenderMais?.size ?: 0
    }

    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {

        val view : View = holder.mView

        val itemObj = item(position)

        view.tv_title_pravender.text = itemObj.title
        view.tv_subtitle_pravender.text = itemObj.subtitle

        view.iv_pra_vender.apply {
            setImageDrawable(itemObj.imageUrl)
        }

        view.constraint_view_pravender.setOnClickListener {
            mView.selectItemPraVenderMais(itemObj)
        }
    }

    private fun item(position: Int) : ItemPraVenderMais = listVenderMais!!.get(position)

}
