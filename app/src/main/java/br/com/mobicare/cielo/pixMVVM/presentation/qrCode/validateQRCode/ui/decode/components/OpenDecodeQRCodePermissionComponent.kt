package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.validateQRCode.ui.decode.components

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.cameraIsEnabled
import br.com.mobicare.cielo.commons.utils.openAppSettings
import br.com.mobicare.cielo.extensions.activity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@Composable
fun OpenDecodeQRCodePermissionComponent(
    onSuccess: (String) -> Unit,
    onError: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember { mutableStateOf(false) }
    var permissionDenied by remember { mutableStateOf(false) }
    var dialog: AlertDialog? by remember { mutableStateOf(null) }

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { granted ->
                hasCameraPermission = granted
                permissionDenied = granted.not()
            },
        )

    LaunchedEffect(Unit) {
        if (cameraIsEnabled(context)) {
            hasCameraPermission = true
        } else {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onResume(owner: LifecycleOwner) {
                    super.onResume(owner)

                    if (permissionDenied && cameraIsEnabled(context)) {
                        hasCameraPermission = true
                        permissionDenied = false
                        dialog?.dismiss()
                        dialog = null
                    } else if (permissionDenied && dialog?.isShowing == false) {
                        context.activity()?.finish()
                    }
                }
            },
        )
    }

    if (hasCameraPermission) {
        DecodeQRCodeComponent(onSuccess, onError)
    } else {
        Box(
            modifier =
                Modifier
                    .background(colorResource(id = R.color.cloud_60_opacity_60))
                    .fillMaxSize(),
        )

        if (permissionDenied && dialog == null) {
            dialog = createDialog(context)
            dialog?.show()
        }
    }
}

private fun createDialog(context: Context): AlertDialog =
    MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialogCieloTheme)
        .setMessage(context.getString(R.string.pix_qr_code_decode_message_request_access_camera))
        .setPositiveButton(context.getString(R.string.access_permission_button)) { _, _ ->
            openAppSettings(context)
        }.setNegativeButton(context.getString(R.string.cancelar)) { _, _ ->
            context.activity()?.finish()
        }.setOnCancelListener {
            context.activity()?.finish()
        }.show()
