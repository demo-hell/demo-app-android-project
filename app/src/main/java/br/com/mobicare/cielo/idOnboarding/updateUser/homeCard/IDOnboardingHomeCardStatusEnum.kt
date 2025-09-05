package br.com.mobicare.cielo.idOnboarding.updateUser.homeCard

import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.ANALYST

enum class IDOnboardingHomeCardStatusEnum {
    NONE,
    UPDATE_DATA,
    DATA_ANALYSIS,
    SEND_DOCUMENTS,
    APPROVED_DOCUMENTS;

    val image: Int
        get() = when (this) {
            NONE -> R.drawable.ic_onboarding_status_send_docs
            UPDATE_DATA, DATA_ANALYSIS -> R.drawable.ic_onboarding_status_waiting
            SEND_DOCUMENTS -> R.drawable.ic_onboarding_status_send_docs
            APPROVED_DOCUMENTS -> R.drawable.ic_onboarding_status_completed
        }

    val title: Int
    get() = when(this) {
        NONE -> R.string.id_onboarding_home_status_card_send_docs_title
        UPDATE_DATA -> R.string.id_onboarding_home_status_card_update_data_title
        DATA_ANALYSIS -> R.string.id_onboarding_home_status_card_waiting_title
        SEND_DOCUMENTS -> R.string.id_onboarding_home_status_card_send_docs_title
        APPROVED_DOCUMENTS -> R.string.id_onboarding_home_status_card_approved_title
    }

    val message: Int
        get() = when (this) {
            NONE -> getMessage(
                R.string.id_onboarding_home_status_card_send_docs_message,
                R.string.id_onboarding_home_status_card_send_docs_message_analyst
            )
            UPDATE_DATA -> R.string.id_onboarding_home_status_card_update_data_message
            DATA_ANALYSIS -> R.string.id_onboarding_home_status_card_waiting_message
            SEND_DOCUMENTS -> getMessage(
                R.string.id_onboarding_home_status_card_send_docs_message,
                R.string.id_onboarding_home_status_card_send_docs_message_analyst
            )
            APPROVED_DOCUMENTS -> R.string.id_onboarding_home_status_card_approved_message
        }

    val titleColor: Int
        get() = when (this) {
            APPROVED_DOCUMENTS -> R.color.success_400
            else -> R.color.alert_500
        }

    var mainRole: String? = null

    private fun getMessage(adminMessage: Int, analystMessage: Int): Int {
        return if (mainRole == ANALYST) analystMessage else adminMessage
    }
}