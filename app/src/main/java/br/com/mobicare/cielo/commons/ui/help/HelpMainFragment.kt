package br.com.mobicare.cielo.commons.ui.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.recebaMais.domain.Doub
import br.com.mobicare.cielo.recebaMais.domain.HelpCenter
import br.com.mobicare.cielo.recebaMais.presentation.ui.adapter.HelpMainAdapter
import kotlinx.android.synthetic.main.fragment_receba_mais_help_main.*

class HelpMainFragment internal constructor() : BaseFragment() {

    private lateinit var helpCenter: HelpCenter
    private var selectItem: (item: Doub) -> Unit = {}

    companion object {
        fun create(helpCenter: HelpCenter,
                   selectItem: (item: Doub) -> Unit): HelpMainFragment {
            val fragment = HelpMainFragment().apply {
                this.selectItem = selectItem
                this.helpCenter = helpCenter
            }
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_receba_mais_help_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.layoutManager = LinearLayoutManager(activity)
        recycler_view.adapter = HelpMainAdapter(helpCenter.doubts) {
            selectItem(it)
        }
    }
}