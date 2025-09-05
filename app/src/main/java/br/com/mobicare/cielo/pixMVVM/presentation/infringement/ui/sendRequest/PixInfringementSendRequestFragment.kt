package br.com.mobicare.cielo.pixMVVM.presentation.infringement.ui.sendRequest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.DEFAULT_ERROR_MESSAGE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.convertToBrDateFormat
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.FragmentPixInfringementSendRequestBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.toHomePix
import br.com.mobicare.cielo.pixMVVM.presentation.infringement.utils.UIPixInfringementSendRequestState
import org.koin.androidx.viewmodel.ext.android.viewModel

class PixInfringementSendRequestFragment : BaseFragment(), CieloNavigationListener {

    private val viewModel: PixInfringementSendRequestViewModel by viewModel()

    private var binding: FragmentPixInfringementSendRequestBinding? = null
    private var navigation: CieloNavigation? = null

    private val args: PixInfringementSendRequestFragmentArgs by navArgs()
    private val pixCreateNotifyInfringement by lazy {
        args.pixcreateinfringement
    }

    private val collapsingToolbar
        get() = CieloCollapsingToolbarLayout.Configurator(
            layoutMode = CieloCollapsingToolbarLayout.LayoutMode.SCROLLABLE,
            toolbar = CieloCollapsingToolbarLayout.Toolbar(
                title = getString(R.string.pix_infringement_send_request_title_toolbar)
            )
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentPixInfringementSendRequestBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupObservers()
        setupView()
        setupListeners()

        viewModel.setData(pixCreateNotifyInfringement)
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = (requireActivity() as CieloNavigation).also {
                it.configureCollapsingToolbar(collapsingToolbar)
                it.setNavigationListener(this)
            }
        }
    }

    private fun setupView() {
        binding?.apply {
            pixCreateNotifyInfringement?.let {
                tvReason.setText(it.situationDescription.orEmpty())
                tvValue.setText(it.amount?.toPtBrRealString().orEmpty())
                tvDate.setText(it.date?.convertToBrDateFormat().orEmpty())
                tvInstitution.setText(it.institution.orEmpty())
                tvDetailsOfWhatHappened.setText(it.message.orEmpty())
            }
        }
    }

    private fun setupListeners() {
        binding?.btnSendRequest?.setOnClickListener {
            viewModel.sendRequest()
        }
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UIPixInfringementSendRequestState.ShowLoading -> onShowLoading()
                is UIPixInfringementSendRequestState.HideLoading -> onHideLoading()
                is UIPixInfringementSendRequestState.Success -> onSuccess()
                is UIPixInfringementSendRequestState.Error -> onError(state.error)
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
        doWhenResumed {
            navigation?.showCustomHandlerView(
                contentImage = R.drawable.img_122_pix_solicitacao_enviada,
                title = getString(R.string.pix_infringement_send_request_title_bs_success),
                message = getString(R.string.pix_infringement_send_request_message_bs_success),
                labelSecondButton = getString(R.string.pix_infringement_send_request_label_second_button_bs_success),
                isShowButtonClose = false,
                callbackSecondButton = ::goToHomePix,
                callbackBack = ::goToHomePix
            )
        }
    }

    private fun onError(error: NewErrorMessage?) {
        doWhenResumed {
            val message = error?.message.takeIf { it != DEFAULT_ERROR_MESSAGE || it.isNotBlank() }
                ?: getString(R.string.commons_generic_error_message)

            navigation?.showCustomHandlerView(
                contentImage = R.drawable.img_10_erro,
                title = getString(R.string.commons_generic_error_title),
                message = message,
                labelFirstButton = getString(R.string.back),
                labelSecondButton = getString(R.string.text_try_again_label),
                isShowFirstButton = true,
                isShowButtonClose = true,
                callbackSecondButton = {
                    viewModel.sendRequest()
                },
                callbackFirstButton = {
                    navigation?.showContent()
                },
                callbackClose = {
                    navigation?.showContent()
                },
                callbackBack = {
                    navigation?.showContent()
                }
            )
        }
    }

    private fun goToHomePix() {
        requireContext().toHomePix()
    }

}