package br.com.mobicare.cielo.mfa

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.enums.MfaStatusEnums.*
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.main.presentation.ui.activities.ERROR_CODE_OTP
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MfaOtpErrorHandleActivity : AppCompatActivity() {


    private val errorCode by lazy { intent?.extras?.get(ERROR_CODE_OTP) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showMfaOtpError()
    }

    private fun showMfaOtpError() {
        intent?.run {
            when (errorCode) {
                OTP_REQUIRED.mfaStatus,
                OTP_ENROLLMENT_REQUIRED.mfaStatus,
                OTP_ENROLLMENT_EXPIRED.mfaStatus,
                OTP_ENROLLMENT_PENDING.mfaStatus,
                OTP_TEMPORARILY_BLOCKED.mfaStatus,
                INVALID_OTP_CODE.mfaStatus -> {
                    showMfaOtpEnrollment(getString(R.string.txt_security_token_enable_subtitle))
                }
                NOT_ELIGIBLE.mfaStatus -> {
                    showMfaOtpEnrollment(getString(R.string.txt_feature_not_active_for_establishment))
                }
                else -> {
                    val errorMassage =
                        "the status error code of the mfaOtpErrorHandleActivity class is: $errorCode"
                    FirebaseCrashlytics.getInstance()
                        .recordException(IllegalArgumentException(errorMassage))
                }
            }
        }
    }

    private fun showMfaOtpEnrollment(subTitle: String) {

        bottomSheetGenericFlui(
            getString(R.string.text_registro_recebiveis),
            R.drawable.ic_42,
            getString(R.string.txt_token_use_required_title),
            subTitle,
            getString(R.string.btn_retornar_painel),
            getString(R.string.txt_go_to_token),
            statusNameTopBar = false,
            statusTitle = true,
            statusSubTitle = true,
            statusImage = true,
            statusBtnClose = true,
            statusBtnFirst = false,
            statusBtnSecond = true,
            statusView1Line = true,
            statusView2Line = false,
            txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isFullScreen = true
        ).apply {
            this.onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnSecond(dialog: Dialog) {
                    super.onBtnSecond(dialog)
                    dismiss()
                    val i = Intent(context, FluxoNavegacaoMfaActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(i)
                    finish()
                }

                override fun onBtnClose(dialog: Dialog) {
                    super.onBtnClose(dialog)
                    dismiss()
                    finish()
                }

                override fun onSwipeClosed() {
                    super.onSwipeClosed()
                    dismiss()
                    finish()
                }

                override fun onCancel() {
                    super.onCancel()
                    dismiss()
                    finish()
                }
            }
        }.show(supportFragmentManager, getString(R.string.bottom_sheet_generic))
    }
}