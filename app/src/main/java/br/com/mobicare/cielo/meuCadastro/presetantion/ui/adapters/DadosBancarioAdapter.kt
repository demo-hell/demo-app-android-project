package br.com.mobicare.cielo.meuCadastro.presetantion.ui.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.FragmentActivity
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroDomicilioBancario
import kotlinx.android.synthetic.main.item_dados_bancarios.view.*

/**
 * Created by Enzo Teles on 20/03/19
 * email: enzo.carvalho.teles@gmail.com
 * Software Developer Sr.
 */
class DadosBancarioAdapter(var bankDatas: Array<MeuCadastroDomicilioBancario>?, var requireContext: Context, var activity: FragmentActivity) : BaseAdapter(){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = activity.layoutInflater.inflate(R.layout.item_dados_bancarios,parent, false )
        val domicilioBancario = bankDatas!!.get(position)

        domicilioBancario?.apply {

            view.card_first.visibility = if(isPrepaid) View.GONE else View.VISIBLE
            view.card_second.visibility = if(isPrepaid) View.VISIBLE else View.GONE

            //conta corrente
            view.db_tv_title.text = name
            view.db_tv_banco_impl.text = code
            view.db_tv_agencia_impl.text = branch
            view.db_tv_conta_impl.text = account
            ImageUtils.loadImage(view.imageView6, imgUrl)

            //prepago
            view.db_tv_title_prepago.text = name
            view.db_tv_cartao_ativo_impl.text = code
            view.db_tv_num_serie_impl.text = account
            ImageUtils.loadImage(view.iv_cartao_prepago, imgUrl)

            /*view.db_tv_title_prepago.text = name
            view.db_tv_banco_impl.text = code
            view.db_tv_agencia_impl.text = branch
            view.db_tv_conta_impl.text = account
            BindingUtils.loadImage(view.imageView6, imgUrl)*/

        }

        return view
    }

    override fun getItem(position: Int) = bankDatas!!.get(position)

    override fun getItemId(position: Int) = 0.toLong()

    override fun getCount() =  bankDatas!!.size


}