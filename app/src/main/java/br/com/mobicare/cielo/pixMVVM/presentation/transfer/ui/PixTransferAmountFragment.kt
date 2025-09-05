package br.com.mobicare.cielo.pixMVVM.presentation.transfer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.field.TextFieldFlui
import br.com.cielo.libflue.util.imageUtils.setCustomDrawable
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE_DOUBLE
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.ui.fragment.insertAmount.BaseInsertAmountV2Fragment
import br.com.mobicare.cielo.commons.utils.moneyToDoubleValue
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.FragmentPixTransferAmountHeaderBinding
import br.com.mobicare.cielo.extensions.ifNullOrBlank
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.pixMVVM.presentation.key.PixKeyNavigationFlowActivity
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.factory.PixKeyDataFactory
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.viewmodel.PixTransferViewModel
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.math.BigDecimal

class PixTransferAmountFragment : BaseInsertAmountV2Fragment() {

    private val viewModel: PixTransferViewModel by sharedViewModel()
    private val navArgs: PixTransferAmountFragmentArgs by navArgs()

    private var _bindingHeader: FragmentPixTransferAmountHeaderBinding? = null
    private val bindingHeader get() = _bindingHeader!!

    private val pixValidateKey by lazy { navArgs.pixvalidatekey }
    private val pixBankAccountStore by lazy { navArgs.pixbankaccountstore }
    private val pixKeyData by lazy { PixKeyDataFactory.create(pixValidateKey, pixBankAccountStore) }

    private val currentBalance by lazy {
        (navigation?.getData() as? PixKeyNavigationFlowActivity.NavArgs.Data)
            ?.currentBalance ?: -ONE_DOUBLE
    }

    private val isBalanceFound get() = currentBalance >= ZERO_DOUBLE

    override val title get() = getString(R.string.pix_transfer_amount_title)

    override val actionButton get() = ActionButton(
        text = getString(R.string.confirmar),
        onTap = ::onConfirmAmount
    )

    override val headerView get() = bindingHeader.root

    override val footerText get() = if (isBalanceFound) {
        getString(R.string.pix_transfer_amount_footer, currentBalance.toPtBrRealString())
    } else {
        null
    }

    override val validators get() = if (isBalanceFound) {
        super.validators + listOf(
            TextFieldFlui.Validator(
                rule = { it.moneyToDoubleValue() <= currentBalance },
                errorMessage = getString(R.string.pix_transfer_amount_error_insufficient_balance)
            )
        )
    } else {
        super.validators
    }

    override val toolbarMenu get() = CieloCollapsingToolbarLayout.ToolbarMenu(
        menuRes = R.menu.menu_help,
        onOptionsItemSelected = ::onMenuOptionSelected
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): LinearLayout {
        _bindingHeader = FragmentPixTransferAmountHeaderBinding.inflate(inflater, container, false)

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
            tvRecipientName.text = getString(R.string.pix_transfer_amount_info_recipient_name, pixKeyData.ownerName)
            tvRecipientInfo.text = getString(
                R.string.pix_transfer_amount_info_recipient_info,
                pixKeyData.documentType.ifNullOrBlank(getString(R.string.pix_transfer_review_label_document)),
                pixKeyData.formattedDocumentNumber,
                pixKeyData.bankName
            )
        }
    }

    private fun onConfirmAmount(amount: BigDecimal) {
        viewModel.run {
            setAmount(amount.toDouble())
            val balance = if (isBalanceFound) currentBalance else null
            if (store.validateAmount(balance)) {
                keyData = pixKeyData
                navigateToReview()
            }
        }
    }

    private fun navigateToReview() {
        findNavController().safeNavigate(
            PixTransferAmountFragmentDirections.actionPixTransferAmountFragmentToPixTransferReviewFragment()
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