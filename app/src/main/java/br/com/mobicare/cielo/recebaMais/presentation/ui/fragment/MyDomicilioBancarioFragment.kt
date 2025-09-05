package br.com.mobicare.cielo.recebaMais.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.recebaMais.domain.Bank
import br.com.mobicare.cielo.recebaMais.presentation.ui.adapter.DomicilioBancarioAdapter
import kotlinx.android.synthetic.main.content_receba_mais_domicilio_bancario.*

class MyDomicilioBancarioFragment: BaseFragment() {

    private lateinit var banks: List<Bank>
    private  var bank: Bank? = null
    private var selectDom: (dom: Bank) -> Unit = {}

    companion object {
        fun create(selectDomicilio: (select : Bank) -> Unit, banks: List<Bank>, bank: Bank?) : MyDomicilioBancarioFragment {
            val fragment = MyDomicilioBancarioFragment().apply {
                this.selectDom   = selectDomicilio
                this.banks = banks
                this.bank = bank
            }
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_receba_mais_domicilio_bancario, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view_domicilio_bancario.layoutManager = (LinearLayoutManager(requireContext()))
        recycler_view_domicilio_bancario.adapter = DomicilioBancarioAdapter(activity!!, banks, bank, selectDomicilio = {
            selectDom(it)
        } )

    }

}