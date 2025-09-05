package br.com.mobicare.cielo.pixMVVM.presentation.infringement.ui.selectReason

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.DEFAULT_ERROR_MESSAGE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentPixInfringementSelectReasonBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.minhasVendas.fragments.common.ScrollControlledLinearManager
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixEligibilityInfringementResponse
import br.com.mobicare.cielo.pixMVVM.presentation.infringement.ui.selectReason.adapter.PixInfringementSituationAdapter
import br.com.mobicare.cielo.pixMVVM.presentation.infringement.utils.UIPixInfringementSelectReasonState
import org.koin.androidx.viewmodel.ext.android.viewModel

class PixInfringementSelectReasonFragment : BaseFragment(), CieloNavigationListener {

    private val viewModel: PixInfringementSelectReasonViewModel by viewModel()

    private var binding: FragmentPixInfringementSelectReasonBinding? = null
    private var navigation: CieloNavigation? = null
    private var scrollControlledLinearManager: ScrollControlledLinearManager? = null
    private var situationsAdapter: PixInfringementSituationAdapter? = null

    private val collapsingToolbar
        get() = CieloCollapsingToolbarLayout.Configurator(
            layoutMode = CieloCollapsingToolbarLayout.LayoutMode.SCROLLABLE,
            toolbar = CieloCollapsingToolbarLayout.Toolbar(
                title = getString(R.string.text_values_chargeback_faq_label)
            )
        )

    private val idEndToEnd
        get(): String {
            return (navigation?.getData() as String?).orEmpty()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentPixInfringementSelectReasonBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    override fun onResume() {
        super.onResume()

        setupNavigation()
        viewModel.start(idEndToEnd)
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UIPixInfringementSelectReasonState.ShowLoading -> onShowLoading()
                is UIPixInfringementSelectReasonState.HideLoading -> onHideLoading()
                is UIPixInfringementSelectReasonState.Success -> onSuccess()
                is UIPixInfringementSelectReasonState.NavigateToDetailWhatHappened -> onNavigateToDetailWhatHappened()
                is UIPixInfringementSelectReasonState.Ineligible -> onIneligible(state.details)
                is UIPixInfringementSelectReasonState.Error -> onError(state.error)
            }
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = (requireActivity() as CieloNavigation).also {
                it.configureCollapsingToolbar(collapsingToolbar)
                it.setNavigationListener(this)
            }
        }
    }

    private fun onShowLoading() {
        navigation?.showAnimatedLoading()
    }

    private fun onHideLoading() {
        navigation?.hideAnimatedLoading()
    }

    private fun onSuccess() {
        navigation?.showContent()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        scrollControlledLinearManager = ScrollControlledLinearManager(requireContext())
        situationsAdapter = PixInfringementSituationAdapter(::onClickSituation)
        situationsAdapter?.setItems(viewModel.getSituations())

        binding?.apply {
            rvSituations.adapter = situationsAdapter
            rvSituations.layoutManager = scrollControlledLinearManager
        }
    }

    private fun onClickSituation(situation: PixEligibilityInfringementResponse.Situation) {
        viewModel.setSituation(situation)
    }

    private fun onNavigateToDetailWhatHappened() {
        findNavController().safeNavigate(
            PixInfringementSelectReasonFragmentDirections
                .actionPixInfringementSelectReasonFragmentToPixInfringementDetailWhatHappenedFragment(
                    viewModel.getPixCreateNotifyInfringement()
                )
        )
    }

    private fun onIneligible(details: String) {
        val message = details.takeIf { it.isNotBlank() }
            ?: getString(R.string.pix_infringement_select_reason_message_default_ineligible)

        showHandlerView(
            title = getString(R.string.pix_infringement_select_reason_title_ineligible),
            message = message,
            labelSecondButton = getString(R.string.entendi),
            showFirstButton = false
        )
    }

    private fun onError(error: NewErrorMessage?) {
        val message = error?.message.takeIf { it != DEFAULT_ERROR_MESSAGE || it.isNotBlank() }
            ?: getString(R.string.commons_generic_error_message)

        showHandlerView(
            title = getString(R.string.commons_generic_error_title),
            message = message,
            labelSecondButton = getString(R.string.text_try_again_label),
            showFirstButton = true
        )
    }

    private fun showHandlerView(title: String, message: String, labelSecondButton: String, showFirstButton: Boolean) {
        doWhenResumed {
            navigation?.showCustomHandlerView(
                title = title,
                message = message,
                labelFirstButton = getString(R.string.back),
                labelSecondButton = labelSecondButton,
                isShowFirstButton = showFirstButton,
                isShowButtonClose = false,
                callbackFirstButton = {
                    requireActivity().finish()
                },
                callbackSecondButton = {
                    if (showFirstButton) {
                        viewModel.reloadGetPixEligibilityInfringement(idEndToEnd)
                    } else {
                        requireActivity().finish()
                    }
                },
                callbackBack = {
                    requireActivity().finish()
                }
            )
        }
    }

}