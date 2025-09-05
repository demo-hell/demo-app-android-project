package br.com.mobicare.cielo.cancelSale.utils

import android.content.Context
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.recebaMais.presentation.presenter.UserLoanPresenter

object TypeError {
    var titleError = EMPTY
        private set
    var messageError = EMPTY
        private set
    var buttonError = EMPTY
        private set
    var imageError = ZERO
        private set

    fun processTypeError(context: Context, code: String, errorMessage: String) {
        val codesErrorFirst = context.resources.getStringArray(R.array.codes_error_first)
        val codesErrorSecond = context.resources.getStringArray(R.array.codes_error_second)

        titleError = context.resources.getString(R.string.text_title_generic_error)
        messageError = context.resources.getString(R.string.text_message_generic_error)
        buttonError = context.resources.getString(R.string.ok)
        imageError = R.drawable.ic_generic_error_image

        when (code) {
            UserLoanPresenter.CREDIT_NOT_ALLOWED -> {
                titleError =
                    context.resources.getString(R.string.text_title_do_not_contract_possible)
                messageError = errorMessage
            }

            UserLoanPresenter.CONTRACT_NOT_EFFECTED -> {
                titleError =
                    context.resources.getString(R.string.text_title_do_not_contract_possible)
                messageError = errorMessage
                buttonError = context.resources.getString(R.string.rm_cc_credit_contract_button)
            }

            UserLoanPresenter.CONTRACT_IN_PROGRESS -> {
                titleError = context.resources.getString(R.string.text_title_contract_in_progress)
                messageError =
                    context.resources.getString(R.string.text_message_contract_in_progress)
                imageError = R.drawable.ic_02
            }

            UserLoanPresenter.EMPTY -> {
                titleError = context.resources.getString(R.string.text_title_generic_error)
                messageError = context.resources.getString(R.string.text_message_generic_error)
                buttonError = context.resources.getString(R.string.text_button_try_again)
                imageError = R.drawable.ic_generic_error_image
            }

            NetworkConstants.HTTP_STATUS_404.toString() -> {
                titleError = context.resources.getString(R.string.text_title_generic_error)
                messageError = context.resources.getString(R.string.business_error)
                buttonError = context.resources.getString(R.string.ok)
                imageError = R.drawable.ic_generic_error_image
            }

            Text.INVALID_OTP_CODE -> {
                titleError = context.resources.getString(R.string.text_title_generic_error_token)
                messageError =
                    context.resources.getString(R.string.text_subtitle_generic_error_token)
                buttonError = context.resources.getString(R.string.text_button_try_again)
                imageError = R.drawable.ic_token_invalido
            }

            Text.OTP_NOT_REGISTERED, Text.OTP_TEMPORARILY_BLOCKED, Text.OTP_REQUIRED -> {
                titleError = context.resources.getString(R.string.text_title_generic_error_token)
                messageError =
                    context.resources.getString(R.string.text_subtitle_generic_error_token)
                buttonError = context.resources.getString(R.string.text_button_try_again)
                imageError = R.drawable.ic_token_invalido
            }

            in codesErrorFirst -> messageError =
                context.resources.getString(R.string.text_message_generic_error)

            in codesErrorSecond -> {
                messageError =
                    context.resources.getString(R.string.text_message_call_cancellation_center_error)
            }

            else -> messageError = errorMessage

        }
    }
}