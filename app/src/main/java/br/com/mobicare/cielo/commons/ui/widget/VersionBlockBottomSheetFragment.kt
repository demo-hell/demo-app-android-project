package br.com.mobicare.cielo.commons.ui.widget

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.helpers.AppHelper
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationPreference
import kotlinx.android.synthetic.main.fragment_version_block_bottom_sheet.*
import kotlinx.android.synthetic.main.layout_generic_error.*
import kotlinx.android.synthetic.main.toolbar_rounded_white.*

class VersionBlockBottomSheetFragment : FullscreenBottomSheetDialog() {

    var isToForceUpdate: Boolean = false
    var onVersionTerminateListener: OnVersionTerminateListener? = null

    interface OnVersionTerminateListener {
        fun onContinueTask()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_version_block_bottom_sheet,
                container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textCommonDialogToolbarTitle?.text = SpannableStringBuilder
                .valueOf(getString(R.string.text_title_version_block_toolbar_title))

        errorHandlerCieloVersionBlock.configureActionClickListener {
            AppHelper.redirectToGooglePlay(requireContext())
            onVersionTerminateListener?.onContinueTask()
        }

        isCancelable = false
        isDragDisabled = true

        if (isToForceUpdate) {
            btn_rm_close.gone()

            if (AppHelper.canRedirectToGooglePlay().not()) {
                retryButton.gone()
                spaceVersionBottom.gone()

                tvTitle.text = getString(R.string.text_title_version_block_no_store)
                tvMessage.text = getString(R.string.text_message_version_block_error_no_store)
            }
        } else {
            btn_rm_close.visible()
        }

        btn_rm_close.setOnClickListener {
            dismiss()
            onVersionTerminateListener?.onContinueTask()
        }

        val newestVersion = ConfigurationPreference.instance
            .getConfigurationValue(ConfigurationDef.LATEST_VERSION_ANDROID, "")

        if (newestVersion.isNotEmpty()) {
            tvVersion.text =
                getString(R.string.text_version_block_version, newestVersion)
        } else {
            tvVersion.gone()
        }
    }
}