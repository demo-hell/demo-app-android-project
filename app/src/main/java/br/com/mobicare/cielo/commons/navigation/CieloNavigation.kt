package br.com.mobicare.cielo.commons.navigation

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.View.TEXT_ALIGNMENT_TEXT_START
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.screen.HandlerViewBuilderFluiV2
import br.com.cielo.libflue.screen.HandlerViewFluiV2
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE_NEGATIVE
import br.com.mobicare.cielo.commons.constants.SIXTEEN
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity

interface CieloNavigation {
    fun setTextButton(text: String) {}

    fun setTextFirstButton(text: String) {}

    fun setTextToolbar(title: String) {}

    fun setupToolbar(
        title: String = EMPTY,
        isCollapsed: Boolean = false,
    ) {}

    fun setupToolbar(
        title: String = EMPTY,
        isCollapsed: Boolean = false,
        subtitle: String? = null,
    ) {}

    fun setNavigationListener(listener: CieloNavigationListener)

    fun setColorBackgroundButton(
        @ColorRes colorRes: Int,
    ) {}

    fun setColorBackgroundAnimatedProgressView(
        @ColorRes colorRes: Int = R.color.transparent_90,
    ) {}

    fun getSavedData(): Bundle? = null

    fun getData(): Any? = null

    fun getDataIntent(): Intent? = null

    fun getNavController(): NavController? = null

    fun saveData(bundle: Bundle) {}

    fun clearData() {}

    fun changeIconFilter(hasFilter: Boolean) {}

    fun startHelpCenter(tagKey: String) {}

    fun successTermPix() {}

    fun onHelpButtonClicked() {}

    fun enableButton(isEnabled: Boolean = false) {}

    fun enableButtonToolbar(
        enableButtonFinish: Boolean,
        textButton: String,
    ) {}

    fun showToolbar(isShow: Boolean = true) {}

    fun showBackIcon(isShow: Boolean = true) {}

    fun showButton(isShow: Boolean = false) {}

    fun showFirstButton(isShow: Boolean = false) {}

    fun showLoading(isShow: Boolean = true) {
        showLoading(isShow, null)
    }

    fun showContent(isShow: Boolean = true) {
        showContent(isShow, null, null)
    }

    fun showLoading(
        isShow: Boolean = true,
        @StringRes message: Int? = null,
        vararg messageArgs: String,
    ) {
    }

    fun hideLoading() {}

    fun showLoading(
        isShow: Boolean,
        @DrawableRes image: Int?,
        isDocumentCapture: Boolean,
        isSuccess: Boolean? = null,
    ) {
    }

    fun showAnimatedLoading(
        @StringRes message: Int? = null,
    ) {}

    fun showAnimatedLoadingError(
        @StringRes message: Int? = null,
        onAction: () -> Unit = {},
    ) {}

    fun showAnimatedLoadingAlert(
        @StringRes message: Int? = null,
        onAction: () -> Unit = {},
    ) {}

    fun showAnimatedLoadingSuccess(
        @StringRes message: Int? = null,
        onAction: () -> Unit = {},
    ) {}

    fun hideAnimatedLoading() {}

    fun changeAnimatedLoadingText(
        @StringRes message: Int? = null,
    ) {}

    fun showContent(
        isShow: Boolean = true,
        @StringRes loadingSuccessMessage: Int? = null,
        loadingSuccessCallback: (() -> Unit)? = null,
        vararg messageArgs: String,
    ) {
    }

    fun showSuccess() {}

    fun showAlert(
        title: String? = null,
        message: String,
    ) {}

    fun showDialog(
        @StringRes title: Int = R.string.screen_authorize_cielo_facilita_confirmation_alert_title,
        @StringRes message: Int = R.string.screen_authorize_cielo_facilita_confirmation_alert_body,
        @StringRes positiveTextButton: Int = R.string.screen_authorize_cielo_facilita_confirmation_alert_btn_confirm,
        @StringRes cancelTextButton: Int = R.string.cancelar,
        positiveButtonPressed: () -> Unit,
        cancelButtonPressed: () -> Unit,
    ) {
    }

    fun showContainerButton(isShow: Boolean = false) {}

    fun showHelpButton(isShow: Boolean = false) {}

    fun showCloseButton(isShow: Boolean = false) {}

    fun showBackButton(isShow: Boolean = false) {}

    fun showFilterButton(isShow: Boolean) {}

    fun showMenuButton(isShow: Boolean) {}

    fun showSettingsButton(isShow: Boolean) {}

    fun showIneligibleUser(message: String = "Seu estabelecimento não é elegível, por isso você não pode acessar este item.") {}

    fun showMFAStatusPending() {}

    fun showMFAStatusErrorPennyDrop() {}

    fun showError(error: ErrorMessage?) {}

    fun showError(
        textButton: String? = null,
        error: ErrorMessage?,
    ) {}

    fun showBottomSheetError(error: ErrorMessage?) {}

    fun showErrorBottomSheet(
        textButton: String? = null,
        @StringRes textMessage: Int? = null,
        error: ErrorMessage? = null,
        title: String? = null,
        isFullScreen: Boolean = true,
    ) {
    }

    fun showErrorBottomSheet(
        textButton: String? = null,
        title: String? = null,
        subtitle: String? = null,
        callToActionButton: (() -> Unit),
        callToActionSwiped: (() -> Unit),
        isFullScreen: Boolean = true,
    ) {
    }

    fun showError(
        title: String,
        message: String,
        textButton: String,
        @DrawableRes idRes: Int,
        listener: View.OnClickListener? = null,
    ) {
    }

    fun showCustomBottomSheet(
        image: Int? = null,
        title: String? = null,
        message: String? = null,
        bt1Title: String? = null,
        bt2Title: String? = null,
        bt1Callback: (() -> Boolean)? = null,
        bt2Callback: (() -> Boolean)? = null,
        closeCallback: (() -> Unit)? = null,
        isCancelable: Boolean = true,
        isPhone: Boolean = true,
    ) {
    }

    fun showCustomBottomSheet(
        @DrawableRes image: Int? = null,
        title: String? = null,
        message: String? = null,
        bt1Title: String? = null,
        bt2Title: String? = null,
        bt1Callback: (() -> Boolean)? = null,
        bt2Callback: (() -> Boolean)? = null,
        closeCallback: (() -> Unit)? = null,
        isCancelable: Boolean = true,
        isPhone: Boolean = true,
        titleBlack: Boolean = false,
    ) {
    }

    fun showCustomBottomSheet(
        image: Int? = null,
        title: Int,
        message: String? = null,
        bt1Title: Int? = null,
        bt2Title: Int? = null,
        bt1Callback: (() -> Boolean)? = null,
        bt2Callback: (() -> Boolean)? = null,
        closeCallback: (() -> Unit)? = null,
        isCancelable: Boolean = true,
        isFullScreen: Boolean = false,
        isPhone: Boolean = false,
    ) {
    }

    fun showCustomBottomSheet(
        image: Int,
        title: Int,
        message: Int,
        bt1Title: Int? = null,
        bt2Title: Int? = null,
        bt1Show: Boolean = false,
        bt2Show: Boolean = false,
        bt1Callback: (() -> Unit)? = null,
        bt2Callback: (() -> Unit)? = null,
        closeCallback: (() -> Unit)? = null,
        isCancelable: Boolean = true,
        isFullScreen: Boolean = true,
    ) {
    }

    fun showCustomBottomSheet(
        logScreenEvent: () -> Unit = {},
        onBtnSecond: () -> Unit,
        onSwipedClose: () -> Unit,
    ) {
    }

    fun showWarningBottomSheet(
        @DrawableRes image: Int = R.drawable.ic_07,
        message: String,
        title: String? = null,
        bt1Title: String? = null,
        bt2Title: String? = null,
        isShowBt1: Boolean = false,
        isShowBt2: Boolean = true,
        bt1Callback: () -> Unit = {},
        bt2Callback: () -> Unit = {},
        closeCallback: () -> Unit = {},
        isFullScreen: Boolean = true,
        isPhone: Boolean = false,
    ) {
    }

    fun showCustomMessage(
        image: Int? = null,
        title: Int? = null,
        message: Int? = null,
        bt1Title: Int? = null,
        bt2Title: Int? = null,
        bt1Callback: (() -> Boolean)? = null,
        bt2Callback: (() -> Boolean)? = null,
        closeCallback: (() -> Unit)? = null,
        isCancelable: Boolean = true,
        isPhone: Boolean = true,
    ) {
    }

    fun destroyCustomBottomSheet() {}

    fun showError(onAction: () -> Unit = {}) {}

    fun nfcIsNotSupported(gaFlowDetails: String = EMPTY) {}

    fun androidIsNotSupported(gaFlowDetails: String = EMPTY) {}

    fun retryConnectCardReader(
        onAction: () -> Unit = {},
        @StringRes btnLabel: Int = R.string.text_try_again_label,
    ) {
    }

    fun notEligibleForTapOnPhone() {}

    fun showCustomHandler(
        @DrawableRes contentImage: Int = R.drawable.ic_07,
        @DrawableRes headerImage: Int = R.drawable.ic_close_blue_top,
        message: String,
        title: String,
        titleAlignment: Int = TEXT_ALIGNMENT_CENTER,
        messageAlignment: Int = TEXT_ALIGNMENT_TEXT_START,
        labelFirstButton: String = EMPTY,
        labelSecondButton: String = EMPTY,
        isShowFirstButton: Boolean = false,
        isShowSecondButton: Boolean = true,
        firstButtonCallback: () -> Unit = {},
        secondButtonCallback: () -> Unit = {},
        headerCallback: () -> Unit = {},
        finishCallback: () -> Unit = {},
        isBack: Boolean = false,
        isShowButtonBack: Boolean = false,
        isShowHeaderImage: Boolean = true,
    ) {
    }

    fun showCustomErrorHandler(
        @DrawableRes contentImage: Int = R.drawable.ic_07,
        @DrawableRes headerImage: Int = R.drawable.ic_close_blue_top,
        error: ErrorMessage,
        title: String,
        titleAlignment: Int = TEXT_ALIGNMENT_CENTER,
        messageAlignment: Int = TEXT_ALIGNMENT_TEXT_START,
        labelFirstButton: String = EMPTY,
        labelSecondButton: String = EMPTY,
        isShowFirstButton: Boolean = false,
        isShowSecondButton: Boolean = true,
        firstButtonCallback: () -> Unit = {},
        secondButtonCallback: () -> Unit = {},
        headerCallback: () -> Unit = {},
        finishCallback: () -> Unit = {},
        isBack: Boolean = false,
        isShowButtonBack: Boolean = false,
        isShowHeaderImage: Boolean = true,
    ) {
    }

    fun showCustomHandlerView(
        @DrawableRes contentImage: Int = R.drawable.ic_07,
        @DrawableRes headerImage: Int = R.drawable.ic_symbol_close_brand_400_24_dp,
        @StyleRes titleStyle: Int = ONE_NEGATIVE,
        @StyleRes messageStyle: Int = ONE_NEGATIVE,
        message: String,
        title: String,
        messageMargin: Int = SIXTEEN,
        titleAlignment: Int = TEXT_ALIGNMENT_CENTER,
        messageAlignment: Int = TEXT_ALIGNMENT_TEXT_START,
        labelFirstButton: String = EMPTY,
        labelSecondButton: String = EMPTY,
        isShowButtonBack: Boolean = false,
        isShowButtonClose: Boolean = false,
        isShowFirstButton: Boolean = false,
        isShowSecondButton: Boolean = true,
        callbackFirstButton: () -> Unit = {},
        callbackSecondButton: () -> Unit = {},
        callbackClose: () -> Unit = {},
        callbackBack: () -> Unit = {},
    ) {
    }

    fun showCustomHandlerViewWithHelp(
        @DrawableRes contentImage: Int = R.drawable.ic_07,
        message: String,
        title: String,
        messageMargin: Int = SIXTEEN,
        titleAlignment: Int = TEXT_ALIGNMENT_TEXT_START,
        messageAlignment: Int = TEXT_ALIGNMENT_TEXT_START,
        labelFirstButton: String = EMPTY,
        labelSecondButton: String = EMPTY,
        isShowFirstButton: Boolean = false,
        isShowSecondButton: Boolean = true,
        callbackFirstButton: () -> Unit = {},
        callbackSecondButton: () -> Unit = {},
    ) {
    }

    fun showHandlerViewV2(
        @StyleRes titleTextAppearance: Int = br.com.cielo.libflue.R.style.bold_montserrat_20_cloud_800_spacing_8,
        titleAlignment: Int = ConstraintLayout.TEXT_ALIGNMENT_TEXT_START,
        title: String = EMPTY,
        @StyleRes messageTextAppearance: Int = br.com.cielo.libflue.R.style.medium_montserrat_16_neutral_600_spacing_4,
        messageAlignment: Int = ConstraintLayout.TEXT_ALIGNMENT_TEXT_START,
        message: String = EMPTY,
        labelPrimaryButton: String = EMPTY,
        labelSecondaryButton: String = EMPTY,
        @DrawableRes illustration: Int = br.com.cielo.libflue.R.drawable.img_10_erro,
        cardInformationData: HandlerViewFluiV2.CardInformationData? = null,
        isShowBackButton: Boolean = false,
        isShowIconButtonEndHeader: Boolean = true,
        @DrawableRes drawableIconButtonEndHeader: Int = br.com.cielo.libflue.R.drawable.ic_symbol_close_brand_400_24_dp,
        iconButtonEndHeaderContentDescription: String = EMPTY,
        hasPhone: Boolean = false,
        onPrimaryButtonClickListener: HandlerViewBuilderFluiV2.HandlerViewListener? = null,
        onSecondaryButtonClickListener: HandlerViewBuilderFluiV2.HandlerViewListener? = null,
        onBackButtonClickListener: HandlerViewBuilderFluiV2.HandlerViewListener? = null,
        onIconButtonEndHeaderClickListener: HandlerViewBuilderFluiV2.HandlerViewListener? = null,
        onDismiss: ((Dialog?) -> Unit)? = null,
    ) {
    }

    fun goToHome() {}

    fun blockBackPressed(block: Boolean = false) {}

    fun showCards(isShow: Boolean = false) {}

    fun hideKeyboard() {}

    fun configureCollapsingToolbar(configurator: CollapsingToolbarBaseActivity.Configurator) {}

    fun configureCollapsingToolbar(configurator: CieloCollapsingToolbarLayout.Configurator) {}

    fun showAnimatedLoadingWithoutMfa(
        @StringRes message: Int? = null,
    ) {}

    fun hideAnimatedLoadingWithoutMfa() {}

    fun onAdjustSoftInput(softInputMode: Int) {}

    fun onStepChanged(
        currentStep: Int,
        showNumber: Boolean = false,
    ) {}
}
