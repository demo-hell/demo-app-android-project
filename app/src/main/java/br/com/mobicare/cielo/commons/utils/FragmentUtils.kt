package br.com.mobicare.cielo.commons.utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.widget.ViewFlipper
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy.dialog.BottomSheetGenericFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.Serializable
import java.util.concurrent.Executor

const val SCREEN_CURRENT_PATH = "br.com.cielo.screenPath"

fun Fragment.addInFrame(fragmentManager: FragmentManager?, @IdRes frameId: Int) {
    fragmentManager?.run {
        val addTransaction = this.beginTransaction()
        addTransaction.replace(frameId, this@addInFrame, this@addInFrame::class.java.simpleName)
        addTransaction.commitAllowingStateLoss()
    }
}

fun Fragment.addWithTag(fragmentManager: FragmentManager?, tag: String) {
    fragmentManager?.run fm@{
        val addTransaction = fragmentManager.beginTransaction()
        addTransaction.add(this@addWithTag, tag)
        addTransaction.commitAllowingStateLoss()
    }
}

fun Fragment.addWithAnimation(
    fragmentManager: FragmentManager?,
    @IdRes containerId: Int, backAnimation: Boolean = false
) {
    val receiverFrag = this
    fragmentManager?.run {
        val addTransaction = this.beginTransaction()
        if (!backAnimation) {
            addTransaction.setCustomAnimations(
                R.anim.slide_from_right,
                R.anim.slide_to_left
            )
        } else {
            addTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right)
        }
        addTransaction.replace(containerId, receiverFrag)
        addTransaction.commit()
        executePendingTransactions()
    }
    this.userVisibleHint = true
}

fun Fragment.addWithAppearFromBottomAnimation(fragmentManager: FragmentManager?) {
    fragmentManager?.run {
        val addTransaction = this.beginTransaction()
        addTransaction.setCustomAnimations(
            R.anim.slide_from_bottom_to_up,
            R.anim.slide_from_up_to_bottom
        )
        addTransaction.add(
            this@addWithAppearFromBottomAnimation,
            this@addWithAppearFromBottomAnimation::class.java.simpleName
        )
        addTransaction.commitAllowingStateLoss()
        executePendingTransactions()
    }
}

fun Fragment.remove(fragmentManager: FragmentManager?) {
    this.userVisibleHint = false
    fragmentManager?.run {
        val addTransaction = this.beginTransaction()
        this.popBackStack()
        addTransaction.commit()
        executePendingTransactions()
    }
}

class MainThreadExecutor : Executor {
    private val handler = Handler(Looper.getMainLooper())
    override fun execute(command: Runnable?) {
        command?.let {
            handler.post(it)
        }
    }
}

fun Fragment.addArgument(key: String, parcelableArg: Any?) {
    val params: Bundle? = if (arguments == null) {
        Bundle()
    } else {
        arguments
    }
    when (parcelableArg) {
        is Parcelable -> {
            params?.putParcelable(key, parcelableArg)
        }
        is Parcelable? -> {
            params?.putParcelable(key, parcelableArg)
        }
        is String -> {
            params?.putString(key, parcelableArg)
        }
        is Int -> {
            params?.putInt(key, parcelableArg)
        }
        is Boolean -> {
            params?.putBoolean(key, parcelableArg)
        }
        is Serializable -> {
            params?.putSerializable(key, parcelableArg)
        }
        is Serializable? -> {
            params?.putSerializable(key, parcelableArg)
        }
        else -> {
            throw TypeNotPresentException(parcelableArg?.let {
                parcelableArg::class.java.simpleName
            } ?: "none", Exception())
        }
    }
    this.arguments = params
}

fun Fragment.addMaskCPForCNPJ(textoAFormatar: String?, mask: String): String {
    var formatado = ""
    var i = 0
    // vamos iterar a mascara, para descobrir quais caracteres vamos adicionar e quando...
    for (m in mask.toCharArray()) {
        if (m != '#') { // se não for um #, vamos colocar o caracter informado na máscara
            formatado += m
            continue
        }
        // Senão colocamos o valor que será formatado
        try {
            formatado += textoAFormatar?.get(i)
        } catch (e: Exception) {
            break
        }
        i++
    }
    return formatado
}

fun Fragment.displayedChild(value: Int, vf: ViewFlipper) {
    vf.displayedChild = value
}

fun Activity.addMaskCPForCNPJ(textoAFormatar: String, mask: String): String {
    var formatado = ""
    var i = 0
    // vamos iterar a mascara, para descobrir quais caracteres vamos adicionar e quando...
    for (m in mask.toCharArray()) {
        if (m != '#') { // se não for um #, vamos colocar o caracter informado na máscara
            formatado += m
            continue
        }
        // Senão colocamos o valor que será formatado
        try {
            formatado += textoAFormatar[i]
        } catch (e: Exception) {
            break
        }
        i++
    }
    return formatado
}

/**
 * @param  nameTopBar           topbar's name
 * @param  title                title's name
 * @param  subtitle             subtitle's name
 * @param  nameBtnBottom        nameBtnBottom's name
 * @param  statusBtnClose       button's status to show if buttom is visible or gone
 * @param  statusBtnOk          button's status to show if buttom is visible or gone
 * @param  statusViewLine       line's status to show if buttom is visible or gone
 * @param  txtToolbarNameStyle  text name toolbar style
 * @param  txtTitleStyle        title's style
 * @param  txtSubtitleStyle     subtitle's style
 * @param  btnBottomStyle       btnBottom's style
 * @return                      return BottonSheetGeneric
 * @see         image           set a drawable in the view
 */
fun BaseFragment.bottomSheetGeneric(
    nameTopBar: String,
    image: Int,
    title: String,
    subtitle: String,
    nameBtnBottom: String,
    statusBtnClose: Boolean = true,
    statusBtnOk: Boolean = true,
    statusViewLine: Boolean = true,
    txtToolbarNameStyle: TextToolbaNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
    txtTitleStyle: TxtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
    txtSubtitleStyle: TxtSubTitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
    btnBottomStyle: ButtonBottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
    isResizeToolbar: Boolean = false
) = BottomSheetGenericFragment.newInstance(
    nameTopBar,
    image,
    title,
    subtitle,
    nameBtnBottom,
    statusBtnClose,
    statusBtnOk,
    statusViewLine,
    txtToolbarNameStyle,
    txtTitleStyle,
    txtSubtitleStyle,
    btnBottomStyle,
    isResizeToolbar
)

fun BaseFragment.bottomSheetGenericFlui(
    nameTopBar: String = "",
    image: Int,
    title: String,
    subtitle: String,
    nameBtn1Bottom: String = "",
    nameBtn2Bottom: String,
    statusNameTopBar: Boolean = true,
    statusTitle: Boolean = true,
    statusSubTitle: Boolean = true,
    statusImage: Boolean = true,
    statusBtnClose: Boolean = true,
    statusBtnFirst: Boolean = true,
    statusBtnSecond: Boolean = true,
    statusView1Line: Boolean = true,
    statusView2Line: Boolean = true,
    txtToolbarNameStyle: TextToolbaNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
    txtTitleStyle: TxtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
    txtSubtitleStyle: TxtSubTitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
    btn1BottomStyle: ButtonBottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
    btn2BottomStyle: ButtonBottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE_TXT_BLUE,
    isFullScreen: Boolean = false,
    isCancelable: Boolean = true,
    isPhone: Boolean = true
) = BottomSheetFluiGenericFragment.newInstance(
    nameTopBar,
    image,
    title,
    subtitle,
    nameBtn1Bottom,
    nameBtn2Bottom,
    statusNameTopBar,
    statusTitle,
    statusSubTitle,
    statusImage,
    statusBtnClose,
    statusBtnFirst,
    statusBtnSecond,
    statusView1Line,
    statusView2Line,
    txtToolbarNameStyle,
    txtTitleStyle,
    txtSubtitleStyle,
    btn1BottomStyle,
    btn2BottomStyle,
    isFullScreen,
    isCancelable,
    isPhone
)

fun Fragment.registerForActivityResultCustom(callbackResult: (ActivityResult) -> Unit): ActivityResultLauncher<Intent> {
    return this.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        callbackResult(it)
    }
}

fun <T> Fragment.flowCollectLatest(flow: Flow<T>, block: suspend (T) -> Unit) {
    lifecycleScope.launch {
        flow.collectLatest {
            block(it)
        }
    }
}