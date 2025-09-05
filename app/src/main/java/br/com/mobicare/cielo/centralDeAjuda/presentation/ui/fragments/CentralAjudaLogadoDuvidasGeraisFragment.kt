package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.helpers.AppHelper
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.viewBinding
import br.com.mobicare.cielo.contactCielo.analytics.ContactCieloAnalytics.trackClickCentralAjudaButtons
import br.com.mobicare.cielo.contactCielo.data.mapper.ContactCieloWhatsappMapper
import br.com.mobicare.cielo.databinding.CentralAjudaLogadoDuvidasGeraisFragmentBinding

class CentralAjudaLogadoDuvidasGeraisFragment : BaseFragment() {


    private val binding : CentralAjudaLogadoDuvidasGeraisFragmentBinding by viewBinding()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val duvidasGerais = ContactCieloWhatsappMapper.generalDoubts

        binding.cardViewMain.setOnClickListener {
            trackClickCentralAjudaButtons(duvidasGerais)
            AppHelper.showWhatsAppMessage(
                requireActivity(),
                getString(duvidasGerais.whatsappNumber),
                EMPTY
            )
        }
    }
}