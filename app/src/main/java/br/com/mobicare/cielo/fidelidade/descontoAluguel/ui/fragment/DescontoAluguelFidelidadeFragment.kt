package br.com.mobicare.cielo.fidelidade.descontoAluguel.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseFragment

/**
 * Created by david on 16/08/17.
 */

class DescontoAluguelFidelidadeFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_desconto_aluguel_fidelidade, container, false)
    }

}