package br.com.mobicare.cielo.commons.helpers

import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import br.com.mobicare.cielo.commons.constants.ELEVEN
import br.com.mobicare.cielo.commons.constants.FIFTEEN
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceEditTextView
import br.com.mobicare.cielo.commons.utils.*
import com.google.android.material.textfield.TextInputLayout
import com.redmadrobot.inputmask.helper.Mask
import com.redmadrobot.inputmask.model.CaretString

class EditTextHelper {

    companion object {

        private const val PHONE_MASK = "([00]) [000000000]"
        private const val LANDLINE_PHONE_LENGTH = 14

        fun emailField(til: TextInputLayout, tetv: TypefaceEditTextView) {
            tetv.afterTextChangesNotEmptySubscribe {
                tetv.text?.let { it1 -> callEmailValidate(it1, til, true) }
            }

            tetv.afterTextChangesEmptySubscribe {
                errorFieldCancel(til)
            }
        }

        private fun callEmailValidate(it: Editable?, til: TextInputLayout, isShowError: Boolean) {
            val email = it.toString()
            if (email.isBlank()) {
                errorFieldCancel(til)
                return
            }
            if (ValidationUtils.isEmail(email)) {
                errorFieldCancel(til)
            } else {
                if (isShowError) errorFieldShow(til, "Por favor, digite um e-mail válido.")
                return
            }
        }

        fun dateField(editText: EditText, til: TextInputLayout? = null) {
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(10))
            editText.addTextChangedListener(object : TextWatcher {
                var isUpdate = false
                var dateValue = ""
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    s?.let {
                        if (!isUpdate) {
                            isUpdate = true
                            dateValue = it.toString()

                            val dateMask = dateMaskFormatter(dateValue)
                            val date = dateMask.formattedText.string
                            editText.setText(date)
                            editText.setSelection(date.length)

                            callDateValidation(date, til)

                            isUpdate = false
                        }
                    }
                }
            })
        }

        private fun callDateValidation(date: String, til: TextInputLayout?) {
            if (date.isEmpty()) {
                errorFieldCancel(til)
                return
            }
            if (ValidationUtils.isValidDate(date)) {
                errorFieldCancel(til)
            } else {
                errorFieldShow(til, "Por favor, digite uma data válida.")
            }
        }


        fun cpfField(editTextField: EditText?, til: TextInputLayout? = null) {
            editTextField?.inputType = InputType.TYPE_CLASS_NUMBER
            editTextField?.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(CPF_LENGTH))
            editTextField?.addTextChangedListener(object : TextWatcher {
                var isUpdate = false
                var cpfValue = ""

                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    //if (isAttached()) {
                    s?.let {

                        if (!isUpdate) {
                            isUpdate = true
                            cpfValue = it.toString()

                            val cpfMask = cpfMaskFormatter(cpfValue)
                            val cpf = cpfMask.formattedText.string
                            editTextField.setText(cpf)
                            editTextField.setSelection(cpf.length)

                            til?.let {
                                callCPFValidade(cpf, til)
                            }

                            isUpdate = false
                        }
                    }
                    //}
                }

            })
        }

        fun phoneField(
            til: TextInputLayout? = null,
            editTextField: AppCompatEditText,
            phoneMask: String = PHONE_MASK,
            length: Int = FIFTEEN
        ) {
            editTextField.inputType = InputType.TYPE_CLASS_PHONE
            editTextField.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(length))
            editTextField.addTextChangedListener(object : TextWatcher {
                var isUpdate = false
                var value = ""

                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    s?.let {
                        if (isUpdate.not()) {
                            isUpdate = true
                            value = it.toString().getNumber()
                            val mask =
                                if (phoneMask == PHONE_MASK_FORMAT && value.length < ELEVEN)
                                    phoneMaskFormatter(value, LANDLINE_MASK_FORMAT)
                                else
                                    phoneMaskFormatter(value, phoneMask)
                            val phone = mask.formattedText.string
                            editTextField.setText(phone)
                            editTextField.setSelection(phone.length)

                            callPhoneNumberValidate(phone, til)

                            isUpdate = false
                        }
                    }
                }
            })
        }

        fun phoneMaskFormatter(input: String, mask: String = PHONE_MASK): Mask.Result {
            val phoneMask = Mask(mask)
            return phoneMask.apply(getCaretString(input))
        }

        fun dateMaskFormatter(inputDate: String): Mask.Result {
            val dateMask = Mask("[00]/[00]/[0000]")
            return dateMask.apply(getCaretString(inputDate))
        }

        fun cpfMaskFormatter(inputCpf: String): Mask.Result {
            val cpfMask = Mask(CPF_MASK_FORMAT)
            return cpfMask.apply(getCaretString(inputCpf))
        }

        fun cnpjMaskFormatter(inputCnpj: String): Mask.Result {
            val cnpjMask = Mask(CNPJ_MASK_COMPLETE_FORMAT)
            return cnpjMask.apply(getCaretString(inputCnpj))
        }

        fun zipCodeMaskFormatter(zipCode: String): Mask.Result {
            val zipCodeMask = Mask(ZIP_CODE_MASK_FORMAT)
            return zipCodeMask.apply(getCaretString(zipCode))
        }

        private fun getCaretString(input: String) = CustomCaretString.forward(input)

        private fun callCPFValidade(cpf: String, til: TextInputLayout) {
            if (isValidCpf(cpf)) {
                errorFieldCancel(til)
            } else {
                errorFieldShow(til, "Por favor, digite um número de CPF válido.")
            }
        }

        private fun isValidCpf(cpf: String) =
            when {
                cpf.isNullOrEmpty() -> true
                ValidationUtils.isCPF(cpf) -> true
                else -> false
            }

        private fun callPhoneNumberValidate(number: String, til: TextInputLayout?) {
            if (isValidPhoneNumber(number)) {
                errorFieldCancel(til)
            } else {
                errorFieldShow(til, "Por favor, digite um número de telefone válido.")
            }
        }

        private fun isValidPhoneNumber(number: String) =
            when {
                number.isNullOrEmpty() -> true
                ValidationUtils.isValidPhoneNumber(number) -> true
                else -> false
            }

        private fun errorFieldShow(til: TextInputLayout?, errorString: String) {
            til?.error = errorString
            til?.isErrorEnabled = true
        }

        private fun errorFieldCancel(til: TextInputLayout?) {
            til?.error = null
            til?.isErrorEnabled = false
        }

    }

}