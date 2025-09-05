package br.com.mobicare.cielo.chargeback.presentation.details.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chargeback.domain.model.Chargeback
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackDocument
import br.com.mobicare.cielo.chargeback.presentation.details.viewmodel.ChargebackDocumentViewModel
import br.com.mobicare.cielo.chargeback.presentation.details.viewmodel.ChargebackDoneDetailsViewModel
import br.com.mobicare.cielo.chargeback.utils.UiState
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentChargebackDocumentViewBinding
import kotlinx.android.synthetic.main.error_chargeback_document.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class ChargebackDocumentViewFragment : BaseFragment(), CieloNavigationListener {

    private var _binding: FragmentChargebackDocumentViewBinding? = null
    private val binding get() = _binding!!

    private val args: ChargebackDocumentViewFragmentArgs by navArgs()
    private val viewModel: ChargebackDocumentViewModel by viewModel()

    private var navigation: CieloNavigation? = null
    private lateinit var chargeback: Chargeback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chargeback = args.chargeback
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentChargebackDocumentViewBinding.inflate(
        inflater, container, false
    ).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()

        binding.includeError.root.btnReload.setOnClickListener { onReload() }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> onDocumentLoading()
                is UiState.Success -> onDocumentSuccess(state.data)
                is UiState.Error, is UiState.Empty -> onDocumentError()
            }
        }

        onReload()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun onReload() {
        viewModel.getChargebackDocument(chargeback)
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.let {
                it.setNavigationListener(this)
                it.showHelpButton(true)
                it.setTextToolbar(getString(R.string.chargeback_documentation))
            }
        }
    }

    private fun onDocumentLoading() {
        binding.apply {
            pdfView.gone()
            photoView.gone()
            includeError.root.gone()
            progressIndicator.visible()
        }
    }

    private fun onDocumentError() {
        binding.apply {
            progressIndicator.gone()
            pdfView.gone()
            photoView.gone()
            includeError.root.visible()
        }
    }

    private fun onDocumentSuccess(document: ChargebackDocument?) {
        if (document == null || document.file.isNullOrBlank() || document.fileName.isNullOrBlank()) {
            onDocumentError()
            return
        }

        loadFile(document.fileName, document.file)
    }

    private fun loadFile(fileName: String, encodedFile: String) {
        when (File(fileName).extension.lowercase()) {
            PDF -> loadPdfFromBase64(encodedFile)
            else -> loadImageFromBase64(encodedFile)
        }
    }

    private fun loadPdfFromBase64(encodedFile: String) {
        try {
            val pdfBytes = decodeFromBase64(encodedFile)
            binding.apply {
                pdfView.apply {
                    visible()
                    fromBytes(pdfBytes)
                        .onLoad { progressIndicator.gone() }
                        .spacing(resources.getDimension(R.dimen.dimen_8dp).toInt())
                        .load()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onDocumentError()
        }
    }

    private fun loadImageFromBase64(encodedFile: String) {
        try {
            val imageBytes = decodeFromBase64(encodedFile)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, ZERO, imageBytes.size)
            binding.apply {
                progressIndicator.gone()
                photoView.apply {
                    setImageBitmap(bitmap)
                    visible()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onDocumentError()
        }
    }

    private fun decodeFromBase64(value: String) = Base64.decode(value, Base64.DEFAULT)

    companion object {
        private const val PDF = "pdf"
    }

}