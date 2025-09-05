package br.com.mobicare.cielo.meuCadastro.presetantion.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.FragmentActivity
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.meuCadastro.domains.entities.Products
import kotlinx.android.synthetic.main.item_taxas_bandeiras_qtd_parcelas.view.*

/**
 * Created by Enzo Teles on 20/03/19
 * email: enzo.carvalho.teles@gmail.com
 * Software Developer Sr.
 */
class MSTaxasEBandeirasAdapter(var products: List<Products>, var activity: FragmentActivity) : BaseAdapter() {

    //View Holder pattern
    var view: View? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        view = convertView ?: LayoutInflater.from(parent?.context)
                .inflate(R.layout.item_taxas_bandeiras_qtd_parcelas,parent, false )

        val parcelas = products[position]

        parcelas.apply {
            view?.tv_tipo_conta?.text = name
            view?.tv_tipo_conta_impl?.text = fee
        }

        return view as View
    }

    override fun getItem(position: Int) = products[position]

    override fun getItemId(position: Int) = 0.toLong()

    override fun getCount() =  products.size


}