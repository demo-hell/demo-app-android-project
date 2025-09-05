package br.com.mobicare.cielo.pix.enums

import androidx.annotation.StringRes
import br.com.mobicare.cielo.R

enum class PixExtractInformativeEnum(
    @StringRes val titleInformative: Int,
    @StringRes val channelUsedLabel: Int,
    @StringRes val merchantLabel: Int
) {
    FAILED(
        R.string.text_pix_transfer_title_informative_sending,
        R.string.text_pix_transfer_channel_used_sending,
        R.string.text_pix_transfer_merchant_sending
    ),
    PROCESSING(
        R.string.text_pix_transfer_title_informative_sending,
        R.string.text_pix_transfer_channel_used_sending,
        R.string.text_pix_transfer_merchant_sending
    ),
    RECEIVING(
        R.string.text_pix_transfer_title_informative_receiving,
        R.string.text_pix_transfer_channel_used_receiving,
        R.string.text_pix_transfer_merchant_receiving
    ),
    SENDING(
        R.string.text_pix_transfer_title_informative_sending,
        R.string.text_pix_transfer_channel_used_sending,
        R.string.text_pix_transfer_merchant_sending
    )
}
