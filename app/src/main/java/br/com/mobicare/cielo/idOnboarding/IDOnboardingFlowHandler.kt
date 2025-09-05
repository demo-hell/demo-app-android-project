package br.com.mobicare.cielo.idOnboarding

import android.app.Dialog
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.extensions.orZero
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointP1
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointP1.POLICY_1_REQUESTED
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointP2
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointP2.POLICY_2_REQUESTED
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointUserCnpj
import br.com.mobicare.cielo.idOnboarding.model.IDOnboardingCurrentUserStatus
import br.com.mobicare.cielo.idOnboarding.updateUser.homeCard.IDOnboardingHomeCardStatusEnum
import br.com.mobicare.cielo.idOnboarding.updateUser.homeCard.IDOnboardingHomeCardStatusEnum.*

object IDOnboardingFlowHandler {
    const val SMS = "SMS"
    const val WHATSAPP = "WHATSAPP"

    const val RG = "RG"
    const val CNH = "CNH"
    const val CNH2022 = "CNHCEL2022"
    const val DNI = "DNI"
    const val CRNM = "CRNM"
    const val RNE = "RNE"

    object StoneAgeDocumentValidation {
        const val RG_FRENTE = "RG_FRENTE"
        const val RG_VERSO = "RG_VERSO"
        const val RGNOVO_FRENTE = "RGNOVO_FRENTE"
        const val RGNOVO_VERSO = "RGNOVO_VERSO"
        const val CIN_FRENTE = "CIN_FRENTE"
        const val CIN_VERSO = "CIN_VERSO"
        const val CNH_FRENTE = "CNH_FRENTE"
        const val CNH_VERSO = "CNH_VERSO"
    }

    var isShowWarning = true
    var isLogin = false

    var userStatus = IDOnboardingCurrentUserStatus()

    val canPostponeOnboarding: Boolean
        get() {
            return userStatus.p1Flow?.deadlineRemainingDays.orZero > 0
        }

    var checkpointUserCnpj
        get() = IDOCheckpointUserCnpj.fromCode(
            userStatus.onboardingStatus?.onboardingCheckpointCode
        )
        set(value) {
            userStatus.onboardingStatus?.run {
                onboardingCheckpointCode = value.code
                onboardingCheckpoint = value.name
            }
        }

    var checkpointP1
        get() = IDOCheckpointP1.fromCode(
            userStatus.p1Flow?.p1CheckpointCode
        )
        set(value) {
            userStatus.p1Flow?.run {
                p1CheckpointCode = value.code
                p1Checkpoint = value.name
            }
        }

    var checkpointP2
        get() = IDOCheckpointP2.fromCode(
            userStatus.p2Flow?.p2CheckpointCode
        )
        set(value) {
            userStatus.p2Flow?.run {
                p2CheckpointCode = value.code
                p2Checkpoint = value.name
            }
        }

    val onboardingHomeCardStatus: IDOnboardingHomeCardStatusEnum
        get() = when {
            checkpointP1 == POLICY_1_REQUESTED || checkpointP2 == POLICY_2_REQUESTED ->
                DATA_ANALYSIS
            checkpointUserCnpj == IDOCheckpointUserCnpj.NONE ->
                UPDATE_DATA
            checkpointUserCnpj == IDOCheckpointUserCnpj.USER_CNPJ_CHECKED ->
                SEND_DOCUMENTS
            else ->
                NONE
        }

    fun showCustomBottomSheet(
        activity: FragmentActivity? = null,
        image: Int? = null,
        title: String? = null,
        message: String? = null,
        bt1Title: String? = null,
        bt2Title: String? = null,
        bt1Callback: (() -> Boolean)? = null,
        bt2Callback: (() -> Boolean)? = null,
        closeCallback: (() -> Unit)? = null,
        isCancelable: Boolean = true,
        isFullScreen: Boolean = false,
        isPhone: Boolean = true
    ) {
        activity?.run {
            this.lifecycleScope.launchWhenResumed {
                bottomSheetGenericFlui(
                    image = image ?: R.drawable.ic_generic_error_image,
                    title = title ?: getString(R.string.generic_error_title),
                    subtitle = message ?: getString(R.string.error_generic),
                    nameBtn1Bottom = bt1Title ?: "",
                    nameBtn2Bottom = bt2Title ?: getString(R.string.ok),
                    txtTitleStyle = TxtTitleStyle.TXT_TITLE_DARK_BLUE,
                    txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_FLUI_BOTTOM_SHEET,
                    statusBtnFirst = bt1Title != null,
                    btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE_TXT_BLUE,
                    isCancelable = isCancelable,
                    isFullScreen = isFullScreen,
                    isPhone = isPhone
                ).apply {
                    onClick =
                        object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {

                            override fun onBtnFirst(dialog: Dialog) {
                                if (bt1Callback?.invoke() != true) dismiss()
                            }

                            override fun onBtnSecond(dialog: Dialog) {
                                if (bt2Callback?.invoke() != true) dismiss()
                            }

                            override fun onSwipeClosed() {
                                closeCallback?.invoke()
                            }

                            override fun onCancel() {
                                closeCallback?.invoke()
                            }
                        }
                }.show(
                    supportFragmentManager,
                    getString(R.string.bottom_sheet_generic)
                )
            }
        }
    }
}