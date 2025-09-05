package br.com.mobicare.cielo.picture


import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import br.com.mobicare.cielo.R
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.fragment_take_picture.*
import kotlinx.android.synthetic.main.fragment_take_picture_constraint.view.*
import java.io.FileOutputStream
import java.io.IOException


@Suppress("DEPRECATION")
class PrePaidTakePictureActivity : BasePicture(), SurfaceHolder.Callback {

    @Suppress("DEPRECATION")
    private var camera: Camera? = null
    private var surface: SurfaceView? = null
    private var surfaceHolder: SurfaceHolder? = null

    private var overlay: View? = null
    private var isFlash = false

    var textPhoto: String? = null
        get() = intent?.getStringExtra(PHOTO_TEXT)

    private val hasFlash: Boolean by lazy {
        this.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_take_picture)

        configureOverlay()
        permissionsAndroidM()
        configureBack()
    }

    private fun configureOverlay() {
        val inflater = LayoutInflater.from(baseContext)
        overlay = inflater.inflate(R.layout.fragment_take_picture_constraint, null)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        addContentView(overlay, params)

        overlay?.findViewById<TextView>(R.id.typefaceTextView)?.text = textPhoto
        overlay?.findViewById<ImageButton>(R.id.imgButtonTakePicture)?.isEnabled = true
    }

    override fun openCamera() {
        if (!isPermissions()) {
            onBackPressed()
            return
        }

        try {
            surface = sv_surface
            surfaceHolder = surface?.holder
            surfaceHolder?.apply {
                this.addCallback(this@PrePaidTakePictureActivity)
            }

            releaseCameraAndPreview()

            openCameraFacingBack()

            configureActiveFlashClick()
            configureTakePictureClick()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            callErrorOpenCamera()
        }

    }

    private fun openCameraFacingBack() {

        val info = Camera.CameraInfo()

        for (i in 0 until Camera.getNumberOfCameras()) {
            Camera.getCameraInfo(i, info)

            // Gets only the back camera
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                camera = Camera.open(i)
                if (camera == null) {
                    finalizeScreenCameraNotFound()
                    return
                }
                break
            }
        }

        if (camera == null) {
            camera = Camera.open()

            /**
             * O processo de checagem de existencia de cameras em [com.ms.core.business.DeviceRecursoBusiness]
             * pode gerar 'falsos positivos'. Usar a checagem abaixo para validacao.
             *
             * Link de discussao: https://stackoverflow.com/a/22652689
             */
            if (camera == null) {
                finalizeScreenCameraNotFound()
                return
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        permissionsAndroidM()
    }

    override fun onPause() {
        super.onPause()
        releaseCameraAndPreview()
        onBackPressed()
    }

    private fun releaseCameraAndPreview() {
        if (camera != null) {
            camera!!.lock()
            camera!!.stopPreview()
            camera!!.release()
            camera = null
        }
    }


    private fun configureBack() {
        overlay!!.findViewById<ImageView>(R.id.btnBack)
            .setOnClickListener {
                onBackPressed()
            }
    }

    private fun configureTakePictureClick() {

        overlay!!.findViewById<ImageButton>(R.id.imgButtonTakePicture).setOnClickListener {
            overlay!!.findViewById<ImageButton>(R.id.imgButtonTakePicture).isEnabled = false
            overlay!!.progressView.visibility = View.VISIBLE
            camera?.run {
                this.takePicture(null, null, Camera.PictureCallback { data, _ ->
                    val pictureFile = getOutputMediaFile() ?: return@PictureCallback
                    try {
                        val fos = FileOutputStream(pictureFile)
                        fos.write(data)
                        fos.close()
                        saveImageClean(pictureFile)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        overlay!!.progressView.visibility = View.GONE
                    }
                })
            }
        }
    }

    private fun configureActiveFlashClick() {
        overlay!!.findViewById<ImageButton>(R.id.btnFlash).setOnClickListener {
            isFlash = !isFlash
            configCamera(isFlash)
        }
    }

    private fun configCamera(withFlash: Boolean) {
        camera?.run {

            this.setDisplayOrientation(90)

            setAutoFocus()
            setFlash(withFlash)

            this.setPreviewDisplay(surfaceHolder)
            this.startPreview()
        }

    }

    private fun setFlash(withFlash: Boolean) {

        if (hasFlash) {
            camera?.run {

                val paramCamp = this.parameters
                if (!withFlash) {
                    paramCamp.flashMode = Camera.Parameters.FLASH_MODE_OFF
                } else {
                    paramCamp.flashMode = Camera.Parameters.FLASH_MODE_ON
                }
                this.parameters = paramCamp
            }
            configureImageFlash(overlay!!, withFlash)
        } else {
            overlay!!.findViewById<ImageButton>(R.id.btnFlash).visibility = View.GONE
        }
    }


    private fun setAutoFocus() {
        camera?.run {
            /* Set Auto focus */
            val parameters = this.parameters
            val focusModes = parameters.supportedFocusModes


            when {
                focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) -> {
                    parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                    parameters.focusAreas = null
                    parameters.meteringAreas = null
                }
                focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO) -> parameters.focusMode =
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
                focusModes.contains(Camera.Parameters.FOCUS_MODE_EDOF) -> parameters.focusMode =
                    Camera.Parameters.FOCUS_MODE_EDOF
                focusModes.contains(Camera.Parameters.FOCUS_MODE_MACRO) -> parameters.focusMode =
                    Camera.Parameters.FOCUS_MODE_MACRO
                focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO) -> parameters.focusMode =
                    Camera.Parameters.FOCUS_MODE_AUTO
                focusModes.contains(Camera.Parameters.FOCUS_MODE_FIXED) -> parameters.focusMode =
                    Camera.Parameters.FOCUS_MODE_FIXED
            }

            this.parameters = parameters
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST -> {
                var permissionGranted = isRequestPermission(grantResults)
                if (permissionGranted) {
                    openCamera()
                    configCamera(isFlash)
                } else {
                    createAlertDialogPermission()
                }
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        camera?.apply {
            this.stopPreview()
            this.release()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        configCamera(isFlash)
    }


}
