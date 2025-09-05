package br.com.mobicare.cielo.chargeback.presentation.details.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chargeback.domain.model.Chargeback
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackDocumentSender
import br.com.mobicare.cielo.chargeback.presentation.details.adapter.ChargebackDocumentSenderAdapter
import br.com.mobicare.cielo.chargeback.presentation.details.viewmodel.ChargebackDocumentSenderViewModel
import br.com.mobicare.cielo.chargeback.utils.UiState
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.FileUtils
import br.com.mobicare.cielo.databinding.FragmentChargebackDocumentSenderBinding
import kotlinx.android.synthetic.main.error_chargeback_document.view.btnReload
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChargebackDocumentSenderFragment : BaseFragment(), CieloNavigationListener {

    private var _binding: FragmentChargebackDocumentSenderBinding? = null
    private val binding get() = _binding!!

    private val args: ChargebackDocumentSenderFragmentArgs by navArgs()
    private val viewModel: ChargebackDocumentSenderViewModel by viewModel()
    private var navigation: CieloNavigation? = null
    private lateinit var chargeback: Chargeback
    private var adapter: ChargebackDocumentSenderAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chargeback = args.chargeback
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentChargebackDocumentSenderBinding.inflate(
        inflater, container, false
    ).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        titleItemSize()
        setupObservers()
        onReload()
        setupListeners()

    }

    override fun onResume() {
        super.onResume()
        setupObservers()
        onReload()
    }

    private fun titleItemSize(){
        var documentSize = chargeback.chargebackDetails?.refundFileInformation?.size
        binding.tvCountItem.text = getString(R.string.message_documents_found, documentSize)
    }
    private fun setupObservers() {
        viewModel.documentSenderLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> onDocumentLoading()
                is UiState.Success -> onDocumentSuccess(state.data)
                is UiState.Error, is UiState.Empty -> onDocumentError()
            }
        }
    }

    private fun setupListeners(){
        binding.apply{
            includeError.root.btnReload.setOnClickListener {
                onReload()
                setupObservers()
            }
            btnClose.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun onDocumentLoading() {
        binding.apply {
            recycleview.gone()
            includeError.root.gone()
            llContainerShimmer.apply {
                visible()
                contentDescription = getString(R.string.chargeback_accessibility_shimmer_load)
            }
        }
    }

    private fun onHideLoading() {
        binding.apply {
            llContainerShimmer.gone()
            recycleview.visible()
        }
    }

    private fun onDocumentSuccess(document: ChargebackDocumentSender?) {
        onHideLoading()
        setupAdapter()
        adapter?.setOnTap {
            document?.let { refundFileInformationList -> loadFile(refundFileInformationList.nameFile, refundFileInformationList.fileBase64) }
        }
    }

    private fun loadFile(fileName: String, encodedFile: String) {
        encodedFile.let { base64 ->
            val tempFile = FileUtils(requireContext()).convertBase64ToFile(
                base64String = base64,
                fileName = fileName,
                fileType = EMPTY
            )
            FileUtils(requireContext()).startShare(tempFile)
        }
    }

    private fun onDocumentError() {
        binding.apply {
            layoutSender.gone()
            includeError.root.visible()
        }
    }

    private fun setupAdapter() {
        val refundFileInformation = chargeback.chargebackDetails?.refundFileInformation
        if (!refundFileInformation.isNullOrEmpty()) {
            adapter = ChargebackDocumentSenderAdapter(refundFileInformation)
            binding.recycleview.layoutManager = LinearLayoutManager(context)
            binding.recycleview.adapter = adapter
        } else {
            onDocumentError()
        }
    }

    private fun onReload() {
        chargeback.chargebackDetails?.refundFileInformation?.firstOrNull()?.let { documentId ->
            viewModel.getChargebackDocumentSender(chargeback, documentId)
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.let {
                it.setNavigationListener(this)
                it.showHelpButton(false)
                it.setTextToolbar(getString(R.string.chargeback_document_attachment_sender))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}