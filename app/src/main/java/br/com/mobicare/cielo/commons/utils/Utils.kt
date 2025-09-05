package br.com.mobicare.cielo.commons.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.Intent.createChooser
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.nfc.NfcManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Base64
import android.util.DisplayMetrics
import android.view.ActionMode
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import br.com.cielo.libflue.alert.CieloAskQuestionDialogFragment
import br.com.cielo.libflue.util.TEN
import br.com.cielo.libflue.util.imageUtils.setCustomDrawable
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.utils.ArvConstants.POINT_OF_SALE
import br.com.mobicare.cielo.chargeback.utils.ChargebackConstants
import br.com.mobicare.cielo.commons.constants.AT_SIGN
import br.com.mobicare.cielo.commons.constants.FORMAT_DATE_PORTUGUESE
import br.com.mobicare.cielo.commons.constants.FOUR
import br.com.mobicare.cielo.commons.constants.IntentAction.IMG
import br.com.mobicare.cielo.commons.constants.IntentAction.MAIL_TYPE
import br.com.mobicare.cielo.commons.constants.IntentAction.SHARE_WITH
import br.com.mobicare.cielo.commons.constants.IntentAction.TEXT_PLAIN
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.SHARE_QR_CODE_FILE_PREFIX
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.Text.ONE_SPACE
import br.com.mobicare.cielo.commons.constants.USER_INPUT_CPF
import br.com.mobicare.cielo.commons.constants.USER_INPUT_CPF_TRACK
import br.com.mobicare.cielo.commons.constants.USER_INPUT_EC_NUMBER
import br.com.mobicare.cielo.commons.constants.USER_INPUT_EC_TRACK
import br.com.mobicare.cielo.commons.constants.USER_INPUT_EMAIL
import br.com.mobicare.cielo.commons.constants.USER_INPUT_EMAIL_TRACK
import br.com.mobicare.cielo.commons.constants.USER_INPUT_INTERN_TRACK
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.DEFAULT_ERROR_MESSAGE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.helpers.EditTextHelper
import br.com.mobicare.cielo.commons.helpers.FormHelper
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceEditTextView
import br.com.mobicare.cielo.databinding.CustomSnackGenericBinding
import br.com.mobicare.cielo.extensions.capitalizePTBR
import br.com.mobicare.cielo.extensions.clearCNPJMask
import br.com.mobicare.cielo.extensions.maskCPF
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.linkPaymentCreated.LinkPaymentCreatedFragment
import br.com.mobicare.cielo.pix.constants.AUTHORITY_PROVIDER
import br.com.mobicare.cielo.pix.constants.COUNTRY_CODE
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.constants.KEY_EVP
import br.com.mobicare.cielo.pix.constants.PNG
import br.com.mobicare.cielo.pix.constants.SCREENSHOT
import br.com.mobicare.cielo.pix.constants.WITH_COUNTRY_CODE
import br.com.mobicare.cielo.pix.domain.MyKey
import br.com.mobicare.cielo.pix.enums.PixClaimTypeEnum
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum
import br.com.mobicare.cielo.pix.enums.PixOwnerTypeEnum
import br.com.mobicare.cielo.pix.model.PixKeyType
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.custom_toast.view.clContent
import kotlinx.android.synthetic.main.custom_toast.view.ivLeadingIcon
import kotlinx.android.synthetic.main.custom_toast.view.ivTrailingIcon
import kotlinx.android.synthetic.main.custom_toast.view.tvToastText
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.text.DateFormat.getDateTimeInstance
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object Utils {
    private fun backToHome(activity: Activity) {
        activity.finish()
    }

    fun logout(activity: Activity) {
        UserPreferences.getInstance().logout()
        backToHome(activity)
    }

    fun isEmpty(string: String?): Boolean {
        return string == null || "" == string.trim { it <= ' ' } || "null" == string.toLowerCasePTBR()
    }

    fun addFontMuseoSans700(
        context: Context,
        textView: TextView,
    ) {
        addFont(context, textView, "fonts/MuseoSans-700.ttf")
    }

    fun addFontMuseoSans500(
        context: Context,
        textView: TextView,
    ) {
        addFont(context, textView, "fonts/MuseoSans-500.ttf")
    }

    private fun addFont(
        context: Context,
        textView: TextView,
        path: String,
    ) {
        val titleFont = Typeface.createFromAsset(context.assets, path)

        textView.typeface = titleFont
    }

    fun applyFontForToolbarTitle(
        context: Context,
        toolbar: Toolbar,
    ) {
        for (i in 0..toolbar.childCount - 1) {
            val view = toolbar.getChildAt(i)
            if (view is TextView) {
                val tv = view
                val titleFont =
                    Typeface.createFromAsset(context.assets, "fonts/Museo700-Regular.ttf")

                if (tv.text == toolbar.title) {
                    tv.typeface = titleFont
                    break
                }
            }
        }
    }

    fun unmask(s: String): String {
        return s.replace("[.]".toRegex(), "").replace("[-]".toRegex(), "")
            .replace("[/]".toRegex(), "").replace("[(]".toRegex(), "").replace("[)]".toRegex(), "")
    }

    fun formatValue(value: Double?): String {
        return value?.let {
            formatValue(it)
        } ?: ""
    }

    fun formatValue(value: Double): String {
        val formatter = NumberFormat.getInstance() as DecimalFormat
        formatter.minimumFractionDigits = 2
        formatter.maximumFractionDigits = 2
        formatter.positivePrefix = "R$ "
        formatter.negativePrefix = "- R$ "

        val customSymbols = DecimalFormatSymbols(Locale("pt", "BR"))
        customSymbols.decimalSeparator = ','
        customSymbols.groupingSeparator = '.'

        formatter.decimalFormatSymbols = customSymbols

        return (formatter.format(value))
    }

    fun validateCallPhone(
        activity: Activity,
        numero: String,
        screeName: String,
    ) {
        AlertDialogCustom.Builder(activity, screeName)
            .setTitle(activity.getString(R.string.meus_recebimentos_msg_ligar))
            .setMessage(numero + "?").setBtnRight(activity.getString(R.string.ok))
            .setBtnLeft(activity.getString(R.string.cancelar))
            .setOnclickListenerRight { callPhone(activity, numero) }
            .setOnclickListenerLeft(View.OnClickListener { return@OnClickListener }).show()
    }

    fun convertToPercent(value: Double?): String {
        return value?.let {
            convertToPercent(it)
        } ?: ""
    }

    fun convertToPercent(value: Double): String {
        return ("$value%").replace(".", ",")
    }

    fun callPhone(
        activity: Activity,
        phone: String?,
        skipRationaleRequest: Boolean = true,
    ) {
        if (phone.isNullOrBlank()) return

        Dexter
            .withActivity(activity)
            .withPermission(Manifest.permission.CALL_PHONE)
            .withListener(
                object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        if (ContextCompat.checkSelfPermission(
                                activity,
                                Manifest.permission.CALL_PHONE,
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            val callIntent = Intent(Intent.ACTION_CALL)
                            callIntent.data = Uri.parse("tel:" + phone.trim { it <= ' ' })
                            callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            activity.startActivity(callIntent)
                        }
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        Toast.makeText(
                            activity,
                            "É necessário habilitar a permissão para fazer ligações",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest,
                        token: PermissionToken,
                    ) {
                        if (skipRationaleRequest) {
                            token.cancelPermissionRequest()
                        } else {
                            token.continuePermissionRequest()
                        }
                    }
                },
            ).check()
    }

    fun isNetworkAvailable(activity: Activity): Boolean {
        val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else {
            false
        }
    }

    fun isNetworkAvailable(activity: Context): Boolean {
        val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else {
            false
        }
    }

    @SuppressLint("NewApi")
    fun authorization(): String {
        val id = BuildConfig.CLIENT_ID
        val secret = BuildConfig.CLIENT_SECRET

        val auth = "$id:$secret"
        val encodedParam = Base64.encodeToString(auth.toByteArray(), Base64.NO_WRAP)
        return "Basic $encodedParam"
    }

    fun sendEmail(
        activity: Activity,
        email: String?,
    ) {
        if (email == null || email.isEmpty()) {
            return
        }
        val mailIntent = Intent(Intent.ACTION_SENDTO)
        mailIntent.data = Uri.parse("mailto:$email")
        try {
            activity.startActivity(mailIntent)
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(
                activity,
                "É necessário ter um aplicativo de e-mail instalado.",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    fun openCall(
        activity: Activity,
        tel: String?,
    ) {
        try {
            if (tel.isNullOrEmpty()) return
            val urlIntent = Intent(Intent.ACTION_VIEW)
            urlIntent.data = Uri.parse(activity.getString(R.string.uri_parse_link_tel, tel))
            activity.startActivity(urlIntent)
        } catch (error: Exception) {
            Toast.makeText(
                activity,
                activity.getString(R.string.unexpected_error_occurred),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    fun openBrowser(
        activity: Activity,
        url: String?,
    ) {
        try {
            if (url.isNullOrEmpty()) return
            val urlIntent = Intent(Intent.ACTION_VIEW)
            urlIntent.data = Uri.parse(url)
            activity.startActivity(urlIntent)
        } catch (error: Exception) {
            Toast.makeText(
                activity,
                activity.getString(R.string.unexpected_error_occurred_open_browser),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    fun openLink(
        activity: Activity,
        link: String,
    ) {
        Intent(Intent.ACTION_VIEW).also {
            it.data = Uri.parse(link)
        }.let {
            activity.startActivity(it)
        }
    }

    fun clipboardHasPlainText(context: Context): Boolean {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val isClipboardPlainText =
            clipboard.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true
        val isNullOrEmpty = clipboard.primaryClip?.getItemAt(ZERO)?.text.isNullOrEmpty()
        return clipboard.hasPrimaryClip() && isClipboardPlainText && isNullOrEmpty.not()
    }

    fun pastePlainTextFromClipboard(context: Context): String {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        return if (clipboardHasPlainText(context)) {
            clipboard.primaryClip?.getItemAt(ZERO)?.text?.toString().orEmpty()
        } else {
            EMPTY
        }
    }

    fun copyToClipboard(
        context: Context,
        copyText: String,
        showMessage: Boolean = true,
    ) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(LinkPaymentCreatedFragment.URL, copyText)
        clipboard.setPrimaryClip(clip)

        if (showMessage) Toast.makeText(context, R.string.link_copiado, Toast.LENGTH_SHORT).show()
    }

    fun disableCopyToClipboard() =
        object : ActionMode.Callback {
            override fun onCreateActionMode(
                mode: ActionMode?,
                menu: Menu?,
            ) = false

            override fun onPrepareActionMode(
                mode: ActionMode?,
                menu: Menu?,
            ) = false

            override fun onActionItemClicked(
                mode: ActionMode?,
                item: MenuItem?,
            ) = false

            override fun onDestroyActionMode(mode: ActionMode?) {}
        }

    fun getLoginType(userInputType: Int): String {
        return when (userInputType) {
            USER_INPUT_EC_NUMBER -> USER_INPUT_EC_TRACK
            USER_INPUT_CPF -> USER_INPUT_CPF_TRACK
            USER_INPUT_EMAIL -> USER_INPUT_EMAIL_TRACK
            else -> USER_INPUT_INTERN_TRACK
        }
    }
}

fun Any?.notNull(f: () -> Unit) {
    if (this != null) {
        f()
    }
}

inline fun <reified T> Any?.tryCast(block: T.() -> Unit): Unit? {
    return if (this is T) {
        block()
    } else {
        null
    }
}

fun Context.convertDpToPixel(dp: Float): Int {
    val resources = this.resources
    val metrics = resources.displayMetrics
    return (dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}

fun enableFlagSecure(window: Window) {
    if (BuildConfig.FLAVOR.contains("store")) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE,
        )
    }
}

fun getDateCurrency(mask: String = FORMAT_DATE_PORTUGUESE): String {
    val formatData = SimpleDateFormat(mask)
    return formatData.format(Date())
}

fun getDoubleValueForMoneyInput(editInput: TypefaceEditTextView): Double? {
    return try {
        if (TextUtils.isEmpty(editInput.text).not()) {
            return editInput.text.toString()
                .moneyToBigDecimalValue().toDouble()
        }

        return null
    } catch (ex: NumberFormatException) {
        null
    }
}

fun getDoubleValueForMoneyInput(editInput: AppCompatEditText): Double? {
    return try {
        if (TextUtils.isEmpty(editInput.text).not()) {
            return editInput.text.toString()
                .moneyToBigDecimalValue().toDouble()
        }

        return null
    } catch (ex: NumberFormatException) {
        null
    }
}

fun messageError(
    error: ErrorMessage?,
    activity: Activity,
    @StringRes defaultMessage: Int = R.string.business_error,
): String =
    if (error != null && error.message.isNotEmpty() && error.message != activity.getString(R.string.error_generic)) {
        error.message
    } else {
        activity.getString(defaultMessage)
    }

fun validateMessageErrorPix(
    error: ErrorMessage?,
    activity: Activity,
    defaultMessage: String,
): String =
    when (error?.message) {
        null -> defaultMessage
        EMPTY -> defaultMessage
        activity.getString(R.string.error_generic) -> defaultMessage
        else -> error.message
    }

fun processErrorMessage(
    errorMessage: ErrorMessage?,
    defaultMessage: String,
    newMessage: String,
): ErrorMessage {
    var error = errorMessage
    if (error == null || error.message == ErrorMessage().message || error.message == defaultMessage) {
        error = ErrorMessage()
        error.message = newMessage
    }
    return error
}

fun captureEmailDomain(email: String?): String {
    return email?.split(AT_SIGN)?.let {
        if (it.size == TWO) {
            AT_SIGN + it[ONE]
        } else {
            EMPTY
        }
    } ?: EMPTY
}

fun getErrorMessage(
    errorMessage: ErrorMessage?,
    defaultMessage: String,
    newMessage: String,
): String {
    return if (errorMessage?.message == defaultMessage) {
        newMessage
    } else {
        errorMessage?.message ?: newMessage
    }
}

fun Activity.getErrorMessage(
    error: ErrorMessage?,
    newMessage: String,
): String =
    messageError(
        processErrorMessage(
            error,
            getString(R.string.business_error),
            newMessage,
        ),
        this,
    )

fun setFullHeight(bottomSheet: View?) {
    bottomSheet?.let { view ->
        val layoutParams = view.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        view.layoutParams = layoutParams
    }
}

fun setupBottomSheet(
    dialog: Dialog?,
    action: () -> Unit = {},
    isFullScreen: Boolean = false,
    isShowShadow: Boolean = true,
    disableShapeAnimations: Boolean = false,
) {
    if (isShowShadow.not()) {
        dialog?.window?.clearFlags(FLAG_DIM_BEHIND)
    }
    dialog?.setOnShowListener {
        val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet) as? FrameLayout

        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(bottomSheet)
            if (isFullScreen) setFullHeight(bottomSheet)

            if (disableShapeAnimations) {
                behavior.disableShapeAnimations()
            }

            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = ZERO

            behavior.addBottomSheetCallback(
                object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(
                        bottomSheet: View,
                        newState: Int,
                    ) {
                        if (newState >= FOUR) action.invoke()
                    }

                    override fun onSlide(
                        bottomSheet: View,
                        slideOffset: Float,
                    ) {
                    }
                },
            )
        }
    }
}

fun Toast.showCustomToast(
    message: String,
    activity: Activity,
    @ColorRes textColor: Int = R.color.white,
    @DrawableRes leadingIcon: Int? = null,
    @ColorRes leadingIconColor: Int? = null,
    @DrawableRes trailingIcon: Int? = null,
    @ColorRes trailingIconColor: Int? = null,
    @ColorRes backgroundColor: Int = R.color.success_500,
    @DimenRes cornerRadius: Int = R.dimen.dimen_8dp,
) {
    activity.layoutInflater.inflate(
        R.layout.custom_toast,
        activity.findViewById(R.id.toastContainer),
    ).apply {
        tvToastText?.apply {
            text = message
            setTextColor(context.getColor(textColor))
        }

        leadingIcon?.let {
            ivLeadingIcon.visible()
            ivLeadingIcon?.setImageResource(it)
        }

        leadingIconColor?.let {
            ivLeadingIcon?.setColorFilter(context.getColor(it), PorterDuff.Mode.SRC_IN)
        }

        trailingIcon?.let {
            ivTrailingIcon.visible()
            ivTrailingIcon?.setImageResource(it)
        }

        trailingIconColor?.let {
            ivTrailingIcon?.setColorFilter(context.getColor(it), PorterDuff.Mode.SRC_IN)
        }

        clContent?.setCustomDrawable {
            radius = cornerRadius
            solidColor = backgroundColor
        }

        setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, ZERO, ZERO)
        duration = Toast.LENGTH_LONG
        view = this
        show()
    }
}

fun showGenericSnackBar(
    anchorView: View,
    message: String,
    @DrawableRes iconImage: Int,
    durationInSeconds: Int = 2,
    closeCallback: (() -> Unit)? = null
) {
    val binding = CustomSnackGenericBinding.inflate(LayoutInflater.from(anchorView.context))
    binding.tvToastText.text = message

    binding.ivTypeIcon.setImageResource(iconImage)

    Snackbar.make(anchorView, "", Snackbar.LENGTH_INDEFINITE).apply {
        view.setBackgroundColor(anchorView.context.getColor(android.R.color.transparent))
        view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).visibility =
            View.GONE

        val snackBarLayout = view as Snackbar.SnackbarLayout
        snackBarLayout.addView(binding.root, 0)

        binding.btClose.setOnClickListener {
            closeCallback?.invoke()
            this.dismiss()
        }

        setAnchorView(anchorView)
        val params = snackBarLayout.layoutParams as ViewGroup.MarginLayoutParams
        params.bottomMargin = anchorView.context.resources.getDimensionPixelSize(R.dimen.dimen_8dp)
        snackBarLayout.layoutParams = params

        show()

        if (durationInSeconds > 0) {
            Handler(Looper.getMainLooper()).postDelayed({
                closeCallback?.invoke()
                this.dismiss()
            }, durationInSeconds * 1000L)
        }
    }
}

fun Toast.showSuccess(
    message: String,
    activity: Activity,
) {
    this.showCustomToast(
        message,
        activity = activity,
        trailingIcon = R.drawable.ic_check_toast,
    )
}

fun TextView?.setLeftDrawable(drawable: Drawable?) {
    this?.setCompoundDrawables(drawable, null, null, null)
}

fun TextView?.setRightDrawable(drawable: Drawable?) {
    this?.setCompoundDrawables(null, null, drawable, null)
}

fun Bitmap?.saveFile(pictureFile: File): Boolean {
    try {
        val stream = FileOutputStream(pictureFile)
        this?.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()
        return true
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
    }
    return false
}

fun View.takeScreenshot(): Uri? {
    val bitmap =
        Bitmap.createBitmap(
            this.width,
            this.height,
            Bitmap.Config.ARGB_8888,
        )
    val canvas = Canvas(bitmap)
    this.draw(canvas)

    val dir = this.context.cacheDir
    val file = File(dir, SCREENSHOT)
    file.mkdir()

    val path = "$SCREENSHOT/${UUID.randomUUID()}$PNG"
    val screenshotFile = File(dir, path)
    bitmap.saveFile(screenshotFile)

    return FileProvider.getUriForFile(
        this.context,
        AUTHORITY_PROVIDER,
        screenshotFile,
    )
}

fun keyTypes(
    context: Context,
    isEVP: Boolean = false,
    isBranch: Boolean = false,
    isCNPJ: Boolean = true,
): List<PixKeyType> {
    val list =
        arrayListOf(
            PixKeyType(
                type = PixKeyTypeEnum.CNPJ,
                title = context.getString(R.string.text_pix_key_type_document),
                image = R.drawable.ic_document_key_pix,
            ),
            PixKeyType(
                type = PixKeyTypeEnum.PHONE,
                title = context.getString(R.string.text_pix_my_keys_phone),
                image = R.drawable.ic_phone_key_pix,
            ),
            PixKeyType(
                type = PixKeyTypeEnum.EMAIL,
                title = context.getString(R.string.text_pix_my_keys_email),
                image = R.drawable.ic_email_key_pix,
            ),
        )

    if (isEVP) {
        list.add(
            PixKeyType(
                type = PixKeyTypeEnum.EVP,
                title = context.getString(R.string.text_pix_key_type_random),
                image = R.drawable.ic_random_key_pix,
            ),
        )
    }

    if (isBranch) {
        list.add(
            PixKeyType(
                type = PixKeyTypeEnum.ACCOUNT,
                title = context.getString(R.string.text_pix_key_type_account),
                image = R.drawable.ic_account_key_pix,
            ),
        )
    }

    if (isCNPJ.not()) list.removeAt(ZERO)

    return list
}

fun getKeyType(
    key: String? = null,
    type: String?,
    isType: Boolean = false,
): String {
    return if (ValidationUtils.isCPF(key)) {
        PixKeyTypeEnum.CPF.name
    } else {
        if (ValidationUtils.isCNPJ(key)) {
            PixKeyTypeEnum.CNPJ.name
        } else {
            when (type) {
                PixKeyTypeEnum.PHONE.name -> if (isType) PixKeyTypeEnum.PHONE.name else PixKeyTypeEnum.PHONE.key
                PixKeyTypeEnum.EMAIL.name -> if (isType) PixKeyTypeEnum.EMAIL.name else PixKeyTypeEnum.EMAIL.key
                PixKeyTypeEnum.EVP.name -> if (isType) PixKeyTypeEnum.EVP.name else PixKeyTypeEnum.EVP.key
                else -> EMPTY
            }
        }
    }
}

fun getKey(
    key: String,
    type: String?,
    isCodeCountry: Boolean = false,
): String {
    return when (type) {
        PixKeyTypeEnum.CPF.name, PixKeyTypeEnum.CNPJ.name -> key.clearCNPJMask()
        PixKeyTypeEnum.PHONE.name -> {
            val clearKey = key.clearCNPJMask()
            if (isCodeCountry) {
                if (clearKey.first().toString() == WITH_COUNTRY_CODE) {
                    clearKey
                } else {
                    "$COUNTRY_CODE$clearKey"
                }
            } else {
                clearKey
            }
        }

        PixKeyTypeEnum.EMAIL.name -> key
        PixKeyTypeEnum.EVP.name -> key
        else -> EMPTY
    }
}

fun getFormattedKey(
    key: String,
    type: String?,
    isMask: Boolean = true,
): String {
    return when (type) {
        PixKeyTypeEnum.CPF.name -> {
            val value = EditTextHelper.cpfMaskFormatter(key).formattedText.string
            if (isMask) {
                value.maskCPF()
            } else {
                value
            }
        }

        PixKeyTypeEnum.CNPJ.name -> {
            FormHelper.maskFormatter(key, CNPJ_MASK_COMPLETE_FORMAT).formattedText.string
        }

        PixKeyTypeEnum.PHONE.name -> {
            if (key.first().toString() == WITH_COUNTRY_CODE) {
                key.substring(THREE, key.length)
                    .phone()
            } else {
                key.phone()
            }
        }

        PixKeyTypeEnum.EMAIL.name -> key
        PixKeyTypeEnum.EVP.name -> key
        else -> EMPTY
    }
}

fun getIconKeyPix(keyType: String?): Int {
    return when (keyType) {
        PixKeyTypeEnum.CPF.name, PixKeyTypeEnum.CNPJ.name -> R.drawable.ic_cnh_pix_16dp
        PixKeyTypeEnum.EMAIL.name -> R.drawable.ic_email_pix_16dp
        PixKeyTypeEnum.PHONE.name -> R.drawable.ic_phone_key_pix_16_dp
        PixKeyTypeEnum.EVP.name -> R.drawable.ic_random_key_pix_16dp
        else -> R.drawable.ic_cnh_pix_16dp
    }
}

fun shareText(
    value: String,
    context: Context,
) {
    Intent().apply {
        action = Intent.ACTION_SEND
        type = TEXT_PLAIN
        putExtra(Intent.EXTRA_TEXT, value)
        context.startActivity(createChooser(this, SHARE_WITH))
    }
}

fun shareBitmap(
    bitmap: Bitmap,
    context: Context,
) {
    val timeStamp = getDateTimeInstance().format(Date())
    val imagePath =
        MediaStore.Images.Media.insertImage(
            context.contentResolver,
            bitmap,
            "${SHARE_QR_CODE_FILE_PREFIX}$timeStamp",
            null,
        )
    Intent().apply {
        action = Intent.ACTION_SEND
        type = IMG
        putExtra(Intent.EXTRA_STREAM, Uri.parse(imagePath))
        context.startActivity(createChooser(this, SHARE_WITH))
    }
}

fun sendEmail(
    activity: Activity,
    email: String?,
    subject: String? = null,
    content: String? = null,
) {
    if (email.isNullOrEmpty()) {
        return
    }

    val mailIntent = Intent(Intent.ACTION_SEND)
    mailIntent.type = MAIL_TYPE
    mailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
    subject?.let { mailIntent.putExtra(Intent.EXTRA_SUBJECT, it) }
    content?.let { mailIntent.putExtra(Intent.EXTRA_TEXT, it) }

    try {
        activity.startActivity(mailIntent)
    } catch (ex: ActivityNotFoundException) {
        Toast.makeText(
            activity,
            activity.getString(R.string.toast_email_app_required),
            Toast.LENGTH_SHORT,
        ).show()
    }
}

fun getKeyTypeName(type: String?): String {
    return when (type) {
        PixKeyTypeEnum.CPF.name -> PixKeyTypeEnum.CPF.key
        PixKeyTypeEnum.CNPJ.name -> PixKeyTypeEnum.CNPJ.key
        PixKeyTypeEnum.PHONE.name -> PixKeyTypeEnum.PHONE.key
        PixKeyTypeEnum.EMAIL.name -> PixKeyTypeEnum.EMAIL.key
        PixKeyTypeEnum.EVP.name -> KEY_EVP
        else -> EMPTY
    }
}

fun hasValidKeyToQRCode(myKeys: List<MyKey>?): Boolean {
    myKeys?.let {
        it.forEach { key ->
            if (key.claimType != PixClaimTypeEnum.PORTABILITY.name && key.claimType != PixClaimTypeEnum.OWNERSHIP.name) return true
        }
    }
    return false
}

fun formattedDocument(
    document: String,
    type: String?,
): String {
    return when (type) {
        PixOwnerTypeEnum.NATURAL_PERSON.name, PixOwnerTypeEnum.NATURAL_PERSON.owner -> {
            val value = EditTextHelper.cpfMaskFormatter(document)
            value.formattedText.string.maskCPF()
        }

        PixOwnerTypeEnum.LEGAL_PERSON.name, PixOwnerTypeEnum.LEGAL_PERSON.owner ->
            FormHelper.maskFormatter(
                document,
                CNPJ_MASK_COMPLETE_FORMAT,
            ).formattedText.string

        else -> EMPTY
    }
}

fun getTotalValueChange(
    value: Double?,
    change: Double?,
): Double {
    val total =
        if (value == null || change == null) {
            BigDecimal.ZERO
        } else {
            BigDecimal.valueOf(value).add(BigDecimal.valueOf(change))
        }

    return total.setScale(TWO, BigDecimal.ROUND_HALF_UP).toDouble()
}

fun getTitlePix(isTrustedDestination: Boolean): Int =
    if (isTrustedDestination) {
        R.string.pix_text_my_limits_add_new_trusted_destination_toolbar
    } else {
        R.string.toolbar_screen_transfer_pix
    }

fun dialogLocationActivation(
    context: Activity,
    fragmentManager: FragmentManager,
) {
    CieloAskQuestionDialogFragment.Builder()
        .title(context.getString(R.string.permission_needed_Localization_title))
        .message(context.getString(R.string.permission_needed_Localization_message))
        .cancelTextButton(context.getString(R.string.cancelar))
        .setCancelButtonBackgroundResource(ResourcesCompat.ID_NULL)
        .positiveTextButton(context.getString(R.string.permission_location_denied_config)).build()
        .let {
            it.onPositiveButtonClickListener =
                View.OnClickListener {
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            it.show(
                fragmentManager,
                context.getString(R.string.bottom_sheet_generic),
            )
        }
}

fun firstWord(
    input: String?,
    default: String,
) = input?.split(ONE_SPACE)?.toTypedArray()?.get(ZERO) ?: default

fun firstWordCapitalize(
    input: String?,
    default: String,
) = input?.substringBefore(ONE_SPACE)?.toLowerCasePTBR()?.capitalizePTBR() ?: default

fun createError(
    code: String,
    message: String?,
): ErrorMessage? {
    message?.let {
        val error = ErrorMessage()
        error.message = it
        error.code = code
        return error
    }
    return null
}

fun getFormattedUsername(
    username: String,
    inputType: Int,
): String {
    return if (inputType != USER_INPUT_EMAIL &&
        inputType == USER_INPUT_CPF
    ) {
        username.removeNonNumbers()
    } else {
        username
    }
}

fun capitalize(str: String): String {
    return str.trim().lowercase().split(ChargebackConstants.BROKE.toRegex())
        .joinToString(ChargebackConstants.SPACE) { it ->
            it.replaceFirstChar {
                if (it.isLowerCase()) {
                    it.titlecase(
                        Locale.getDefault(),
                    )
                } else {
                    it.toString()
                }
            }
        }
}

fun Context.getNewErrorMessage(
    error: NewErrorMessage? = null,
    @StringRes newMessage: Int,
): String {
    return if (error?.message == DEFAULT_ERROR_MESSAGE) {
        this.getString(newMessage)
    } else {
        error?.message ?: this.getString(newMessage)
    }
}

fun Context.isDevMode(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Settings.Secure.getInt(
            this.contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
            ZERO,
        ) != ZERO
    } else {
        Settings.Secure.getInt(
            this.contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
            ONE,
        ) != ZERO
    }
}

fun Activity.deviceHasNFC(): Boolean {
    val manager = this.getSystemService(Context.NFC_SERVICE) as? NfcManager
    return manager?.defaultAdapter != null
}

fun getVersionAndroid() = Build.VERSION.SDK_INT

inline fun <reified T : Parcelable?> Intent.getParcelableCustom(name: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.getParcelableExtra(name, T::class.java)
    } else {
        this.getParcelableExtra<T>(name)
    }
}

fun NewErrorMessage.getErrorMessage(
    @StringRes message: Int,
): Any {
    return if (this.message != DEFAULT_ERROR_MESSAGE) {
        this.message
    } else {
        message
    }
}

fun Context.formatterErrorMessage(message: Any): String {
    return if (message is Int) {
        this.getString(message)
    } else {
        message as String
    }
}

fun isRoot(): Boolean {
    UserPreferences.getInstance().userInformation?.let { me ->
        when (me.activeMerchant.hierarchyLevel) {
            POINT_OF_SALE -> {
                return false
            }

            else -> {
                return true
            }
        }
    }
    return false
}

fun merchantType() =
    UserPreferences.getInstance().userInformation?.activeMerchant?.cnpj?.number?.let { itDoc ->
        when {
            ValidationUtils.isCNPJ(itDoc) -> MerchantType.PJ
            ValidationUtils.isCPF(itDoc) -> MerchantType.PF
            else -> Unit
        }
    }

enum class MerchantType {
    PF,
    PJ,
}

fun hasRepeatedDigitsOrDozens(passwordValue: String): Boolean {
    val repeatedDozens = Regex("(\\d{2})\\1")
    val sequentialDigits = setOf("123456", "234567", "345678", "456789", "567890",
        "678901", "789012", "890123", "901234", "012345")

    return repeatedDozens.containsMatchIn(passwordValue) || sequentialDigits.any { passwordValue.contains(it) }
}

fun hasDozensSequential(passwordValue: String): Boolean {
    val dozensSequential = Regex("(?:(\\d{2})(?=\\d{2}\\d{2})|\\d{2})")

    val match = dozensSequential.findAll(passwordValue)
    val numbers = match.map { it.value.toInt() }.toList()

    if (numbers.size != br.com.cielo.libflue.util.THREE) return false

    val first = numbers[br.com.cielo.libflue.util.ZERO]
    val second = numbers[br.com.cielo.libflue.util.ONE]
    val third = numbers[br.com.cielo.libflue.util.TWO]
    return (second - first == TEN) && (third - second == TEN)
}

fun hasMirroredDigits(passwordValue: String): Boolean {
    val mirroedDigits = Regex("(\\d)(\\d)(\\d)\\3\\2\\1")
    return mirroedDigits.matches(passwordValue)
}

fun isDrawingKeyboard(input: String): Boolean {
    val numericKeyboardPatterns = listOf(
        "147896", "478963", "789632", "896321", "963214",
        "632147", "321478", "214789", "123698", "236987", "369874", "698741", "987412",
        "874123", "741236", "412369", "159357", "159753", "951357", "951753", "753951",
        "753159", "357951", "357159"
    )

    return if (numericKeyboardPatterns.contains(input)) {
        true
    } else {
        false
    }
}

fun cameraIsEnabled(context: Context): Boolean =
    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

fun openAppSettings(context: Context) {
    val intent =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data =
                Uri.fromParts(
                    context.getString(R.string.permission_needed_package),
                    context.packageName,
                    null,
                )
        }

    context.startActivity(intent)
}
