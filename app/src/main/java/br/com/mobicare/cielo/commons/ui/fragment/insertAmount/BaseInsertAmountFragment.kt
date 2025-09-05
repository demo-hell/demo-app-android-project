package br.com.mobicare.cielo.commons.ui.fragment.insertAmount

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.cielo.libflue.field.TextFieldFlui
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.databinding.FragmentBaseInsertAmountBinding
import br.com.mobicare.cielo.pix.constants.EMPTY
import java.math.BigDecimal

abstract class BaseInsertAmountFragment : BaseFragment(), CieloNavigationListener {

    private var binding: FragmentBaseInsertAmountBinding? = null
    var navigation: CieloNavigation? = null

    abstract fun getTitle(): String

    abstract fun getTextButton(): String

    abstract fun getOnButtonClicked(): () -> Unit

    abstract fun observe(): () -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentBaseInsertAmountBinding.inflate(
        inflater,
        container,
        false
    ).also { binding = it }.root

    override fun onResume() {
        super.onResume()

        setupNavigation()
        setupListeners()
        observe().invoke()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onButtonClicked(labelButton: String) {
        navigation?.hideKeyboard()
        getOnButtonClicked().invoke()
    }

    protected fun getAmount(): BigDecimal {
        return binding?.itAmount?.getTextField()?.textToMoneyBigDecimalFormat()
            ?: BigDecimal.ZERO
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.apply {
                setNavigationListener(this@BaseInsertAmountFragment)
                showButton(true)
                enableButton(false)
                setTextButton(getTextButton())
                configureCollapsingToolbar(
                    CollapsingToolbarBaseActivity.Configurator(
                        isExpanded = true,
                        toolbarTitle = getTitle()
                    )
                )
            }
        }
    }

    private fun setupListeners() {
        binding?.itAmount?.apply {
            setInputTypeTextField(InputType.TYPE_CLASS_NUMBER)
            forceKeyboardOpening()
            setOnTextChangeListener(object :
                TextFieldFlui.TextChangeListener {
                override fun afterTextChanged(s: Editable?) {
                    super.afterTextChanged(s)
                    setInputError(s)
                }
            })
        }
    }

    private fun setInputError(value: Editable?) {
        binding?.itAmount?.apply {
            value.toString().moneyToDoubleValue().also { itValue ->
                val isAnError = itValue <= ZERO_DOUBLE

                navigation?.enableButton(isAnError.not())
                isShowErrorIcon = isAnError
                isShowError = isAnError
                iconError = R.drawable.ic_symbol_alert_round_danger_400_14_dp

                errorMessage = if (isAnError)
                    getString(R.string.txt_error_base_insert_amount_fragment)
                else EMPTY

                errorMessageContentDescription = if (isAnError)
                    getString(R.string.content_description_error_base_insert_amount_fragment)
                else EMPTY
            }
        }
    }

}