package br.com.mobicare.cielo.chargeback.utils

import android.content.Context
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackDetails
import br.com.mobicare.cielo.commons.constants.EMPTY_STRING
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.TWO

object ChargebackDialogUtils {

    fun createReasonDialog(
        useFeatureToggle: Boolean,
        dialogTitle: String?,
        chargebackDetails: ChargebackDetails?,
        context: Context
    ): CieloDialog {
        var message = EMPTY_STRING
        if(useFeatureToggle.not()){
            when(chargebackDetails?.reasonType){
                ONE -> message = context.getString(R.string.chargeback_reason_message_type1)
                TWO -> message = context.getString(R.string.chargeback_reason_message_type2)
                THREE -> message = context.getString(R.string.chargeback_reason_message_type3)
            }
        }else {
            message = chargebackDetails?.descriptionReasonType.toString()
        }
        return CieloDialog.create(
            title = dialogTitle,
            message = message
        )
            .closeButtonVisible(true)
            .setPrimaryButton(context.getString(R.string.text_close))
            .setTitleTextAppearance(R.style.bold_montserrat_20_cloud_500)
    }
}