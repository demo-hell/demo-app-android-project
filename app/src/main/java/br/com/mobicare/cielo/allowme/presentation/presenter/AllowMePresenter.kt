package br.com.mobicare.cielo.allowme.presentation.presenter

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.com.allowme.android.contextual.AllowMeContextual
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.dialogLocationActivation
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.firebase.crashlytics.FirebaseCrashlytics

private const val GRANTED = 0
private const val DENIED = 1
private const val BLOCKED_OR_NEVER_ASKED = 2

class AllowMePresenter(
    private var mView: AllowMeContract.View,
    private val userPreferences: UserPreferences,
) : AllowMeContract.Presenter {
    private var mAllowMeContextualLocal: AllowMeContextual = AllowMeContextual()
    private var mandatory: Boolean = false

    private val useSecurityHashLocation: Boolean? by lazy {
        FeatureTogglePreference.instance.getFeatureTogle(
            FeatureTogglePreference.SECURITY_HASH_LOCATION
        )
    }

    override fun init(context: Context): AllowMeContextual {
        mAllowMeContextualLocal.start(context)
        return mAllowMeContextualLocal
    }

    override fun collect(
        mAllowMeContextual: AllowMeContextual,
        context: Activity,
        mandatory: Boolean,
        hasAnimation: Boolean,
        askLocalizationPermission: Boolean
    ) {
        if (askLocalizationPermission.not()) {
            collectFingerprint(mAllowMeContextual, context)
            return
        }

        if (mandatory && useSecurityHashLocation == true) {
            this.mandatory = mandatory
            val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (permissionValidate(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION,
                    mandatory
                )
            ) {
                collectAllowMe(context, mandatory)
            } else if (statusOfGPS.not())
                showRationaleOnLocal(context, hasAnimation)

        } else if (useSecurityHashLocation == true) {
            permissionValidateNotMandatory(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION,
                mandatory
            )

        } else {
            collectFingerprint(mAllowMeContextual, context)
        }
    }

    private fun collectFingerprint(
        mAllowMeContextual: AllowMeContextual,
        context: Activity
    ) {
        mAllowMeContextual.collect(context, { result ->
            mView.successCollectToken(result)
        }) { errorMessage ->
            FirebaseCrashlytics.getInstance().recordException(Throwable(errorMessage))
            mView.errorCollectToken(EMPTY, errorMessage, false)
        }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray, context: Activity
    ) {
        userPreferences.locationPermissionCheck = false
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    collectAllowMe(context, true)
                    FirebaseCrashlytics.getInstance()
                        .log(context.getString(R.string.accept_permission_allowme))
                } else if (mandatory) {
                    onCreateBottomSheetPermission(requestCode, permissions[0], context, mandatory)
                } else {
                    collectAllowMe(context, true)
                }
            }
        }
    }

    private fun collectAllowMe(context: Activity, mandatory: Boolean) {
        mAllowMeContextualLocal.collect(context, { result ->
            mView.successCollectToken(result)
        }) { errorMessage ->
            FirebaseCrashlytics.getInstance().recordException(Throwable(errorMessage))
            mView.errorCollectToken(EMPTY, errorMessage, mandatory)
        }
    }

    override fun permissionValidate(
        activity: Activity,
        permission: String,
        permissionValue: Int,
        mandatory: Boolean
    ): Boolean {
        val isGranted = getPermissionStatus(activity, permission)
        if (isGranted == DENIED || isGranted == BLOCKED_OR_NEVER_ASKED) {
            onCreateBottomSheetPermission(permissionValue, permission, activity, mandatory)
        } else {
            val manager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            return (isGranted == GRANTED) && statusOfGPS
        }
        return isGranted == GRANTED

    }

    private fun permissionValidateNotMandatory(
        activity: Activity,
        permission: String,
        permissionValue: Int,
        mandatory: Boolean
    ) {
        when (getPermissionStatus(activity, permission)) {
            DENIED -> {
                onCreateBottomSheetPermission(permissionValue, permission, activity, mandatory)
            }
            BLOCKED_OR_NEVER_ASKED -> {
                collectAllowMe(activity, mandatory)
            }
            else -> {
                collectAllowMe(activity, mandatory)
            }
        }
    }

    override fun onCreateBottomSheetPermission(
        permissionValue: Int,
        permission: String,
        context: Activity,
        mandatory: Boolean
    ) {
        if (mandatory) {
            bottomSheetCreate(
                title = context.getString(R.string.tittle_bottomsheet_permission_mandatory),
                description = context.getString(R.string.description_bottomsheet_permission_mandatory),
                context = context,
                permissionValue = permissionValue,
                permission = permission,
                mandatory = mandatory
            )
        } else {
            bottomSheetCreate(
                title = context.getString(R.string.tittle_bottomsheet_permission),
                description = context.getString(R.string.description_bottomsheet_permission),
                context = context,
                permissionValue = permissionValue,
                permission = permission,
                mandatory = mandatory
            )
        }
    }

    private fun bottomSheetCreate(
        title: String, description: String, context: Activity, permissionValue: Int,
        permission: String,
        mandatory: Boolean
    ) {
        context.bottomSheetGenericFlui(
            "",
            R.drawable.ic_permission01,
            title,
            description,
            "",
            context.getString(R.string.button_bottomsheet_permission),
            statusNameTopBar = false,
            statusTitle = true,
            statusSubTitle = true,
            statusImage = true,
            statusBtnClose = false,
            statusBtnFirst = false,
            statusBtnSecond = true,
            statusView1Line = true,
            statusView2Line = false,
            txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isFullScreen = false
        ).apply {
            onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnSecond(dialog: Dialog) {
                    if (mandatory) {
                        if (getPermissionStatus(activity, permission) == BLOCKED_OR_NEVER_ASKED
                        ) {
                            showRationaleDialog(context)
                        } else {
                            callPermission(context, permission, permissionValue)
                            dismiss()
                        }
                    } else {
                        callPermission(context, permission, permissionValue)
                        dismiss()
                    }
                }

                override fun onSwipeClosed() {
                    callPermission(context, permission, permissionValue)
                    dismiss()
                }

                override fun onCancel() {
                    callPermission(context, permission, permissionValue)
                    dismiss()

                }
            }
        }.show(
            mView.getSupportFragmentManagerInstance(),
            context.getString(R.string.bottom_sheet_generic)
        )
    }

    private fun BottomSheetFluiGenericFragment.callPermission(
        context: Activity,
        permission: String,
        permissionValue: Int
    ) {
        ActivityCompat.requestPermissions(
            context,
            arrayOf(permission),
            permissionValue
        )
        dismiss()
    }

    private fun showRationaleDialog(context: Activity) {
        AlertDialog.Builder(context)
            .setMessage(R.string.permission_needed_title)
            .setPositiveButton(R.string.access_permission_button) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts(
                    context.getString(R.string.permission_needed_package),
                    context.packageName,
                    null
                )
                intent.data = uri
                context.startActivity(intent)
            }
            .setNegativeButton(R.string.permission_needed_button1) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun showRationaleOnLocal(context: Activity, hasAnimation: Boolean) {
        if (hasAnimation)
            mView.stopAction()
        else
            dialogLocationActivation(context, mView.getSupportFragmentManagerInstance())
    }

    annotation class PermissionStatus

    @PermissionStatus
    fun getPermissionStatus(activity: Activity?, androidPermissionName: String?): Int {
        return if (ContextCompat.checkSelfPermission(
                activity!!,
                androidPermissionName!!
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    androidPermissionName
                ).not()
            ) {
                val isFirstLocationPermissionCheck = userPreferences.locationPermissionCheck
                return if (isFirstLocationPermissionCheck) DENIED else BLOCKED_OR_NEVER_ASKED
            } else DENIED
        } else GRANTED
    }

}
