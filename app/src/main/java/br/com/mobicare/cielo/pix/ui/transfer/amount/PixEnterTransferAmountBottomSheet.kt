package br.com.mobicare.cielo.pix.ui.transfer.amount

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.databinding.PixEnterTransferAmountBottomSheetBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class PixEnterTransferAmountBottomSheet : BottomSheetDialogFragment(),
    PixEnterTransferAmountContract.View {

    private var binding: PixEnterTransferAmountBottomSheetBinding? = null
    private val presenter: PixEnterTransferAmountPresenter by inject {
        parametersOf(this)
    }
    private var listener: PixEnterTransferAmountContract.Result? = null
    private var clickedSave = false

    companion object {
        fun onCreate(
            listener: PixEnterTransferAmountContract.Result,
            balance: String,
            amount: Double,
            title: String? = null,
            message: String? = null,
            textButton: String? = null,
            reversalAvailable: Double? = ZERO_DOUBLE,
            isDismiss: Boolean = true
        ) = PixEnterTransferAmountBottomSheet().apply {
            this.listener = listener
            this.arguments = Bundle().apply {
                this.putDouble(PIX_AMOUNT_ARGS, amount)
                this.putString(PIX_BALANCE_ARGS, balance)
                this.putString(PIX_TITLE_AMOUNT_BS_ARGS, title)
                this.putBoolean(PIX_IS_DISMISS_BS_ARGS, isDismiss)
                this.putString(PIX_MESSAGE_AMOUNT_BS_ARGS, message)
                this.putString(PIX_TEXT_BUTTON_AMOUNT_BS_ARGS, textButton)
                this.putDouble(PIX_REVERSAL_AVAILABLE_BS_ARGS, reversalAvailable ?: ZERO_DOUBLE)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupBottomSheet(dialog = dialog,
            action = { dismiss() }
        )
        return PixEnterTransferAmountBottomSheetBinding.inflate(
            inflater,
            container,
            false
        ).also {
            binding = it
        }.root
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogResize
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
    }

    private fun setupView() {
        binding?.apply {
            saveAmount()
            onBalanceView(presenter.isShowBalanceValue())
            getTitle()?.let {
                titleEnterAmountPix.text = it
            }
            getMessage()?.let {
                messageEnterAmountPix.text = it
                messageEnterAmountPix.visible()
            }
            getTextButton()?.let {
                btnSaveAmountPix.setText(it)
            }
            inputAmountPix.setMaskMoney()
            requireActivity().showKeyboard(inputAmountPix)
            setupAmountVisibility()
            onReversalAvailableView()
        }

    }

    private fun getTitle(): String? = (arguments
        ?.getString(PIX_TITLE_AMOUNT_BS_ARGS))

    private fun getMessage(): String? = (arguments
        ?.getString(PIX_MESSAGE_AMOUNT_BS_ARGS))

    private fun getTextButton(): String? = (arguments
        ?.getString(PIX_TEXT_BUTTON_AMOUNT_BS_ARGS))

    private fun balance(): String? = (arguments
        ?.getString(PIX_BALANCE_ARGS))

    private fun amount(): Double? = (arguments
        ?.getDouble(PIX_AMOUNT_ARGS))

    private fun reversalAvailable(): Double? = (arguments
        ?.getDouble(PIX_REVERSAL_AVAILABLE_BS_ARGS))

    private fun isDismiss(): Boolean = (arguments
        ?.getBoolean(PIX_IS_DISMISS_BS_ARGS, false)) ?: false

    private fun hasReversalAvailable(): Boolean {
        reversalAvailable()?.let {
            return it > ZERO_DOUBLE
        }
        return false
    }

    private fun saveAmount() {
        binding?.btnSaveAmountPix?.setOnClickListener {
            validationEnteredAmount()
        }
    }

    private fun validationEnteredAmount() {
        binding?.apply {
            val value = inputAmountPix.getText()
            val amount = if (value.containsNumbers()) value.moneyToDoubleValue() else ZERO_DOUBLE
            if (amount > ZERO_DOUBLE) {
                inputAmountPix.showErrorWithIcon(false)
                listener?.onAmount(amount)
                clickedSave = true
                dismiss()
            } else {
                inputAmountPix.setError(getString(R.string.text_pix_enter_amount_message_error))
                inputAmountPix.setErrorImage(R.drawable.ic_alert_red)
                inputAmountPix.showErrorWithIcon(true)
            }
        }

    }

    private fun setupAmountVisibility() {
        binding?.apply {
            if (balance() == DEFAULT_BALANCE) {
                if (hasReversalAvailable()) {
                    titleBalance.gone()
                    containerBalanceTransferPix.gone()
                } else {
                    containerBalancePix.gone()
                }
            } else {
                balance()?.let {
                    tvPixBalanceValue.text = getString(R.string.text_pix_enter_amount_value, it)
                }
            }
            amount()?.let {
                if (it > ZERO_DOUBLE) {
                    inputAmountPix.setText(it.toPtBrRealString(isPrefix = false))
                    val amountLength = inputAmountPix.getText().length
                    inputAmountPix.setSelection(amountLength)
                }
            }

            containerBalanceTransferPix.setOnClickListener {
                presenter.onSaveShowBalanceValue()
            }
        }
    }

    override fun onBalanceView(isShow: Boolean) {
        binding?.apply {
            if (isShow) {
                ivPixBalanceShow.setBackgroundResource(R.drawable.ic_eye)
                tvPixBalanceValue.visible()
                ivPixBalanceValue.gone()
            } else {
                ivPixBalanceShow.setBackgroundResource(R.drawable.ic_eye_off)
                tvPixBalanceValue.gone()
                ivPixBalanceValue.visible()
            }
        }
    }

    private fun onReversalAvailableView() {
        binding?.apply {
            if (hasReversalAvailable()) {
                titleReversal.visible()
                tvPixReversalValue.visible()
                if (balance() == DEFAULT_BALANCE) {
                    reversalSeparator.gone()
                } else {
                    reversalSeparator.visible()
                }
                titleEnterAmountPix.text = getString(R.string.text_pix_enter_amount_title_reversal)
                tvPixReversalValue.text = reversalAvailable()?.toPtBrRealString()
            } else {
                titleReversal.gone()
                tvPixReversalValue.gone()
                reversalSeparator.gone()
            }
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        try {
            if (isDismiss() && clickedSave) {
                val value = binding?.inputAmountPix?.getText()
                value?.let {
                    if (it.containsNumbers()) listener?.onAmount(it.moneyToDoubleValue())
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().log(getString(R.string.txt_pix_error_crashlytics_on_dismiss_bs, e.message?: EMPTY))
        } finally {
            super.onDismiss(dialog)
        }
    }

    override fun onDestroyView() {
        try {
            binding = null
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().log(getString(R.string.txt_pix_error_crashlytics_on_destroy_view_bs, e.message?: EMPTY))
        } finally {
            super.onDestroyView()
        }
    }
}