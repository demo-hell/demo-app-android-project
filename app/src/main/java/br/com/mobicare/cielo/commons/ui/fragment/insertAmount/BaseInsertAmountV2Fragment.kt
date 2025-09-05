package br.com.mobicare.cielo.commons.ui.fragment.insertAmount

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.updatePadding
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.field.TextFieldFlui
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.moneyToDoubleValue
import br.com.mobicare.cielo.commons.utils.textToMoneyBigDecimalFormat
import br.com.mobicare.cielo.commons.utils.toPtBrRealStringWithoutSymbol
import br.com.mobicare.cielo.databinding.FragmentBaseInsertAmountV2Binding
import br.com.mobicare.cielo.databinding.FragmentBaseInsertAmountV2FooterBinding
import br.com.mobicare.cielo.extensions.fromHtml
import java.math.BigDecimal

abstract class BaseInsertAmountV2Fragment : BaseFragment() {

    private var binding: FragmentBaseInsertAmountV2Binding? = null
    private var bindingFooter: FragmentBaseInsertAmountV2FooterBinding? = null

    protected var navigation: CieloNavigation? = null

    abstract val title: String
    abstract val actionButton: ActionButton

    open val headerView: View? = null
    open val footerText: String? = null
    open val toolbarMenu: CieloCollapsingToolbarLayout.ToolbarMenu? = null

    open val collapsingToolbarConfigurator get() = CieloCollapsingToolbarLayout.Configurator(
        layoutMode = CieloCollapsingToolbarLayout.LayoutMode.SCROLLABLE,
        isExpanded = false,
        disableExpandableMode = true,
        toolbar = CieloCollapsingToolbarLayout.Toolbar(menu = toolbarMenu),
        footerView = bindingFooter?.root
    )

    open val validators get() = listOf(
        TextFieldFlui.Validator(
            rule = { it.moneyToDoubleValue() > ZERO_DOUBLE },
            errorMessage = getString(R.string.txt_error_base_insert_amount_fragment)
        )
    )

    protected val amount: BigDecimal
        get() = binding?.tfAmount?.getTextField()?.textToMoneyBigDecimalFormat() ?: BigDecimal.ZERO

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): LinearLayout {
        bindingFooter = FragmentBaseInsertAmountV2FooterBinding.inflate(inflater, container, false)

        return FragmentBaseInsertAmountV2Binding.inflate(
            inflater,
            container,
            false
        ).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeNavigation()
        setupHeaderView()
        setupTextFieldView()
        setupFooterTextView()
        setupActionButtonView()
    }

    override fun onResume() {
        super.onResume()
        configureCollapsingToolbar()
    }

    override fun onDestroyView() {
        bindingFooter = null
        binding = null
        super.onDestroyView()
    }

    protected fun setAmount(amount: Double) {
        binding?.tfAmount?.text = amount.toPtBrRealStringWithoutSymbol()
    }

    private fun initializeNavigation() {
        navigation = requireActivity() as? CieloNavigation
    }

    private fun configureCollapsingToolbar() {
        navigation?.configureCollapsingToolbar(collapsingToolbarConfigurator)
    }

    private fun setupHeaderView() {
        headerView?.let {
            binding?.containerHeader?.apply {
                addView(it)
                visible()
            }
        }
    }

    private fun setupTextFieldView() {
        binding?.tfAmount?.apply {
            setInputTypeTextField(InputType.TYPE_CLASS_NUMBER)
            forceKeyboardOpening()
            setValidators(*validators.toTypedArray())
            views.containerField.updatePadding(
                top = resources.getDimensionPixelOffset(R.dimen.dimen_0dp),
                bottom = resources.getDimensionPixelOffset(R.dimen.dimen_0dp),
            )
            hintTopText = title
            iconError = R.drawable.ic_symbol_warning_red_500_16_dp
            errorTextStyle = R.style.semi_bold_montserrat_12_red_500
        }
    }

    private fun setupFooterTextView() {
        footerText?.let {
            bindingFooter?.tvFooter?.apply {
                text = it.fromHtml()
                visible()
            }
        }
    }

    private fun setupActionButtonView() {
        bindingFooter?.btnAction?.apply {
            setText(actionButton.text)
            setTextAppearance(R.style.semi_bold_montserrat_16)
            setOnClickListener { actionButton.onTap(amount) }
        }
    }

    data class ActionButton(
        val text: String,
        val onTap: (BigDecimal) -> Unit
    )

}