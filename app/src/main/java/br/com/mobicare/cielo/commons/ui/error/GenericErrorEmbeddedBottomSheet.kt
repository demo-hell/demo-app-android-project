package br.com.mobicare.cielo.commons.ui.error

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_generic_error.*

class EmbeddedGenericErrorBottomSheet : BottomSheetDialogFragment() {

    var title: String? = null
    var message: String? = null
    var listener: OnGenericErrorBottomSheetListener? = null

    companion object {
        fun newInstance(title: String? = null, message: String? = null, listener: OnGenericErrorBottomSheetListener? = null)
                = EmbeddedGenericErrorBottomSheet().apply {
            this.title = title
            this.message = message
            this.listener = listener
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
        = inflater.inflate(R.layout.layout_generic_error, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.title?.let { itTitle ->
            this.tvTitle.text = itTitle
        }

        this.message?.let { itMessage ->
            this.tvMessage.text = itMessage
        }

        this.retryButton?.setOnClickListener {
            this.listener?.let {
                it.onRetry()
            }
        }
    }

    interface OnGenericErrorBottomSheetListener {
        fun onRetry()
    }

}