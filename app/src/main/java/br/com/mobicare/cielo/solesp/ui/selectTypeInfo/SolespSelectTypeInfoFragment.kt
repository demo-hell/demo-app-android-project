package br.com.mobicare.cielo.solesp.ui.selectTypeInfo

import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.text.HtmlCompat
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentSolespSelectTypeInfoBinding
import br.com.mobicare.cielo.databinding.ItemOptionRadioBinding
import br.com.mobicare.cielo.solesp.enums.SolespSelectTypeEnum
import br.com.mobicare.cielo.solesp.enums.SolespSelectTypeEnum.ACCOUNTING_INVOICING
import br.com.mobicare.cielo.solesp.enums.SolespSelectTypeEnum.FULL_STATEMENT
import br.com.mobicare.cielo.solesp.model.SolespModel

class SolespSelectTypeInfoFragment : BaseFragment(), CieloNavigationListener {

    private var binding: FragmentSolespSelectTypeInfoBinding? = null
    private var navigation: CieloNavigation? = null
    private var selectTypeInfo: SolespSelectTypeEnum? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentSolespSelectTypeInfoBinding.inflate(
            inflater,
            container,
            false
        ).also { binding = it }.root
    }

    override fun onResume() {
        super.onResume()

        setup()
        setupNavigation()
        setupListeners()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onButtonClicked(labelButton: String) {
        findNavController().navigate(
            SolespSelectTypeInfoFragmentDirections.actionSolespSelectTypeInfoFragmentToSolespSelectPeriodFragment(
                SolespModel(typeSelected = selectTypeInfo)
            )
        )
    }

    private fun setup() {
        binding?.apply {
            txtDescriptionOne.text = SpannableString(
                HtmlCompat.fromHtml(
                    getString(R.string.txt_description_one_solesp_select_type_info),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            )
            txtDescriptionTwo.text = SpannableString(
                HtmlCompat.fromHtml(
                    getString(R.string.txt_description_two_solesp_select_type_info),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            )
            setDataRadioOption(
                radioButtonFullStatement,
                selectTypeInfo == FULL_STATEMENT,
                R.drawable.ic_bank_slip_cloud_400_24dp,
                R.string.txt_option_full_statement_solesp_select_type_info
            )
            setDataRadioOption(
                radioButtonAccountingInvoicing,
                selectTypeInfo == ACCOUNTING_INVOICING,
                R.drawable.ic_charts_chart_up_cloud_400_24dp,
                R.string.txt_option_accounting_invoicing_solesp_select_type_info
            )
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextButton(getString(R.string.continuar))
            navigation?.enableButton(selectTypeInfo != null)
            navigation?.showHelpButton(false)
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupListeners() {
        binding?.apply {
            radioButtonFullStatement.root.setOnClickListener {
                selectTypeInfo = if (selectTypeInfo == FULL_STATEMENT) null else FULL_STATEMENT
                checkRadio()
            }
            radioButtonAccountingInvoicing.root.setOnClickListener {
                selectTypeInfo =
                    if (selectTypeInfo == ACCOUNTING_INVOICING) null else ACCOUNTING_INVOICING
                checkRadio()
            }
        }
    }

    private fun setDataRadioOption(
        item: ItemOptionRadioBinding,
        isCheck: Boolean,
        @DrawableRes imgIcon: Int? = null,
        @StringRes label: Int? = null
    ) {
        item.imgRadioCheckOption.setImageResource(if (isCheck) R.drawable.ic_circle_radio_button_selected else R.drawable.ic_circle_radio_button_unselected)
        item.root.setBackgroundResource(if (isCheck) R.drawable.background_transparent_border_brand_400_rounded else R.drawable.background_transparent_border_cloud_200_rounded)
        label?.let { item.txtLabelOption.text = getString(it) }
        imgIcon?.let { item.imgIconOption.setImageResource(it) }
        val labelText = item.txtLabelOption.text
        item.root.contentDescription = getString(
            if (isCheck) R.string.description_check_checked_solesp_select_period else R.string.description_check_unchecked_solesp_select_period,
            labelText
        )
    }

    private fun checkRadio() {
        binding?.apply {
            setDataRadioOption(
                radioButtonFullStatement,
                selectTypeInfo == FULL_STATEMENT
            )
            setDataRadioOption(
                radioButtonAccountingInvoicing,
                selectTypeInfo == ACCOUNTING_INVOICING
            )
        }
        navigation?.enableButton(selectTypeInfo != null)
    }

}