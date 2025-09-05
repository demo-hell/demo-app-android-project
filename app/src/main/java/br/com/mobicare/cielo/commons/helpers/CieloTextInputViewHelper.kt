package br.com.mobicare.cielo.commons.helpers

import android.text.InputFilter
import br.com.cielo.libflue.inputtext.CieloTextInputView
import br.com.mobicare.cielo.commons.constants.CPF_MASK
import br.com.mobicare.cielo.commons.constants.ONE_SPACE
import br.com.mobicare.cielo.commons.helpers.EditTextHelper.Companion.cpfMaskFormatter
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.extensions.clearCNPJMask
import br.com.mobicare.cielo.extensions.documentWithoutMask
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.redmadrobot.inputmask.helper.Mask
import com.redmadrobot.inputmask.model.CaretString

class CieloTextInputViewHelper {

    companion object {

        const val LENGTH_PHONE = 15

        fun cpfOrEmailInput(
            inputText: CieloTextInputView?,
            hintTextEmpty: String? = null,
            hintTextCpf: String? = null,
            hintTextEmail: String? = null,
            textChangedComplement: (() -> Unit)? = null
        ) {
            inputText?.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
                var isUpdate = false

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    s?.let {
                        if (isUpdate.not()) {
                            isUpdate = true
                            val value = it.toString()

                            if (value.isEmpty()) {
                                hintTextEmpty?.let { hintText ->
                                    inputText.setHint(hintText)
                                }
                            } else {
                                val plainValue = value.documentWithoutMask()
                                if (plainValue.containsOnlyNumbers()) {
                                    hintTextCpf?.let { hintText ->
                                        inputText.setHint(hintText)
                                    }
                                    val cpf = cpfMaskFormatter(plainValue)
                                    if (cpf.extractedValue.length == CPF_WITHOUT_MASK_LENGTH){
                                        val cpfFormatted = cpf.formattedText.string
                                        inputText.setText(cpfFormatted)
                                        inputText.setSelection(cpfFormatted.length)
                                    }else{
                                        inputText.setText(plainValue)
                                        inputText.setSelection(plainValue.length)
                                    }
                                } else {
                                    hintTextEmail?.let { hintText ->
                                        inputText.setHint(hintText)
                                    }
                                }
                            }
                            isUpdate = false
                        }
                    }
                    inputText.setError(EMPTY)
                    textChangedComplement?.invoke()
                }
            })
        }

        fun emailInput(
            inputText: CieloTextInputView?,
            textChangedComplement: (() -> Unit)? = null
        ) {
            inputText?.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    inputText.setError(EMPTY)
                    textChangedComplement?.invoke()
                }
            })
        }

        fun phoneInput(
            inputText: CieloTextInputView?,
            phoneMask: String,
            textChangedComplement: (() -> Unit)? = null
        ) {
            inputText?.setFilters(arrayOf(InputFilter.LengthFilter(LENGTH_PHONE)))
            inputText?.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
                var isUpdate = false
                var value = EMPTY

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    s?.let {
                        if (isUpdate.not()) {
                            isUpdate = true
                            value = it.toString()
                            val mask = phoneMaskFormatter(value, phoneMask)
                            val phone = mask.formattedText.string
                            with(inputText) {
                                setText(phone)
                                setSelection(phone.length)
                                setError(EMPTY)
                            }
                            isUpdate = false
                        }
                    }
                    textChangedComplement?.invoke()

                }
            })
        }

        fun phoneMaskFormatter(input: String, mask: String): Mask.Result =
            Mask(mask).apply(CustomCaretString.forward(input))

        fun String?.isValidPhoneNumber() =
            when {
                this.isNullOrEmpty() -> true
                ValidationUtils.isValidPhoneNumber(this) -> true
                else -> false
            }

    }

}