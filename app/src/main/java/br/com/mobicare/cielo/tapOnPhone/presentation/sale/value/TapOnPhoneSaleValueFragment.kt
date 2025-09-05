package br.com.mobicare.cielo.tapOnPhone.presentation.sale.value

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.field.TextFieldFlui
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.moneyToDoubleValue
import br.com.mobicare.cielo.commons.utils.showSoftKeyboard
import br.com.mobicare.cielo.commons.utils.textToMoneyBigDecimalFormat
import br.com.mobicare.cielo.databinding.FragmentTapOnPhoneSaleValueBinding
import br.com.mobicare.cielo.tapOnPhone.analytics.TapOnPhoneAnalytics
import br.com.mobicare.cielo.tapOnPhone.analytics.TapOnPhoneGA4
import org.koin.android.ext.android.inject
import java.math.BigDecimal

class TapOnPhoneSaleValueFragment : BaseFragment(), CieloNavigationListener {

    private var navigation: CieloNavigation? = null
    private var binding: FragmentTapOnPhoneSaleValueBinding? = null

    private val args: TapOnPhoneSaleValueFragmentArgs by navArgs()
    private val analytics: TapOnPhoneAnalytics by inject()
    private val ga4: TapOnPhoneGA4 by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentTapOnPhoneSaleValueBinding.inflate(
        inflater, container, false
    ).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeStatusBarColor(R.color.white)
        setAppearanceLightStatusBar()
        setupNavigation()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        analytics.logScreenView(
            name = TapOnPhoneAnalytics.TRANSACTIONAL_SALE_VALUE_PATH,
            className = javaClass
        )
        ga4.logScreenView(TapOnPhoneGA4.SCREEN_VIEW_TRANSACTIONAL_SALE_VALUE)
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = (requireActivity() as CieloNavigation).also {
                it.setTextButton(getString(R.string.text_next_label))
                it.showToolbar()
                it.showBackIcon()
                it.showHelpButton()
                it.showCloseButton()
                it.showButton(isShow = true)
                it.enableButton(isEnabled = false)
                it.showContainerButton(isShow = true)
                it.setNavigationListener(this@TapOnPhoneSaleValueFragment)
            }
        }
    }

    private fun setupListeners() {
        binding?.tvFieldSaleValue?.apply {
            setInputTypeTextField(InputType.TYPE_CLASS_NUMBER)
            requestFocus()
            requestFocusFromTouch()
            requireActivity().showSoftKeyboard(this.rootView)
            setValidators(
                TextFieldFlui.Validator(
                    rule = { it.moneyToDoubleValue() > TWENTY_CENTS },
                    errorMessage = getString(R.string.tap_on_phone_sale_value_error_text),
                    onResult = { isValid, _ -> navigation?.enableButton(isValid) }
                )
            )
        }
    }

    override fun onButtonClicked(labelButton: String) {
        analytics.logScreenActions(
            flowName = TapOnPhoneAnalytics.SALE_VALUE,
            labelName = Action.NEXT
        )

        val saleValue =
            binding?.tvFieldSaleValue?.getTextField()?.textToMoneyBigDecimalFormat()
                ?: BigDecimal.ZERO

        ga4.logSaleValueBeginCheckout(saleValue)

        findNavController().navigate(
            TapOnPhoneSaleValueFragmentDirections.actionTapOnPhoneSaleValueFragmentToTapOnPhoneSelectPaymentTypeFragment(
                args.devicetapargs,
                saleValue
            )
        )
    }

    override fun onBackButtonClicked(): Boolean {
        navigation?.goToHome()
        return super.onBackButtonClicked()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        private const val TWENTY_CENTS = .2
    }

}