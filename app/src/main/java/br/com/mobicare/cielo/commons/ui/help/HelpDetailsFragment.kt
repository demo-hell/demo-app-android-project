package br.com.mobicare.cielo.commons.ui.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.recebaMais.domain.Doub
import kotlinx.android.synthetic.main.fragment_receba_mais_help_detail.*

class HelpDetailsFragment internal constructor() : BaseFragment() {
    private val DOUB = "DOUB"

    companion object {
        fun create(doub: Doub) = HelpDetailsFragment().apply {
            val extras = Bundle()
            extras.putParcelable(DOUB, doub)
            this.arguments = extras
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_receba_mais_help_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        arguments?.let {
            val doub = it.get(DOUB) as Doub
            text_title.text = doub.title
            text_description.text = doub.subtitle
        }
    }
}