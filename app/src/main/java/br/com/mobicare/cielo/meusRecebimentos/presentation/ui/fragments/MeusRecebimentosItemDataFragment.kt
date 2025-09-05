package br.com.mobicare.cielo.meusRecebimentos.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseFragment
import kotlinx.android.synthetic.main.item_meus_recebimentos_data.*

/**
 * Created by benhur.souza on 26/06/2017.
 */

class MeusRecebimentosItemDataFragment: BaseFragment(){

    companion object {
        private val DATA = "data"

        fun newInstance(data: String?): MeusRecebimentosItemDataFragment {
            val fragmentFirst = MeusRecebimentosItemDataFragment()
            val args = Bundle()
            args.putString(DATA, data)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }

    private var data: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data = arguments?.getString(MeusRecebimentosItemDataFragment.DATA)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.item_meus_recebimentos_data, container, false)
    }

    override fun onStart() {
        super.onStart()
        setBandeira()

    }

    fun setBandeira(){
        text_view_meus_recebimentos_data.text = data
    }
}
