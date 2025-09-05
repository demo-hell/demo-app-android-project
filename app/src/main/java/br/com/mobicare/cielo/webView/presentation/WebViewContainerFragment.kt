package br.com.mobicare.cielo.webView.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.databinding.WebviewContainerBinding
import br.com.mobicare.cielo.main.presentation.OnLogoutProceedCallback
import br.com.mobicare.cielo.main.presentation.createSessionTimeoutDialog
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.webView.utils.FLOW_NAME_PARAM
import br.com.mobicare.cielo.webView.utils.URL_PARAM
import br.com.mobicare.cielo.webView.utils.WebViewSharedConfiguration
import org.koin.android.ext.android.inject

class WebViewContainerFragment() : BaseFragment() {
    private var binding: WebviewContainerBinding? = null
    private val sharedConfiguration: WebViewSharedConfiguration by inject()

    private val url: String
        get() = arguments?.getString(URL_PARAM) ?: EMPTY

    private val flowName: String
        get() = arguments?.getString(FLOW_NAME_PARAM) ?: EMPTY

    private val mContext: Context
        get() = requireActivity()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        WebviewContainerBinding.inflate(inflater, container, false).also {
            binding = it
        }.root

    companion object {
        fun newInstance() = WebViewContainerFragment()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()
        configureAndStartWebView()
    }

    private fun configureToolbar() {
        configureToolbarActionListener?.changeTo(
            R.color.colorPrimary,
            R.color.colorPrimaryDark, title =
            getString(R.string.text_values_received_navigation_label)
        )
    }

    private fun configureAndStartWebView() {
        binding?.apply {
            webViewContainer.webView?.let { webView ->
                sharedConfiguration
                    .setupWebView(
                        flowName,
                        url,
                        webView,
                        onPageFinished = {
                            pbLoading.gone()
                            webViewContainer.visible()
                        },
                        onExpiredToken = {
                            mContext.createSessionTimeoutDialog(
                                object :
                                    OnLogoutProceedCallback {
                                    override fun logout() {
                                        SessionExpiredHandler.userSessionExpires(
                                            mContext,
                                            true,
                                        )

                                        requireActivity().finishAffinity()
                                    }
                                },
                            )
                        },
                        onGeneralError = {
                            errorLayout.visible()
                            errorLayout.errorButton.gone()
                            webView.gone()
                        }
                    ).start()
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        sharedConfiguration.finishWebView()
        super.onDestroyView()
    }
}