package br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.validationServiceSupply

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics.Companion.SCREEN_VIEW_REQUEST_MATERIALS
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.selfServiceSupply.SelfServiceSupplyFragment
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.Values.UPDATE_ADDRESS
import br.com.mobicare.cielo.commons.router.FRAGMENT_TO_ROUTER
import br.com.mobicare.cielo.commons.router.RouterFragmentInActivity
import br.com.mobicare.cielo.commons.router.TITLE_ROUTER_FRAGMENT
import br.com.mobicare.cielo.commons.router.deeplink.MEU_CADASTRO_TITLE
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentValidationServiceSupplyBinding
import br.com.mobicare.cielo.main.presentation.ui.activities.MainBottomNavigationActivity
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.MeuCadastroFragmentAtualNovo
import br.com.mobicare.cielo.suporteTecnico.presentation.viewModel.RequestTicketSupportViewModel
import br.com.mobicare.cielo.suporteTecnico.utils.UIStateRequestTicketSupport
import org.koin.androidx.viewmodel.ext.android.viewModel
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics as GA4


class ValidationServiceSupplyFragment : BaseFragment() {
    private var _binding: FragmentValidationServiceSupplyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RequestTicketSupportViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentValidationServiceSupplyBinding.inflate(
        inflater, container, false
    ).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListenerScreenHome()
        setupClickListenerUpdateAddress()
    }

    override fun onResume() {
        super.onResume()
        onReload()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        viewModel.merchantLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UIStateRequestTicketSupport.Loading -> {
                    showLoading()
                }

                is UIStateRequestTicketSupport.AuthorizationSuccess -> {
                    setupViewAuthorization()
                }

                is UIStateRequestTicketSupport.AuthorizationError -> {
                    setupViewNotAuthorization()
                }

                is UIStateRequestTicketSupport.Error -> {
                    onLoadingError()
                }
            }
        }
    }

    private fun onReload() {
        viewModel.getMerchant()
    }

    private fun setupViewAuthorization() {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.routerFragmentContainer, SelfServiceSupplyFragment())
        transaction.commit()
    }

    private fun setupViewNotAuthorization() {
        GA4.logWarningDisplayContent(SCREEN_VIEW_REQUEST_MATERIALS, UPDATE_ADDRESS)
        binding.apply {
            errorInclude.root.gone()
            progress.root.gone()
            containerUpdateAddress.visible()
        }
    }

    private fun showLoading() {
        binding.apply {
            progress.root.visible()
            errorInclude.root.gone()
            containerUpdateAddress.gone()
        }
    }

    private fun onLoadingError() {
        binding.apply {
            progress.root.gone()
            containerUpdateAddress.gone()
            errorInclude.root.visible()

            errorInclude.btnReload.setOnClickListener {
                onReload()
            }
        }
    }

    private fun setupClickListenerScreenHome() {
        binding.btnScreenHome.setOnClickListener {
            val intent = Intent(requireActivity(), MainBottomNavigationActivity::class.java)
            startActivity(intent)
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setupClickListenerUpdateAddress() {
        binding.btnUpdateAddress.setOnClickListener {
            Intent(context, RouterFragmentInActivity::class.java).apply {
                putExtra(FRAGMENT_TO_ROUTER, MeuCadastroFragmentAtualNovo::class.java.canonicalName)
                putExtra(TITLE_ROUTER_FRAGMENT, MEU_CADASTRO_TITLE)
                requireContext().startActivity(this)
            }
        }
    }
}