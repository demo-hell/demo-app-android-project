package br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseFragment

class ContaDigitalCatenoPageTypeFragment02 : BaseFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_conta_digital_step02, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ContaDigitalCatenoPageTypeFragment02()
    }
}