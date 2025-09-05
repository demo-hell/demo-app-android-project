package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.token.presentation.HandlerValidationToken
import br.com.mobicare.cielo.databinding.FragmentPixNewExtractDetailBinding
import br.com.mobicare.cielo.databinding.LayoutPixExtractDetailFooterBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundDetailFull
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail
import br.com.mobicare.cielo.pixMVVM.presentation.extract.PixNewExtractNavigationFlowActivity
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.scheduleCancel.PixCancelScheduleViewHandler
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result.PixRefundUiResult
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result.PixScheduleUiResult
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result.PixTransferUiResult
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.state.PixExtractDetailUiState
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.PixRefundViewSelector
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.PixScheduleViewSelector
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.PixTransferViewSelector
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.model.PixExtractHomeArgs
import br.com.mobicare.cielo.pixMVVM.presentation.infringement.PixInfringementNavigationFlowActivity
import br.com.mobicare.cielo.pixMVVM.presentation.refund.PixRefundNavigationFlowActivity
import br.com.mobicare.cielo.pixMVVM.utils.PixConstants
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PixNewExtractDetailFragment :
    BaseFragment(),
    CieloNavigationListener {
    private val viewModel: PixNewExtractDetailViewModel by viewModel()
    private val handlerValidationToken: HandlerValidationToken by inject()

    private var _binding: FragmentPixNewExtractDetailBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var _footerBinding: LayoutPixExtractDetailFooterBinding? = null
    private val footerBinding get() = requireNotNull(_footerBinding)

    private var navigation: CieloNavigation? = null

    private val args: PixNewExtractDetailFragmentArgs by navArgs()

    private val isRefundTransaction: Boolean? by lazy { args.pixisareversaltransactionargs }
    private val pixExtractHomeArgs: PixExtractHomeArgs? by lazy { args.pixextracthomeargs }

    private val data: PixNewExtractNavigationFlowActivity.NavArgs.Data by lazy {
        (requireActivity() as PixNewExtractNavigationFlowActivity).data
    }

    private val pixCancelScheduleHandler = PixCancelScheduleViewHandler()

    private val isSchedulingReceipt get() = pixExtractHomeArgs?.schedulingCode != null

    private val toolbarTitle
        get() =
            if (isSchedulingReceipt) {
                R.string.pix_extract_detail_title_schedule
            } else {
                R.string.pix_extract_detail_title
            }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _footerBinding = LayoutPixExtractDetailFooterBinding.inflate(inflater, container, false)

        return FragmentPixNewExtractDetailBinding
            .inflate(inflater, container, false)
            .also {
                _binding = it
            }.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupPixCancelScheduleHandler()
        initializeObservers()
        loadTransactionDetails()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()

        setupNavigation()
        setupFooter()
    }

    override fun onDestroyView() {
        _footerBinding = null
        _binding = null
        super.onDestroyView()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation =
                (requireActivity() as CieloNavigation).also {
                    it.configureCollapsingToolbar(
                        CieloCollapsingToolbarLayout.Configurator(
                            toolbar =
                                CieloCollapsingToolbarLayout.Toolbar(
                                    title = getString(toolbarTitle),
                                ),
                            footerView = footerBinding.root,
                        ),
                    )
                }
            pixCancelScheduleHandler.setNavigation(navigation)
        }
    }

    private fun initializeObservers() {
        initializeTransferStateObserver()
        initializeRefundStateObserver()
        initializeScheduleStateObserver()
        initializeRefundReceiptsStateObserver()
        pixCancelScheduleHandler.initializeCancelScheduleStateObserver()
    }

    private fun initializeTransferStateObserver() {
        viewModel.transferState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PixExtractDetailUiState.Loading -> showLoading()
                is PixExtractDetailUiState.Error -> showErrorMessage()
                is PixExtractDetailUiState.Success -> handleTransferResult(state.result)
            }
            setupFooter()
        }
    }

    private fun initializeRefundStateObserver() {
        viewModel.refundState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PixExtractDetailUiState.Loading -> showLoading()
                is PixExtractDetailUiState.Error -> showErrorMessage()
                is PixExtractDetailUiState.Success -> handleRefundResult(state.result)
            }
            setupFooter()
        }
    }

    private fun initializeScheduleStateObserver() {
        viewModel.scheduleState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PixExtractDetailUiState.Loading -> showLoading()
                is PixExtractDetailUiState.Error -> showErrorMessage()
                is PixExtractDetailUiState.Success -> handleScheduleResult(state.result)
            }
            setupFooter()
        }
    }

    private fun initializeRefundReceiptsStateObserver() {
        viewModel.refundReceiptsState.observe(viewLifecycleOwner) {
            setupFooter()
        }
    }

    private fun setupPixCancelScheduleHandler() {
        pixCancelScheduleHandler.setup(
            fragment = this,
            viewModel = viewModel,
            handlerValidationToken = handlerValidationToken,
        )
    }

    private fun loadTransactionDetails() {
        pixExtractHomeArgs?.let {
            viewModel.start(
                it.transactionCode,
                it.idEndToEnd,
                it.schedulingCode,
                isRefundTransaction,
            )
        }
    }

    private fun setupListeners() {
        footerBinding.apply {
            btnRefund.setOnClickListener {
                navigateToPixRefund(viewModel.transferDetail)
            }
            btnCancelSchedule.setOnClickListener {
                pixCancelScheduleHandler.cancelSchedule()
            }
            btnRequestAnalysis.setOnClickListener {
                navigateToPixInfringement()
            }
            btnAccessOriginalTransaction.setOnClickListener {
                onAccessOriginalTransactionTap(viewModel.refundDetailFull)
            }
        }
    }

    private fun handleTransferResult(result: PixTransferUiResult) {
        PixTransferViewSelector(
            inflater = layoutInflater,
            data = viewModel.transferDetail,
            onAmountTap = ::onAmountTap,
            onNetAmountTap = ::onNetAmountTap,
            onFeeAmountTap = ::onFeeAmountTap,
        ).run {
            addContentView(invoke(result))
            showContent()
        }
    }

    private fun handleRefundResult(result: PixRefundUiResult) {
        PixRefundViewSelector(
            inflater = layoutInflater,
            data = viewModel.refundDetailFull,
        ).run {
            addContentView(invoke(result))
            showContent()
        }
    }

    private fun handleScheduleResult(result: PixScheduleUiResult) {
        PixScheduleViewSelector(layoutInflater).run {
            addContentView(invoke(result))
            showContent()
        }
    }

    private fun setupFooter() {
        footerBinding.apply {
            btnRefund.visible(viewModel.isShowButtonRefund)
            btnCancelSchedule.apply {
                text =
                    getString(
                        if (viewModel.isRecurrentTransferSchedule) {
                            R.string.pix_extract_detail_label_button_footer_cancel_recurrence
                        } else {
                            R.string.pix_extract_detail_label_button_footer_cancel_schedule
                        },
                    )
                visible(viewModel.isShowButtonCancelSchedule)
            }
            btnRequestAnalysis.visible(viewModel.isShowButtonRequestAnalysis)
            btnAccessOriginalTransaction.visible(viewModel.isShowButtonAccessOriginalTransaction)
        }
    }

    private fun onAmountTap(credit: PixTransferDetail.Credit?) {
        if (credit == null) return
        navigateToDetails(credit.creditIdEndToEnd, credit.creditTransactionCode)
    }

    private fun onNetAmountTap(settlement: PixTransferDetail.Settlement?) {
        if (settlement == null) return
        navigateToDetails(settlement.settlementIdEndToEnd, settlement.settlementTransactionCode)
    }

    private fun onFeeAmountTap(fee: PixTransferDetail.Fee?) {
        if (fee == null) return
        navigateToDetails(fee.feeIdEndToEnd, fee.feeTransactionCode)
    }

    private fun onAccessOriginalTransactionTap(refundDetailFull: PixRefundDetailFull?) {
        refundDetailFull?.let {
            navigateToDetails(
                it.refundDetail?.idEndToEndOriginal,
                it.transferDetail?.transactionCode,
            )
        }
    }

    private fun addContentView(view: View) {
        binding.content.apply {
            removeAllViews()
            addView(view)
        }
    }

    private fun showContent() {
        navigation?.showContent()
    }

    private fun showLoading() {
        navigation?.showAnimatedLoading()
    }

    private fun showErrorMessage() {
        navigation?.run {
            showCustomHandler(
                title = getString(R.string.commons_generic_error_title),
                message = getString(R.string.commons_generic_error_message),
                titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                isShowHeaderImage = true,
                isShowFirstButton = true,
                labelSecondButton = getString(R.string.text_try_again_label),
                labelFirstButton = getString(R.string.back),
                secondButtonCallback = ::loadTransactionDetails,
                headerCallback = ::navigateBack,
                finishCallback = ::navigateBack,
                firstButtonCallback = ::navigateBack,
            )
        }
    }

    private fun navigateToPixInfringement() {
        requireActivity().startActivity<PixInfringementNavigationFlowActivity>(
            PixConstants.PIX_ID_END_TO_END_ARGS to viewModel.endToEndId,
        )
    }

    private fun navigateToDetails(
        idEndToEnd: String?,
        transactionCode: String?,
    ) {
        findNavController().navigate(
            PixNewExtractDetailFragmentDirections.actionPixNewExtractDetailFragmentToPixNewExtractDetailFragment(
                false,
                PixExtractHomeArgs(idEndToEnd = idEndToEnd, transactionCode = transactionCode),
            ),
        )
    }

    private fun navigateToPixRefund(transferDetail: PixTransferDetail) {
        requireActivity().startActivity<PixRefundNavigationFlowActivity>(
            PixRefundNavigationFlowActivity.NavArgs.TRANSFER_DETAIL_ARGS to transferDetail,
            PixRefundNavigationFlowActivity.NavArgs.PIX_ACCOUNT_ARGS to data.pixAccount,
            PixRefundNavigationFlowActivity.NavArgs.PROFILE_TYPE_ARGS to data.profileType,
        )
    }

    private fun navigateBack() {
        findNavController().popBackStack()
    }
}
