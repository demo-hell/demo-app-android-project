package br.com.mobicare.cielo.commons.navigation

interface CieloNavigationListener {
    fun onRetry() {}

    fun onRetryBottomSheet() {}

    fun onButtonClicked(labelButton: String = "") {}

    fun onFirstButtonClicked(labelButton: String = "") {}

    fun onHelpButtonClicked() {}

    fun onCloseButtonClicked() {}

    fun onFilterButtonClicked() {}

    fun onSettingsButtonClicked() {}

    fun onMenuButtonClicked() {}

    fun onBackButtonClicked() = false

    fun onReturnAction() {}

    fun onShowDialogButtonFinish() {}

    fun onActionSwipe() {}

    fun onClickFirstButtonError() {}

    fun onClickSecondButtonError() {}

    fun onPauseActivity() {}

    fun onBackPressed() {}
}
