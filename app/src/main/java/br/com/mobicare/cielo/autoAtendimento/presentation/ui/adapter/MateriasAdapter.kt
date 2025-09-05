package br.com.mobicare.cielo.autoAtendimento.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.presentation.presenter.AutoAtendimentoContract
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.migration.presentation.presenter.ItemBannerMigration
import kotlinx.android.synthetic.main.itens_material.view.*

/**
 * email: enzo.carvalho.teles@gmail.com
 */
class MateriasAdapter(var materiais: List<ItemBannerMigration>, var activity: FragmentActivity, var mView: AutoAtendimentoContract.View) : RecyclerView.Adapter<DefaultViewHolderKotlin>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        return DefaultViewHolderKotlin(LayoutInflater
                .from(parent.context).inflate(R.layout.itens_material, parent, false))
    }

    override fun getItemCount(): Int {
        return materiais!!.size
    }

    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {

        val view : View = holder.mView

        val itemObj = item(position)

        view.tv_title_materias.text = itemObj.firstName

        view.iv_materias.apply {
            setImageDrawable(itemObj.imageUrl)
        }

        view.constraint_view_materias.setOnClickListener {
            mView.selectItem(itemObj)
        }
    }

    private fun item(position: Int) : ItemBannerMigration = materiais!!.get(position)

}
