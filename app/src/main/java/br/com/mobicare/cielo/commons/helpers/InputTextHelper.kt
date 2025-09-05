package br.com.mobicare.cielo.commons.helpers

import android.text.Editable
import android.text.InputFilter
import br.com.cielo.libflue.inputtext.CieloInputText
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.CustomCaretString
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.afterTextChangesEmptySubscribe
import br.com.mobicare.cielo.commons.utils.afterTextChangesNotEmptySubscribe
import br.com.mobicare.cielo.extensions.clearCNPJMask
import com.redmadrobot.inputmask.helper.Mask
import com.redmadrobot.inputmask.model.CaretString

class InputTextHelper {

    companion object {

        const val LENGTH_PHONE = 15

        fun emailInput(
            inputText: CieloInputText?,
            errorMessage: String
        ) {
            inputText?.afterTextChangesNotEmptySubscribe {
                validateEmail(it.toString(), inputText, errorMessage)
            }

            inputText?.afterTextChangesEmptySubscribe {
                showErrorInput(inputText, errorMessage, false)
            }
        }

        fun cPForCNPJInput(
            inputText: CieloInputText?,
            errorMessage: String? = null
        ) {
            inputText?.setMaskCPForCNPJ()
            if (errorMessage != null)
                inputText?.setOnTextChangeListener(object : CieloInputText.TextChangeListener {
                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        s?.let {
                            val document = s.toString()
                            showErrorInput(
                                inputText,
                                errorMessage,
                                document.isValidDocument().not()
                            )
                        }
                    }
                })
        }

        fun cPForCNPJInput(
            inputText: CieloInputText?,
            errorMessage: String? = null,
            callback: () -> Unit
        ) {
            inputText?.setMaskCPForCNPJ()
            if (errorMessage != null)
                inputText?.setOnTextChangeListener(object : CieloInputText.TextChangeListener {
                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        s?.let {
                            val document = s.toString()
                            showErrorInput(
                                inputText,
                                errorMessage,
                                document.isValidDocument().not()
                            )
                        }
                        callback()
                    }
                })
        }

        fun phoneInput(
            inputText: CieloInputText?,
            errorMessage: String? = null,
            phoneMask: String,
            afterTextChange: (s: Editable?) -> Unit = {}
        ) {
            inputText?.setFilters(arrayOf(InputFilter.LengthFilter(LENGTH_PHONE)))
            inputText?.setOnTextChangeListener(object : CieloInputText.TextChangeListener {
                var isUpdate = false
                var value = ""

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    s?.let {
                        if (isUpdate.not()) {
                            isUpdate = true
                            value = it.toString()
                            val mask = phoneMaskFormatter(value, phoneMask)
                            val phone = mask.formattedText.string
                            inputText.setText(phone)
                            inputText.setSelection(phone.length)

                            if (errorMessage != null)
                                showErrorInput(
                                    inputText,
                                    errorMessage,
                                    phone.isValidPhoneNumber().not()
                                )
                            isUpdate = false
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    afterTextChange.invoke(s)
                }
            })
        }

        fun phoneMaskFormatter(input: String, mask: String): Mask.Result =
            Mask(mask).apply(CustomCaretString.forward(input))

        fun String?.isValidDocument(): Boolean {
            val document = this.clearCNPJMask()
            return when {
                document.isNullOrEmpty() -> true
                ValidationUtils.isCPF(document) -> true
                ValidationUtils.isCNPJ(document) -> true
                else -> false
            }
        }

        fun String?.isValidPhoneNumber() =
            when {
                this.isNullOrEmpty() -> true
                ValidationUtils.isValidPhoneNumber(this) -> true
                else -> false
            }

        private fun validateEmail(
            email: String,
            inputText: CieloInputText?,
            errorMessage: String
        ) {
            if (email.isBlank()) {
                showErrorInput(inputText, errorMessage, false)
                return
            }
            if (ValidationUtils.isEmail(email)) {
                showErrorInput(inputText, errorMessage, false)
            } else {
                showErrorInput(inputText, errorMessage)
                return
            }
        }

        private fun showErrorInput(
            inputText: CieloInputText?,
            errorMessage: String,
            isShow: Boolean = true
        ) {
            inputText?.setError(errorMessage)
            inputText?.setErrorImage(R.drawable.ic_alert_red)
            inputText?.showErrorWithIcon(isShow)
        }
    }
}