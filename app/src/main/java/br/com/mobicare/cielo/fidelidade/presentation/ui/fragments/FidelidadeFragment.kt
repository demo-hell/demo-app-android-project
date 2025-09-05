package br.com.mobicare.cielo.fidelidade.presentation.ui.fragments

import android.os.Bundle
import android.view.*
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseFragment

/**
 * Created by Benhur on 16/08/17.
 */
class FidelidadeFragment: BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fidelidade_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fidelidade, menu)  // Use filter.xml from step 1
    }
}