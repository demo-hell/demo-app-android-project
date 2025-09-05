package br.com.mobicare.cielo.commons.ui.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.login.analytics.LoginAnalytics

class AppUnavailableBottomSheetFragment: FullscreenBottomSheetDialog() {

    private val loginAnalytics = LoginAnalytics()
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(
            R.layout.fragment_app_unavailable_bottom_sheet,
            container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginAnalytics.logAppUnavailableScreenViewGa(getString(R.string.app_unavailable_dialog_message))
        isCancelable = false
        isDragDisabled = true
    }
}