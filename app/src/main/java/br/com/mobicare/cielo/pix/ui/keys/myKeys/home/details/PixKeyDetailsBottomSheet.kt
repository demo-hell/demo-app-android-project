package br.com.mobicare.cielo.pix.ui.keys.myKeys.home.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.getFormattedKey
import br.com.mobicare.cielo.commons.utils.getIconKeyPix
import br.com.mobicare.cielo.commons.utils.getKeyTypeName
import br.com.mobicare.cielo.commons.utils.setupBottomSheet
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.constants.PIX_MY_KEY_DETAILS
import br.com.mobicare.cielo.pix.domain.MyKey
import br.com.mobicare.cielo.pix.ui.keys.myKeys.home.details.PixMyKeysDetails
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.pix_key_details_bottom_sheet.*
import kotlinx.android.synthetic.main.view_pix_my_key_options.*
import kotlinx.android.synthetic.main.view_pix_my_key_options.view.*

class PixKeyDetailsBottomSheet : BottomSheetDialogFragment() {

    private var myKey: MyKey? = null
    private var listener: PixMyKeysDetails.View? = null

    companion object {
        fun onCreate(
                keyDetails: MyKey,
                listener: PixMyKeysDetails.View,
        ) = PixKeyDetailsBottomSheet().apply {
            this.listener = listener
            this.arguments = Bundle().apply {
                this.putParcelable(PIX_MY_KEY_DETAILS, keyDetails)
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        setupBottomSheet(dialog = dialog,
                action = { dismiss() }
        )
        return inflater.inflate(R.layout.pix_key_details_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getKeyInfo()
        setupView()
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogResize
    }

    private fun getKeyInfo() {
        myKey = arguments?.getParcelable(PIX_MY_KEY_DETAILS)
    }

    private fun setupView() {
        myKey?.let { itKey ->
            tvKeyValue?.text = itKey.key?.let { getFormattedKey(it, itKey.keyType, isMask = false) }
            ivKeyType?.setBackgroundResource(getIconKeyPix(itKey.keyType))

            setupListeners(itKey)

            if (itKey.main == true) setupMainKey()
            tvKeyName?.text = getKeyTypeName(itKey.keyType)
        }
    }

    private fun setupListeners(key: MyKey) {
        containerKeyOptions?.let {
            val currentKey = key.key ?: EMPTY

            copyKeyContainer?.setOnClickListener {
                listener?.copyKey(currentKey)
                dismiss()
            }

            shareKeyContainer?.setOnClickListener {
                listener?.shareKey(currentKey)
                dismiss()
            }

            whatIsMainKeyContainer?.setOnClickListener {
                listener?.showWhatIsMainKeyFAQ()
                dismiss()
            }

            deleteKeyContainer?.setOnClickListener {
                if (key.main == true)
                    listener?.deleteMainKey(currentKey)
                else
                    listener?.deleteNormalKey(currentKey)

                dismiss()
            }
        }
    }

    private fun setupMainKey() {
        containerMainKey.visible()
        containerKeyOptions?.whatIsMainKeyContainer?.visible()
    }
}