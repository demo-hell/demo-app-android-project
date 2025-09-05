package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.meuCadastroNovo.domain.Bank
import br.com.mobicare.cielo.meuCadastroNovo.domain.Brand
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_dados_conta.view.*
import kotlinx.android.synthetic.main.item_tipo_conta.view.*

/**
 * Created by Enzo Teles
 */
class DadosContaAdapter(val listBank: List<Bank>, val callback: (Bank) -> Unit, val clickLink: (List<Bank>, Bank) -> Unit) :
        RecyclerView.Adapter<VH>() {

    var context: Context? = null
    lateinit var adapter: DadosContaABandeiradapter
    var click = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_dados_conta, parent, false)
        val vh = VH(v)
        this.context = parent.context

        vh.itemView.setOnClickListener {
            if(click){
                val bank = listBank[vh.adapterPosition]
                callback(bank)
                click = false
            }
            //evitar que o dialog seja criado mais de uma vez
            Handler().postDelayed({
                click = true
            }, 1000)

        }
        return vh
    }

    override fun getItemCount() = listBank.size

    @SuppressLint("ResourceAsColor", "NewApi")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val solution = listBank[position]

        solution.let { bnk ->
            //BindingUtils.loadImage(holder.itemView.dc_iv_brand, bnk.imgSource)

            if (bnk.digitalAccount){
                holder.itemView.dc_tv_num_agencia_impl.visibility = View.GONE
                holder.itemView.dc_tv_num_agencia.visibility = View.GONE
                holder.itemView.dc_tv_tipo_conta_impl.text = "Digital"
                adapter = DadosContaABandeiradapter(bnk.brands, false, 4)
                holder.itemView.rv_dc_brands.layoutManager = GridLayoutManager(context, 4)
                holder.itemView.dc_tv_name_bank.text = "Conta Digital"
            }else{
                holder.itemView.dc_tv_num_agencia_impl.visibility = View.VISIBLE
                holder.itemView.dc_tv_num_agencia.visibility = View.VISIBLE
                holder.itemView.dc_tv_num_agencia_impl.text = if(bnk.agencyDigit.isNullOrEmpty()) bnk.agency else "${bnk.agency}-${bnk.agencyDigit}"
                holder.itemView.dc_tv_tipo_conta_impl.text = if(bnk.savingsAccount) "Poupança" else "Corrente"
                adapter = DadosContaABandeiradapter(bnk.brands, false, 3)
                holder.itemView.rv_dc_brands.layoutManager = GridLayoutManager(context, 3)
                holder.itemView.dc_tv_name_bank.text = bnk.name
            }

            holder.itemView.dc_tv_num_conta_impl.text = if(bnk.accountDigit.isNullOrEmpty()) bnk.accountNumber else "${bnk.accountNumber}-${bnk.accountDigit}"

            Picasso.get()
                    .load(bnk.imgSource)
                    .into(holder.itemView.dc_iv_brand, object : Callback {
                        override fun onSuccess() {
                            holder.itemView.progress_dc.visibility = View.GONE
                            holder.itemView.dc_iv_brand.visibility = View.VISIBLE
                        }

                        override fun onError(e: Exception?) {
                            e?.printStackTrace()
                        }


                    })

            holder.itemView.rv_dc_brands.adapter = adapter
            holder.itemView.tv_validador_dc.visibility = isQtdBrandsVisible(bnk.brands!!)
            holder.itemView.tv_validador_dc.text = calcQtdBrands(bnk.brands!!)

            val filteredList = listBank.filter { !it.digitalAccount && !it.savingsAccount }

            if(filteredList.size > 1 && !bnk.digitalAccount && !bnk.savingsAccount){
                holder.itemView.constraint_view_item_bandeira.isEnabled = true
                holder.itemView.constraint_view_item_bandeira.alpha = 1f
                this.context?.let { itContext ->
                    holder.itemView.tv_link_flag_transfer.setTextColor( ContextCompat.getColor(itContext, R.color.blue) )
                }
                holder.itemView.constraint_view_item_bandeira.setOnClickListener {
                    clickLink(listBank, bnk)
                }
            }else{
                holder.itemView.constraint_view_item_bandeira.isEnabled = false
                holder.itemView.constraint_view_item_bandeira.alpha = 0.3f
                holder.itemView.tv_link_flag_transfer.setTextColor(ColorStateList.valueOf(R.color.grey_cecece))
            }

        }
    }

    private fun calcQtdBrands(brands: List<Brand>): CharSequence? {
        if (brands.isEmpty()) return ""
        return if (brands.size > 3) (brands.size - 3).toString() + "+"  else "0"
    }

    private fun isQtdBrandsVisible(brands: List<Brand>): Int {
        if (brands.isEmpty()) return View.GONE
        return if (brands.size > 3) View.VISIBLE else View.GONE
    }

}

class VH(itemView: View) : RecyclerView.ViewHolder(itemView)



