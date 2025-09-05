package br.com.mobicare.cielo.meuCadastro.presetantion.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroSolucoesContratadas
import kotlinx.android.synthetic.main.item_solucoes_contratadas.view.*


class MinhasSolucoesAdapter(var solucoesContratadas: Array<MeuCadastroSolucoesContratadas>?,
                            var requireContext: Context,
                            var activity: FragmentActivity) :
        RecyclerView.Adapter<MinhasSolucoesAdapter.ContractedSolutionsViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContractedSolutionsViewHolder {
        return ContractedSolutionsViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_solucoes_contratadas, parent, false))
    }

    override fun getItemCount(): Int {
        return solucoesContratadas?.size ?: 0
    }

    override fun onBindViewHolder(holder: ContractedSolutionsViewHolder, position: Int) {
        holder.bind(solucoesContratadas?.get(position))
    }


    inner class ContractedSolutionsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {


        fun bind(contractedSolution: MeuCadastroSolucoesContratadas?) {

            contractedSolution?.run {
                view.ms_tv_title.text = name
                view.ms_tv_valor_mensal_impl.text = price
                view.ms_tv_plano_impl.text = description
            }

        }

    }

}