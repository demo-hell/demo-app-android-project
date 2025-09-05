package br.com.mobicare.cielo.picture

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Environment
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.AlertDialogCustom
import br.com.mobicare.cielo.commons.utils.showMessage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

abstract class BasePicture : AppCompatActivity() {


    companion object {
        const val PERMISSIONS_REQUEST = 1
        const val EXTRA_PHOTO_RETURN: String = "PHOTO_RETURN"
        const val PHOTO_TEXT: String = "br.com.mobicare.cielo.picture.PrePaidTakePictureActivity.photo_text"
        const val LIMIT_FILE_SIZE_IN_MB = 9
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    protected fun isRequestPermission(grantResults: IntArray): Boolean {
        grantResults.forEach {
            if (it != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }


    protected fun createAlertDialogPermission() {
        AlertDialogCustom.Builder(this, "")
                .setTitle("Permissão para uso da câmera")
                .setMessage("É necessário dar permissão para tirar e gravar fotos. Permitir que o app Cielo tire e grave fotos?")
                .setBtnRight("Ok")
                .setBtnLeft("Cancel")
                .setCancelable(false)
                .setOnclickListenerRight {
                    apply {
                        onBackPressed()
                    }
                }
                .setOnclickListenerLeft {
                    onBackPressed()
                }
                .show()
    }


    protected fun configureImageFlash(overlay: View, withFlash: Boolean) {
        val imageButton = overlay.findViewById<ImageButton>(R.id.btnFlash)
        imageButton.setImageResource(
                if (!withFlash)
                    R.drawable.ic_flash_off
                else
                    R.drawable.ic_flash_on)
    }

    protected fun finalizeScreenCameraNotFound() {
        throw java.lang.Exception("Camera não encotrada")
    }

    protected fun getOutputMediaFile(): File {
        val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CieloApp")
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdir()
            if (!mediaStorageDir.exists()) {
                onBackPressed()
                RuntimeException("failed to create directory")
            }
        }

        val timeStamp = SimpleDateFormat("yyMMdd_HHmmssSSS").format(Date())
        val mediaFile: File
        mediaFile = File(mediaStorageDir.path + File.separator + "DOC_" + timeStamp + ".jpg")
        //Toast.makeText(this, "foto gravada em:${mediaFile.absolutePath}", Toast.LENGTH_SHORT).show()
        return mediaFile
    }

    protected fun saveImageClean(pictureFile: File) {
        photoReturn(pictureFile)//.compressImage(this, imageWidth, imageHeight))
    }

    protected fun photoReturn(pictureFile: File) {
        val result = Intent()
        result.putExtra(EXTRA_PHOTO_RETURN, pictureFile.absoluteFile)
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    protected fun callErrorOpenCamera() {
        showMessage(message = getString(R.string.picture_error_description),
                title = getString(R.string.picture_error_title),
                customBuilder = {
                    this.setBtnLeft(null)
                    this.setBtnRight("Ok")
                    this.setCancelable(false)
                    this.setOnclickListenerRight {
                        onBackPressed()
                    }
                })
    }


    protected fun permissionsAndroidM() {
        if (!isPermissions()) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSIONS_REQUEST)
        } else {
            openCamera()
        }
    }

    abstract fun openCamera()

    protected fun isPermissions() = isPermissionCamera() && isPermissionStorage()

    private fun isPermissionCamera() =
            (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED)

    private fun isPermissionStorage() =
            (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)
}