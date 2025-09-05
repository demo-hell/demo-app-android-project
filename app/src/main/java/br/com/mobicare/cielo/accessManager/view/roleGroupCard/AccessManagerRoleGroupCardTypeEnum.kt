package br.com.mobicare.cielo.accessManager.view.roleGroupCard

import br.com.mobicare.cielo.R

enum class AccessManagerRoleGroupCardTypeEnum(val id: Int) {
    NONE(0),
    ADMIN(1),
    ADMIN_DESC(2),
    READER(3),
    READER_DESC(4),
    EXPIRED(5),
    ANALYST(6),
    ANALYST_DESC(7),
    FOREIGN(8),
    TECHNICAL(9),
    TECHNICAL_DESC(10),
    CUSTOM(11),
    CUSTOM_DESC(12);

    val detailImage: Int
        get() = when(this) {
            NONE -> R.drawable.ic_gray_user_with_bg
            ADMIN, ADMIN_DESC -> R.drawable.ic_green_user_with_bg
            READER, READER_DESC -> R.drawable.ic_blue_user_with_bg
            ANALYST, ANALYST_DESC -> R.drawable.ic_purple_user_with_bg
            TECHNICAL, TECHNICAL_DESC -> R.drawable.ic_user_sunsent_100_24_dp
            CUSTOM, CUSTOM_DESC -> R.drawable.ic_people_feedback_user_pistachio_500_24_dp
            FOREIGN -> R.drawable.ic_user_500_24_dp
            EXPIRED -> R.drawable.ic_alert_id_17dp
        }

    val title: Int
        get() = when(this) {
            NONE -> R.string.access_manager_role_group_card_no_role_title
            ADMIN, ADMIN_DESC -> R.string.access_manager_role_group_card_admin_title
            READER, READER_DESC -> R.string.access_manager_role_group_card_reader_title
            ANALYST, ANALYST_DESC -> R.string.access_manager_role_group_card_analyst_title
            TECHNICAL, TECHNICAL_DESC -> R.string.access_manager_role_group_card_technical_title
            CUSTOM, CUSTOM_DESC -> R.string.access_manager_role_group_card_custom_title
            FOREIGN -> R.string.access_manager_role_group_card_foreign_title
            EXPIRED -> R.string.access_manager_role_group_card_expired_title
        }

    val subtitle: Int
        get() = when(this) {
            NONE -> R.string.access_manager_role_group_card_no_role_subtitle
            ADMIN -> R.string.access_manager_role_group_card_admin_subtitle
            ADMIN_DESC -> R.string.access_manager_role_group_card_admin_subtitle_desc
            READER -> R.string.access_manager_role_group_card_reader_subtitle
            READER_DESC -> R.string.access_manager_role_group_card_reader_subtitle_desc
            ANALYST -> R.string.access_manager_role_group_card_analyst_subtitle
            ANALYST_DESC -> R.string.access_manager_role_group_card_analyst_subtitle_desc
            TECHNICAL -> R.string.access_manager_role_group_card_technical_subtitle
            TECHNICAL_DESC -> R.string.access_manager_role_group_card_technical_subtitle_desc
            CUSTOM -> R.string.access_manager_role_group_card_custom_subtitle
            CUSTOM_DESC -> R.string.access_manager_role_group_card_custom_subtitle_desc
            FOREIGN -> R.string.access_manager_role_group_card_foreign_subtitle
            EXPIRED -> R.string.access_manager_role_group_card_expired_subtitle
        }

    val detailName: Int
        get() = when(this) {
            NONE -> R.string.access_manager_role_group_card_no_role_detail_name
            ADMIN, ADMIN_DESC -> R.string.access_manager_role_group_card_admin_detail_name
            READER, READER_DESC -> R.string.access_manager_role_group_card_reader_detail_name
            ANALYST, ANALYST_DESC -> R.string.access_manager_role_group_card_analyst_detail_name
            TECHNICAL, TECHNICAL_DESC -> R.string.access_manager_role_group_card_technical_detail_name
            CUSTOM, CUSTOM_DESC -> R.string.access_manager_role_group_card_custom_detail_name
            FOREIGN -> R.string.access_manager_role_group_card_foreign_detail_name
            EXPIRED -> R.string.access_manager_role_group_card_expired_detail_name
        }

    val titleColor: Int
        get() = when(this) {
            NONE -> R.color.display_400
            ADMIN, ADMIN_DESC -> R.color.success_500
            READER, READER_DESC -> R.color.brand_400
            ANALYST, ANALYST_DESC -> R.color.color_purple
            TECHNICAL, TECHNICAL_DESC -> R.color.sunset_500
            CUSTOM, CUSTOM_DESC -> R.color.pistachio_500
            FOREIGN -> R.color.ocean_500
            EXPIRED -> R.color.alert_500
        }

    val hasDetail: Boolean
        get() = when(this) {
            ADMIN_DESC, READER_DESC , ANALYST_DESC, TECHNICAL_DESC, CUSTOM_DESC -> false
            else -> true
        }

    val showDetailName: Boolean
        get() = when(this) {
            EXPIRED-> false
            else -> true
        }

    companion object {
        fun fromId(id: Int?) = values().firstOrNull{ it.id == id } ?: NONE
    }
}