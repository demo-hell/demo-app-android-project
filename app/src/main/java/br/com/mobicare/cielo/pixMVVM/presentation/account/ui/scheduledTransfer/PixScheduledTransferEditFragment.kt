package br.com.mobicare.cielo.pixMVVM.presentation.account.ui.scheduledTransfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.Text.SIMPLE_LINE
import br.com.mobicare.cielo.commons.utils.joinWithLastCustomSeparator
import br.com.mobicare.cielo.databinding.FragmentPixScheduledTransferEditBinding
import br.com.mobicare.cielo.pixMVVM.presentation.account.ui.PixAccountBaseFragment
import br.com.mobicare.cielo.pixMVVM.presentation.account.viewmodel.PixReceiptMethodViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PixScheduledTransferEditFragment : PixAccountBaseFragment() {

    private val viewModel: PixReceiptMethodViewModel by sharedViewModel()

    override val toolbarTitle get() = getString(R.string.pix_account_scheduled_transfer_title)

    private var _binding: FragmentPixScheduledTransferEditBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val scheduledList get() = viewModel.onBoardingFulfillment?.settlementScheduled?.list

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        return FragmentPixScheduledTransferEditBinding
            .inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSelectedTime()
        setupImmediateTransfer()
        setupShowImmediateTransferButtonObserver()
        checkImmediateTransferButtonVisibility()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun checkImmediateTransferButtonVisibility() {
        viewModel.fetchFeatureToggleTransferScheduledBalance()
    }

    private fun setupShowImmediateTransferButtonObserver() {
        viewModel.ftTransferScheduledBalanceEnabled.observe(viewLifecycleOwner) { showImmediateTransferButton ->
            if (showImmediateTransferButton) {
                binding.apply {
                    includeImmediateTransfer.root.visible()
                    divider.visible()
                }
            }
        }
    }

    private fun setupSelectedTime() {
        binding.tbEditTime.apply {
            text = scheduledList?.joinWithLastCustomSeparator() ?: SIMPLE_LINE
            setOnClickListener(::onConfigureTap)
        }
    }

    private fun setupImmediateTransfer() {
        binding.includeImmediateTransfer.apply {
            tvTitle.text = getString(R.string.pix_account_scheduled_transfer_edit_immediate_transfer_title)
            tvDescription.text = getString(R.string.pix_account_scheduled_transfer_edit_immediate_transfer_description)
            ivIcon.setImageResource(R.drawable.ic_money_coin_up_24_dp)
            container.setOnClickListener(::onImmediateTransferTap)
        }
    }

    private fun onConfigureTap(v: View) {
        findNavController().navigate(
            PixScheduledTransferEditFragmentDirections
                .actionPixScheduledTransferEditFragmentToPixScheduledTransferConfigureFragment()
        )
    }

    private fun onImmediateTransferTap(v: View) {
        findNavController().navigate(
            PixScheduledTransferEditFragmentDirections
                .actionPixScheduledTransferEditFragmentToPixScheduledTransferBalanceFragment()
        )
    }

}