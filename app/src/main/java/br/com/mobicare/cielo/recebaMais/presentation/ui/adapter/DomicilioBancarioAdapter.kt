package br.com.mobicare.cielo.recebaMais.presentation.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.utils.getAccountFormatted
import br.com.mobicare.cielo.commons.utils.getAgencyFormatted
import br.com.mobicare.cielo.recebaMais.domain.Bank
import kotlinx.android.synthetic.main.item_receba_mais_domicilio_bancario.view.*

class DomicilioBancarioAdapter(val context: Context,
                               val banks: List<Bank>,
                               val bank: Bank?,
                               val selectDomicilio: (select: Bank) -> Unit) :
        RecyclerView.Adapter<DefaultViewHolderKotlin>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        val vh: RecyclerView.ViewHolder
        val view = LayoutInflater.from(context).inflate(R.layout.item_receba_mais_domicilio_bancario, parent, false)
        vh = DefaultViewHolderKotlin(view)
        return vh
    }

    override fun getItemCount(): Int {
        return banks.size
    }

    private var mPosition: Int = -1
    private var mFlag = false
    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {
        //val item = getItem(position)

        val view = holder.mView
        val item = getItem(position)

        configureComponent(holder, item)

        configureOptionClick(view, position)
        selectChosenOption(item, position)
        selectOptionWhenOneOption(position)
        selectOption(position, item, view)

        if (itemCount - 1 == position) holder.mView.view_line.visibility = View.GONE
    }

    private fun configureComponent(holder: DefaultViewHolderKotlin, item: Bank) {
        holder.mView.text_input_banco.text = item.name
        holder.mView.text_input_agencia.text = item.getAgencyFormatted(context)
        holder.mView.text_input_conta.text = item.getAccountFormatted(context)

        ImageUtils.loadImage(holder.mView.imageview_domicilio_bancario, item.imageURL)
    }


    private fun configureOptionClick(view: View, position: Int) {
        view.radio_button_domicilio_bancario.setOnClickListener {
            mPosition = position
            notifyItemRangeChanged(0, banks.size)
        }
    }

    private fun selectOption(position: Int, item: Bank, view: View) {
        if (mPosition == position) selectDomicilio(item)
        view.radio_button_domicilio_bancario.isChecked = mPosition == position
    }

    private fun selectOptionWhenOneOption(position: Int) {
        if (itemCount == 1) {
            mPosition = position
        }
    }

    private fun selectChosenOption(item: Bank, position: Int) {
        if (!mFlag) {
            bank?.let {
                if (it.agency == item.agency && it.name == item.name && it.accountNumber == item.accountNumber) {
                    mPosition = position
                    mFlag = true
                }
            }
        }
    }

    fun getItem(position: Int): Bank {
        return banks.get(position)
    }


}