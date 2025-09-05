package br.com.mobicare.cielo.esqueciSenha.presentation.ui.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.esqueciSenha.domains.entities.BankMaskVO

/**
 * Created by david on 10/07/17.
 */

class BankAdapter(internal var context: Context,
                  internal var layoutResourceId: Int,
                  internal var layoutListResourceId: Int,
                  private val data: Array<BankMaskVO>) : ArrayAdapter<BankMaskVO>(context, layoutResourceId, layoutListResourceId, data) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return view(position, convertView, parent, layoutListResourceId)
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return view(position, convertView, parent, layoutResourceId)
    }

    private fun view(position: Int, convertView: View?, parent: ViewGroup?,
                     layoutResourceId: Int): View {
        var row = convertView
        var holder: BankHolder?

        if (row == null) {
            val inflater = (context as Activity).layoutInflater
            row = inflater.inflate(layoutResourceId, parent, false)

            holder = BankHolder()
            holder.bankName = row!!.findViewById(R.id.bankName) as TextView

            row.tag = holder
        } else {
            holder = row.tag as BankHolder
        }

        val bank = data[position]
        val codeAndBank = if (bank.code.isNullOrEmpty()) bank.name else "${bank.code.padStart(3, '0')} - ${bank.name}"
        holder.bankName!!.text = codeAndBank

        return row
    }

    internal class BankHolder {
        var bankName: TextView? = null
    }
}
