package br.com.mobicare.cielo.pixMVVM.presentation.refund.ui.refundReceipts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import br.com.cielo.libflue.button.v2.CieloButton
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.ifNull
import br.com.mobicare.cielo.databinding.FragmentPixRefundReceiptsBinding
import br.com.mobicare.cielo.databinding.LayoutPixRefundReceiptsFooterBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pixMVVM.presentation.refund.adapters.PixRefundReceiptsAdapter
import br.com.mobicare.cielo.pixMVVM.presentation.refund.utils.PixRefundReceiptsUiState
import br.com.mobicare.cielo.pixMVVM.presentation.refund.viewmodel.PixRequestRefundViewModel
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

abstract class PixRefundReceiptsBaseFragment : BaseFragment() {

    abstract val footerConfigurator: FooterConfigurator

    private val viewModel: PixRequestRefundViewModel by sharedViewModel()

    private var _binding: FragmentPixRefundReceiptsBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var _footerBinding: LayoutPixRefundReceiptsFooterBinding? = null
    private val footerBinding get() = requireNotNull(_footerBinding)

    private var navigation: CieloNavigation? = null

    protected val receipts get() = viewModel.refundReceipts?.receipts
    protected val deadlineDate get() = viewModel.transferDetail?.transactionReversalDeadline
    protected val availableAmountToRefund get() = viewModel.refundReceipts?.totalAmountPossibleReversal

    override fun onCreateView( 
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentPixRefundReceiptsBinding
        .inflate(inflater, container, false)
        .also {
            _binding = it
            _footerBinding = LayoutPixRefundReceiptsFooterBinding.inflate(inflater, container, false)
        }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeNavigation()
        setupObserver()
        loadReceipts()
    }

    override fun onResume() {
        super.onResume()
        configureCollapsingToolbar()
        configureFooter()
    }

    override fun onDestroyView() {
        _footerBinding = null
        _binding = null
        super.onDestroyView()
    }

    private fun initializeNavigation() {
        navigation = requireActivity() as? CieloNavigation
    }

    private fun configureCollapsingToolbar() {
        navigation?.configureCollapsingToolbar(
            CieloCollapsingToolbarLayout.Configurator(
                toolbar = CieloCollapsingToolbarLayout.Toolbar(
                    title = getString(R.string.pix_refund_receipts_title),
                    menu = CieloCollapsingToolbarLayout.ToolbarMenu(
                        menuRes = R.menu.menu_help,
                        onOptionsItemSelected = ::onMenuOptionSelected
                    )
                ),
                footerView = footerBinding.root
            )
        )
    }

    private fun loadReceipts() {
        if (receipts != null) handleSuccessState() else setupReload()
    }

    private fun setupReload() {
        binding.apply {
            rvRefundReceipts.gone()
            containerReload.visible()
            btReload.setOnClickListener { viewModel.getRefundReceipts() }
        }
        setFooterVisible(false)
    }

    private fun setupObserver() {
        viewModel.refundReceiptsUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PixRefundReceiptsUiState.Loading -> handleLoadingState()
                is PixRefundReceiptsUiState.Success -> handleSuccessState()
                is PixRefundReceiptsUiState.Error -> handleErrorState()
            }
        }
    }

    private fun handleLoadingState() {
        binding.apply {
            containerReload.gone()
            shimmerReload.visible()
        }
    }

    private fun handleErrorState() {
        binding.apply {
            shimmerReload.gone()
            containerReload.visible()
        }
    }

    private fun handleSuccessState() {
        binding.apply {
            shimmerReload.gone()
            containerReload.gone()
            rvRefundReceipts.apply {
                adapter = PixRefundReceiptsAdapter(receipts.orEmpty())
                visible()
            }
        }
        setFooterVisible()
    }

    private fun configureFooter() {
        footerConfigurator.let { configurator ->
            configureAvailableToRefundText(configurator.availableAmountToRefundText)
            configureNoteText(configurator.noteText)
            configureButton(footerBinding.btPrimaryButton, configurator.primaryButton)
            configureButton(footerBinding.btSecondaryButton, configurator.secondaryButton)
        }
    }

    private fun configureAvailableToRefundText(availableToRefundText: String?) {
        footerBinding.apply {
            tvRefundableAmount.text = availableToRefundText
            containerRefundableAmount.visible(availableToRefundText.isNullOrBlank().not())
        }
    }

    private fun configureNoteText(noteText: String?) {
        footerBinding.cardInformation.apply {
            cardText = noteText.orEmpty()
            visible(noteText.isNullOrBlank().not())
        }
    }

    private fun configureButton(buttonView: CieloButton, buttonConfigurator: FooterButtonConfigurator?) {
        buttonView.apply {
            buttonConfigurator?.let { configurator ->
                text = configurator.text
                setOnClickListener { configurator.onTap() }
                visible()
            }.ifNull {
                gone()
            }
        }
    }

    private fun onMenuOptionSelected(menuItem: MenuItem) {
        if (menuItem.itemId == R.id.menuActionHelp) {
            requireActivity().openFaq(
                tag = ConfigurationDef.TAG_HELP_CENTER_PIX,
                subCategoryName = getString(R.string.cielo_facilita_central_de_ajuda_pix)
            )
        }
    }

    protected fun onCloseTap() {
        requireActivity().finish()
    }

    private fun setFooterVisible(isVisible: Boolean = true) {
        footerBinding.root.visible(isVisible)
    }

    data class FooterConfigurator(
        val availableAmountToRefundText: String? = null,
        val noteText: String? = null,
        val primaryButton: FooterButtonConfigurator? = null,
        val secondaryButton: FooterButtonConfigurator? = null
    )

    data class FooterButtonConfigurator(
        val text: String,
        val onTap: () -> Unit
    )

}