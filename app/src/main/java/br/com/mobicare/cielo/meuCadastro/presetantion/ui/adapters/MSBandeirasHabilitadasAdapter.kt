package br.com.mobicare.cielo.meuCadastro.presetantion.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.meuCadastro.domains.entities.CardBrands
import kotlinx.android.synthetic.main.item_taxas_bandeiras.view.*

/**
 * Created by Enzo Teles on 20/03/19
 * email: enzo.carvalho.teles@gmail.com
 * Software Developer Sr.
 */
class MSBandeirasHabilitadasAdapter(var cardBrands: List<CardBrands>, var requireActivity: FragmentActivity, var requireContext: Context) : RecyclerView.Adapter<DefaultViewHolderKotlin>() {

    var isCheck = true
    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {

        if (cardBrands == null || itemCount == 0) {
            return
        }


        val item = getItem(position)

        item?.apply {

            /*setContentView(R.id.main)
            val iv = findViewById(R.id.left) as ImageView
            val width = 60
            val height = 60
            val parms = LinearLayout.LayoutParams(width, height)
            iv.setLayoutParams(parms)*/

            //layout_load_more
            //lv_quantidade_parcelas

            val adapter = MSTaxasEBandeirasAdapter(products, requireActivity)
            ImageUtils.loadImage(holder.mView.iv_icon_taxas_bandeiras, imageURL)
            holder.mView.lv_quantidade_parcelas.adapter = adapter

            if (item.products.size > 3) {

                holder.mView.layout_load_more.visibility = View.VISIBLE
                holder.mView.layout_load_more.setOnClickListener {
                    if(isCheck){
                        val height = 550
                        val parms = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
                        holder.mView.lv_quantidade_parcelas.layoutParams = parms
                        isCheck = false
                        holder.mView.db_tv_title.text = requireActivity.getString(R.string.tv_item_tx_bandeiras_ver_menos)
                    }else{

                        val height = 450
                        val parms = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
                        holder.mView.lv_quantidade_parcelas.layoutParams = parms
                        isCheck = true
                        holder.mView.db_tv_title.text = requireActivity.getString(R.string.tv_item_tx_bandeiras_ver_mais)
                    }



                }

            } else if(item.products.size > 4){

                holder.mView.layout_load_more.visibility = View.VISIBLE
                holder.mView.layout_load_more.setOnClickListener {
                    if(isCheck){
                        val height = 650
                        val parms = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
                        holder.mView.lv_quantidade_parcelas.layoutParams = parms
                        isCheck = false
                        holder.mView.db_tv_title.text = requireActivity.getString(R.string.tv_item_tx_bandeiras_ver_menos)
                    }else{

                        val height = 450
                        val parms = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
                        holder.mView.lv_quantidade_parcelas.layoutParams = parms
                        isCheck = true
                        holder.mView.db_tv_title.text = requireActivity.getString(R.string.tv_item_tx_bandeiras_ver_mais)
                    }



                }

            }else{
                holder.mView.layout_load_more.visibility = View.GONE
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        val view = LayoutInflater.from(requireContext).inflate(R.layout.item_taxas_bandeiras, parent, false)
        return DefaultViewHolderKotlin(view)
    }

    override fun getItemCount() = if (cardBrands != null) cardBrands.size else 0

    fun getItem(position: Int): CardBrands {
        return cardBrands!![position]
    }
}