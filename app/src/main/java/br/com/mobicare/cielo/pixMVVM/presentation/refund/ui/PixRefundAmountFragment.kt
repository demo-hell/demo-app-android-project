package br.com.mobicare.cielo.pixMVVM.presentation.refund.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.field.TextFieldFlui
import br.com.cielo.libflue.util.imageUtils.setCustomDrawable
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.ui.fragment.insertAmount.BaseInsertAmountV2Fragment
import br.com.mobicare.cielo.commons.utils.moneyToDoubleValue
import br.com.mobicare.cielo.commons.utils.orSimpleLine
import br.com.mobicare.cielo.commons.utils.orZero
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.FragmentPixRefundAmountHeaderBinding
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.pixMVVM.presentation.refund.viewmodel.PixRequestRefundViewModel
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.math.BigDecimal

class PixRefundAmountFragment : BaseInsertAmountV2Fragment() {

    private val viewModel: PixRequestRefundViewModel by sharedViewModel()

    private var _bindingHeader: FragmentPixRefundAmountHeaderBinding? = null
    private val bindingHeader get() = requireNotNull(_bindingHeader)

    override val toolbarMenu get() = CieloCollapsingToolbarLayout.ToolbarMenu(
        menuRes = R.menu.menu_help,
        onOptionsItemSelected = ::onMenuOptionSelected
    )

    override val headerView get() = bindingHeader.root

    override val title get() = getString(R.string.pix_refund_amount_title)

    override val validators get() = super.validators + mutableListOf<TextFieldFlui.Validator>().apply {
        if (isBalanceFound) {
            add(
                TextFieldFlui.Validator(
                    rule = { it.moneyToDoubleValue() <= currentBalance },
                    errorMessage = getString(R.string.pix_refund_amount_error_insufficient_balance)
                )
            )
        }
        add(
            TextFieldFlui.Validator(
                rule = { it.moneyToDoubleValue() <= availableAmountToRefund },
                errorMessage = getString(
                    R.string.pix_refund_amount_error_unavailable_amount_to_refund,
                    availableAmountToRefund.toPtBrRealString()
                )
            )
        )
    }

    override val footerText get() = if (isBalanceFound) {
        getString(
            R.string.pix_refund_amount_footer_1,
            currentBalance.toPtBrRealString(),
            availableAmountToRefund.toPtBrRealString()
        )
    } else {
        getString(
            R.string.pix_refund_amount_footer_2,
            availableAmountToRefund.toPtBrRealString()
        )
    }

    override val actionButton get() = ActionButton(
        text = getString(R.string.confirmar),
        onTap = ::onConfirmAmount
    )

    private val isBalanceFound get() = viewModel.currentBalance != null
    private val currentBalance get() = viewModel.currentBalance.orZero()
    private val availableAmountToRefund get() = viewModel.availableAmountToRefund.orZero()
    private val debitParty get() = viewModel.transferDetail?.debitParty

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): LinearLayout {
        _bindingHeader = FragmentPixRefundAmountHeaderBinding.inflate(inflater, container, false)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHeaderView()
    }

    override fun onDestroyView() {
        _bindingHeader = null
        super.onDestroyView()
    }

    private fun setupHeaderView() {
        bindingHeader.apply {
            root.setCustomDrawable {
                solidColor = R.color.cloud_100
                radius = R.dimen.dimen_8dp
            }
            tvRecipientName.text = getString(
                R.string.pix_refund_amount_info_recipient_name, debitParty?.name.orSimpleLine()
            )
            tvRecipientInfo.text = getString(
                R.string.pix_refund_amount_info_recipient_info,
                debitParty?.nationalRegistration.orSimpleLine(),
                debitParty?.bankName.orSimpleLine()
            )
        }
    }

    private fun onConfirmAmount(amount: BigDecimal) {
        viewModel.run {
            setAmount(amount.toDouble())
            if (validate()) navigateToReview()
        }
    }

    private fun navigateToReview() {
        findNavController().safeNavigate(
            PixRefundAmountFragmentDirections
                .actionPixRefundAmountFragmentToPixRefundReviewFragment()
        )
    }

    private fun onMenuOptionSelected(menuItem: MenuItem) {
        if (menuItem.itemId == R.id.menuActionHelp) {
            requireActivity().openFaq(
                tag = ConfigurationDef.TAG_HELP_CENTER_PIX,
                subCategoryName = getString(R.string.cielo_facilita_central_de_ajuda_pix)
            )
        }
    }

}