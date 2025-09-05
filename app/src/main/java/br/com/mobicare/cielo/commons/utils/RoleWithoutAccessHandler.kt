package br.com.mobicare.cielo.commons.utils

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.Text.FORBIDDEN_ADM
import br.com.mobicare.cielo.commons.constants.Text.MESSAGE
import br.com.mobicare.cielo.commons.constants.Text.ONBOARDING_REQUIRED
import br.com.mobicare.cielo.commons.constants.Text.ROLE_WITHOUT_ACCESS
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.extensions.backToHome
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.moveToHome
import br.com.mobicare.cielo.idOnboarding.router.IDOnboardingRouter
import br.com.mobicare.cielo.main.presentation.ui.activities.MainBottomNavigationActivity
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import com.google.firebase.crashlytics.FirebaseCrashlytics

object RoleWithoutAccessHandler {

    fun showNoAccessAlert(activity: FragmentActivity?) {
        try {
            activity?.doWhenResumed {
                val cieloDialog = CieloDialog.create(
                    activity.getString(R.string.access_manager_no_permission_dialog_title),
                    activity.getString(R.string.access_manager_no_permission_dialog_message),
                )
                cieloDialog.setImage(R.drawable.img_whitout_profile_access)
                    .closeButtonVisible(false)
                    .setPrimaryButton(activity.getString(R.string.go_to_initial_screen))
                    .setOnPrimaryButtonClickListener {
                        cieloDialog.dismiss()
                        activity.moveToHome()
                    }
                    .setOnCancelListener { activity.moveToHome() }
                    .show(
                        activity.supportFragmentManager,
                        activity.getString(R.string.bottom_sheet_generic)
                    )
            }
        } catch (exception: Exception) {
            exception.message?.let { error ->
                FirebaseCrashlytics.getInstance().log(error)
            }
        }
    }

    fun showNoAccessAlertUpdateInfo(activity: FragmentActivity?) {
        try {
            activity?.doWhenResumed {
                val cieloDialog = CieloDialog.create(
                    activity.getString(R.string.access_manager_no_permission_otp_dialog_title),
                    activity.getString(R.string.access_manager_no_permission_otp_dialog_message),
                )
                cieloDialog.setImage(R.drawable.img_no_profile_access)
                    .closeButtonVisible(false)
                    .setPrimaryButton(activity.getString(R.string.access_manager_update))
                    .setOnPrimaryButtonClickListener {
                        cieloDialog.dismiss()
                        IDOnboardingRouter(
                            activity = activity,
                            showLoadingCallback = {},
                            hideLoadingCallback = {}
                        ).showOnboarding()
                    }
                    .setSecondaryButton(activity.getString(R.string.access_manager_why_update))
                    .setOnSecondaryButtonClickListener {
                        cieloDialog.dismiss()
                        activity.openFaq(
                            tag = ConfigurationDef.TAG_HELP_CENTER_IDENTIDADE_DIGITAL,
                            subCategoryName = activity.getString(R.string.access_manager_name)
                        )
                    }
                    .setOnCancelListener { activity.moveToHome() }
                    .show(
                        activity.supportFragmentManager,
                        activity.getString(R.string.bottom_sheet_generic)
                    )
            }
        } catch (exception: Exception) {
            exception.message?.let { error ->
                FirebaseCrashlytics.getInstance().log(error)
            }
        }
    }

    fun showNoAccessAlertAdmin(activity: FragmentActivity?, messageAlert: String) {
        try {
            activity?.doWhenResumed {
                val cieloDialog = CieloDialog.create(
                    activity.getString(R.string.access_manager_no_permission_forbidden_admin_title),
                    messageAlert,
                )
                cieloDialog.setImage(R.drawable.img_whitout_profile_access)
                    .closeButtonVisible(false)
                    .setPrimaryButton(activity.getString(R.string.go_to_initial_screen))
                    .setOnPrimaryButtonClickListener {
                        cieloDialog.dismiss()
                    }
                    .setOnCancelListener { activity.moveToHome()}
                    .show(
                        activity.supportFragmentManager,
                        activity.getString(R.string.bottom_sheet_generic)
                    )
            }
        } catch (exception: Exception) {
            exception.message?.let { error ->
                FirebaseCrashlytics.getInstance().log(error)
            }
        }
    }

    fun broadcastRoleWithoutAccess(context: Context?) {
        context?.let {
            LocalBroadcastManager.getInstance(it)
                .sendBroadcast(
                    Intent(ROLE_WITHOUT_ACCESS)
                )
        }
    }

    fun broadcastRoleWithoutAccessUpdateInfo(context: Context?) {
        context?.let {
            LocalBroadcastManager.getInstance(it)
                .sendBroadcast(
                    Intent(ONBOARDING_REQUIRED)
                )
        }
    }

    fun broadcastRoleWithoutAccessAdmin(context: Context?, message: String?) {
        context?.let {
            LocalBroadcastManager.getInstance(it)
                .sendBroadcast(
                    Intent(FORBIDDEN_ADM).apply {
                        putExtra(MESSAGE, message)
                    }
                )
        }
    }
}