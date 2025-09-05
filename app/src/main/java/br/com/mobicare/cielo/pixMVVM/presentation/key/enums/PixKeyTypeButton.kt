package br.com.mobicare.cielo.pixMVVM.presentation.key.enums

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import br.com.mobicare.cielo.R

enum class PixKeyTypeButton(
    @StringRes val label: Int,
    @DrawableRes val iconId: Int,
) {
    CPF_CNPJ(
        R.string.pix_insert_all_keys_cpf_or_cnpj,
        R.drawable.ic_documents_cnh_accent_500_24_dp,
    ),
    PHONE(
        R.string.pix_insert_all_keys_cellphone,
        R.drawable.ic_devices_smartphone_accent_500_24_dp,
    ),
    EMAIL(
        R.string.pix_insert_all_keys_email,
        R.drawable.ic_message_and_communication_mail_accent_500_24_dp,
    ),
    EVP(
        R.string.pix_insert_all_keys_hint_input_random,
        R.drawable.ic_security_key_accent_500_24_dp,
    ),
    BANK_ACCOUNT(
        R.string.pix_insert_all_keys_agency_and_account,
        R.drawable.ic_places_bank_accent_500_24_dp,
    )
}