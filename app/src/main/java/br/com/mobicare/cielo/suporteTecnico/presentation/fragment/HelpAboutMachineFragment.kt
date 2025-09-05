package br.com.mobicare.cielo.suporteTecnico.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentHelpAboutMachineBinding

class HelpAboutMachineFragment : BaseFragment(), CieloNavigationListener {

    private var _binding: FragmentHelpAboutMachineBinding? = null
    private val binding get() = _binding!!
    private var navigation: CieloNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentHelpAboutMachineBinding.inflate(inflater, container, false)
        .also {
            _binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupListeners()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.setupToolbar(isCollapsed = false)
            navigation?.showToolbar(isShow = true)
            navigation?.showBackButton(isShow = false)
            navigation?.showCloseButton(isShow = true)
            navigation?.showHelpButton(isShow = false)
        }
    }

    private fun setupListeners() {
        binding.apply {
            btnBackEquipments.setOnClickListener {
                goBack()
            }
        }
    }

    override fun onButtonClicked(labelButton: String) {
        goBack()
    }

    override fun onCloseButtonClicked() {
        goBack()
    }

    private fun goBack() {
        findNavController().popBackStack()
    }
}