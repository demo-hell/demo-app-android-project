package br.com.mobicare.cielo.chargeback.presentation.details.builder

import android.content.Context
import android.content.res.Resources
import br.com.cielo.libflue.enum.CieloCardBrandIcons
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chargeback.domain.model.Chargeback
import br.com.mobicare.cielo.chargeback.presentation.details.model.ChargebackInfoContent
import br.com.mobicare.cielo.chargeback.presentation.details.model.ChargebackInfoContentField
import br.com.mobicare.cielo.chargeback.presentation.details.model.ChargebackInfoContentFieldType
import br.com.mobicare.cielo.chargeback.utils.createChargebackStatusLabel
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.utils.convertToBrDateFormat
import br.com.mobicare.cielo.extensions.capitalizePTBR
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import java.time.format.DateTimeFormatter

class ChargebackInfoContentListBuilder(
    private val res: Resources,
    private val context: Context,
    private val showRDRCardFeatureToggle: Boolean?
) {
    fun build(chargeback: Chargeback): List<ChargebackInfoContent> {
        return buildChargebackInfoContentList(chargeback)
    }

    private fun buildDateAndReason(chargeback: Chargeback) = ChargebackInfoContent(
        firstField = ChargebackInfoContentField(
            labelText = res.getString(
                if (chargeback.isDone)
                    R.string.chargeback_label_notification_date
                else
                    R.string.chargeback_label_dispute_date
            ),
            contentIcon = R.drawable.ic_chargeback_calendar_16,
            contentText = chargeback.chargebackDetails?.receptionDate?.toString()
                ?.convertToBrDateFormat(
                    DateTimeFormatter.ISO_LOCAL_DATE
                )
        ),
        secondField = ChargebackInfoContentField(
            type = ChargebackInfoContentFieldType.REASON,
            labelText = res.getString(R.string.chargeback_label_reason),
            contentIcon = R.drawable.ic_chargeback_help_16,
            contentText = chargeback.reasonName
        )
    )

    private fun buildProcessAndCardBrand(chargeback: Chargeback) = ChargebackInfoContent(
        firstField = ChargebackInfoContentField(
            labelText = res.getString(R.string.chargeback_label_process),
            contentIcon = R.drawable.ic_flag,
            contentText = createChargebackStatusLabel(chargeback.process, context)
        ),
        secondField = ChargebackInfoContentField(
            type = ChargebackInfoContentFieldType.CARD_BRAND,
            labelText = res.getString(R.string.chargeback_label_card_brand),
            contentIcon = CieloCardBrandIcons.getCardBrandIconResourceId(chargeback.transactionDetails?.cardBrandCode),
            contentText = chargeback.transactionDetails?.cardBrandName?.toLowerCasePTBR()
                ?.capitalizePTBR()
        )
    )

    private fun buildMerchantAndCase(chargeback: Chargeback) = ChargebackInfoContent(
        firstField = ChargebackInfoContentField(
            labelText = res.getString(R.string.chargeback_label_merchant),
            contentIcon = R.drawable.ic_store_gray,
            contentText = chargeback.merchantId.toString()
        ),
        secondField = ChargebackInfoContentField(
            labelText = res.getString(R.string.chargeback_label_case),
            contentIcon = R.drawable.ic_file_text,
            contentText = chargeback.caseId.toString()
        )
    )

    private fun buildReferenceAndMessage(chargeback: Chargeback): ChargebackInfoContent {
        return ChargebackInfoContent(
            firstField = ChargebackInfoContentField(
                labelText = res.getString(R.string.chargeback_label_reference_number),
                contentIcon = R.drawable.ic_chargeback_hashtag_16,
                contentText = chargeback.transactionDetails?.referenceNumber
            ),
            secondField = ChargebackInfoContentField(
                type = ChargebackInfoContentFieldType.MESSAGE,
                labelText = res.getString(R.string.chargeback_label_message),
                contentIcon = R.drawable.ic_chargeback_balloon_empty_16,
                contentText = chargeback.chargebackDetails?.chargebackFraudMessage
            ),
            hideSecondField = chargeback.chargebackDetails?.chargebackFraudMessage.isNullOrEmpty()
        )
    }

    private fun buildChargebackRDRCard(): ChargebackInfoContent {
        return ChargebackInfoContent(
            firstField = ChargebackInfoContentField(
                type = ChargebackInfoContentFieldType.UNIQUE_FIELD,
                labelText = res.getString(R.string.chargeback_rdr_card_label),
                contentIcon = R.drawable.ic_cards_credit_card_check_cloud_300_24dp,
                contentText = res.getString(R.string.chargeback_rdr_card_content)
            ),
            secondField = null
        )
    }

    private fun buildChargebackInfoContentList(chargeback: Chargeback): List<ChargebackInfoContent> {
        val contentList: MutableList<ChargebackInfoContent> = mutableListOf(
            buildDateAndReason(chargeback),
            buildProcessAndCardBrand(chargeback),
            buildMerchantAndCase(chargeback),
            buildReferenceAndMessage(chargeback),
        )
        if (showRDRCardFeatureToggle == true && chargeback.chargebackDetails?.fastDisputeResolution == true)
            contentList.add(ONE, buildChargebackRDRCard())
        return contentList.toList()
    }
}