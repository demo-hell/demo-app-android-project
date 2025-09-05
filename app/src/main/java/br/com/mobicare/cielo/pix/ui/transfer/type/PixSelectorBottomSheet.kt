package br.com.mobicare.cielo.pix.ui.transfer.type

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.setupBottomSheet
import br.com.mobicare.cielo.databinding.PixSelectorBottomSheetBinding
import br.com.mobicare.cielo.pix.constants.PIX_LIST_KEY_TYPE_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_LIST_KEY_TYPE_TITLE_ARGS
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum
import br.com.mobicare.cielo.pix.model.ListKeyType
import br.com.mobicare.cielo.pix.ui.transfer.type.adapter.SelectorPixAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PixSelectorBottomSheet : BottomSheetDialogFragment(), PixSelectorContract.View {

    lateinit var binding: PixSelectorBottomSheetBinding
    private var listener: PixSelectorContract.Result? = null

    companion object {
        fun onCreate(
                listener: PixSelectorContract.Result,
                listKeyType: ListKeyType,
                title: String
        ) = PixSelectorBottomSheet().apply {
            this.listener = listener
            this.arguments = Bundle().apply {
                this.putParcelable(PIX_LIST_KEY_TYPE_ARGS, listKeyType)
                this.putString(PIX_LIST_KEY_TYPE_TITLE_ARGS, title)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupBottomSheet(dialog = dialog,
            action = { dismiss() }
        )
        binding = PixSelectorBottomSheetBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogResize
    }

    private fun getKeyTypes(): ListKeyType? = (arguments
            ?.getParcelable(PIX_LIST_KEY_TYPE_ARGS))

    private fun getTitle(): String? = (arguments
        ?.getString(PIX_LIST_KEY_TYPE_TITLE_ARGS))

    private fun setupView() {
        getTitle()?.let {
            binding.titleSelectorPix.text = it
        }
        binding.rvSelectorPix.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )

        getKeyTypes()?.list?.let {
            binding.rvSelectorPix.adapter = SelectorPixAdapter(it, this)
        }
    }

    override fun onSelectedKeyType(keyType: PixKeyTypeEnum) {
        dismiss()
        listener?.onShowKeyTypeSelected(keyType)
    }
}