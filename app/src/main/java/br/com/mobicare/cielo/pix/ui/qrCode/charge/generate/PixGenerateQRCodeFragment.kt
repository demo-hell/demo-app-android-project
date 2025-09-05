package br.com.mobicare.cielo.pix.ui.qrCode.charge.generate

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.DOUBLE
import br.com.mobicare.cielo.commons.constants.MAXIMUM_360_DAYS
import br.com.mobicare.cielo.commons.constants.Text.OTP
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.databinding.FragmentPixGenerateQrCodeBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.constants.PIX_SELECTED_KEY_ARGS
import br.com.mobicare.cielo.pix.domain.MyKey
import br.com.mobicare.cielo.pix.domain.QRCodeChargeResponse
import br.com.mobicare.cielo.pix.ui.qrCode.identifier.PixIdentifierBottomSheet
import br.com.mobicare.cielo.pix.ui.qrCode.identifier.PixIdentifierContract
import br.com.mobicare.cielo.pix.ui.qrCode.receivable.PixAmountReceivableBottomSheet
import br.com.mobicare.cielo.pix.ui.qrCode.receivable.PixAmountReceivableContract
import br.com.mobicare.cielo.pix.ui.transfer.message.PixEnterTransferMessageBottomSheet
import br.com.mobicare.cielo.pix.ui.transfer.message.PixEnterTransferMessageContract
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixKeysResponse
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.net.HttpURLConnection

class PixGenerateQRCodeFragment : BaseFragment(), CieloNavigationListener,
    PixGenerateQRCodeContract.View {

    private val presenter: PixGenerateQRCodePresenter by inject {
        parametersOf(this)
    }

    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }

    private var navigation: CieloNavigation? = null

    private var isOtpInitialized = false
    private var myKey: PixKeysResponse.KeyItem? = null

    private var _binding: FragmentPixGenerateQrCodeBinding? = null
    private val binding get() = _binding

    private var wasAmountBottomSheetShown: Boolean = false
    private var isOnDestroy = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPixGenerateQrCodeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setAmount()
        setupView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        saveData()
        _binding = null
        isOnDestroy = true
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
        if (isOnDestroy) {
            presenter.onGetData()
            isOnDestroy = false
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.screen_text_toolbar_generate_qr_code))
            navigation?.setTextButton(getString(R.string.screen_text_btn_generate_qr_code))
            navigation?.showContainerButton(isShow = true)
            navigation?.showButton(isShow = true)
            navigation?.showFirstButton()
            navigation?.showHelpButton()
            navigation?.setNavigationListener(this)
        }
        observeNavigationCallBack()
    }

    private fun observeNavigationCallBack() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<PixKeysResponse.KeyItem>(
            PIX_SELECTED_KEY_ARGS
        )
            ?.observe(viewLifecycleOwner) { myKeyArgs ->
                myKey = myKeyArgs
                myKey?.let {
                    setKeyLabel(it.key?.let { keyValue ->
                        getFormattedKey(keyValue, it.keyType)
                    })
                }
            }
    }

    private fun saveData() {
        presenter.onSaveData(
            amount = getAmount(),
            message = getMessage(),
            expirationDate = getDate(),
            identifier = getIdentifier()
        )
    }

    private fun getMyKeys() = navigation?.getData() as? List<PixKeysResponse.KeyItem>

    private fun setupView() {
        setDefaultKey(getMyKeys())
        setMessage()
        setIdentifier()
        listenerSetAmount()
        listenerChangeKey()
        setDateExpiration()
    }

    private fun setDefaultKey(myKeys: List<PixKeysResponse.KeyItem>?) {
        presenter.onValidateKey(myKeys)?.let {
            setKeyLabel(it)
        }
        myKey = myKeys?.let { presenter.getFirstActiveKey(it) }
    }

    private fun setKeyLabel(key: String?) {
        binding?.tvYourKeyValue?.text = key
    }

    private fun setAmount() {
        if (wasAmountBottomSheetShown) {
            wasAmountBottomSheetShown = false
            return
        }
        PixAmountReceivableBottomSheet.onCreate(
            object : PixAmountReceivableContract {
                override fun onAmount(amount: Double) {
                    setAmountValue(amount)
                }
            },
            binding?.tvValue?.text?.toString()?.moneyToDoubleValue() ?: ZERO_DOUBLE
        ).show(childFragmentManager, tag)
    }

    private fun listenerSetAmount() {
        binding?.tvValue?.setOnClickListener {
            setAmount()
        }
    }

    private fun listenerChangeKey() {
        getMyKeys()?.let {
            binding?.tvYourKeyValue?.setOnClickListener {
                wasAmountBottomSheetShown = true
                findNavController().navigate(
                    PixGenerateQRCodeFragmentDirections.actionPixGenerateQRCodeFragmentToPixQRCodeChangeKeyFragment(
                        myKey as MyKey
                    )
                )
            }
        }
    }

    private fun setMessage() {
        binding?.tvMessage?.setOnClickListener {
            PixEnterTransferMessageBottomSheet.onCreate(
                object : PixEnterTransferMessageContract {
                    override fun onMessage(message: String) {
                        binding?.tvMessage?.text =
                            message.ifEmpty { getString(R.string.text_pix_summary_transfer_insert_msg_hint) }
                    }
                },
                binding?.tvMessage?.text?.toString() ?: EMPTY
            ).show(childFragmentManager, tag)
        }
    }

    private fun getMessage(): String? = if (binding?.tvMessage?.text?.toString() ==
        getString(R.string.text_pix_summary_transfer_insert_msg_hint)
    ) null else binding?.tvMessage?.text?.toString()

    private fun setIdentifier() {
        binding?.tvIdentifier?.setOnClickListener {
            PixIdentifierBottomSheet.onCreate(
                object : PixIdentifierContract {
                    override fun onIdentifier(identifier: String) {
                        val mIdentifier = identifier.trim().replace("[^a-zA-Z0-9]".toRegex(), "")
                        binding?.tvIdentifier?.text =
                            mIdentifier.ifEmpty { getString(R.string.screen_text_generate_qr_code_billing_data_identifier) }
                    }
                },
                binding?.tvIdentifier?.text?.toString() ?: EMPTY
            ).show(childFragmentManager, tag)
        }
    }

    private fun getIdentifier(): String? = if (binding?.tvIdentifier?.text?.toString() ==
        getString(R.string.screen_text_generate_qr_code_billing_data_identifier)
    ) null else binding?.tvIdentifier?.text?.toString()

    private fun setDateExpiration() {
        binding?.tvDateExpiration?.setOnClickListener {
            CalendarDialogCustom(
                ZERO,
                MAXIMUM_360_DAYS,
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                getString(R.string.screen_text_generate_qr_code_billing_data_title_calendar_expiration),
                context as Context,
                { _, year, monthOfYear, dayOfMonth ->
                    val dataFilter = DataCustom(year, monthOfYear, dayOfMonth)
                    binding?.tvDateExpiration?.text = dataFilter.formatBrDateNowOrFuture()
                    binding?.tvInfoAboutDateExpiration?.gone()

                },
                R.style.DialogThemeMeusRecebimentos
            ).show()
        }
    }

    private fun getDate(): String? = if (binding?.tvDateExpiration?.text?.toString() ==
        getString(R.string.screen_text_generate_qr_code_billing_data_date_expiration)
    ) null else binding?.tvDateExpiration?.text?.toString()

    private fun setAmountValue(amount: Double) {
        binding?.tvValue?.text = amount.toPtBrRealString()
        binding?.tvInformativeAmount?.visible(amount == ZERO_DOUBLE)
    }

    private fun getAmount(): Double? = binding?.tvValue?.text?.toString()?.moneyToDoubleValue()

    private fun generateQRCode(isAnimation: Boolean = true) {
        val amount = if (getAmount() == DOUBLE) null else getAmount()

        validationTokenWrapper.generateOtp(showAnimation = isAnimation) { otpCode ->
            presenter.onGenerateQRCode(
                amount = amount,
                message = getMessage(),
                expirationDate = getDate()?.dateInternationalFormat(),
                identifier = getIdentifier(),
                otp = otpCode
            )
        }
    }

    override fun onShowData(
        amount: Double?,
        message: String?,
        expirationDate: String?,
        identifier: String?
    ) {
        amount?.let { setAmountValue(it) }
        message?.let { binding?.tvMessage?.text = it }
        identifier?.let { binding?.tvIdentifier?.text = it }
        expirationDate?.let {
            binding?.tvDateExpiration?.text = it
            binding?.tvInfoAboutDateExpiration?.gone()
        }
    }

    override fun onButtonClicked(labelButton: String) {
        generateQRCode()
    }

    override fun onSuccessGenerateQRCode(response: QRCodeChargeResponse) {
        validationTokenWrapper.playAnimationSuccess(
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenSuccess() {
                    findNavController().navigate(
                        PixGenerateQRCodeFragmentDirections.actionPixGenerateQrCodeToPixShowQrCodeDetails(
                            response
                        )
                    )
                }
            })
    }

    override fun onErrorGenerateQRCode(onGenericError: () -> Unit) {
        validationTokenWrapper.playAnimationError(callbackValidateToken =
        object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
            override fun callbackTokenError() {
                onGenericError.invoke()
            }
        })
    }

    override fun showError(error: ErrorMessage?) {
        if (error?.code != HttpURLConnection.HTTP_FORBIDDEN.toString() || error.errorCode.contains(
                OTP
            )
        )
            navigation?.showErrorBottomSheet(
                textButton = getString(R.string.text_try_again_label),
                error = processErrorMessage(
                    error,
                    getString(R.string.business_error),
                    getString(R.string.text_pix_generate_qr_code_error_message)
                ),
                title = getString(R.string.text_pix_generate_qr_code_error_title),
                isFullScreen = false
            )
    }

    override fun onClickSecondButtonError() {
        generateQRCode()
    }
}