package br.com.mobicare.cielo.pixMVVM.presentation.home.factories

import android.content.Context
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.pixMVVM.presentation.home.models.PixTransactionButton
import br.com.mobicare.cielo.pixMVVM.presentation.home.models.PixTransactionButtonId

object PixTransactionsMenuFactory {

    fun create(context: Context) = listOf(
        PixTransactionButton(
            id = PixTransactionButtonId.TRANSFER,
            title = context.getString(R.string.pix_home_transaction_transfer),
            image = R.drawable.ic_money_note_24dp,
            contentDescription = context.getString(R.string.pix_home_content_description_transaction_transfer_button),
        ),
        PixTransactionButton(
            id = PixTransactionButtonId.READ_QR_CODE,
            title = context.getString(R.string.pix_home_transaction_read_qr_code),
            image = R.drawable.ic_payments_qr_code_24_dp,
            contentDescription = context.getString(R.string.pix_home_content_description_transaction_read_qr_code_button)
        ),
        PixTransactionButton(
            id = PixTransactionButtonId.GENERATE_CHARGE,
            title = context.getString(R.string.pix_home_transaction_generate_charge),
            image = R.drawable.ic_cielo_system_charge_money_24dp,
            contentDescription = context.getString(R.string.pix_home_content_description_transaction_generate_charge_button)
        ),
        PixTransactionButton(
            id = PixTransactionButtonId.COPY_AND_PASTE,
            title = context.getString(R.string.pix_home_transaction_copy_and_paste),
            image = R.drawable.ic_actions_copy_24dp,
            contentDescription = context.getString(R.string.pix_home_content_description_transaction_copy_and_paste_button)
        )
    )

}