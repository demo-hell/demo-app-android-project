package br.com.mobicare.cielo.openFinance.presentation.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.utils.Utils.openBrowser
import br.com.mobicare.cielo.commons.utils.registerForActivityResultCustom
import br.com.mobicare.cielo.component.impersonate.data.model.response.MerchantResponse
import br.com.mobicare.cielo.component.impersonate.presentation.ImpersonateNavigationFlowActivity
import br.com.mobicare.cielo.component.impersonate.presentation.model.ImpersonateUI
import br.com.mobicare.cielo.component.impersonate.utils.TypeImpersonateEnum
import br.com.mobicare.cielo.databinding.FragmentOpenFinanceHomeBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.openFinance.presentation.utils.FlowReturnHolder.deleteParamsHolder
import br.com.mobicare.cielo.openFinance.presentation.utils.FlowReturnHolder.flowReturnHolder
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateMerchantList
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.PIX_URL
import org.koin.androidx.viewmodel.ext.android.viewModel

class OpenFinanceHomeFragment : Fragment(), CieloNavigationListener {
    private var _binding: FragmentOpenFinanceHomeBinding? = null
    private val binding get() = _binding

    private var impersonateActivityResult: ActivityResultLauncher<Intent>? = null

    private val openFinanceVM: OpenFinanceHomeViewModel by viewModel()

    private var navigation: CieloNavigation? = null

    private val toolbarBlank get() = CieloCollapsingToolbarLayout
        .Configurator(layoutMode = CieloCollapsingToolbarLayout.LayoutMode.BLANK)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openFinanceVM.getPixMerchantAccountList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOpenFinanceHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerImpersonateActivity()
        setupNavigation()
        navigation?.configureCollapsingToolbar(toolbarBlank)
        showView()
    }

    private fun registerImpersonateActivity() {
        impersonateActivityResult =
            registerForActivityResultCustom(::callbackResultRequiredDataFieldFlowActivity)
    }

    private fun callbackResultRequiredDataFieldFlowActivity(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            findNavController().navigate(OpenFinanceHomeFragmentDirections
                .actionOpenFinanceHomeFragmentToResumePaymentHolderFragment(), NavOptions.Builder()
                .setPopUpTo(R.id.resumePaymentHolderFragment, true)
                .build())
        } else {
            finishScreen()
            flowReturnHolder(requireActivity())
        }
    }

    private fun showView() {
        openFinanceVM.getPixMerchantAccountListLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UIStateMerchantList.Success -> {
                    hideLoading()
                    uiState.data?.let { listMerchantResponse ->
                        showImpersonate(listMerchantResponse)
                    }
                }

                is UIStateMerchantList.Error -> {
                    hideLoading()
                    showHandlerUnavailableService()
                }

                is UIStateMerchantList.NotFound -> {
                    hideLoading()
                    showHandlerWithoutAccountPix()
                }

                is UIStateMerchantList.Loading ->{
                    showLoading()
                }
            }
        }
    }

    private fun showImpersonate(listMerchantResponse: List<MerchantResponse>) {
        impersonateActivityResult?.launch(
            ImpersonateNavigationFlowActivity.launch(
                requireContext(),
                args = ImpersonateUI(
                    title = R.string.txt_open_finance_impersonating_title,
                    typeImpersonate = TypeImpersonateEnum.MERCHANT,
                    subTitle = getString(R.string.txt_open_finance_impersonating_subtitle),
                    merchants = listMerchantResponse,
                    flowOpenFinance = true
                )
            )
        )
    }

    private fun showHandlerUnavailableService() {
        navigation?.showCustomHandlerView(
            contentImage = R.drawable.ic_07,
            title = getString(R.string.bs_claim_generic_error_title),
            message = getString(R.string.txt_pix_open_finance_unavailable_service),
            labelSecondButton = getString(R.string.txt_pix_open_finance_back_institution),
            isShowFirstButton = false,
            isShowSecondButton = true,
            callbackSecondButton = {
                finishScreen()
                flowReturnHolder(requireActivity())
            },
            callbackBack = {
                finishScreen()
                flowReturnHolder(requireActivity())
            },
            callbackClose = {
                finishScreen()
                flowReturnHolder(requireActivity())
            }
        )
    }

    private fun showHandlerWithoutAccountPix() {
        navigation?.showCustomHandlerView(
            contentImage = R.drawable.ic_07,
            title = getString(R.string.txt_pix_open_finance_not_account_pix),
            message = getString(R.string.txt_pix_open_finance_request_pix_account),
            labelFirstButton = getString(R.string.txt_pix_open_finance_back_institution),
            labelSecondButton = getString(R.string.txt_pix_open_finance_meet_pix_account),
            isShowFirstButton = true,
            isShowSecondButton = true,
            callbackFirstButton = {
                finishScreen()
                flowReturnHolder(requireActivity())
            },
            callbackSecondButton = {
                finishScreen()
                deleteParamsHolder()
                openBrowser(requireActivity(), PIX_URL)
            },
            callbackBack = {
                finishScreen()
                flowReturnHolder(requireActivity())
            },
            callbackClose = {
                finishScreen()
                flowReturnHolder(requireActivity())
            }
        )
    }

    private fun finishScreen() {
        activity?.finish()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.apply {
                setNavigationListener(this@OpenFinanceHomeFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading(){
        binding?.containerLoading.visible()
    }

    private fun hideLoading(){
        binding?.containerLoading.gone()
    }
}