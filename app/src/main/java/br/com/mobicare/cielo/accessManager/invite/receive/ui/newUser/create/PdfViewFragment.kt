package br.com.mobicare.cielo.accessManager.invite.receive.ui.newUser.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.databinding.FragmentPdfViewBinding
import com.github.barteksc.pdfviewer.PDFView
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.net.URL
import android.util.Base64
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.invite.receive.ui.newUser.create.base.InviteReceiveBaseFragment
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import kotlinx.coroutines.CoroutineExceptionHandler

class PdfViewFragment : InviteReceiveBaseFragment(), CieloNavigationListener {

    private var binding: FragmentPdfViewBinding? = null
    val args: PdfViewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentPdfViewBinding.inflate(
            inflater, container, false
        ).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadPdf()
    }

    private fun loadPdf() {
        binding?.apply {
            loadPdf(args.pdfargs, pdfView) {
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
}