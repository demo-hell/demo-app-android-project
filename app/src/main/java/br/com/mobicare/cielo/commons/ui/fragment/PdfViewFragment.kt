package br.com.mobicare.cielo.commons.ui.fragment

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import com.github.barteksc.pdfviewer.PDFView
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.net.URL
import android.util.Base64
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.handleSslError
import br.com.mobicare.cielo.commons.utils.open
import br.com.mobicare.cielo.databinding.TermFragmentViewBinding
import br.com.mobicare.cielo.extensions.isValidUrl
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import kotlinx.coroutines.CoroutineExceptionHandler

class TermViewFragment : BaseFragment(), CieloNavigationListener {

    private var binding: TermFragmentViewBinding? = null
    val args: TermViewFragmentArgs by navArgs()

    private var navigation: CieloNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return TermFragmentViewBinding.inflate(
            inflater, container, false
        ).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUrl()
    }

    private fun loadUrl() {
        if(args.termargs.isValidUrl() && args.termargs.endsWith(PDF_EXTENSION, true).not()) {
            loadWebView()
        } else {
            loadPdf()
        }
    }

    private fun loadWebView() {
        binding?.webView?.open(
            args.termargs,
            object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    binding?.pbPdfLoading.visible()
                }
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding?.pbPdfLoading.gone()
                    binding?.webView.visible()
                }
                override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError?) {
                    view?.handleSslError(handler, requireContext())
                }
            }
        )
    }

    private fun loadPdf() {
        binding?.apply {
            loadPdf(args.termargs, pdfView) {
                binding?.pbPdfLoading.gone()
            }
        }
    }

    override fun onRetry() {
        loadPdf()
    }

    override fun onClickSecondButtonError() {
        findNavController().navigateUp()
    }

    override fun onActionSwipe() {
        findNavController().navigateUp()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun loadPdf(
        pdf: String,
        pdfView: PDFView,
        onLoadCompleteListener: OnLoadCompleteListener? = null
    ) {
        if (URLUtil.isValidUrl(pdf))
            downloadFile(pdf, pdfView, onLoadCompleteListener)
        else
            loadBase64(pdf, pdfView, onLoadCompleteListener)
    }

    private fun downloadFile(
        pdf: String,
        pdfView: PDFView,
        onLoadCompleteListener: OnLoadCompleteListener? = null
    ) {
        lifecycleScope.launch(IO + CoroutineExceptionHandler { _, _ ->
            showErrorBottomSheet()
        }) {
            pdfView.fromStream(URL(pdf).openStream().buffered()).onLoad {
                onLoadCompleteListener?.loadComplete(it)
            }.onError {
                showErrorBottomSheet()
            }.load()
        }
    }

    private fun loadBase64(
        pdf: String,
        pdfView: PDFView,
        onLoadCompleteListener: OnLoadCompleteListener? = null
    ) {
        val pdfBytes = Base64.decode(pdf, Base64.DEFAULT)
        pdfView.fromBytes(pdfBytes).onLoad {
            onLoadCompleteListener?.loadComplete(it)
        }.load()
    }

    private fun showErrorBottomSheet() {
        navigation?.showErrorBottomSheet(
            textButton = getString(R.string.back),
            isFullScreen = true
        )
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private companion object {
        const val PDF_EXTENSION = ".pdf"
    }
}