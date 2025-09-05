package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.dialog

import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentManager
import br.com.cielo.libflue.bottomsheet.CieloContentBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.enum.CieloBankIcons
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.databinding.LayoutPixBankDomicileBinding
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.pixMVVM.domain.model.OnBoardingFulfillment

class PixBankDomicileBottomSheet(
    private val context: Context,
    private val pixAccount: OnBoardingFulfillment.PixAccount
) {

    fun show(fm: FragmentManager, tag: String? = null) {
        CieloContentBottomSheet.create(
            headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                title = context.getString(R.string.pix_extract_bank_domicile_title),
                showCloseButton = true
            ),
            contentLayoutRes = R.layout.layout_pix_bank_domicile,
            onContentViewCreated = ::buildContentView
        ).show(fm, tag)
    }

    private fun buildContentView(view: View, bs: CieloBottomSheet){
        LayoutPixBankDomicileBinding.bind(view).apply {
            getBankIcon(pixAccount.bank).apply {
                ivBankBrand.setImageResource(icon)
                tvBankName.text = pixAccount.bankName ?: bankName
            }

            tvAgencyNumber.text = context.getString(
                R.string.pix_extract_bank_domicile_agency,
                pixAccount.agency
            ).fromHtml()

            tvAccountNumber.text = context.getString(
                R.string.pix_extract_bank_domicile_account,
                pixAccount.accountWithDigit
            ).fromHtml()
        }
    }

    private fun getBankIcon(code: String?) = CieloBankIcons.getBankFromCode(code.orEmpty())

}