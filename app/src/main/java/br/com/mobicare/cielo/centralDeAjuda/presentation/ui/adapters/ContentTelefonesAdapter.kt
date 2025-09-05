package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.domains.entities.CentralAjudaDefaultObj
import br.com.mobicare.cielo.centralDeAjuda.domains.entities.PhoneSupport
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.OnClickPhoneListener
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import kotlinx.android.synthetic.main.item_content_ajuda_telefones.view.*
import java.util.*

/**
 * Created by benhur.souza on 19/04/2017.
 */

class ContentTelefonesAdapter(private val context: Context, private val listener: OnClickPhoneListener, phones: List<PhoneSupport>) : androidx.recyclerview.widget.RecyclerView.Adapter<DefaultViewHolderKotlin>() {

    val telefones: List<PhoneSupport>

    init {
        this.telefones = phones
    }

    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {
        val view : View = holder.mView
        val phone : PhoneSupport = getItem(position)

        view.textview_ajuda_telefones_title.text = phone.title
        view.textview_ajuda_telefones_description.text = phone.description
        view.textview_ajuda_telefones_description_hour.text = phone.timeDescription
        initPhoneList(view, phone.title, phone.items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        return DefaultViewHolderKotlin(LayoutInflater.from(parent.context).inflate(R.layout.item_content_ajuda_telefones, parent, false))
    }

    private fun initPhoneList(view: View, title: String?,  phones: List<CentralAjudaDefaultObj>) {
        view.recycler_view_ajuda_telefones_uteis.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        view.recycler_view_ajuda_telefones_uteis.adapter = ItemTelefonesAdapter(
                title,
                listener,
                phones
        )

    }

    fun getItem(position: Int): PhoneSupport {
        return telefones[position]
    }

    override fun getItemCount(): Int {
        return telefones.size
    }
}
