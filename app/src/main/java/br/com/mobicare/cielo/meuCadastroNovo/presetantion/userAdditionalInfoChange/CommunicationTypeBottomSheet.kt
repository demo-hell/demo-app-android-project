package br.com.mobicare.cielo.meuCadastroNovo.presetantion.userAdditionalInfoChange

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.DividerItemDecoration
import br.com.mobicare.cielo.databinding.BottomSheetContactTypeBinding
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.TypeOfCommunication
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.userAdditionalInfoChange.adapter.CommunicationTypeAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CommunicationTypeBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetContactTypeBinding
    private lateinit var communicationAdapter: CommunicationTypeAdapter
    private lateinit var typesOfCommunication: List<TypeOfCommunication>
    private lateinit var onButtonClicked: (List<TypeOfCommunication>) -> Unit

    companion object {
        fun create(
            typesOfCommunication: List<TypeOfCommunication>,
            onButtonClicked: (List<TypeOfCommunication>) -> Unit
        ) = CommunicationTypeBottomSheet().apply {
            this.typesOfCommunication = typesOfCommunication
            this.onButtonClicked = onButtonClicked
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetContactTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureBottomSheet()
        setupView()
        configureAdapter()
    }

    private fun configureBottomSheet() {
        dialog?.let { dialog ->
            dialog.setOnShowListener {
                val bottomSheetDialog = it as BottomSheetDialog
                val bottomSheet =
                    bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                val behavior = BottomSheetBehavior.from(bottomSheet)
                val layoutParams = bottomSheet.layoutParams

                val displayMetrics = resources.displayMetrics
                layoutParams.height = (displayMetrics.heightPixels * 0.6).toInt()
                bottomSheet.layoutParams = layoutParams

                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.isDraggable = false
            }
        }
    }

    private fun setupView() {
        binding.apply {
            binding.btnContinue.setOnClickListener {
                this@CommunicationTypeBottomSheet.dismiss()
                onButtonClicked.invoke(communicationAdapter.getSelectedList())
            }
        }
    }

    private fun configureAdapter() {
        communicationAdapter = CommunicationTypeAdapter(
            onItemClicked = {
                binding.btnContinue.isEnabled = communicationAdapter.getSelectedList().isNotEmpty()
            }
        )
        communicationAdapter.setData(typesOfCommunication)
        binding.recyclerView.apply {
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            this.adapter = communicationAdapter
        }
    }

}