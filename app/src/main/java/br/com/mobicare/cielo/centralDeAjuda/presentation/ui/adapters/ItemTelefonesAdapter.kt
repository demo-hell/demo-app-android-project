package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.domains.entities.CentralAjudaDefaultObj
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.OnClickPhoneListener
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import kotlinx.android.synthetic.main.item_ajuda_telefones_uteis.view.*
import java.util.*


/**
 * É necessário passar o parentTitle para formar o screenName do tagueamento
 * @param parentTitle
 * *
 * @param listener
 * *
 * @param itens
 */
class ItemTelefonesAdapter(private val parentTitle: String?, private val listener: OnClickPhoneListener, itens: List<CentralAjudaDefaultObj>) : androidx.recyclerview.widget.RecyclerView.Adapter<DefaultViewHolderKotlin>() {

    val telefones: List<CentralAjudaDefaultObj>

    init {
        this.telefones = itens
    }

    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {
        val view : View = holder.mView
        val phone : CentralAjudaDefaultObj = getItem(position)

        view.textview_item_ajuda_telefone_number.text = phone.value
        view.textview_item_ajuda_telefone_description.text = phone.description
        view.textview_item_ajuda_telefone_number.setOnClickListener {
            listener.onClickPhone(
                    parentTitle,
                    phone.description,
                    phone.value
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        return DefaultViewHolderKotlin(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_ajuda_telefones_uteis, parent, false))
    }

    fun getItem(position: Int): CentralAjudaDefaultObj {
        return telefones[position]
    }

    override fun getItemCount(): Int {
        return telefones.size
    }
}

