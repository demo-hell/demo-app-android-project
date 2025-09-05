package br.com.mobicare.cielo.openFinance.presentation.manager.newShare.conclusion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.SIMPLE_DATE_FORMAT_DATE_TIME_FULL_MINUS
import br.com.mobicare.cielo.commons.utils.SIMPLE_DT_FORMAT_MASK
import br.com.mobicare.cielo.commons.utils.getScreenHeight
import br.com.mobicare.cielo.databinding.OpenFinanceConclusionFragmentBinding
import br.com.mobicare.cielo.extensions.formatterDate
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.openFinance.domain.model.ConfirmShare
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateConclusionShare
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.isSharedDataFragmentActive
import br.com.mobicare.cielo.pix.constants.EMPTY
import org.koin.androidx.viewmodel.ext.android.viewModel

class OpenFinanceConclusionFragment : BaseFragment(), CieloNavigationListener {
    private var binding: OpenFinanceConclusionFragmentBinding? = null
    private var navigation: CieloNavigation? = null
    private val conclusionViewModel by viewModel<OpenFinanceConclusionViewModel>()

    private val toolbarBlank
        get() = CieloCollapsingToolbarLayout
            .Configurator(layoutMode = CieloCollapsingToolbarLayout.LayoutMode.BLANK)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = OpenFinanceConclusionFragmentBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        configureToolbar()
        setMinimumHeight()
        conclusionViewModel.confirmOrGivenUpShare()
        observeConclusionShare()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
        }
    }

    private fun setMinimumHeight() {
        binding?.root?.minimumHeight = requireActivity().getScreenHeight()
    }

    private fun configureToolbar() {
        navigation?.configureCollapsingToolbar(toolbarBlank)
    }

    private fun defineStatusSuccess(confirmShare: ConfirmShare) {
        val dateFormated = confirmShare.expirationDateTime.formatterDate(
            SIMPLE_DATE_FORMAT_DATE_TIME_FULL_MINUS,
            SIMPLE_DT_FORMAT_MASK
        )
        binding?.apply {
            txtBank.text = getString(
                R.string.desc_conclusion_success_opf,
                confirmShare.customerFrindlyName
            ).fromHtml()
            txtDate.text = getString(
                R.string.desc_period_conclusion_success_opf,
                dateFormated
            ).fromHtml()
            btnConclusionShare.setOnClickListener {
                conclusionViewModel.clearUserPreferencesSuccess()
                findNavController().navigate(
                    OpenFinanceConclusionFragmentDirections.actionOpenFinanceConclusionFragmentToConsentDetailFragment(
                        confirmShare.consentId, EMPTY, true
                    )
                )
            }
        }
    }

    private fun defineStatusError() {
        binding?.apply {
            txtTitle.text = getString(R.string.title_conclusion_error_opf)
            txtBank.text = getString(R.string.desc_conclusion_error_opf)
            txtDate.text = getString(R.string.try_again_conclusion_error_opf)
            imgStatusShare.setImageResource(R.drawable.block_ec)
            descBtnConclusion.text = getString(R.string.ok_understand)
            btnConclusionShare.setOnClickListener {
                conclusionViewModel.clearUserPreferencesError()
                checkTocorrectFlow()
            }
        }
    }

    private fun showView() {
        binding?.apply {
            loading.gone()
            containerItems.visible()
            btnConclusionShare.visible()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun observeConclusionShare() {
        conclusionViewModel.conclusionShareLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UIStateConclusionShare.Loading -> {
                    stateLoading()
                }

                is UIStateConclusionShare.SuccessShare -> {
                    stateSuccess(uiState)
                }

                is UIStateConclusionShare.ErrorShare -> {
                    stateError()
                }
            }
        }
    }

    private fun stateError() {
        showView()
        defineStatusError()
    }

    private fun stateSuccess(uiState: UIStateConclusionShare.SuccessShare<ConfirmShare>) {
        showView()
        uiState.data?.let {
            defineStatusSuccess(it)
        }
    }

    private fun stateLoading() {
        binding?.apply {
            loading.visible()
            containerItems.gone()
            btnConclusionShare.gone()
        }
    }

    private fun checkTocorrectFlow() {
        isSharedDataFragmentActive.also { isActive ->
            if (isActive) requireActivity().finish()
            else findNavController().navigate(OpenFinanceConclusionFragmentDirections
                .actionOpenFinanceConclusionFragmentToOpenFinanceSharedDataFragment(true))
        }
    }
}