package br.com.mobicare.cielo.pixMVVM.presentation.key.enums

import android.view.inputmethod.EditorInfo
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import br.com.cielo.libflue.inputtext.CieloTextInputField
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.EIGHTEEN
import br.com.mobicare.cielo.commons.constants.FIFTEEN
import br.com.mobicare.cielo.commons.constants.THIRTY_SIX
import br.com.mobicare.cielo.commons.constants.THREE_HUNDRED

enum class PixKeyTypeInput(
    @StringRes val toolbarText: Int?,
    @StringRes val hintText: Int?,
    val mask: CieloTextInputField.MaskFormat?,
    val inputType: Int?,
    val maxLength: Int
) {
    CPF_CNPJ(
        R.string.pix_insert_all_keys_toolbar_cpf_or_cnpj,
        R.string.pix_insert_all_keys_hint_input_cpf_or_cnpj,
        CieloTextInputField.MaskFormat.CPF_OR_CNPJ,
        null,
        EIGHTEEN
    ),
    PHONE(
        R.string.pix_insert_all_keys_toolbar_cellphone,
        R.string.pix_insert_all_keys_hint_input_cellphone,
        CieloTextInputField.MaskFormat.PHONE_WITH_DDD,
        EditorInfo.TYPE_CLASS_PHONE,
        FIFTEEN
    ),
    EMAIL(
        R.string.pix_insert_all_keys_toolbar_email,
        R.string.pix_insert_all_keys_hint_input_email,
        null,
        EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
        THREE_HUNDRED
    ),
    EVP(
        R.string.pix_insert_all_keys_toolbar_random,
        R.string.pix_insert_all_keys_hint_input_random,
        CieloTextInputField.MaskFormat.EVP,
        null,
        THIRTY_SIX
    ),
    BANK_ACCOUNT(
        null,
        null,
        null,
        null,
        THREE_HUNDRED
    );

    companion object {

        fun fromOrdinal(id: Int): PixKeyTypeInput? {
            return values().firstOrNull { it.ordinal == id }
        }

    }

}