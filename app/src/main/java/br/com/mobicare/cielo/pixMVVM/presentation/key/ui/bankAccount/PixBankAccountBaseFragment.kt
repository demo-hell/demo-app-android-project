package br.com.mobicare.cielo.pixMVVM.presentation.key.ui.bankAccount

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.StringRes
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.Text.SIMPLE_LINE
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.pixMVVM.presentation.key.viewmodel.PixBankAccountKeyViewModel
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef

abstract class PixBankAccountBaseFragment : BaseFragment(), CieloNavigationListener {

    abstract val viewModel: PixBankAccountKeyViewModel
    abstract val toolbarConfigurator: CieloCollapsingToolbarLayout.Configurator

    protected var navigation: CieloNavigation? = null

    protected val information get() = Information(
        bankName = getInformationString(
            R.string.pix_key_bank_account_info_bank,
            viewModel.bankAccount.bank?.name ?: SIMPLE_LINE
        ),
        accountType = getInformationString(
            R.string.pix_key_bank_account_info_account_type,
            viewModel.bankAccount.bankAccountType?.nameRes?.let { getString(it) } ?: SIMPLE_LINE
        ),
        bankBranchName = getInformationString(
            R.string.pix_key_bank_account_info_agency,
            viewModel.bankAccount.bankBranchNumber ?: SIMPLE_LINE
        ),
        bankAccountNumber = getInformationString(
            R.string.pix_key_bank_account_info_account_number,
            viewModel.bankAccount.bankAccountNumber ?: SIMPLE_LINE
        ),
        bankAccountDigit = getInformationString(
            R.string.pix_key_bank_account_info_account_digit,
            viewModel.bankAccount.bankAccountDigit ?: SIMPLE_LINE
        )
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeNavigation()
    }

    override fun onResume() {
        super.onResume()
        configureToolbar()
    }

    private fun initializeNavigation() {
        navigation = requireActivity() as? CieloNavigation
    }

    private fun configureToolbar() {
        navigation?.configureCollapsingToolbar(toolbarConfigurator)
    }

    protected fun buildCollapsingToolbar(
        layoutMode: CieloCollapsingToolbarLayout.LayoutMode = CieloCollapsingToolbarLayout.LayoutMode.SCROLLABLE,
        title: String,
        floatingTopSectionView: CieloCollapsingToolbarLayout.FloatingTopSectionView? = null,
        footerView: View? = null
    ) = CieloCollapsingToolbarLayout.Configurator(
        layoutMode = layoutMode,
        toolbar = CieloCollapsingToolbarLayout.Toolbar(
            title = title,
            menu = CieloCollapsingToolbarLayout.ToolbarMenu(
                menuRes = R.menu.menu_help,
                onOptionsItemSelected = ::onMenuOptionSelected
            )
        ),
        floatingTopSectionView = floatingTopSectionView,
        footerView = footerView
    )

    private fun onMenuOptionSelected(menuItem: MenuItem) {
        if (menuItem.itemId == R.id.menuActionHelp) {
            requireActivity().openFaq(
                tag = ConfigurationDef.TAG_HELP_CENTER_PIX,
                subCategoryName = getString(R.string.cielo_facilita_central_de_ajuda_pix)
            )
        }
    }

    private fun getInformationString(@StringRes labelRes: Int, value: String) =
        getString(labelRes, value).fromHtml()

    data class Information(
        val bankName: CharSequence,
        val accountType: CharSequence,
        val bankBranchName: CharSequence,
        val bankAccountNumber: CharSequence,
        val bankAccountDigit: CharSequence
    )

}