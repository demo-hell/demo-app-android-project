package br.com.mobicare.cielo.fidelidade.busca.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseFragment

/**
 * Created by david on 22/08/17.
 */

class BuscaFidelidadeFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.content_busca_fidelidade, container, false)
    }

}
