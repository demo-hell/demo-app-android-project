package br.com.mobicare.cielo.cancelSale.presentation.detail

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import br.com.cielo.libflue.util.moneyUtils.toPtBrRealString
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.cancelSale.domain.model.BalanceInquiry
import br.com.mobicare.cielo.cancelSale.domain.model.CancelSale
import br.com.mobicare.cielo.cancelSale.presentation.utils.UIStateBalanceInquiry
import br.com.mobicare.cielo.cancelSale.presentation.utils.UIStateCancelSale
import br.com.mobicare.cielo.cancelSale.utils.CancelSaleConstants.AUTHORIZATION_CODE
import br.com.mobicare.cielo.cancelSale.utils.CancelSaleConstants.AUTHORIZATION_DATE
import br.com.mobicare.cielo.cancelSale.utils.CancelSaleConstants.CARD_BRAND_CODE
import br.com.mobicare.cielo.cancelSale.utils.CancelSaleConstants.GROSS_AMOUNT
import br.com.mobicare.cielo.cancelSale.utils.CancelSaleConstants.NSU
import br.com.mobicare.cielo.cancelSale.utils.CancelSaleConstants.PAYMENT_TYPE_CODE
import br.com.mobicare.cielo.cancelSale.utils.CancelSaleConstants.SALE_MERCHANT
import br.com.mobicare.cielo.cancelSale.utils.CancelSaleConstants.TRUNCATED_CARD_NUMBER
import br.com.mobicare.cielo.cancelSale.utils.TypeError
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.BUTTON
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ONE_FLOAT
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.constants.ZERO_COMMA_FIVE_FLOAT
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.LocaleUtil
import br.com.mobicare.cielo.commons.utils.afterTextChangesNotEmptySubscribe
import br.com.mobicare.cielo.commons.utils.convertIsoDateToBr
import br.com.mobicare.cielo.commons.utils.currencyToDouble
import br.com.mobicare.cielo.commons.utils.token.presentation.HandlerValidationToken
import br.com.mobicare.cielo.databinding.DetailCancelSaleFragmentBinding
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.minhasVendas.activities.CANCELAR_VENDAS_CATEGORY
import br.com.mobicare.cielo.minhasVendas.activities.CANCELAR_VENDAS_EVENT
import br.com.mobicare.cielo.minhasVendas.activities.SCREENVIEW_CANCELAR_VENDA_DETALHES
import br.com.mobicare.cielo.minhasVendas.activities.SOLICITACAO_DE_CANCELAMENTO_EVENT
import br.com.mobicare.cielo.mySales.analytics.MySalesGA4
import br.com.mobicare.cielo.mySales.analytics.MySalesGA4.Companion.SCREEN_NAME_SALES_DETAILS
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.BigDecimal

class DetailCancelSaleFragment : BaseFragment(), CieloNavigationListener {
    private var binding: DetailCancelSaleFragmentBinding? = null
    private var navigation: CieloNavigation? = null
    private val detailCancelSaleViewModel: DetailCancelSaleViewModel by viewModel()
    private var radio: RadioButton? = null
    private val handlerValidationToken: HandlerValidationToken by inject()
    private val ga4: MySalesGA4 by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DetailCancelSaleFragmentBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        observeBalanceInquiry()
        observeCancelSale()
        binding?.containerCancelSale?.displayedChild = ONE
        getDataByWebPage()
        startViewGA4()
    }

    private fun getDataByWebPage() {
        requireActivity().intent.extras.let {
            detailCancelSaleViewModel.getBalanceInquiry(
                it?.getString(CARD_BRAND_CODE).toString(),
                it?.getString(AUTHORIZATION_CODE).toString(),
                it?.getString(NSU).toString(),
                it?.getString(TRUNCATED_CARD_NUMBER).toString(),
                it?.getString(AUTHORIZATION_DATE).toString(),
                it?.getString(PAYMENT_TYPE_CODE).toString(),
                it?.getString(GROSS_AMOUNT).toString(),
                it?.getString(SALE_MERCHANT).toString()
            )
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
        }
    }

    private fun observeBalanceInquiry() {
        detailCancelSaleViewModel.getBalanceInquiryLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UIStateBalanceInquiry.Loading -> {
                    binding?.progressBar.visible()
                }

                is UIStateBalanceInquiry.Success -> {
                    stateSuccessBalanceInquiry(uiState)
                }

                is UIStateBalanceInquiry.Error -> {
                    stateErrorBalanceInquiry()
                }

                is UIStateBalanceInquiry.ErrorSaleHasBeenCancelled -> {
                    stateErrorSaleHasBeenCancelled()
                }
            }
        }
    }

    private fun stateSuccessBalanceInquiry(uiState: UIStateBalanceInquiry.Success<BalanceInquiry>) {
        binding?.apply {
            progressBar.gone()
            containerCancelSale.visible()
        }
        uiState.data?.let { mountView(it) }
    }

    private fun stateErrorBalanceInquiry() {
        binding?.progressBar.gone()
        showHandlerErrorBalanceInquiry()
        exceptionGA4()
    }

    private fun stateErrorSaleHasBeenCancelled() {
        binding?.progressBar.gone()
        showHandlerSaleHasBeenCancelled()
        exceptionGA4()
    }

    private fun observeCancelSale() {
        detailCancelSaleViewModel.cancelSaleLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UIStateCancelSale.Success -> {
                    stateSuccessCancelSale(uiState)
                }

                is UIStateCancelSale.ErrorEspecify -> {
                    stateEspecificErrorCancelSale(uiState)
                }

                is UIStateCancelSale.ErrorGeneric -> {
                    stateGenericErrorCancelSale()
                }
            }
        }
    }

    private fun stateSuccessCancelSale(uiState: UIStateCancelSale.Success<CancelSale>) {
        hideLoadingToken()
        showHandlerSuccess()
        uiState.data?.toString()?.let { gaSendCallbackCancel(it) }
    }

    private fun stateEspecificErrorCancelSale(uiState: UIStateCancelSale.ErrorEspecify<CancelSale>) {
        hideLoadingToken()
        uiState.data?.let { showHandlerEspecify(it) }
        exceptionGA4()
    }

    private fun stateGenericErrorCancelSale() {
        hideLoadingToken()
        showHandlerErrorCancelSale()
        exceptionGA4()
    }

    private fun mountView(balanceInquiry: BalanceInquiry) {
        binding?.apply {
            evValorDisponivel.setText(balanceInquiry.availableAmount?.toPtBrRealString())
            evValorDisponivel.addTextChangedListener(
                evValorDisponivel.getMaskMoney(
                    evValorDisponivel
                )
            )
            tvDateValue.text = balanceInquiry.saleDate?.convertIsoDateToBr()
            tvValueSale.text = balanceInquiry.grossAmount?.toPtBrRealString()
            Picasso.get().load(balanceInquiry.imgCardBrand).into(imgBrand)
            tvFormPaymentValue.text = balanceInquiry.paymentTypeDescription
            tvDisponibleValue.text = balanceInquiry.availableAmount?.toPtBrRealString()
            setRadioOptions(balanceInquiry)
            setListeners()
        }
    }

    private fun setRadioOptions(balanceInquiry: BalanceInquiry) {
        binding?.apply {
            disableValueDisponible()
            btnConfirmCancel.isEnabled = false
            rgOptionsCancel.setOnCheckedChangeListener { group, checkedId ->
                btnConfirmCancel.isEnabled = true
                btnConfirmCancel.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.btn_cancel_selector
                    )
                radio = checkedId.let { group.findViewById(it) }
                checkStateRadio(balanceInquiry)
                checkValueDisponibleAfterChange(balanceInquiry)
            }
        }
    }

    private fun disableValueDisponible() {
        binding?.apply {
            evValorDisponivel.isEnabled = false
            evValorDisponivel.alpha = ZERO_COMMA_FIVE_FLOAT
        }
    }

    private fun enableValueDisponible() {
        binding?.apply {
            evValorDisponivel.isEnabled = true
            evValorDisponivel.alpha = ONE_FLOAT
        }
    }

    private fun checkStateRadio(balanceInquiry: BalanceInquiry) {
        binding?.apply {
            when {
                radio?.text?.equals(getString(R.string.valor_total)) == true -> {
                    disableValueDisponible()
                    evValorDisponivel.setText(balanceInquiry.availableAmount?.toPtBrRealString())
                    gaSendCheckBox(radio?.text.toString())
                }

                else -> {
                    enableValueDisponible()
                    evValorDisponivel.setText(balanceInquiry.availableAmount?.toPtBrRealString())
                    evValorDisponivel.requestFocus()
                    val imm: InputMethodManager =
                        requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(evValorDisponivel, InputMethodManager.SHOW_IMPLICIT)
                    gaSendCheckBox(radio?.text.toString())
                }
            }
        }
    }

    private fun checkValueDisponibleAfterChange(balanceInquiry: BalanceInquiry) {
        binding?.apply {
            evValorDisponivel.afterTextChangesNotEmptySubscribe {
                if (it.toString().currencyToDouble()
                        .toBigDecimal() > BigDecimal.ZERO && it.toString().trim()
                        .currencyToDouble()
                        .toBigDecimal() <= balanceInquiry.availableAmount?.toBigDecimal()
                ) {
                    btnConfirmCancel.isEnabled = true
                    btnConfirmCancel.background =
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.btn_cancel_selector
                        )
                } else {
                    btnConfirmCancel.isEnabled = false
                    btnConfirmCancel.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.btn_cancel_unselector
                    )
                }
            }
        }
    }

    private fun setListeners() {
        binding?.apply {
            btnConfirmCancel.setOnClickListener {
                gaSendCancelEvent()
                containerCancelSale.displayedChild = ZERO
                verifyToken()
            }
        }
    }

    private fun getValueToCancel(): Double {
        val value =
            binding?.evValorDisponivel?.text.toString().trim().replace("R$", "")
                .replace(" ", "")
        return value.currencyToDouble()
    }

    private fun verifyToken() {
        handlerValidationToken.getToken(
            childFragmentManager,
            object : HandlerValidationToken.CallbackToken {
                override fun onSuccess(token: String) =
                    detailCancelSaleViewModel.cancelSale(
                        getValueToCancel(),
                        LocaleUtil.currentDate(),
                        token
                    )

                override fun onError() = onErrorToken()
            }
        )
    }

    private fun onErrorToken(error: NewErrorMessage? = null) {
        handlerValidationToken.playAnimationError(
            error,
            object : HandlerValidationToken.CallbackAnimationError {
                override fun onTryAgain() {
                    verifyToken()
                }
            }
        )
    }

    private fun hideLoadingToken() {
        handlerValidationToken.hideAnimation(
            isDelay = false,
            callbackStopAnimation = object : HandlerValidationToken.CallbackStopAnimation {}
        )
    }

    private fun showHandlerSuccess() {
        navigation?.showCustomHandlerView(
            contentImage = R.drawable.ic_cancel_sucess,
            title = getString(R.string.tv_cancel_sucess_title),
            message = getString(R.string.message_cancel_sale),
            labelSecondButton = getString(R.string.ok),
            isShowFirstButton = false,
            isShowSecondButton = true,
            callbackSecondButton = { close() },
            callbackBack = { close() },
            callbackClose = { close() }

        )
    }

    private fun showHandlerErrorCancelSale() {
        navigation?.showCustomHandlerView(
            contentImage = R.drawable.ic_generic_error_image,
            title = getString(R.string.text_title_generic_error),
            message = getString(R.string.text_message_generic_error),
            labelSecondButton = getString(R.string.text_try_again_label),
            isShowFirstButton = false,
            isShowSecondButton = true,
            callbackSecondButton = { verifyToken() },
            callbackBack = { close() },
            callbackClose = { close() }
        )
    }

    private fun showHandlerErrorBalanceInquiry() {
        navigation?.showCustomHandlerView(
            contentImage = R.drawable.ic_generic_error_image,
            title = getString(R.string.text_title_generic_error),
            message = getString(R.string.text_message_generic_error),
            labelSecondButton = getString(R.string.ok),
            isShowFirstButton = false,
            isShowSecondButton = true,
            callbackSecondButton = { close() },
            callbackBack = { close() },
            callbackClose = { close() }
        )
    }

    private fun showHandlerSaleHasBeenCancelled() {
        val message = getString(R.string.txt_sale_has_been_cancelled).fromHtml()
        navigation?.showCustomHandlerView(
            contentImage = R.drawable.ic_generic_error_image,
            title = getString(R.string.text_pix_generic_error_title),
            message = getString(R.string.txt_sale_has_been_cancelled),
            labelSecondButton = getString(R.string.ok),
            isShowFirstButton = false,
            isShowSecondButton = true,
            callbackSecondButton = { close() },
            callbackBack = { close() },
            callbackClose = { close() }
        )
    }

    private fun showHandlerEspecify(cancelSale: CancelSale) {
        TypeError.processTypeError(
            requireContext(),
            cancelSale.errorCode.toString(),
            cancelSale.errorMessage.toString()
        )
        navigation?.showCustomHandlerView(
            contentImage = TypeError.imageError,
            title = TypeError.titleError,
            message = TypeError.messageError,
            labelSecondButton = TypeError.buttonError,
            isShowFirstButton = false,
            isShowSecondButton = true,
            callbackSecondButton = { close() },
            callbackBack = { close() },
            callbackClose = { close() }
        )
    }

    private fun close() {
        requireActivity().finish()
    }

    override fun onResume() {
        super.onResume()
        ga4.logScreenView(MySalesGA4.SCREEN_NAME_CANCELLATION_CANCEL)
    }

    private fun startViewGA4() {
        Analytics.trackScreenView(
            screenName = SCREENVIEW_CANCELAR_VENDA_DETALHES,
            screenClass = javaClass
        )
        ga4.beginCancel(SCREEN_NAME_SALES_DETAILS, BUTTON)
    }

    private fun exceptionGA4() {
        ga4.logException(
            MySalesGA4.SCREEN_NAME_SALES_MADE,
            newErrorMessage = detailCancelSaleViewModel.getError()
        )
    }

    private fun gaSendCheckBox(name: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, CANCELAR_VENDAS_CATEGORY),
            action = listOf(CANCELAR_VENDAS_EVENT),
            label = listOf(Label.BOTAO, name)
        )
    }

    private fun gaSendCancelEvent() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, CANCELAR_VENDAS_CATEGORY),
            action = listOf(CANCELAR_VENDAS_EVENT),
            label = listOf(Label.BOTAO, binding?.rgOptionsCancel?.checkedRadioButtonId.toString().toLowerCasePTBR())
        )
    }

    private fun gaSendCallbackCancel(message: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, CANCELAR_VENDAS_CATEGORY),
            action = listOf(SOLICITACAO_DE_CANCELAMENTO_EVENT),
            label = listOf(Label.MENSAGEM, message)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}