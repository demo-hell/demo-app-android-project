package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.receipt.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.screen.HandlerViewBuilderFluiV2
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentPixTransferSentReceiptBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.toHomePix
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixQrCodeOperationType
import br.com.mobicare.cielo.pixMVVM.domain.model.PixSchedulingDetail
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.scheduled.PixReceiptTransferScheduledViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.sent.PixReceiptQrCodeChangeTransferSentViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.sent.PixReceiptQrCodeTransferSentViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.sent.PixReceiptQrCodeWithdrawalTransferSentViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.receipt.viewModel.PixQRCodeReceiptViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.utils.PixReceiptQRCodeUIState
import org.koin.androidx.viewmodel.ext.android.viewModel

class PixQRCodeReceiptFragment : BaseFragment() {
    private var _binding: FragmentPixTransferSentReceiptBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: PixQRCodeReceiptViewModel by viewModel()

    private var navigation: CieloNavigation? = null

    private val navArgs: PixQRCodeReceiptFragmentArgs by navArgs()
    private val transferResult by lazy { navArgs.pixqrcodereceipttransferresultargs }
    private val qrCodeType by lazy { navArgs.pixqrcodereceiptqrcodetypeargs }

    private val toolbarConfigurator
        get() =
            CieloCollapsingToolbarLayout.Configurator(
                toolbar =
                    CieloCollapsingToolbarLayout.Toolbar(
                        title = getString(R.string.pix_transfer_receipt_title),
                        showBackButton = false,
                        menu =
                            CieloCollapsingToolbarLayout.ToolbarMenu(
                                menuRes = R.menu.menu_common_close_blue,
                                onOptionsItemSelected = ::onOptionsItemSelectedToolbar,
                            ),
                        onBackPressed = ::finish,
                    ),
            )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentPixTransferSentReceiptBinding
        .inflate(inflater, container, false)
        .also {
            _binding = it
        }.root

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun loadTransferOrSchedulingDetails() = viewModel.getTransferOrSchedulingDetails(transferResult)

    private fun initView() {
        setNavigation()
        addObserver()
        loadTransferOrSchedulingDetails()
    }

    private fun setNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.configureCollapsingToolbar(toolbarConfigurator)
        }
    }

    private fun addObserver() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PixReceiptQRCodeUIState.ShowLoading -> onShowLoading()
                is PixReceiptQRCodeUIState.HideLoading -> onHideLoading()
                is PixReceiptQRCodeUIState.TransactionExecutedSuccess -> onTransactionExecutedSuccess(state.data)
                is PixReceiptQRCodeUIState.TransactionScheduledSuccess -> onTransactionScheduledSuccess(state.data)
                is PixReceiptQRCodeUIState.Error -> onError()
                is PixReceiptQRCodeUIState.ReturnBackScreen -> finish()
            }
        }
    }

    private fun onShowLoading() {
        navigation?.showAnimatedLoading()
    }

    private fun onHideLoading() {
        navigation?.hideAnimatedLoading()
    }

    private fun onTransactionExecutedSuccess(data: PixTransferDetail) {
        doWhenResumed {
            navigation?.showContent()

            binding.content.addView(
                when (qrCodeType) {
                    PixQrCodeOperationType.WITHDRAWAL -> PixReceiptQrCodeWithdrawalTransferSentViewBuilder(layoutInflater, data).build()
                    PixQrCodeOperationType.CHANGE -> PixReceiptQrCodeChangeTransferSentViewBuilder(layoutInflater, data).build()
                    else -> PixReceiptQrCodeTransferSentViewBuilder(layoutInflater, data).build()
                },
            )
        }
    }

    private fun onTransactionScheduledSuccess(data: PixSchedulingDetail) {
        doWhenResumed {
            binding.content.addView(
                PixReceiptTransferScheduledViewBuilder(layoutInflater, data).build(),
            )
        }
    }

    private fun onError() {
        doWhenResumed {
            navigation?.showHandlerViewV2(
                title = getString(R.string.commons_generic_error_title),
                message = getString(R.string.commons_generic_error_message),
                labelPrimaryButton = getString(R.string.text_try_again_label),
                onPrimaryButtonClickListener =
                    object : HandlerViewBuilderFluiV2.HandlerViewListener {
                        override fun onClick(dialog: Dialog?) {
                            dialog?.dismiss()
                            loadTransferOrSchedulingDetails()
                        }
                    },
                onBackButtonClickListener =
                    object : HandlerViewBuilderFluiV2.HandlerViewListener {
                        override fun onClick(dialog: Dialog?) {
                            dialog?.dismiss()
                            finish()
                        }
                    },
            )
        }
    }

    private fun onOptionsItemSelectedToolbar(item: MenuItem) {
        if (item.itemId == R.id.action_close) finish()
    }

    private fun finish() = requireActivity().toHomePix()
}
