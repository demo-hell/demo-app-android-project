package br.com.mobicare.cielo.suporteTecnico.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.ScreenView.PATH_OPEN_A_TICKET
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.Values.OPEN_A_TICKET
import br.com.mobicare.cielo.commons.constants.HelpCenter
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.databinding.FragmentCentralOpenTicketBinding
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics as GA4

class CentralOpenTicketFragment : BaseFragment(), CieloNavigationListener {
    private var _binding: FragmentCentralOpenTicketBinding? = null
    private val binding get() = _binding!!
    private var navigation: CieloNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentCentralOpenTicketBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupListeners()
        GA4.logWarningDisplayContent(PATH_OPEN_A_TICKET, OPEN_A_TICKET)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.setupToolbar(isCollapsed = false)
            navigation?.showToolbar(isShow = true)
            navigation?.showBackButton(isShow = true)
            navigation?.showCloseButton(isShow = false)
            navigation?.showHelpButton(isShow = false)
        }
    }

    private fun setupListeners() {
        binding.apply {
            btnFirstPhone.setOnClickListener {
                openCallCenter(HelpCenter.PHONE_CALL_CENTER_CAPITAL)

            }
            btnSecondPhone.setOnClickListener {
                openCallCenter(HelpCenter.PHONE_CALL_CENTER_OTHERS)
            }
            btnBack.setOnClickListener {
                findNavController().popBackStack(R.id.merchantEquipamentsFragment, false)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        findNavController().popBackStack()

    }

    private fun openCallCenter(phone: String) {
        Utils.openCall(requireActivity(), phone)
        navigation?.goToHome()
    }
}
