package br.com.mobicare.cielo.pix.ui.mylimits.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentPixMyLimitsBinding
import br.com.mobicare.cielo.pix.enums.BeneficiaryTypeEnum

class PixMyLimitsFragment : BaseFragment(), CieloNavigationListener {

    private var binding: FragmentPixMyLimitsBinding? = null
    private var navigation: CieloNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentPixMyLimitsBinding.inflate(
        inflater, container, false
    ).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initOnClick()
    }

    override fun onResume() {
        super.onResume()
        setupNavigation()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.text_toolbar_pix_my_limits))
            navigation?.showContainerButton()
            navigation?.showHelpButton()
            navigation?.showContent(isShow = true)
            navigation?.setNavigationListener(this)
        }
    }

    private fun initOnClick() {
        binding?.apply {
            tvMyLimitsPixPf.setOnClickListener(::onPixPfClick)
            tvMyLimitsPixPj.setOnClickListener(::onPixPjClick)
            tvMyLimitsWithdrawAndChange.setOnClickListener(::onWithdrawAndChangeClick)
            tvMyLimitsAccountRegistration.setOnClickListener(::onAccountRegistrationClick)
            tvMyLimitsTimeManagement.setOnClickListener(::onTimeManagementClick)
        }
    }

    private fun onPixPfClick(view: View?) {
        findNavController().navigate(
            PixMyLimitsFragmentDirections.actionPixMyLimitsFragmentToPixMyLimitsTransactionFragment(
                BeneficiaryTypeEnum.CPF
            )
        )
    }

    private fun onPixPjClick(view: View?) {
        findNavController().navigate(
            PixMyLimitsFragmentDirections.actionPixMyLimitsFragmentToPixMyLimitsTransactionFragment(
                BeneficiaryTypeEnum.CNPJ
            )
        )
    }

    private fun onWithdrawAndChangeClick(view: View?) {
        findNavController().navigate(
            PixMyLimitsFragmentDirections.actionPixMyLimitsFragmentToPixMylimitsWithdrawAndChargeFragment()
        )
    }

    private fun onAccountRegistrationClick(view: View?) {
        findNavController().navigate(
            PixMyLimitsFragmentDirections.actionPixMyLimitsFragmentToPixMyLimitsTrustedDestinations(
                false
            )
        )
    }

    private fun onTimeManagementClick(view: View?) {
        findNavController().navigate(
            PixMyLimitsFragmentDirections.actionPixMyLimitsFragmentToPixMyLimitsTimeManagementFragment()
        )
    }

}