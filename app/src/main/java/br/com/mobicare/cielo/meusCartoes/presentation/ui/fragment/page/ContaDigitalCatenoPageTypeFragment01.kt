package br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.helpers.AppHelper
import br.com.mobicare.cielo.commons.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_conta_digital_step01.*

class ContaDigitalCatenoPageTypeFragment01 : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_conta_digital_step01, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }
    private fun init(){
        buttonContract.setOnClickListener {
            AppHelper.redirectToGooglePlay(requireContext(), true)
        }

        val cnpj = UserPreferences.getInstance()?.userInformation?.activeMerchant?.cnpj
        cnpj?.number?.let {
            txt_cnpj_or_cpj?.text = if (it.length > 14) getString(R.string.mask_cnpj_step4_text) else getString(R.string.mask_cpf_step4_text)
            txt_cpnj_cd?.text = it
        }

    }
    companion object {
        @JvmStatic
        fun newInstance() = ContaDigitalCatenoPageTypeFragment01()
    }
}